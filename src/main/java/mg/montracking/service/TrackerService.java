package mg.montracking.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mg.montracking.entity.BaseTracker;
import mg.montracking.entity.MotorProvider;
import mg.montracking.entity.NoPIDTracker;
import mg.montracking.entity.PIDTracker;

public class TrackerService {

	private ScheduledExecutorService motorMovementScheduler;
	private int bottomMotorPWM = 500, upperMotorPWM = 400;

	MotorProvider motorProvider = MotorProvider.getInstance();

	BaseTracker tracker;
	Runnable rTracker;

	private TrackerService() {
	}

	public static TrackerService getInstance() {
		return TrackerServiceHolder.INSTANCE;
	}

	private static class TrackerServiceHolder {
		private static final TrackerService INSTANCE = new TrackerService();
	}

	public void init() {
		System.out.println("Initializing tracker...");
		tracker = new NoPIDTracker(motorProvider.getBottomMotor(), motorProvider.getUpperMotor());
//		tracker = new PIDTracker(motorProvider.getBottomMotor(), motorProvider.getUpperMotor());
		rTracker = () -> tracker.start(bottomMotorPWM, upperMotorPWM);
		System.out.println("Tracker initialization done.");
	}
	
	public void startTracker() {
		if (!tracker.isRunning()) {
			System.out.println("Starting tracker task");
			this.motorMovementScheduler = Executors.newSingleThreadScheduledExecutor();
			this.motorMovementScheduler.scheduleAtFixedRate(rTracker, 0, 20, TimeUnit.MILLISECONDS);
		}
	}

	public void stopTracker() {
		if (tracker.isRunning()) {
			System.out.println("Stoping tracker task");
			tracker.stop();
			try {
				this.motorMovementScheduler.shutdown();
				this.motorMovementScheduler.awaitTermination(20, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				System.err.println("Exception in stopping the tracker... " + e);
			}
		}
	}

	public void setBottomMotorPWM(int pwm) {
		this.bottomMotorPWM = pwm;
	}
	
	public void setUpperMotorPWM(int pwm) {
		this.upperMotorPWM = pwm;
	}

}
