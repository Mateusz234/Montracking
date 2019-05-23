package mg.montracking.core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mg.montracking.controllers.ImageProcessingController;
import mg.montracking.controllers.OverseerController;
import mg.montracking.controllers.ScreenController;
import mg.montracking.controllers.SearcherController;
import mg.montracking.controllers.TrackerController;

import org.opencv.core.Core;

/**
 * The main class of Montracking. Starts threads to controll motors and process captured video
 * 
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 * 
 */

public class MainInterface extends Application {
	
	OverseerController overseerController = OverseerController.getInstance();
	ImageProcessingController imageProcessingController = ImageProcessingController.getInstance();
	SearcherController searcherController = SearcherController.getInstance();
	TrackerController trackerController = TrackerController.getInstance();
	
	FXMLLoader loader = null;
	
	@Override
	public void start(Stage primaryStage)
	{
		try
		{	// Loading components and controllers from fxml files
			loader = new FXMLLoader(getClass().getResource("/resources/view/Overseer.fxml"));
			loader.setController(overseerController);
			BorderPane overseerPane = (BorderPane) loader.load();
			loader = new FXMLLoader(getClass().getResource("/resources/view/ImageProcessing.fxml"));
			loader.setController(imageProcessingController);
			BorderPane imageProcessingPane = (BorderPane) loader.load();
			loader = new FXMLLoader(getClass().getResource("/resources/view/Searcher.fxml"));
			loader.setController(searcherController);
			BorderPane searcherTrackergPane = (BorderPane) loader.load();
			
			Scene scene = new Scene(overseerPane, 800, 600);
			primaryStage.setTitle("Montracking - Main menu");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			ScreenController screenController = ScreenController.getInstance();
			screenController.init(scene);
			screenController.addScreen("Overseer", overseerPane);
			screenController.addScreen("ImageProcessing", imageProcessingPane);
			screenController.addScreen("SearcherTracker", searcherTrackergPane);
			
			searcherController.initGpio();
			imageProcessingController.init();
			trackerController.init();
			
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we)
				{
					imageProcessingController.stopImageProcessing();
					searcherController.stopSearcher();
					trackerController.stopTracker();
				}
			}));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
        
       
        
    }
	
}
