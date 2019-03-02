package mg.montracking.service;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mg.montracking.controllers.VideoCaptureController;

import org.opencv.core.Core;

/**
 * The main class of Montracking. Starts threads to controll motors and process captured video
 * 
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 * 
 */

public class Interface extends Application{

	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/view/MainMenu.fxml"));
			BorderPane rootElement = (BorderPane) loader.load();
			Scene scene = new Scene(rootElement, 800, 600);
			primaryStage.setTitle("Montracking - Main menu");
			primaryStage.setScene(scene);
			primaryStage.show();
			VideoCaptureController vcController = loader.getController();
			vcController.initGpio();
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we)
				{
					vcController.setClosed();
					vcController.stopSearcher();
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
