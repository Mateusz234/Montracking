package mg.montracking.controllers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
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
	@FXML
	private Button startCameraButton, imageProcessingViewButton, searcherTrackerViewButton;
	@FXML
	private ImageView currentFrame;

	// Singleton object to manipulate views
	ScreenController sController = ScreenController.getInstance();
	// a timer for acquiring the video stream
	private ScheduledExecutorService frameGrabberTimer;
	// the OpenCV object that implements the video capture
	private VideoCapture capture = new VideoCapture();
	// a flag to change the button behavior
	private boolean cameraActive = false;
	// the id of the camera to be used
	private static int cameraId = 0;

	// face cascade classifier
	private CascadeClassifier faceCascade;
	private int absoluteFaceSize;

	public void init() {
		String path = "/home/pi/opencv-3.3.0/data/lbpcascades/lbpcascade_frontalface.xml";
		this.faceCascade = new CascadeClassifier(path);
		absoluteFaceSize = 0;
	}

	/**
	 * The action triggered by pushing the button on the GUI
	 *
	 * @param event the push button event
	 */
	@FXML
	protected void startCamera(ActionEvent event) {

		if (!this.cameraActive) {
			this.capture.open(cameraId);
			this.capture.set(3, 640); // width
			this.capture.set(4, 480); // height
			if (this.capture.isOpened()) {
				this.cameraActive = true;
				Runnable frameGrabber = () -> updateImageView(currentFrame, ImageProcessingService.mat2Image(grabFrame()));
				this.frameGrabberTimer = Executors.newSingleThreadScheduledExecutor();
				this.frameGrabberTimer.scheduleAtFixedRate(frameGrabber, 0, 40, TimeUnit.MILLISECONDS);
				this.startCameraButton.setText("Stop Camera");
			} else {
				System.err.println("Impossible to open the camera connection...");
			}
		} else {
			this.cameraActive = false;
			this.startCameraButton.setText("Start Camera");
			this.stopAcquisition();
		}
	}

	/**
	 * Get a frame from the opened video stream (if any)
	 *
	 * @return the {@link Mat} to show
	 */
	private Mat grabFrame() {
		Mat frame = new Mat();
		if (this.capture.isOpened()) {
			try {
				this.capture.read(frame);
				if (!frame.empty()) {
					detectAndDisplay(frame);
				}
			} catch (Exception e) {
				System.err.println("Exception during the image elaboration: " + e);
			}
		}

		return frame;
	}

	/**
	 * Method for face detection and tracking
	 * 
	 * @param frame it looks for faces in this frame
	 */
	private void detectAndDisplay(Mat frame) {
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(grayFrame, grayFrame);
		if (this.absoluteFaceSize == 0) {
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0) {
				this.absoluteFaceSize = Math.round(height * 0.2f);
			}
		}
		// detect faces
		this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
		Rect[] facesArray = faces.toArray();
		if (facesArray.length != 0) {
			for (int i = 0; i < facesArray.length; i++)
				Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 2);
		}
	}

	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	private void stopAcquisition() {
		if (this.frameGrabberTimer != null && !this.frameGrabberTimer.isShutdown()) {
			try {
				this.frameGrabberTimer.shutdown();
				this.frameGrabberTimer.awaitTermination(10, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		if (this.capture.isOpened())
			this.capture.release();
	}

	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view  the {@link ImageView} to update
	 * @param image the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image) {
		ImageProcessingService.onFXThread(view.imageProperty(), image);
	}

	/**
	 * On application close, stop the acquisition from the camera
	 */
	public void setClosed() {
		this.stopAcquisition();
	}

	/**
	 * Switch view to ImageProcessing
	 */
	@FXML
	public void showViewImageProcessing() {
		sController.activate("ImageProcessing");
	}

	/**
	 * Switch view to SearcherTracker
	 */
	@FXML
	public void showViewSearcherTracker() {
		sController.activate("SearcherTracker");
	}

}
