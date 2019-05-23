package mg.montracking.entity;

import mg.montracking.core.interfaces.Tracker;
import mg.montracking.repository.RegulationRepository;

/**
 * Provide methods to move motors in different manners, continuous, pulse etc to look choosen object
 * One searcher object is made of one {@link Motor} which is bottom one. Upper motor is managed by listeners in 
 * {@link mg.montracking.controllers.SearcherController} and {@link Tracker}.
 * 
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 * 
 */

public class BaseTracker implements Tracker {
	
	protected final Motor bottomMotor;
	protected final Motor upperMotor;
	public boolean isRunning;
	int bottomMotorPWM, upperMotorPWM;
	protected RegulationRepository regulationRepository = new RegulationRepository();
	protected Regulation regulation = new Regulation();
	
	public BaseTracker (Motor bottomMotor, Motor upperMotor){
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
	
	public void setBottomMotorDirectionToRight(){
		bottomMotor.setMotorConfigToTurnRight();
	}
	
	public void setBottomMotorDirectionToLeft(){
		bottomMotor.setMotorConfigToTurnLeft();
	}
	
	public void setUpperMotorDirectionToRight(){
		upperMotor.setMotorConfigToTurnRight();
	}
	
	public void setUpperMotorDirectionToLeft(){
		upperMotor.setMotorConfigToTurnLeft();
	}

	@Override
	public void start(int bottomMotorPWM, int upperMotorPWM) {
		setRunning(true);
	}
	
	@Override
	public void stop() {
		setRunning(false);
		bottomMotor.stop();
    	upperMotor.stop();
	}

}
