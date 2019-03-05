package mg.montracking.controllers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

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

import mg.montracking.Utils.*;
import mg.montracking.service.Motor;
import mg.montracking.service.Searcher;
import mg.montracking.service.SmoothSearcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * The controller for application, where the application logic is
 * implemented. It handles the button for starting/stopping the camera and the
 * acquired video stream.
 *
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 *
 */
public class VideoCaptureController {
	// the FXML button
	@FXML
	private Button startCameraButton, startSearcherButton;
	// the FXML image view
	@FXML
	private ImageView currentFrame;

	// a timer for acquiring the video stream
	private ScheduledExecutorService frameGrabberTimer;
	// the OpenCV object that realizes the video capture
	private VideoCapture capture = new VideoCapture();
	// a flag to change the button behavior
	private boolean cameraActive = false,
			searcherActive = false;
	// the id of the camera to be used
	private static int cameraId = 0;

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
	
	public void initGpio() {
		System.out.println("Starting init");
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

		//SmoothSearcher smoothSearcher = new SmoothSearcher(bottomMotor, upperMotor);
		searcher = new SmoothSearcher(bottomMotor, upperMotor);
		/**************************************************************************************/
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
							upperMotor.stepMove(400, 300);
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
							upperMotor.stepMove(400, 300);
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
        		searcher.start(320);
        	};
			this.frameGrabberTimer = Executors.newSingleThreadScheduledExecutor();
			this.frameGrabberTimer.scheduleAtFixedRate(rSearcher, 0, 40, TimeUnit.MILLISECONDS);
        	searcherActive = true;
        	startSearcherButton.setText("Stop searcher");
        }
        else {
        	stopSearcher();
        	try
			{
				// stop the timer
				this.frameGrabberTimer.shutdown();
				this.frameGrabberTimer.awaitTermination(10, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
        	searcherActive = false;
        	startSearcherButton.setText("Start searcher");
        }
	}
	
	public void stopSearcher() {
		searcher.stop();
	}
	
	/**
	 * The action triggered by pushing the button on the GUI
	 *
	 * @param event
	 *            the push button event
	 */
	@FXML
	protected void startCamera(ActionEvent event) {
		if (!this.cameraActive)
		{
			this.capture.open(cameraId);
			this.capture.set(3, 640);	// width
			this.capture.set(4, 480);	// height
			if (this.capture.isOpened())
			{
				this.cameraActive = true;
				Runnable frameGrabber = () -> updateImageView(currentFrame, Utils.mat2Image(grabFrame()));
				this.frameGrabberTimer = Executors.newSingleThreadScheduledExecutor();
				this.frameGrabberTimer.scheduleAtFixedRate(frameGrabber, 0, 40, TimeUnit.MILLISECONDS);
				this.startCameraButton.setText("Stop Camera");
			}
			else
			{
				System.err.println("Impossible to open the camera connection...");
			}
		}
		else
		{
			this.cameraActive = false;
			this.startCameraButton.setText("Start Camera");
			this.stopAcquisition();
		}
	}
	
	/**
	 * Get a frame from the opened video stream (if any)
	 *
	 * @return the {@link Mat} to show
	 */
	private Mat grabFrame() {
		Mat frame = new Mat();
		if (this.capture.isOpened())
		{
			try
			{
				this.capture.read(frame);
				// if the frame is not empty, process it
				if (!frame.empty())
				{
					Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
				}
				
			}
			catch (Exception e)
			{
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		
		return frame;
	}
	
	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	private void stopAcquisition() {
		if (this.frameGrabberTimer!=null && !this.frameGrabberTimer.isShutdown())
		{
			try
			{
				// stop the timer
				this.frameGrabberTimer.shutdown();
				this.frameGrabberTimer.awaitTermination(10, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.capture.isOpened())
		{
			// release the camera
			this.capture.release();
		}
	}
	
	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view
	 *            the {@link ImageView} to update
	 * @param image
	 *            the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}
	
	/**
	 * On application close, stop the acquisition from the camera
	 */
	public void setClosed()	{
		this.stopAcquisition();
	}
	
}
