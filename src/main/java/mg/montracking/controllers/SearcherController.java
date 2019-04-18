package mg.montracking.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import mg.montracking.entity.BaseSearcher;
import mg.montracking.service.SearcherService;

import java.lang.Math;
import javafx.scene.control.Label;

/**
 * Handles the button for starting/stopping searcher algorithm. Initializes GPIO
 * and manages {@link BaseSearcher} algorithm
 *
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 *
 */
public class SearcherController {

	@FXML
	private Button startSearcherButton, imageProcessingViewButton, searcherTrackerViewButton;
	@FXML
	private Slider sliderBottomMotorPWM, sliderUpperMotorPWM;
	@FXML
	private Label labelBottomMotorPwm, labelUpperMotorPwm;

	SearcherService searcherService = SearcherService.getInstance();
	ScreenController screenController = ScreenController.getInstance();

	private boolean searcherActive = false;
	private int bottomMotorPWM, upperMotorPWM;

	@FXML
	protected void startSearcher(ActionEvent event) {
		if (!searcherActive) {
			searcherActive = true;
			startSearcherButton.setText("Stop searcher");
			searcherService.startSearcher();
		} else {
			searcherActive = false;
			startSearcherButton.setText("Start searcher");
			searcherService.stopSearcher();
		}
	}

	public void stopSearcher() {
		searcherService.stopSearcher();
	}

	@FXML
	public void setBottomMotorPWM() {
		bottomMotorPWM = (int) sliderBottomMotorPWM.getValue();
		labelBottomMotorPwm.setText("" + bottomMotorPWM);
		bottomMotorPWM = (int) Math.sqrt(Math.pow(bottomMotorPWM, 2));
		searcherService.setBottomMotorPWM(bottomMotorPWM);
	}

	@FXML
	public void setUpperMotorPWM() {
		upperMotorPWM = (int) sliderUpperMotorPWM.getValue();
		labelUpperMotorPwm.setText("" + upperMotorPWM);
		upperMotorPWM = (int) Math.sqrt(Math.pow(upperMotorPWM, 2));
		searcherService.setUpperMotorPWM(upperMotorPWM);
	}

	@FXML
	public void showViewImageProcessing() {
		System.out.println("show view image processing");
		screenController.activate("ImageProcessing");
	}

	@FXML
	public void showViewSearcherTracker() {
		System.out.println("show view  searcher tracker");
		screenController.activate("SearcherTracker");
	}
}
