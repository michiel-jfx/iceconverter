package nl.dotjava.javafx.support;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * Support class for refreshing issues for Gluon on mobile phones.
 */
public class SceneSupport {

    private final Stage stage;
    private final HashMap<String, Scene> sceneMap = new HashMap<>();
    private double initialPortraitWidth = 432.0;
    private double initialPortraitHeight = 855.0;
    private final SimpleBooleanProperty isPortrait = new SimpleBooleanProperty(true);

    public SceneSupport(Stage stage) {
        this.stage = stage;
        System.out.println("***** Constructor SceneSupport, stage size: " + this.stage.getWidth() + " x " + this.stage.getHeight());
    }

    public void addScene(String name, Scene scene) {
        this.sceneMap.put(name, scene);
        setOrientationListeners(name);
    }

    /** Return width based on scene on stage. */
    public double getWidth() {
        return this.stage.getScene().getWidth();
    }

    /** Return height based on scene on stage. */
    public double getHeight() {
        return this.stage.getScene().getHeight();
    }

    /** Return width based on stage. */
    public double getStageWidth() {
        return this.stage.getWidth();
    }

    /** Return height based on stage. */
    public double getStageHeight() {
        return this.stage.getHeight();
    }

    public Scene getScene(String name) {
        return (this.sceneMap.containsKey(name))
                ? this.sceneMap.get(name)
                : null;
    }

    public void setPortrait(String who, boolean portrait) {
        System.out.println("***** " + who + " sets portrait mode to: " + portrait);
        this.isPortrait.set(portrait);
    }

    public SimpleBooleanProperty isPortrait() {
        return this.isPortrait;
    }

    /** Add orientation listener to the scene */
    public void setOrientationListeners(String name) {
        if (this.sceneMap.containsKey(name)) {
            Scene scene = this.sceneMap.get(name);
            this.sceneMap.get(name).widthProperty().addListener((obs, oldVal, newVal)  -> setPortrait(name, scene.getHeight() > newVal.doubleValue()));
            this.sceneMap.get(name).heightProperty().addListener((obs, oldVal, newVal) -> setPortrait(name, newVal.doubleValue() > scene.getWidth()));
        }
    }

    /** Switch to a scene in the HashMap and put it on stage. */
    public void switchToScene(String name) {
        if (this.sceneMap.containsKey(name)) {
            switchToScene(this.sceneMap.get(name));
        } else {
            System.err.println("***** ERROR: scene with name " + name + " not found!");
        }
    }

    /** Switch to a scene and put it on stage. */
    private void switchToScene(Scene scene) {
        if (this.stage != null && scene != null) {
            this.stage.setScene(scene);
            refresh(scene);
        } else {
            System.err.println("***** ERROR: stage or scene is null!");
        }
    }

    /**
     * Refresh a scene. Due to Gluon differences between desktop and mobile implementation, this custom method is used
     * to refresh the scene and (re)position the scene and or stage.
     */
    private void refresh(Scene scene) {
        System.out.println("***** Before refreshing");
        printSizes(scene);

        // just before the refresh
        this.stage.setX(0);
        this.stage.setY(0);
        this.stage.setScene(scene);

        // refresh
        Platform.runLater(() -> {
            scene.getRoot().requestLayout();
            scene.getRoot().applyCss();
        });

        printSizes(scene);
        System.out.println("***** Scene refreshed");
    }

    private void printSizes(Scene scene) {
        System.out.println("***** Scene size (according to this scene): "  + scene.getWidth() + " x " + scene.getHeight());
        System.out.println("***** Scene size (according to this stage): "  + getStageWidth()  + " x " + getStageHeight());
        System.out.println("***** Scene size (according to stage scene): " + getWidth()       + " x " + getHeight());
    }
}
