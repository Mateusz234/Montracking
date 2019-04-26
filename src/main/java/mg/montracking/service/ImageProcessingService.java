package mg.montracking.service;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
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

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mg.montracking.core.utils.FaceExtractor;
import mg.montracking.entity.Overseer;
import mg.montracking.entity.Person;

/**
 * Service for image processing that handles controller methods.
 */
public final class ImageProcessingService {

	private ScheduledExecutorService frameGrabberTimer;
	private VideoCapture capture;
	private CascadeClassifier faceCascade;
	private int absoluteFaceSize;
	private Runnable frameGrabber;
	private boolean isRunning = false;

	private Person person = new Person();
	private Overseer overseer = Overseer.getInstance();

	private ImageProcessingService() {
	}

	public static ImageProcessingService getInstance() {
		return ImageProcessingServiceHolder.INSTANCE;
	}

	private static class ImageProcessingServiceHolder {
		private static final ImageProcessingService INSTANCE = new ImageProcessingService();
	}

	public void init() {
		System.out.println("initing image");
		String path = "/home/pi/opencv-3.3.0/data/lbpcascades/lbpcascade_frontalface.xml";
		this.faceCascade = new CascadeClassifier(path);
		absoluteFaceSize = 0;
	}

	/**
	 * The action triggered by pushing the button on the GUI
	 *
	 * @param event the push button event
	 */
	public void startImageProcessing(VideoCapture capture, ImageView currentFrame) {
		if (!this.isRunning) {
			System.out.println("Starting image processing");
			this.isRunning = true;
			this.capture = capture;
			frameGrabber = () -> updateImageView(currentFrame, ImageProcessingService.mat2Image(grabFrame()));
			this.frameGrabberTimer = Executors.newSingleThreadScheduledExecutor();
			this.frameGrabberTimer.scheduleAtFixedRate(frameGrabber, 0, 40, TimeUnit.MILLISECONDS);
		}
	}

	public void stopImageProcessing() {
		if (this.frameGrabberTimer != null && !this.frameGrabberTimer.isShutdown()) {
			try {
				this.frameGrabberTimer.shutdown();
				this.frameGrabberTimer.awaitTermination(40, TimeUnit.MILLISECONDS);
				this.isRunning = false;
				System.out.println("Stopping image processing");
			} catch (InterruptedException e) {
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		if (this.capture.isOpened())
			this.capture.release();
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
		// check if array is not empty, stop searcher if is started and start tracker
		// call tracker controller method start with coordinates as parameters.
		if (facesArray.length != 0) {
			overseer.setPersonFound(true);
			person.setFaceCoordinates(facesArray[0]);
			for (int i = 0; i < facesArray.length; i++)
				Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 2);
		} else
			overseer.setPersonFound(false);
	}

	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view  the {@link ImageView} to update
	 * @param image the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image) {
		onFXThread(view.imageProperty(), image);
	}

	/**
	 * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
	 *
	 * @param frame the {@link Mat} representing the current frame
	 * @return the {@link Image} to show
	 */
	public static Image mat2Image(Mat frame) {
		try {
			return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
		} catch (Exception e) {
			System.err.println("Cannot convert the Mat obejct: " + e);
			return null;
		}
	}

	/**
	 * Generic method for putting element running on a non-JavaFX thread on the
	 * JavaFX thread, to properly update the UI
	 * 
	 * @param property a {@link ObjectProperty}
	 * @param value    the value to set for the given {@link ObjectProperty}
	 */
	public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
		Platform.runLater(() -> {
			property.set(value);
		});
	}

	/**
	 * Support for the {@link mat2image()} method
	 * 
	 * @param original the {@link Mat} object in BGR or grayscale
	 * @return the corresponding {@link BufferedImage}
	 */
	private static BufferedImage matToBufferedImage(Mat original) {
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);

		if (original.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		return image;
	}

	public boolean extractAndSaveFace(String personName, Label infoLabel) {
		person.setName(personName);
		if (overseer.isPersonFound()) {
			FaceExtractor.extractFaceFromImage(person, grabFrame(), infoLabel);
		} else
			infoLabel.setText("No face found!");
		return false;
	}
}
