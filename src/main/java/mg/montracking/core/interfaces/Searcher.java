package mg.montracking.core.interfaces;

public interface Searcher {

	/**
	 * Starts implemented searching algorithm
	 * 
	 * @param pwm - motor speed
	 */
	default void start(int pwm) {

	}

	/**
	 * Stops motors
	 */
	default void stop() {

	}

}
