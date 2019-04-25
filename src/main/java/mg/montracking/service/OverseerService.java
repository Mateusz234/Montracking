package mg.montracking.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import mg.montracking.controllers.ImageProcessingController;
import mg.montracking.controllers.SearcherController;
import mg.montracking.entity.BaseSearcher;
import mg.montracking.entity.Overseer;

public class OverseerService {

	private SearcherController searcherController = SearcherController.getInstance();
	private ImageProcessingController imageProcessingController = ImageProcessingController.getInstance();
	private Overseer overseer = Overseer.getInstance();

	private boolean isTimerStoppedAndRestarted = false;
	private ScheduledExecutorService overseerScheduler;
	private int delayTimeForSearcherToLaunchAgain = 1500; // in [ms]

	Timer timer = new Timer(delayTimeForSearcherToLaunchAgain, null);

	private Runnable montrackingAlgorithm = () -> {
		if (overseer.isPersonFound()) {
			if (!isTimerStoppedAndRestarted) {
				timer.stop();
				timer.restart();
				isTimerStoppedAndRestarted = true;
			}
			searcherController.stopSearcher();
//			trackerController.startTracker();
		} else {
			if (isTimerStoppedAndRestarted) {
				timer.start();
				isTimerStoppedAndRestarted = false;
			}
			if (!timer.isRunning()) {
				searcherController.startSearcher();
			}
		}
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
		}
	}

	public void stopOverseer() {
		if (this.overseerScheduler != null && !this.overseerScheduler.isShutdown()) {
			try {
				this.overseerScheduler.shutdown();
				this.overseerScheduler.awaitTermination(500, TimeUnit.MILLISECONDS);
				overseer.setRunning(false);
			} catch (InterruptedException e) {
				System.err.println("Exception in stopping the overseer... " + e);
			}
		}
		searcherController.stopSearcher();
		imageProcessingController.stopImageProcessing();
	}

}
