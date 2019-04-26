package mg.montracking.entity;

import org.opencv.core.Rect;

/**
 * Person holds basic info about people defined in program.
 * 
 * @author Mateusz Goluchowski
 *
 */
public class Person {

	String name;
	Rect faceCoordinates;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Rect getFaceCoordinates() {
		return faceCoordinates;
	}

	public void setFaceCoordinates(Rect faceCoordinates) {
		this.faceCoordinates = faceCoordinates;
	}

}
