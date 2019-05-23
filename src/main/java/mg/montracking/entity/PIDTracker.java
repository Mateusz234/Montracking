package mg.montracking.entity;

import mg.montracking.core.utils.PIDController;

public class PIDTracker extends BaseTracker {

	private PIDController bottomMotorPidController, upperMotorPidController;

	public PIDTracker(Motor bottomMotor, Motor upperMotor) {
		super(bottomMotor, upperMotor);
		bottomMotorPidController = new PIDController(1, 0, 0);
		bottomMotorPidController.setOutputLimits(500);
		upperMotorPidController = new PIDController(1, 0, 0);
		upperMotorPidController.setOutputLimits(500);
	}

	@Override
	public void start(int xError, int yError) {
		super.start(bottomMotorPWM, upperMotorPWM);
		if (xError >= 0)
			setBottomMotorDirectionToRight();
		else
			setBottomMotorDirectionToLeft();
		if (yError >= 0)
			setUpperMotorDirectionToLeft();
		else
			setUpperMotorDirectionToRight();

		regulation.setBottomMotorPwm(bottomMotorPWM);
		regulation.setUpperMotorPwm(upperMotorPWM);
		regulation.setxError(xError);
		regulation.setyError(yError);
		regulationRepository.saveToFile(regulation);
		
		bottomMotorPWM =  Math.abs(calculatePwm(bottomMotorPidController, Math.abs(xError)));
		upperMotorPWM =  Math.abs(calculatePwm(upperMotorPidController, Math.abs(yError)));
		
		if(bottomMotorPWM<180) bottomMotorPWM += 120;
		if(upperMotorPWM<180) upperMotorPWM += 120;
		
		bottomMotor.move(bottomMotorPWM);
		upperMotor.move(upperMotorPWM);
	}

	private int calculatePwm(PIDController pidController, int error) {
		return (int) pidController.getOutput(error, 0);
	}

}
