package mg.montracking.core.utils;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Used to calculate euclidean distance to determine if face that was found
 * is similar to one of the faces defined in database
 *
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-04-03)
 *
 */
public class EuclideanDistance {
	
	/**
	 * Calculates euclidean distance
	 *
	 * @param array1 contains {@link INDArray} with face features of the person found by the camera
	 * @param array2 contains {@link INDArray} with face from database
	 * @return returns euclidean distance which can be used to determine if faces on the input belongs to the same person
	 */
	public double calculateEuclideanDistance(INDArray array1, INDArray array2) {
		return array1.distance2(array2);
	}
}
