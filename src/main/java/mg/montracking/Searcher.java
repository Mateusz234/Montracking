package mg.montracking;

public class Searcher {
	
	private final Motor motorDown, motorUp;
	private final int motorDownPwmValue, motorUpPwmValue;
	private Boolean isMotorUpMoved;
	private int motorUpMovesCount = 0; 			// we can move motor only 3 times before we reset its position
	private static final int MOTOR_UP_MOVES_LIMIT = 3;
	
	Searcher (Motor motorDown, Motor motorUp, int motorDownPwmValue, int motorUpPwmValue){
		this.motorDown = motorDown;
		this.motorUp  = motorUp;
		this.motorDownPwmValue = motorDownPwmValue;
		this.motorUpPwmValue = motorUpPwmValue;
	}
	
	public Boolean startSearching() throws InterruptedException{
		Boolean isMotorUpMoved = false;
		while(true){
	    	if(motorDown.isStopLow()){
	    		motorDown.moveRight(motorDownPwmValue/2+motorDownPwmValue/4);
	    		if(!isMotorUpMoved ){ // check whether motor has been moved to move only once per scan
	    			if(motorUpMovesCount >= MOTOR_UP_MOVES_LIMIT){
		    			motorUp.moveLeft(motorUpPwmValue);
		    			motorUpMovesCount = 0;
		    		}
	    			else{
	    				motorUp.moveRight(motorUpPwmValue);
		    			Thread.sleep(100);
			    		motorUp.stopPwm();
			    		motorUpMovesCount++;
	    			}
	    			isMotorUpMoved = true;
		    		
		    		
	    		}
	    	}
	    	if(motorDown.isStopHigh()){
	    		motorDown.moveLeft(motorDownPwmValue);
	        	Thread.sleep(100);
	        	motorDown.stopHigh();
	        	Thread.sleep(550);
	        	if(isMotorUpMoved) isMotorUpMoved = false;
	    	}
		}
	}
	public void stopSearching(){
		
	}
}
