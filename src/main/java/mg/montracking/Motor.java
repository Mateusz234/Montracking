package mg.montracking;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;

public class Motor {
	
	private GpioPinDigitalOutput motorMoveRight;
	private GpioPinDigitalOutput motorMoveLeft;
	private GpioPinPwmOutput pwm;
	
	Motor(GpioPinDigitalOutput motorMoveRight, GpioPinDigitalOutput motorMoveLeft, GpioPinPwmOutput pwm){
		this.motorMoveRight = motorMoveRight;
		this.motorMoveLeft = motorMoveLeft;
		this.pwm = pwm;
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
	public Boolean isStopLow(){
		if(motorMoveRight.isLow() && motorMoveLeft.isLow()) return true;
		else return false;
	}
	public Boolean isStopHigh(){
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
	public void moveRight(int pwmValue){
		motorMoveRight.high();
		motorMoveLeft.low();
		pwm.setPwm(pwmValue);
	}
	public void moveLeft(int pwmValue){
		motorMoveRight.low();
		motorMoveLeft.high();
		pwm.setPwm(pwmValue);
	}
}
