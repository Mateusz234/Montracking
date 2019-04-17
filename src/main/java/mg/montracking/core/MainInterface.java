package mg.montracking.core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mg.montracking.controllers.ImageProcessingController;
import mg.montracking.controllers.ScreenController;
import mg.montracking.controllers.SearcherController;

import org.opencv.core.Core;

/**
 * The main class of Montracking. Starts threads to controll motors and process captured video
 * 
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 * 
 */

public class MainInterface extends Application {
	@Override
	public void start(Stage primaryStage)
	{
		try
		{	// Loading components and controllers from fxml files
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/view/ImageProcessing.fxml"));
			BorderPane imageProcessingPane = (BorderPane) loader.load();
			ImageProcessingController ipController = loader.getController();
			loader = new FXMLLoader(getClass().getResource("/resources/view/SearcherTracker.fxml"));
			BorderPane searcherTrackergPane = (BorderPane) loader.load();
			SearcherController stController = loader.getController();
			
			Scene scene = new Scene(imageProcessingPane, 800, 600);
			primaryStage.setTitle("Montracking - Main menu");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			ScreenController sController = ScreenController.getInstance();
			sController.init(scene);
			sController.addScreen("SearcherTracker", searcherTrackergPane);
			sController.addScreen("ImageProcessing", imageProcessingPane);
			
			
			stController.initGpio();
			ipController.init();
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we)
				{
					ipController.setClosed();
					stController.stopSearcher();
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
