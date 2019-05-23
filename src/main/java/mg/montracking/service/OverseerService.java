package mg.montracking.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanSortedMaps.SynchronizedSortedMap;
import mg.montracking.controllers.ImageProcessingController;
import mg.montracking.controllers.SearcherController;
import mg.montracking.controllers.TrackerController;
import mg.montracking.entity.Overseer;
import mg.montracking.entity.Person;
import mg.montracking.core.utils.RegulationError;

public class OverseerService {

	private SearcherController searcherController = SearcherController.getInstance();
	private ImageProcessingController imageProcessingController = ImageProcessingController.getInstance();
	private TrackerController trackerController = TrackerController.getInstance();
	private Person person = Person.getInstance();
	private Overseer overseer = Overseer.getInstance();

	private boolean isTimerStoppedAndRestarted = false;
	private ScheduledExecutorService overseerScheduler;
	private ScheduledExecutorService calculationScheduler;
	private int delayTimeForSearcherToLaunchAgain = 1500; // in [ms]
	private int centerOfScreenPositionX = 190, centerOfScreenPositionY = 150;
	
	Timer timer = new Timer(delayTimeForSearcherToLaunchAgain, null);

	private Runnable montrackingAlgorithm = () -> {
		if (overseer.isPersonFound()) {
			if (!isTimerStoppedAndRestarted) {
				timer.stop();
				timer.restart();
				isTimerStoppedAndRestarted = true;
			}
			searcherController.stopSearcher();
			trackerController.startTracker();
		} else {
			if (isTimerStoppedAndRestarted) {
				timer.start();
				isTimerStoppedAndRestarted = false;
			}
			if (!timer.isRunning()) {
				trackerController.stopTracker();
				searcherController.startSearcherViaOverseer();
			}
		}
	};
	
	private Runnable calculateError = () -> {
		trackerController.setBottomMotorPWM(RegulationError.
				calculateError(centerOfScreenPositionX, person.getXFaceCoordinates()));
		trackerController.setUpperMotorPWM(RegulationError.
				calculateError(centerOfScreenPositionY, person.getYFaceCoordinates()));
		
		
//		System.out.println("srodek dla x: " + centerOfScreenPositionX);
//		System.out.println("srodek dla y: " + centerOfScreenPositionY);
//		System.out.println("wspolrzedne twarzy x: " + person.getXFaceCoordinates());
//		System.out.println("wspolrzedne twarzy y: " + person.getYFaceCoordinates());
//		System.out.println("uchyb dla X: " + RegulationError.calculateError(centerOfScreenPositionX, person.getXFaceCoordinates()));
//		System.out.println("uchyb dla y: " + RegulationError.calculateError(centerOfScreenPositionY, person.getYFaceCoordinates()));
	};

	private OverseerService() {
	}

	public static OverseerService getInstance() {
		return MainControllerHolder.INSTANCE;
	}

	private static class MainControllerHolder {
		private static final OverseerService INSTANCE = new OverseerService();
	}

	public void startOverseer() {
		if (!overseer.isRunning()) {
			overseer.setRunning(true);
			timer.setRepeats(false);
			searcherController.startSearcher();
			imageProcessingController.startImageProcessing();
			this.overseerScheduler = Executors.newSingleThreadScheduledExecutor();
			this.overseerScheduler.scheduleAtFixedRate(montrackingAlgorithm, 0, 500, TimeUnit.MILLISECONDS);
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.calculationScheduler = Executors.newSingleThreadScheduledExecutor();
			this.calculationScheduler.scheduleAtFixedRate(calculateError, 0, 50, TimeUnit.MILLISECONDS);
			
		}
	}

	public void stopOverseer() {
		if (this.overseerScheduler != null && !this.overseerScheduler.isShutdown()) {
			try {
				this.overseerScheduler.shutdown();
				this.overseerScheduler.awaitTermination(500, TimeUnit.MILLISECONDS);
				this.calculationScheduler.shutdown();
				this.calculationScheduler.awaitTermination(500, TimeUnit.MILLISECONDS);
				overseer.setRunning(false);
			} catch (InterruptedException e) {
				System.err.println("Exception in stopping the overseer... " + e);
			}
		}
		searcherController.stopSearcher();
		imageProcessingController.stopImageProcessing();
		trackerController.stopTracker();
	}

}
