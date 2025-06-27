package nl.dotjava.javafx.support;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
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
    private final SimpleBooleanProperty isPortrait = new SimpleBooleanProperty(true);
    // screen resolution properties defaulted to portrait
    private double actualScreenWidth;
    private double actualScreenHeight;
    private String currentSceneName;

    public SceneSupport(Stage stage) {
        this.stage = stage;
        initializeScreenDimensions();
    }

    /** Initialize actual screen dimensions */
    private void initializeScreenDimensions() {
        try {
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            this.actualScreenWidth = Math.min(screenBounds.getWidth(), screenBounds.getHeight());
            this.actualScreenHeight = Math.max(screenBounds.getWidth(), screenBounds.getHeight());
            setPortrait("Initialization", (screenBounds.getHeight() > screenBounds.getWidth()));
            setStageSize();
        } catch (Exception e) {
            System.err.println("***** ERROR: Could not determine screen dimensions: " + e.getMessage());
        }
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

    public double getActualScreenWidth() {
        return actualScreenWidth;
    }

    public double getActualScreenHeight() {
        return actualScreenHeight;
    }

    public void addScene(String name, Scene scene) {
        this.sceneMap.put(name, scene);
        setOrientationListeners(name);
    }

    public Scene getScene(String name) {
        return this.sceneMap.getOrDefault(name, null);
    }

    public void setPortrait(String who, boolean portrait) {
        if (this.currentSceneName == null || this.currentSceneName.equals(who)) {
            System.out.println("***** " + who + " sets portrait mode " + portrait);
            this.isPortrait.set(portrait);
        }
    }

    public SimpleBooleanProperty isPortrait() {
        return this.isPortrait;
    }

    /** Add orientation listener to the scene */
    public void setOrientationListeners(String name) {
        if (this.sceneMap.containsKey(name)) {
            Scene scene = this.sceneMap.get(name);
            //this.sceneMap.get(name).widthProperty().addListener((obs, oldVal, newVal)  -> setPortrait(name+"1", scene.getHeight() > newVal.doubleValue()));
            this.sceneMap.get(name).heightProperty().addListener((obs, oldVal, newVal) -> setPortrait(name+"2", newVal.doubleValue() > scene.getWidth()));
        }
    }

    /** Switch to a scene in the HashMap and put it on stage. */
    public void switchToScene(String name) {
        if (this.sceneMap.containsKey(name)) {
            refresh(this.sceneMap.get(name));
            currentSceneName = name;
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
        System.out.println("***** Before refreshing, mode is currently " + isPortrait.get());
        // just before the refresh
        this.stage.setX(0);
        this.stage.setY(0);
        this.stage.setScene(scene);
        setStageSize();
        printSizes(scene);

        // refresh
        Platform.runLater(() -> {
            //scene.getRoot().autosize();
            scene.getRoot().requestLayout();
            scene.getRoot().applyCss();
            System.out.println("***** After layout refresh:");
            printSizes(scene);
        });
        System.out.println("***** Scene refreshed");
    }

    /** actualWidth and actualHeight are kept in portrait mode, so when setting them check the mode first. */
    private void setStageSize() {
        if (this.isPortrait.get()) {
            System.out.println("***** Setting stage width x height to " + actualScreenWidth + " x " + actualScreenHeight + " (portrait is true)");
            this.stage.setWidth(actualScreenWidth);
            this.stage.setHeight(actualScreenHeight);
        } else {
            System.out.println("***** Setting stage width x height to " + actualScreenHeight + " x " + actualScreenWidth + " (portrait is false)");
            this.stage.setWidth(actualScreenHeight);
            this.stage.setHeight(actualScreenWidth);
        }
    }

    private void printSizes(Scene scene) {
        System.out.println("***** Scene size (according to this scene): "  + scene.getWidth() + " x " + scene.getHeight());
        System.out.println("***** Scene size (according to this stage): "  + getStageWidth()  + " x " + getStageHeight());
        System.out.println("***** Scene size (according to stage scene): " + getWidth()       + " x " + getHeight());
        System.out.println("***** Actual screen resolution: " + actualScreenWidth + " x " + actualScreenHeight);
    }
}
