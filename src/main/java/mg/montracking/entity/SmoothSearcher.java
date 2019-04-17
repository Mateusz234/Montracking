package mg.montracking.entity;

/**
 * Provides method to move motor continuously.
 * One searcher object is made of one {@link Motor} which is bottom one. Upper motor is managed by listeners in 
 * {@link SearcherController} and {@link Tracker}.
 * 
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 * 
 */

public class SmoothSearcher extends BaseSearcher{
		
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
