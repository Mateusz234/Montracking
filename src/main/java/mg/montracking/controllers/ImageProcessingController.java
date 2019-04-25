package mg.montracking.controllers;

import org.opencv.videoio.VideoCapture;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import mg.montracking.service.ImageProcessingService;

/**
 * Handles the button for starting/stopping the camera and the acquired video
 * stream.
 *
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 *
 */
public class ImageProcessingController {

	private ImageProcessingController() {
	}

	public static ImageProcessingController getInstance() {
		return ImageProcessingControllerHolder.INSTANCE;
	}

	private static class ImageProcessingControllerHolder {
		private static final ImageProcessingController INSTANCE = new ImageProcessingController();
	}

	@FXML
	private Button startCameraButton, imageProcessingViewButton, searcherTrackerViewButton;
	@FXML
	private ImageView currentFrame;

	private ScreenController screenController = ScreenController.getInstance();
	private ImageProcessingService imageProcessingService = ImageProcessingService.getInstance();

	private VideoCapture capture = new VideoCapture();
	private boolean cameraActive = false;
	private static int cameraId = 0;

	/**
	 * The action triggered by pushing the button on the GUI
	 *
	 * @param event the push button event
	 */
	@FXML
	public void toggleImageProcessing() {

		if (!this.cameraActive) {
			this.capture.open(cameraId);
			this.capture.set(3, 640); // width
			this.capture.set(4, 480); // height
			if (this.capture.isOpened()) {
				this.cameraActive = true;
				this.startCameraButton.setText("Stop Camera");
				imageProcessingService.startImageProcessing(this.capture, this.currentFrame);
			} else {
				System.err.println("Impossible to open the camera connection...");
			}
		} else {
			this.cameraActive = false;
			this.startCameraButton.setText("Start Camera");
			imageProcessingService.stopImageProcessing();
		}
	}

	public void startImageProcessing() {
		if (!this.capture.isOpened())
			this.capture.open(cameraId);
		this.capture.set(3, 640); // width
		this.capture.set(4, 480); // height
		if (this.capture.isOpened()) {
			this.cameraActive = true;
			this.startCameraButton.setText("Stop Camera");
			imageProcessingService.startImageProcessing(this.capture, this.currentFrame);
		} else {
			System.err.println("Impossible to open the camera connection...");
		}
	}

	public void stopImageProcessing() {
		if (this.cameraActive) {
			this.cameraActive = false;
			this.startCameraButton.setText("Start Camera");
			imageProcessingService.stopImageProcessing();
		}
	}

	/**
	 * Switch view to ImageProcessing
	 */
	@FXML
	public void showViewImageProcessing() {
		screenController.activate("ImageProcessing");
	}

	/**
	 * Switch view to SearcherTracker
	 */
	@FXML
	public void showViewSearcherTracker() {
		screenController.activate("SearcherTracker");
	}

	/**
	 * Switch view to Main
	 */
	@FXML
	public void showViewOverseer() {
		screenController.activate("Overseer");
	}

	public void init() {
		imageProcessingService.init();
	}

}
