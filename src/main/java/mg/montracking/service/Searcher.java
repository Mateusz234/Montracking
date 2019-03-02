package mg.montracking.service;

import mg.montracking.Utils.ISearcher;

/**
 * Provide methods to move motors in different manners, continuous, pulse etc to look choosen object
 * One searcher object is made of two {@link Motor}s
 * 
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 * 
 */

public class Searcher implements ISearcher {
	
	private final Motor bottomMotor, upperMotor;
	
	
	/**
	 * Constructor
	 */
	public Searcher (Motor bottomMotor, Motor upperMotor){
		this.bottomMotor = bottomMotor;
		this.upperMotor  = upperMotor;
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
	public void stop() {
		bottomMotor.stopPwm();
    	upperMotor.stopPwm();
	}

}
