package nl.dotjava.javafx.support;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * Support class for refreshing issues for Gluon on mobile phones.
 */
public class SceneSupport {

    private final Stage stage;
    private final HashMap<String, Scene> sceneMap = new HashMap<>();
    // screen resolution properties defaulted to portrait
    private double actualScreenWidth;
    private double actualScreenHeight;

    public SceneSupport(Stage stage) {
        this.stage = stage;
        initializeScreenDimensions();
    }

    /** Initialize actual screen dimensions. */
    private void initializeScreenDimensions() {
        try {
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            this.actualScreenWidth = Math.min(screenBounds.getWidth(), screenBounds.getHeight());
            this.actualScreenHeight = Math.max(screenBounds.getWidth(), screenBounds.getHeight());
            setSceneAndSize(null);
        } catch (Exception e) {
            System.err.println("***** ERROR: Could not determine screen dimensions: " + e.getMessage());
        }
    }

    public void addScene(String name, Scene scene) {
        this.sceneMap.put(name, scene);
    }

    public Scene getScene(String name) {
        return this.sceneMap.getOrDefault(name, null);
    }

    /** Switch to a scene in the HashMap and put it on stage. */
    public void switchToScene(String name) {
        if (this.sceneMap.containsKey(name)) {
            refresh(this.sceneMap.get(name));
            System.out.println("***** Scene switched to " + name);
        } else {
            System.err.println("***** ERROR: scene with name " + name + " not found!");
        }
    }

    /**
     * Refresh a scene. Due to Gluon differences between desktop and mobile implementation, this custom method is used
     * to refresh the scene and (re)position the scene and or stage.
     */
    private void refresh(Scene scene) {
        setSceneAndSize(scene);
        scene.getRoot().requestLayout();
        scene.getRoot().applyCss();
    }

    /** actualWidth and actualHeight are kept in portrait mode, so when setting them check the mode first. */
    private void setSceneAndSize(Scene scene) {
        this.stage.setX(0);
        this.stage.setY(0);
        if (scene != null) {
            this.stage.setScene(scene);
        } else {
            System.out.println("***** Stage set and sized to " + actualScreenWidth + " x " + actualScreenHeight);
        }
        this.stage.setWidth(actualScreenWidth);
        this.stage.setHeight(actualScreenHeight);
    }
}
