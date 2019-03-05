package mg.montracking.controllers;

import java.util.HashMap;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

/**
 * Stores views that can be switched to anywhere in the program. Implements singleton pattern.
 *
 * @author Mateusz Goluchowski
 * @version 1.0 (2019-02-25)
 *
 */

public class ScreenController {
	private HashMap<String, Pane> screenMap = new HashMap<>();
    private Scene main;
    private ScreenController() {}
    
    /**
     * Takes reference to root object on the stage
     * 
     * @param main - root scene on the stage
     */
    public void init(Scene main) {
    	this.main = main;
    }
    
    public static ScreenController getInstance() {
    	return ScreenControllerHolder.INSTANCE;
    }

    /**
     * Adds views to the map.
     * @param name - used to switch views in {@link #activate} method
     * @param pane - reference to pane object
     */
    public void addScreen(String name, Pane pane){
         screenMap.put(name, pane);
    }

    /**
     * removes unused view from map
     * @param name - name of pane to be deleted
     */
    public void removeScreen(String name){
        screenMap.remove(name);
    }

    /**
     * Switches to choosen screen
     * @param name - name of screen to be activated
     */
    public void activate(String name){
        main.setRoot( screenMap.get(name) );
    }
    
    private static class ScreenControllerHolder {
    	private static final ScreenController INSTANCE = new ScreenController();
    }
    
}
