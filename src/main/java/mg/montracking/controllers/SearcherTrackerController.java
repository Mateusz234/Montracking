package mg.montracking.controllers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.CommandArgumentParser;

import mg.montracking.service.Motor;
import mg.montracking.service.PulseSearcher;
import mg.montracking.service.Searcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;


/**
 * Handles the button for starting/stopping searcher algorithm. Initializes GPIO 
 * and manages {@link Searcher} algorithm 
 *
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 *
 */
public class SearcherTrackerController {
	@FXML
	private Button startSearcherButton,
				   imageProcessingViewButton,
				   searcherTrackerViewButton;
	
	@FXML
	private Slider sliderBottomMotorPWM,
				   sliderUpperMotorPWM;
	
	
	//Singleton object to manipulate views
	ScreenController sController = ScreenController.getInstance();
	// a timer for acquiring the video stream
	private ScheduledExecutorService motorMoveTimer;
	// a flag to change the button behavior
	private boolean searcherActive = false;
	private int bottomMotorPWM = 250,
				upperMotorPWM = 400;
	
	
	GpioController gpioInstance;
	Pin bottomMotorPwmPin;
	Pin upperMotorPwmPin;
	GpioPinPwmOutput bottomMotorPwm;
	GpioPinPwmOutput upperMotorPwm;
	Motor bottomMotor;
	Motor upperMotor;
	Searcher searcher;
	GpioPinDigitalInput bottomLimitSwitchFront,
						bottomLimitSwitchBack, 
						upperLimitSwitchFront, 
						upperLimitSwitchBack;
	GpioPinDigitalOutput bottomMotorLeftDirPin,
						 bottomMotorRightDirPin, 
						 upperMotorLeftDirPin, 
						 upperMotorRightDirPin;
	
	/**
	 * Initializes all used pins in Raspberry PI such as {@link GpioPinDigitalInput} or {@link GpioPinDigitalOutput}
	 * and creates {@link Motor}s and {@link Searcher} objects.
	 */
	public void initGpio() {
		gpioInstance = GpioFactory.getInstance();
		bottomLimitSwitchFront = gpioInstance.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
		bottomLimitSwitchBack = gpioInstance.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_DOWN);
		upperLimitSwitchFront = gpioInstance.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_DOWN);
		upperLimitSwitchBack = gpioInstance.provisionDigitalInputPin(RaspiPin.GPIO_10, PinPullResistance.PULL_DOWN);

		bottomMotorLeftDirPin = gpioInstance.provisionDigitalOutputPin(RaspiPin.GPIO_12, "bottomMotorLeftDirPin", PinState.LOW);
		bottomMotorRightDirPin = gpioInstance.provisionDigitalOutputPin(RaspiPin.GPIO_13, "bottomMotorRightDirPin", PinState.LOW);
		upperMotorLeftDirPin = gpioInstance.provisionDigitalOutputPin(RaspiPin.GPIO_15, "upperMotorLeftDirPin", PinState.LOW);
		upperMotorRightDirPin = gpioInstance.provisionDigitalOutputPin(RaspiPin.GPIO_16, "upperMotorRightDirPin", PinState.LOW);

		// PWM pins definitions
		bottomMotorPwmPin = CommandArgumentParser.getPin(
				RaspiPin.class,    // pin provider class to obtain pin instance from
				RaspiPin.GPIO_01);  // default pin if no pin argument found
		upperMotorPwmPin = CommandArgumentParser.getPin(
				RaspiPin.class,    // pin provider class to obtain pin instance from
				RaspiPin.GPIO_24);  // default pin if no pin argument found
		bottomMotorPwm = gpioInstance.provisionPwmOutputPin(bottomMotorPwmPin);
		upperMotorPwm = gpioInstance.provisionPwmOutputPin(upperMotorPwmPin);

		// Motors definition
		bottomMotor = new Motor(bottomMotorLeftDirPin, bottomMotorRightDirPin, bottomMotorPwm);
		upperMotor = new Motor(upperMotorLeftDirPin, upperMotorRightDirPin, upperMotorPwm);

		//searcher = new SmoothSearcher(bottomMotor, upperMotor);
		searcher = new PulseSearcher(bottomMotor, upperMotor);

		// PWM configuration
		com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
		com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
		com.pi4j.wiringpi.Gpio.pwmSetClock(500);

		// Program
		bottomLimitSwitchFront.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(event.getState() == PinState.HIGH) {
					searcher.toggleSearchingDirectionBottomMotor();
					new Thread(() -> {
						try {
							upperMotor.stepMove(upperMotorPWM, 300);
						} catch (InterruptedException e) {
							upperMotor.stopPwm();
							e.printStackTrace();
						}
					}).start();
				}
			}
		});
		bottomLimitSwitchBack.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(event.getState() == PinState.HIGH) {
					searcher.toggleSearchingDirectionBottomMotor();
					new Thread(() -> {
						try {
							upperMotor.stepMove(upperMotorPWM, 300);
						} catch (InterruptedException e) {
							upperMotor.stopPwm();
							e.printStackTrace();
						}
					}).start();
				}
			}
		});
		upperLimitSwitchFront.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(event.getState() == PinState.HIGH) {
					searcher.toggleSearchingDirectionUpperMotor();
				}
			}
		});
		upperLimitSwitchBack.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(event.getState() == PinState.HIGH) {
					searcher.toggleSearchingDirectionUpperMotor();
				}
			}
		});
	}
	@FXML
	protected void startSearcher(ActionEvent event) {
        // starting searching algorithm
        if(!searcherActive) {
        	Runnable rSearcher = () -> {
        		searcher.start(bottomMotorPWM);
        	};
			this.motorMoveTimer = Executors.newSingleThreadScheduledExecutor();
			this.motorMoveTimer.scheduleAtFixedRate(rSearcher, 0, 20, TimeUnit.MILLISECONDS);
        	searcherActive = true;
        	startSearcherButton.setText("Stop searcher");
        }
        else {
        	stopSearcher();
        	try
			{
				// stop the timer
				this.motorMoveTimer.shutdown();
				this.motorMoveTimer.awaitTermination(20, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				System.err.println("Exception in stopping the searcher... " + e);
			}
        	searcherActive = false;
        	startSearcherButton.setText("Start searcher");
        }
	}
	
	public void stopSearcher() {
		searcher.stop();
	}
	
	@FXML public void showViewImageProcessing() {
		System.out.println("show view image processing");
		sController.activate("ImageProcessing");
	}
	@FXML public void showViewSearcherTracker() {
		System.out.println("show view  searcher tracker");
		sController.activate("SearcherTracker");
	}
	@FXML public void setBottomMotorPWM() {
		bottomMotorPWM = (int) sliderBottomMotorPWM.getValue();
	}
	@FXML public void setUpperMotorPWM() {
		upperMotorPWM = (int) sliderUpperMotorPWM.getValue();
	}
	
}
