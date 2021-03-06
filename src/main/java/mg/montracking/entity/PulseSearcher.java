package mg.montracking.entity;

import javax.swing.Timer;

/**
 * Provides method to move motor with short pulses on the contrary to continuous movement.
 * One searcher object is made of one {@link Motor} which is bottom one. Upper motor is managed by listeners in 
 * {@link mg.montracking.controllers.SearcherController} and {@link Tracker}.
 * 
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 * 
 */

public class PulseSearcher extends BaseSearcher{
	
	private final Motor bottomMotor;
	static Timer timer;
	private boolean toggleMotorMove = false;
	
		
	/**
	 * Pulse searcher implements timer that toggles flag to stop and start motor in fixed time intervals
	 */
	public PulseSearcher (Motor bottomMotor, Motor upperMotor){
		super(bottomMotor,upperMotor);
		this.bottomMotor = bottomMotor;
		timer = new Timer(500, (event) -> {
			if(!toggleMotorMove) toggleMotorMove = true;
			else toggleMotorMove = false;
		});
	}

	@Override
	public void start(int pwm) {
		super.start(pwm);
		if(!timer.isRunning()) timer.start();
		if(toggleMotorMove) bottomMotor.move(pwm);
		else bottomMotor.stop();
		
	}
}
