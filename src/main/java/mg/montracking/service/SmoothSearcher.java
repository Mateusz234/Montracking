package mg.montracking.service;

/**
 * Provide methods to move motors in different manners, continuous, pulse etc to look choosen object
 * One searcher object is made of two {@link Motor}s. When object is found, calls Tracker and gives him control.
 * 
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 * 
 */

public class SmoothSearcher extends Searcher{
		
	private final Motor bottomMotor, upperMotor;
	
	/**
	 * Constructor
	 */
	public SmoothSearcher (Motor bottomMotor, Motor upperMotor){
		super(bottomMotor,upperMotor);
		this.bottomMotor = bottomMotor;
		this.upperMotor  = upperMotor;
	}
	
	@Override
	public void start(int pwm) {
		bottomMotor.move(pwm);
	}
	
}
