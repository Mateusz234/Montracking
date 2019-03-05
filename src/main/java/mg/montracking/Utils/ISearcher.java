package mg.montracking.Utils;


public interface ISearcher {
	
	/**
	 * Starts implemented searching algorithm
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

