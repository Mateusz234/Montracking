package mg.montracking.service;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

import com.pi4j.io.gpio.GpioPinPwmOutput;

/**
 * Combines {@link Gpio} pins from raspberry PI into Motor object.  
 * 
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 * 
 */
public class Motor {
	
	private GpioPinDigitalOutput motorMoveRight;
	private GpioPinDigitalOutput motorMoveLeft;
	private GpioPinPwmOutput pwm;
	
	private boolean makeStep;
	
	public Motor(GpioPinDigitalOutput motorMoveRight, GpioPinDigitalOutput motorMoveLeft, GpioPinPwmOutput pwm){
	this.motorMoveRight = motorMoveRight;
		this.motorMoveLeft = motorMoveLeft;
		this.pwm = pwm;
		initializeMotors();
	}
	
	/**
	 * Initializes motors when starting program to start searching algorithm turning right
	 */
	private void initializeMotors() {
		setMotorConfigToTurnRight();
		pwm.setPwm(0);
	}
	
	public void stopHigh(){
		motorMoveRight.high();
		motorMoveLeft.high();
		pwm.setPwm(0);
	}
	public void stopLow(){
		motorMoveRight.low();
		motorMoveLeft.low();
		pwm.setPwm(0);
	}
	public void stopPwm(){
		pwm.setPwm(0);
	}
	public Boolean isStoppedLow(){
		if(motorMoveRight.isLow() && motorMoveLeft.isLow()) return true;
		else return false;
	}
	public Boolean isStoppedHigh(){
		if(motorMoveRight.isHigh() && motorMoveLeft.isHigh()) return true;
		else return false;
	}
	public Boolean isLeft(){
		if(motorMoveRight.isLow() && motorMoveLeft.isHigh()) return true;
		else return false;
	}
	public Boolean isRight(){
		if(motorMoveRight.isHigh() && motorMoveLeft.isLow()) return true;
		else return false;
	}
	
	/**
	 * Sets gpio pins that controls rotate direction of motors to turn motor right
	 */
	public void setMotorConfigToTurnRight(){
		motorMoveRight.high();
		motorMoveLeft.low();
	}
	
	/**
	 * Sets gpio pins that controls rotate direction of motors to turn motor left
	 */
	public void setMotorConfigToTurnLeft(){
		motorMoveRight.low();
		motorMoveLeft.high();
	}
	
	/**
	 * Sets pwm of motor to move in direction set by {@link setMotorConfigToTurnRight()} 
	 * and {@link setMotorConfigToTurnLeft()} methods causing motor movement
	 * 
	 * @param pwmValue
	 *            motor speed
	 */
	public void move(int pwmValue) {
		pwm.setPwm(pwmValue);
	}
	
	/**
	 * Provides mechanism to move motor step by step when needed
	 * 
	 * @param pwmValue
	 *            motor speed
	 * @param ms
	 * 			  time of step in milliseconds
	 * @throws InterruptedException 
	 */
	public void stepMove(int pwmValue, int ms) throws InterruptedException {
		pwm.setPwm(pwmValue);
		Thread.sleep(ms);
		pwm.setPwm(0);
	}

	public boolean isMakeStep() {
		return makeStep;
	}

	public void setMakeStep(boolean makeStep) {
		this.makeStep = makeStep;
	}
}
