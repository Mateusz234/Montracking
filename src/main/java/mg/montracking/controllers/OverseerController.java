package mg.montracking.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import mg.montracking.entity.Person;
import mg.montracking.service.OverseerService;

public class OverseerController {

	@FXML
	private Button toggleOverseerButton;
	@FXML
	private static ImageView imageView;

	private ScreenController screenController = ScreenController.getInstance();
	private OverseerService overseerService = OverseerService.getInstance();

	private boolean overseerActive = false;

	private OverseerController() {
	}

	public static OverseerController getInstance() {
		return MainControllerHolder.INSTANCE;
	}

	private static class MainControllerHolder {
		private static final OverseerController INSTANCE = new OverseerController();
	}

	@FXML
	public void toggleOverseer() {
		if (!overseerActive) {
			overseerActive = true;
			toggleOverseerButton.setText("Stop overseer");
			showViewImageProcessing();
			overseerService.startOverseer();
		} else {
			overseerActive = false;
			toggleOverseerButton.setText("Start overseer");
			overseerService.stopOverseer();
		}
	}

	@FXML
	public void showViewImageProcessing() {
		screenController.activate("ImageProcessing");
	}

	@FXML
	public void showViewSearcherTracker() {
		screenController.activate("SearcherTracker");
	}

	@FXML
	public void showViewOverseer() {
		screenController.activate("Overseer");
	}

}
