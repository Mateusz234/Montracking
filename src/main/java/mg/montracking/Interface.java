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
        final int pwmValue = 600;
        // create GPIO controller instance
        GpioController gpio = GpioFactory.getInstance();
        // Digital pins definitions
        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput limitSwitchFrontDown = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02,
                                                  PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput limitSwitchBackDown = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03,
                PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput limitSwitchFrontUp = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04,
                PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput limitSwitchBackUp = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05,
                PinPullResistance.PULL_DOWN);
        final GpioPinDigitalOutput motor1Left = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "MyLED", PinState.LOW);
        final GpioPinDigitalOutput motor1Right = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13, "MyLED", PinState.LOW);
        // PWM pins deifnitions
        Pin pin = CommandArgumentParser.getPin(
                RaspiPin.class,    // pin provider class to obtain pin instance from
                RaspiPin.GPIO_24,  // default pin if no pin argument found
                args);             // argument array to search in

        final GpioPinPwmOutput pwm = gpio.provisionPwmOutputPin(pin);

        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
        com.pi4j.wiringpi.Gpio.pwmSetClock(500);
        
     // Program
        pwm.setPwm(pwmValue);
        System.out.println("PWM = " + pwm.getPwm());
        limitSwitchFrontDown.addListener(new GpioPinListenerDigital() {
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            	pwm.setPwm(0);
            	try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	motor1Left.low();
            	motor1Right.high();
            	pwm.setPwm(pwmValue);
            	System.out.println("PWM = " + pwm.getPwm());
            }
        });
        limitSwitchBackDown.addListener(new GpioPinListenerDigital() {
        	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
        		pwm.setPwm(0);
            	try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	motor1Left.high();
            	motor1Right.low();
            	pwm.setPwm(pwmValue);
            	System.out.println("PWM = " + pwm.getPwm());
            }
        });
        System.out.println("Program starts. Type anything in console to stop");
        while(!scanner.hasNext()){
        	Thread.sleep(500);
        }
        pwm.setPwm(0);
        gpio.shutdown();
        System.out.print("Shutting down.");
        /*
        pwm.setPwm(100);
        System.out.println("PWM = " + pwm.getPwm() + " hit ENTER to stop.");
        scanner.nextLine();
        
        // set the PWM rate to 0
        pwm.setPwm(0);
        System.out.println("Program has stopped");*/
        
    }
	
}
