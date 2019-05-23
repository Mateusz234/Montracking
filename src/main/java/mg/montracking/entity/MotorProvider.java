package mg.montracking.entity;

public class MotorProvider {
	
	private Motor upperMotor, bottomMotor;
		
    public Motor getUpperMotor() {
		return upperMotor;
	}

	public void setUpperMotor(Motor upperMotor) {
		this.upperMotor = upperMotor;
	}

	public Motor getBottomMotor() {
		return bottomMotor;
	}

	public void setBottomMotor(Motor bottomMotor) {
		this.bottomMotor = bottomMotor;
	}

	private MotorProvider() {}

    public static MotorProvider getInstance() {
    	return MotorProviderHolder.INSTANCE;
    }
    
    private static class MotorProviderHolder {
    	private static final MotorProvider INSTANCE = new MotorProvider();
    }
	
}
