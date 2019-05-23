package mg.montracking.entity;

public class NoPIDTracker extends BaseTracker {

	public boolean isRunning;
	private int veryLowBottomPwm = 30, veryLowUpperPwm = 15, lowPwm = 180;
	private int k = 1, xSign, ySign; // regulation gain
	int bottomMotorPWM, upperMotorPWM;

	public NoPIDTracker(Motor bottomMotor, Motor upperMotor) {
		super(bottomMotor, upperMotor);
	}

	@Override
	public void start(int xError, int yError) {
		super.start(xError, yError);
		if (xError >= 0) {
			setBottomMotorDirectionToRight();
			xSign = -1;

		} else {
			setBottomMotorDirectionToLeft();
			xSign = 1;
		}
		if (yError >= 0) {
			setUpperMotorDirectionToLeft();
			ySign = -1;
		} else {
			setUpperMotorDirectionToRight();
			ySign = 1;
		}

		regulation.setBottomMotorPwm(bottomMotorPWM*xSign);
		regulation.setUpperMotorPwm(upperMotorPWM*ySign);
		regulation.setxError(xError);
		regulation.setyError(yError);
		regulationRepository.saveToFile(regulation);

		bottomMotorPWM = Math.abs(xError);
		upperMotorPWM = Math.abs(yError);

		if (bottomMotorPWM <= veryLowBottomPwm)
			bottomMotorPWM = 0;
		else if (bottomMotorPWM > veryLowBottomPwm && bottomMotorPWM < lowPwm)
			bottomMotorPWM = 180 + bottomMotorPWM * k;
		if (upperMotorPWM <= veryLowUpperPwm)
			upperMotorPWM = 0;
		else if (upperMotorPWM > veryLowUpperPwm && upperMotorPWM < lowPwm)
			upperMotorPWM = 180 + upperMotorPWM * k;

		bottomMotor.move(bottomMotorPWM);
		upperMotor.move(upperMotorPWM);
	}

}
