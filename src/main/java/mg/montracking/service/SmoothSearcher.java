package mg.montracking.service;

/**
 * Provides method to move motor continuously.
 * One searcher object is made of one {@link Motor} which is bottom one. Upper motor is managed by listeners in 
 * {@link SearcherTrackerController} and {@link Tracker}.
 * 
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 * 
 */

public class SmoothSearcher extends Searcher{
		
	private final Motor bottomMotor;
	
	public SmoothSearcher (Motor bottomMotor, Motor upperMotor){
		super(bottomMotor,upperMotor);
		this.bottomMotor = bottomMotor;
	}
	
	@Override
	public void start(int pwm) {
		bottomMotor.move(pwm);
	}
	
}
