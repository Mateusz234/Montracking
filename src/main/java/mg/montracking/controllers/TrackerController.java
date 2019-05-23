package mg.montracking.controllers;

import mg.montracking.service.TrackerService;

public class TrackerController {

	private TrackerController() {
	}

	public static TrackerController getInstance() {
		return TrackerControllerHolder.INSTANCE;
	}

	private static class TrackerControllerHolder {
		private static final TrackerController INSTANCE = new TrackerController();
	}

	private TrackerService trackerService = TrackerService.getInstance();

	public void init() {
		trackerService.init();
	}
	
	public void startTracker() {
		trackerService.startTracker();
	}

	public void stopTracker() {
		trackerService.stopTracker();
	}
	
	public void setBottomMotorPWM(int pwm) {
		trackerService.setBottomMotorPWM(pwm);
//		System.out.println("Setting bottom motor PWM: " + pwm);
	}
	
	public void setUpperMotorPWM(int pwm) {
		trackerService.setUpperMotorPWM(pwm);
//		System.out.println("Setting upper motor PWM: " + pwm);
	}

	

}
