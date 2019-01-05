package mg.montracking;

import java.util.Scanner;
import java.util.concurrent.Callable;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

public class Interface {

	public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);
        final int pwmValue = 800;
        Boolean searchForObj = false;
        // create GPIO controller instance
        GpioController gpio = GpioFactory.getInstance();
        // Digital pins definitions
        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput limitSwitchFrontDown = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04,
                PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput limitSwitchBackDown = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05,
                PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput limitSwitchFrontUp = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06,
                PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput limitSwitchBackUp = gpio.provisionDigitalInputPin(RaspiPin.GPIO_10,
                PinPullResistance.PULL_DOWN);
        final GpioPinDigitalOutput motor1Left = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "MyLED", PinState.LOW);
        final GpioPinDigitalOutput motor1Right = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13, "MyLED", PinState.LOW);
        final GpioPinDigitalOutput motor2Left = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_15, "MyLED", PinState.LOW);
        final GpioPinDigitalOutput motor2Right = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16, "MyLED", PinState.LOW);
        
        // PWM pins deifnitions
        Pin pinPwm0 = CommandArgumentParser.getPin(
                RaspiPin.class,    // pin provider class to obtain pin instance from
                RaspiPin.GPIO_01,  // default pin if no pin argument found
                args);             // argument array to search in
      
        Pin pinPwm1 = CommandArgumentParser.getPin(
                RaspiPin.class,    // pin provider class to obtain pin instance from
                RaspiPin.GPIO_24,  // default pin if no pin argument found
                args);             // argument array to search in
        final GpioPinPwmOutput pwm0 = gpio.provisionPwmOutputPin(pinPwm0);
        final GpioPinPwmOutput pwm1 = gpio.provisionPwmOutputPin(pinPwm1);

        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
        com.pi4j.wiringpi.Gpio.pwmSetClock(500);
        
     // Program
        pwm1.setPwm(pwmValue);
        System.out.println("PWM = " + pwm1.getPwm());
        limitSwitchFrontDown.addListener(new GpioPinListenerDigital() {
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            	pwm1.setPwm(0);
            	try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	motor1Left.high();
            	motor1Right.high();
            	System.out.println("switch nr 1 PWM = " + pwm1.getPwm());
            }
        });
        limitSwitchBackDown.addListener(new GpioPinListenerDigital() {
        	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
        		pwm1.setPwm(0);
            	try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	motor1Left.low();
            	motor1Right.low();
            	System.out.println("switch nr 2 PWM = " + pwm1.getPwm());
            }
        });
        limitSwitchBackUp.addListener(new GpioPinListenerDigital() {
        	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
        		pwm0.setPwm(0);
            	try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	motor2Left.high();
            	motor2Right.low();
            	pwm0.setPwm(pwmValue);
            	System.out.println("switch nr 3 PWM = " + pwm0.getPwm());
            }
        });
        limitSwitchFrontUp.addListener(new GpioPinListenerDigital() {
        	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
        		pwm0.setPwm(0);
            	try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	motor2Left.low();
            	motor2Right.high();
            	pwm0.setPwm(pwmValue);
            	System.out.println("switch nr 4 PWM = " + pwm0.getPwm());
            }
        });
        System.out.println("Program starts. Type anything in console to stop");
        //while(!scanner.hasNext()){
        while(true){
        	if(motor1Left.isLow() && motor1Right.isLow()){
        		searchForObj = false;
        		motor1Left.low();
        		motor1Right.high();
        		pwm0.setPwm(pwmValue-400);
        		Thread.sleep(3000);
        		motor2Left.low();
        		motor2Right.high();
        		pwm1.setPwm(pwmValue-450);
        		Thread.sleep(100);
        		motor2Left.high();
        		motor2Right.high();
        		pwm1.setPwm(0);
        	}
        	if((motor1Left.isHigh() && motor1Right.isHigh()) || searchForObj){
        		searchForObj = true;
	        		motor1Left.high();
	        		motor1Right.low();
	        		pwm0.setPwm(pwmValue);
	        		Thread.sleep(100);
	        		motor1Left.high();
	        		motor1Right.high();
	        		pwm0.setPwm(0);
	        		Thread.sleep(2000);
        		
        	}
        	//Thread.sleep(200);
        }
      /*motor1Left.high();  // turn right
		motor1Right.low();
		motor1Left.low();  // turn left
		motor1Right.high();
		pwm0.setPwm(pwmValue);*/
        /*pwm1.setPwm(0);
        pwm0.setPwm(0);
        gpio.shutdown();
        System.out.print("Shutting down.");*/
        
    }
	
}
