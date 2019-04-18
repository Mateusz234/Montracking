package mg.montracking.service;

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

import mg.montracking.entity.BaseSearcher;
import mg.montracking.entity.Motor;
import mg.montracking.entity.SmoothSearcher;
import mg.montracking.repository.MotorRepository;

public class SearcherService {

	private ScheduledExecutorService motorMovementScheduler;
	private int bottomMotorPWM = 500, upperMotorPWM = 400;

	MotorRepository motorRepository = MotorRepository.getInstance();
	
	GpioController gpioInstance;
	Pin bottomMotorPwmPin, upperMotorPwmPin;
	GpioPinPwmOutput bottomMotorPwm, upperMotorPwm;
	Motor bottomMotor, upperMotor;
	BaseSearcher searcher;
	GpioPinDigitalInput bottomLimitSwitchFront, bottomLimitSwitchBack, upperLimitSwitchFront, upperLimitSwitchBack;
	GpioPinDigitalOutput bottomMotorLeftDirPin, bottomMotorRightDirPin, upperMotorLeftDirPin, upperMotorRightDirPin;
	Runnable rSearcher;

	private SearcherService() {
	}

	public static SearcherService getInstance() {
		return SearcherServiceHolder.INSTANCE;
	}

	private static class SearcherServiceHolder {
		private static final SearcherService INSTANCE = new SearcherService();
	}

	/**
	 * Initializes all used pins in Raspberry PI such as {@link GpioPinDigitalInput}
	 * or {@link GpioPinDigitalOutput} and creates {@link Motor}s and
	 * {@link BaseSearcher} objects.
	 */
	public void initGpio() {

		gpioInstance = GpioFactory.getInstance();
		bottomLimitSwitchFront = gpioInstance.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
		bottomLimitSwitchBack = gpioInstance.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_DOWN);
		upperLimitSwitchFront = gpioInstance.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_DOWN);
		upperLimitSwitchBack = gpioInstance.provisionDigitalInputPin(RaspiPin.GPIO_10, PinPullResistance.PULL_DOWN);

		bottomMotorLeftDirPin = gpioInstance.provisionDigitalOutputPin(RaspiPin.GPIO_12, "bottomMotorLeftDirPin",
				PinState.LOW);
		bottomMotorRightDirPin = gpioInstance.provisionDigitalOutputPin(RaspiPin.GPIO_13, "bottomMotorRightDirPin",
				PinState.LOW);
		upperMotorLeftDirPin = gpioInstance.provisionDigitalOutputPin(RaspiPin.GPIO_15, "upperMotorLeftDirPin",
				PinState.LOW);
		upperMotorRightDirPin = gpioInstance.provisionDigitalOutputPin(RaspiPin.GPIO_16, "upperMotorRightDirPin",
				PinState.LOW);

		// PWM pins definitions
		bottomMotorPwmPin = CommandArgumentParser.getPin(RaspiPin.class, // pin provider class to obtain pin instance
																			// from
				RaspiPin.GPIO_01); // default pin if no pin argument found
		upperMotorPwmPin = CommandArgumentParser.getPin(RaspiPin.class, // pin provider class to obtain pin instance
																		// from
				RaspiPin.GPIO_24); // default pin if no pin argument found
		bottomMotorPwm = gpioInstance.provisionPwmOutputPin(bottomMotorPwmPin);
		upperMotorPwm = gpioInstance.provisionPwmOutputPin(upperMotorPwmPin);

		// Motors definition
		bottomMotor = new Motor(bottomMotorLeftDirPin, bottomMotorRightDirPin, bottomMotorPwm);
		upperMotor = new Motor(upperMotorLeftDirPin, upperMotorRightDirPin, upperMotorPwm);

		searcher = new SmoothSearcher(bottomMotor, upperMotor);
		// searcher = new PulseSearcher(bottomMotor, upperMotor);

		rSearcher = () -> searcher.getBottomMotor().move(bottomMotorPWM);
		
		// PWM configuration
		com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
		com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
		com.pi4j.wiringpi.Gpio.pwmSetClock(500);

		// Program
		bottomLimitSwitchFront.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getState() == PinState.HIGH) {
					searcher.toggleSearchingDirectionBottomMotor();
					new Thread(() -> {
						try {
							upperMotor.stepMove(upperMotorPWM, 300);
						} catch (InterruptedException e) {
							upperMotor.stop();
							e.printStackTrace();
						}
					}).start();
				}
			}
		});
		bottomLimitSwitchBack.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getState() == PinState.HIGH) {
					searcher.toggleSearchingDirectionBottomMotor();
					new Thread(() -> {
						try {
							upperMotor.stepMove(upperMotorPWM, 300);
						} catch (InterruptedException e) {
							upperMotor.stop();
							e.printStackTrace();
						}
					}).start();
				}
			}
		});
		upperLimitSwitchFront.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getState() == PinState.HIGH) {
					searcher.toggleSearchingDirectionUpperMotor();
				}
			}
		});
		upperLimitSwitchBack.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getState() == PinState.HIGH) {
					searcher.toggleSearchingDirectionUpperMotor();
				}
			}
		});
	}

	public void startSearcher() {
		this.motorMovementScheduler = Executors.newSingleThreadScheduledExecutor();
		this.motorMovementScheduler.scheduleAtFixedRate(rSearcher, 0, 20, TimeUnit.MILLISECONDS);
	}

	public void stopSearcher() {
		searcher.stop();
		try {
			// stop the timer
			this.motorMovementScheduler.shutdown();
			this.motorMovementScheduler.awaitTermination(20, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			System.err.println("Exception in stopping the searcher... " + e);
		}
	}

	public void setBottomMotorPWM(int bottomMotorPWM) {
		this.bottomMotorPWM = bottomMotorPWM;
	}

	public void setUpperMotorPWM(int upperMotorPWM) {
		this.upperMotorPWM = upperMotorPWM;
	}

	public void saveToRepoMotorPwm() {
		motorRepository.saveToRepoMotorPwm();
	}

}
