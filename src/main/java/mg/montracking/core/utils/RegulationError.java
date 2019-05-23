package mg.montracking.core.utils;

public class RegulationError {

	public static int calculateError(int centerOfScreenPosition, int facePosition) {
		return centerOfScreenPosition-facePosition;
	}
	
}
