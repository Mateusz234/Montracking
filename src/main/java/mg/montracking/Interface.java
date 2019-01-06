package mg.montracking;

import java.util.Scanner;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.CommandArgumentParser;

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
        final GpioPinDigitalOutput motor1Right = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "MyLED", PinState.LOW);
        final GpioPinDigitalOutput motor1Left = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13, "MyLED", PinState.LOW);
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
        
        // Motors definition
        final Motor motorDown = new Motor(motor1Right, motor1Left, pwm0);
        final Motor motorUp = new Motor(motor2Right, motor2Left, pwm1);
     // Program
        limitSwitchFrontDown.addListener(new GpioPinListenerDigital() {
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            	motorDown.stopHigh();
            	System.out.println("switch nr 1 PWM0 = " + pwm0.getPwm());
            }
        });
        limitSwitchBackDown.addListener(new GpioPinListenerDigital() {
        	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        		motorDown.stopLow();
            	System.out.println("switch nr 2 PWM0 = " + pwm0.getPwm());
            }
        });
        
        motorDown.startSearching();
       // TODO in other thread, if object found, call method motorDown.stopSearching() and start Tracker.track();
    }
	
}
