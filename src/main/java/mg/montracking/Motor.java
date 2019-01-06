package mg.montracking;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;

public class Motor {
	
	private GpioPinDigitalOutput motorMoveRight;
	private GpioPinDigitalOutput motorMoveLeft;
	private GpioPinPwmOutput pwm;
	final private int pwmValue = 800;
	
	Motor(GpioPinDigitalOutput motorMoveRight, GpioPinDigitalOutput motorMoveLeft, GpioPinPwmOutput pwm){
		this.motorMoveRight = motorMoveRight;
		this.motorMoveLeft = motorMoveLeft;
		this.pwm = pwm;
	}
	public Boolean startSearching() throws InterruptedException{
		
		while(true){
	    	if(motorMoveRight.isLow() && motorMoveLeft.isLow()){
	    		motorMoveRight.low();
	    		motorMoveLeft.high();
	    		pwm.setPwm(pwmValue-400);
	    	}
	    	if(motorMoveRight.isHigh() && motorMoveLeft.isHigh()){
	    		motorMoveRight.high();
	    		motorMoveLeft.low();
	        	pwm.setPwm(pwmValue);
	        	Thread.sleep(100);
	        	motorMoveRight.high();
	        	motorMoveLeft.high();
	        	pwm.setPwm(0);
	        	Thread.sleep(2000);
	    	}
		}
	}
	public void stopSearching(){
		
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
	public Boolean isLow(){
		if(motorMoveRight.isLow() && motorMoveLeft.isLow()) return true;
		else return false;
	}
	public Boolean isHigh(){
		if(motorMoveRight.isHigh() && motorMoveLeft.isHigh()) return true;
		else return false;
	}
}
