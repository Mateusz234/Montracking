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
	double[] faceFeatures;

	private Person() {
	}

	public static Person getInstance() {
		return PersonHolder.INSTANCE;
	}

	private static class PersonHolder {
		private static final Person INSTANCE = new Person();
	}
		
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

	public double[] getFaceFeatures() {
		return faceFeatures;
	}

	public void setFaceFeatures(double[] faceFeatures) {
		this.faceFeatures = faceFeatures;
	}

	public int getXFaceCoordinates() {
		return (this.faceCoordinates.x + this.faceCoordinates.width)/2;
	}
	
	public int getYFaceCoordinates() {
		return (this.faceCoordinates.y + this.faceCoordinates.height)/2;
	}
}
