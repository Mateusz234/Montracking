package mg.montracking.controllers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import mg.montracking.Utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * Handles the button for starting/stopping the camera and the
 * acquired video stream.
 *
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 *
 */
public class ImageProcessingController {
	@FXML
	private Button startCameraButton,
				   imageProcessingViewButton,
				   searcherTrackerViewButton;
	@FXML
	private ImageView currentFrame;

	
	//Singleton object to manipulate views
	ScreenController sController = ScreenController.getInstance();
	// a timer for acquiring the video stream
	private ScheduledExecutorService frameGrabberTimer;
	// the OpenCV object that realizes the video capture
	private VideoCapture capture = new VideoCapture();
	// a flag to change the button behavior
	private boolean cameraActive = false;
	// the id of the camera to be used
	private static int cameraId = 0;
	
	/**
	 * The action triggered by pushing the button on the GUI
	 *
	 * @param event
	 *            the push button event
	 */
	@FXML
	protected void startCamera(ActionEvent event) {
		if (!this.cameraActive)
		{
			this.capture.open(cameraId);
			this.capture.set(3, 640);	// width
			this.capture.set(4, 480);	// height
			if (this.capture.isOpened())
			{
				this.cameraActive = true;
				Runnable frameGrabber = () -> updateImageView(currentFrame, Utils.mat2Image(grabFrame()));
				this.frameGrabberTimer = Executors.newSingleThreadScheduledExecutor();
				this.frameGrabberTimer.scheduleAtFixedRate(frameGrabber, 0, 40, TimeUnit.MILLISECONDS);
				this.startCameraButton.setText("Stop Camera");
			}
			else
			{
				System.err.println("Impossible to open the camera connection...");
			}
		}
		else
		{
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
		if (this.capture.isOpened())
		{
			try
			{
				this.capture.read(frame);
				// if the frame is not empty, process it
				if (!frame.empty())
				{
					//Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
				}
				
			}
			catch (Exception e)
			{
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		
		return frame;
	}
	
	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	private void stopAcquisition() {
		if (this.frameGrabberTimer!=null && !this.frameGrabberTimer.isShutdown())
		{
			try
			{
				// stop the timer
				this.frameGrabberTimer.shutdown();
				this.frameGrabberTimer.awaitTermination(10, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.capture.isOpened())
		{
			// release the camera
			this.capture.release();
		}
	}
	
	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view
	 *            the {@link ImageView} to update
	 * @param image
	 *            the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}
	
	/**
	 * On application close, stop the acquisition from the camera
	 */
	public void setClosed()	{
		this.stopAcquisition();
	}
	
	/**
	 * Switch view to ImageProcessing
	 */
	@FXML public void showViewImageProcessing() {
		sController.activate("ImageProcessing");
	}
	
	/**
	 * Switch view to SearcherTracker
	 */
	@FXML public void showViewSearcherTracker() {
		sController.activate("SearcherTracker");
	}
	
}
