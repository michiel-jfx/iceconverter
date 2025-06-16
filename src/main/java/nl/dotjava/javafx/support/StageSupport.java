package nl.dotjava.javafx.support;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Support class for refreshing issues for Gluon on mobile phones.
 */
public class StageSupport {

    private final Stage mainStage;
    private final Scene mainScene;

    public StageSupport(Stage mainStage, Scene mainScene) {
        this.mainStage = mainStage;
        this.mainScene = mainScene;
        System.out.println("***** Stage size: " + this.mainStage.getWidth() + " x " + this.mainStage.getHeight());
        System.out.println("***** Scene size (according to scene): " + this.mainScene.getWidth() + " x " + this.mainScene.getHeight());
        System.out.println("***** Scene size (according to stage): " + this.mainStage.getScene().getWidth() + " x " + this.mainStage.getScene().getHeight());
    }

    /** Return width based on stage. */
    public double getWidth() {
        // return this.mainScene.getWidth();
        return this.mainStage.getWidth();
    }

    /** Return height based on stage. */
    public double getHeight() {
        // return this.mainScene.getHeight();
        return this.mainStage.getHeight();
    }

    /** Switch to a custom scene and put it on the main stage. */
    public void switchToScene(Scene otherScene) {
        if (this.mainStage != null && otherScene != null) {
            this.mainStage.setScene(otherScene);
            refresh(otherScene);
        } else {
            System.err.println("***** ERROR: mainStage or otherScene is null!");
        }
    }

    /** Switch to the main scene on the main stage. */
    public void switchBackToMainScene() {
        if (this.mainStage != null && this.mainScene != null) {
            this.mainStage.setScene(this.mainScene);
            refresh(this.mainScene);
        } else {
            System.err.println("***** ERROR: mainStage or mainScene is null!");
        }
    }

    /**
     * Refresh a scene. Due to Gluon differences between desktop and mobile implementation, this custom method is used
     * to refresh the scene and (re)position the scene and or stage.
     */
    private void refresh(Scene scene) {
        System.out.println("***** Before refresh");
        System.out.println("***** Scene size (according to scene): " + scene.getWidth() + " x " + scene.getHeight());
        System.out.println("***** Scene size (according to stage): " + this.mainStage.getScene().getWidth() + " x " + this.mainStage.getScene().getHeight());

        // just before the refresh
        this.mainStage.setX(0);
        this.mainStage.setY(0);

        // refresh
        Platform.runLater(() -> {
            scene.getRoot().requestLayout();
            scene.getRoot().applyCss();
        });

        System.out.println("***** Scene refreshed successfully");
        System.out.println("***** Scene size (according to scene): " + scene.getWidth() + " x " + scene.getHeight());
        System.out.println("***** Scene size (according to stage): " + this.mainStage.getScene().getWidth() + " x " + this.mainStage.getScene().getHeight());
    }
}
