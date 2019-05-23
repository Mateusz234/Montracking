package mg.montracking.core.interfaces;

public interface Tracker {

	/**
	 * Starts implemented tracking algorithm
	 * 
	 * @param pwm - motor speed
	 */
	default void start(int bottomMotorPWM, int upperMotorPWM) {

	}

	/**
	 * Stops motors
	 */
	default void stop() {

	}

}
