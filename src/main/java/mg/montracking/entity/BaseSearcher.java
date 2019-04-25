package mg.montracking.entity;

import mg.montracking.core.interfaces.Searcher;

/**
 * Provide methods to move motors in different manners, continuous, pulse etc to look choosen object
 * One searcher object is made of one {@link Motor} which is bottom one. Upper motor is managed by listeners in 
 * {@link mg.montracking.controllers.SearcherController} and {@link Tracker}.
 * 
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 * 
 */

public class BaseSearcher implements Searcher {
	
	private final Motor bottomMotor, upperMotor;
	public boolean isRunning;

	public BaseSearcher (Motor bottomMotor, Motor upperMotor){
		this.bottomMotor = bottomMotor;
		this.upperMotor  = upperMotor;
	}
	
	public Motor getBottomMotor() {
		return bottomMotor;
	}

	public Motor getUpperMotor() {
		return upperMotor;
	}
	
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	/**
	 * Toggles direction of motor when it gets signal from reed switch
	 */
	public void toggleSearchingDirectionBottomMotor(){
		if (bottomMotor.isRight()) bottomMotor.setMotorConfigToTurnLeft();
		else bottomMotor.setMotorConfigToTurnRight();
	}
	
	/**
	 * Toggles direction of motor when it gets signal from reed switch
	 */
	public void toggleSearchingDirectionUpperMotor(){
		System.out.println("Toggling direction of motor");
		if (upperMotor.isRight()) upperMotor.setMotorConfigToTurnLeft();
		else upperMotor.setMotorConfigToTurnRight();
	}
	
	@Override
	public void start(int pwm) {
		setRunning(true);
	}
	
	@Override
	public void stop() {
		setRunning(false);
		bottomMotor.stop();
    	upperMotor.stop();
	}

}
