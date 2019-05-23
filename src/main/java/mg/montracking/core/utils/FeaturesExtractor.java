package mg.montracking.core.utils;

import java.io.IOException;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.transferlearning.TransferLearningHelper;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.Mat;

public class FeaturesExtractor {

	private TransferLearningHelper transferLearningHelper;
	private NativeImageLoader nativeImageLoader;
	private DataNormalization scaler;
	private boolean isInitialized = false;

	public FeaturesExtractor() {
//		try {
//			System.out.println("Loading DL4J");
//			ZooModel zooModel = new VGG16();
//			ZooModel zooModel = new VGG16(10, null, 0, null, null, null, null);
//			ComputationGraph computationGraph = null;
//			computationGraph = (ComputationGraph) zooModel.initPretrained(PretrainedType.VGGFACE);
//			System.out.println("Loaded DL4J");
//			transferLearningHelper = new TransferLearningHelper(computationGraph, "pool4");
//			nativeImageLoader = new NativeImageLoader(224, 224, 3);
//			scaler = new VGG16ImagePreProcessor();
//			isInitialized = true;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	public double[] extractFeaturesFromFace(Mat frame) {
		if (!isInitialized)
			return null;
		try {
			INDArray imageMatrix = nativeImageLoader.asMatrix(frame);
			scaler.transform(imageMatrix);

			DataSet objDataSet = new DataSet(imageMatrix, Nd4j.create(new float[] { 0, 0 }));

			DataSet objFeaturized = transferLearningHelper.featurize(objDataSet);
			INDArray featuresArray = objFeaturized.getFeatures();

			int reshapeDimension = 1;
			for (long dimension : featuresArray.shape()) {
				reshapeDimension *= dimension;
			}

			featuresArray = featuresArray.reshape(1, reshapeDimension);

			return featuresArray.data().asDouble();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
