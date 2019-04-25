package mg.montracking.entity;

import org.opencv.core.Rect;

/**
 * Person holds basic info about people defined in program.
 * 
 * @author Mateusz Goluchowski
 *
 */
public class Person {

	Rect faceCoordinates;
	
	private Person() {
	}

	public static Person getInstance() {
		return PersonHolder.INSTANCE;
	}

	private static class PersonHolder {
		private static final Person INSTANCE = new Person();
	}

	public Rect getFaceCoordinates() {
		return faceCoordinates;
	}

	public void setFaceCoordinates(Rect faceCoordinates) {
		this.faceCoordinates = faceCoordinates;
	}
	
}
