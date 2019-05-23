package mg.montracking.service;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mg.montracking.core.utils.FaceExtractor;
import mg.montracking.core.utils.FeaturesExtractor;
import mg.montracking.entity.Overseer;
import mg.montracking.entity.Person;

/**
 * Service for image processing that handles controller methods.
 */
public final class ImageProcessingService {

	private ScheduledExecutorService frameGrabberTimer;
	private VideoCapture capture;
	private CascadeClassifier lbpFaceCascade;
	private int absoluteFaceSize;
	private Runnable frameGrabber;
	private boolean isRunning = false;

	private Person person = Person.getInstance();
	private Overseer overseer = Overseer.getInstance();
	private FeaturesExtractor featuresExtractor = new FeaturesExtractor();

	private ImageProcessingService() {
	}

	public static ImageProcessingService getInstance() {
		return ImageProcessingServiceHolder.INSTANCE;
	}

	private static class ImageProcessingServiceHolder {
		private static final ImageProcessingService INSTANCE = new ImageProcessingService();
	}

	public void init() {
		System.out.println("Initializing image processing");
		String lbpCascadeProfilePath = "/home/pi/opencv-3.3.0/data/lbpcascades/lbpcascade_profileface.xml";
		String lbpCascadeFrontalPath = "/home/pi/opencv-3.3.0/data/lbpcascades/lbpcascade_frontalface.xml";
		String haarCascadeProfilePath = "/home/pi/opencv-3.3.0/data/haarcascades/haarcascade_profileface.xml";
		String haarCascadeFrontalPath = "/home/pi/opencv-3.3.0/data/haarcascades/haarcascade_frontalface_alt.xml";

		this.lbpFaceCascade = new CascadeClassifier(lbpCascadeFrontalPath);
		absoluteFaceSize = 0;

		System.out.println("Image processing initialization done.");
	}

	/**
	 * The action triggered by pushing the button on the GUI
	 *
	 * @param event the push button event
	 */
	public void startImageProcessing(VideoCapture capture, ImageView currentFrame) {
		if (!this.isRunning) {
			System.out.println("Starting image processing");
			// Testing
			if (person.getName() == "Mateusz")
				person.setName("Unknown");
			else
				person.setName("Mateusz");
			// end testing. delet after testing
			this.isRunning = true;
			this.capture = capture;
			frameGrabber = () -> updateImageView(currentFrame, ImageProcessingService.mat2Image(grabFrame()));
			this.frameGrabberTimer = Executors.newSingleThreadScheduledExecutor();
			this.frameGrabberTimer.scheduleAtFixedRate(frameGrabber, 0, 30, TimeUnit.MILLISECONDS);
		}
	}

	public void stopImageProcessing() {
		if (this.frameGrabberTimer != null && !this.frameGrabberTimer.isShutdown()) {
			try {
				this.frameGrabberTimer.shutdown();
				this.frameGrabberTimer.awaitTermination(30, TimeUnit.MILLISECONDS);
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
//		double execTime = Core.getTickCount();
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
//		execTime = Core.getTickCount() - execTime;
//		execTime /= Core.getTickFrequency();
//		System.out.println("Current exec time: " + execTime + " person is found?" + overseer.isPersonFound());
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
				this.absoluteFaceSize = Math.round(height * 0.06f);
			}
			System.out.println(this.absoluteFaceSize);
		}
		this.lbpFaceCascade.detectMultiScale(grayFrame, faces, 1.3, 3, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
		Rect[] facesArray = faces.toArray();
		if (facesArray.length != 0) {
			overseer.setPersonFound(true);
			person.setFaceCoordinates(facesArray[0]);
			Imgproc.rectangle(frame, facesArray[0].tl(), facesArray[0].br(), new Scalar(0, 255, 0), 2);
//			Imgproc.putText(frame, 
//					person.getName(), 
//					new Point(facesArray[0].tl().x, facesArray[0].tl().y - 15), 
//					2, 
//					1, 
//					new Scalar(0, 255, 0), 
//					2
//			);
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
		double[] faceFeatures = null;
		Mat frame = grabFrame();
//		person.setName(personName);
		if (overseer.isPersonFound()) {
			// FaceExtractor.extractFaceFromImage(person, frame, infoLabel);
			FaceExtractor.screenShot(personName, frame, infoLabel);
//			faceFeatures = featuresExtractor.extractFeaturesFromFace(frame);
//			System.out.println("extracted face features: " + faceFeatures);
		} else
			infoLabel.setText("No face found! Made screenshot.");
		FaceExtractor.screenShot(personName, frame, infoLabel);
		return false;
	}
}
