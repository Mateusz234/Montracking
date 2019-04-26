package mg.montracking.core.utils;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javafx.scene.control.Label;
import mg.montracking.entity.Person;

/**
 * Handles extracting faces from given images in given coordinates
 *
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 *
 */
public class FaceExtractor {

	/**
	 * Extracts face from image and saves it.
	 *
	 * @param person - person to be extracted
	 * @param frame  - contains face to be extracted
	 * @param infoLabel - info to be displayed in image processing view
	 */
	public static void extractFaceFromImage(Person person, Mat frame, Label infoLabel) {
		Mat faceImage = frame.submat(person.getFaceCoordinates());
		String fileName = person.getName() + "Face.jpg";
		boolean isFileSavedProperly = Imgcodecs.imwrite(fileName, faceImage);
		if (isFileSavedProperly)
			infoLabel.setText("Face saved to fifle succesfully!");
		else
			infoLabel.setText("Cannot save face to a file.");
	}
}
