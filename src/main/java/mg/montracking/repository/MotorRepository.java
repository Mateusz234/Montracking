package mg.montracking.repository;

public class MotorRepository {

	private MotorRepository() {
	}

	public static MotorRepository getInstance() {
		return MotorRepositoryHolder.INSTANCE;
	}

	private static class MotorRepositoryHolder {
		private static final MotorRepository INSTANCE = new MotorRepository();
	}
	
	public void saveToRepoMotorPwm() {
		
	}

}
