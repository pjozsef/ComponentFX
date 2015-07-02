package com.github.pjozsef.componentfx.floatingstage;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * 
 */
public class DraggableFloatingStage<T extends Region> extends Stage implements FloatingStage {

    private final double radius, diameter, width, height;
    private final StackPane root;
    private final T content;
    private final Region background;
    private final Scene scene;
    private final DoubleProperty centerX, centerY;
    private final Rectangle clip;

    private final DragManager dragManager;

    /**
     * Creates a DraggableFloatinsStage instance with the given parameters.
     *
     * @param width the width of the expanded stage
     * @param height the height of the expanded stage
     * @param diameter the diameter of the minimized circle
     * @param content the content to display when the stage expands
     * @param background the background to be displayed when the stage is
     * minimized
     */
    public DraggableFloatingStage(double width, double height, double diameter, T content, T background) {
        this.diameter = diameter;
        this.radius = diameter / 2;
        this.width = width;
        this.height = height;
        this.content = content;
        this.content.setPrefHeight(this.height);
        this.content.setPrefWidth(this.width);
        this.background = background;
        this.background.setPrefHeight(this.height);
        this.background.setPrefWidth(this.width);
        this.dragManager = new DragManager(this);
        this.centerX = new SimpleDoubleProperty();
        this.centerY = new SimpleDoubleProperty();

        this.root = new StackPane(this.background);
        this.scene = new Scene(this.root, this.width, this.height, Color.TRANSPARENT);
        this.clip = new Rectangle(0, 0, this.diameter, this.diameter);

        this.setScene(scene);
        this.initStyle(StageStyle.TRANSPARENT);

        this.clip.xProperty().bind(scene.xProperty().add(scene.widthProperty().divide(2)).subtract(this.radius));
        this.clip.yProperty().bind(scene.yProperty().add(scene.heightProperty().divide(2).subtract(this.radius)));
        this.clip.setArcHeight(diameter);
        this.clip.setArcWidth(diameter);
        this.root.setClip(clip);

        this.centerX.bind(scene.xProperty().add(scene.widthProperty().divide(2)));
        this.centerY.bind(scene.yProperty().add(scene.heightProperty().divide(2)));

        this.setAlwaysOnTop(true);
        this.setResizable(false);

        this.background.setOnMouseClicked((event) -> {
            if (!dragManager.wasMoved()) {
                grow();
            }
        });
        this.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean focused) -> {
            if (!focused) {
                shrink();
            }
        });

        this.background.setOnMousePressed(dragManager::onPress);
        this.background.setOnMouseDragged(dragManager::onDrag);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void grow() {
        KeyValue kv11 = new KeyValue(this.clip.arcHeightProperty(), 0);
        KeyValue kv12 = new KeyValue(this.clip.arcWidthProperty(), 0);
        KeyFrame kf11 = new KeyFrame(Duration.millis(250), kv11, kv12);
        Timeline rectangular = new Timeline(kf11);

        content.setOpacity(0);
        root.getChildren().add(content);

        KeyValue kv21 = new KeyValue(this.clip.widthProperty(), width);
        KeyValue kv22 = new KeyValue(this.clip.heightProperty(), height);
        KeyValue kv23 = new KeyValue(clip.translateXProperty(), -1 * (width - diameter) / 2);
        KeyValue kv24 = new KeyValue(clip.translateYProperty(), -1 * (height - diameter) / 2);
        KeyFrame kf21 = new KeyFrame(Duration.millis(500), kv21, kv22, kv23, kv24);
        Timeline grow = new Timeline(kf21);

        KeyValue kv31 = new KeyValue(content.opacityProperty(), 1);
        KeyFrame kf31 = new KeyFrame(Duration.millis(250), kv31);
        Timeline visible = new Timeline(kf31);
        visible.setDelay(Duration.millis(250));

        visible.play();
        rectangular.play();
        grow.play();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void shrink() {
        KeyValue kv11 = new KeyValue(clip.arcHeightProperty(), diameter);
        KeyValue kv12 = new KeyValue(clip.arcWidthProperty(), diameter);
        KeyFrame kf11 = new KeyFrame(Duration.millis(250), kv11, kv12);
        Timeline rounded = new Timeline(kf11);

        KeyValue kv21 = new KeyValue(this.clip.widthProperty(), diameter);
        KeyValue kv22 = new KeyValue(this.clip.heightProperty(), diameter);
        KeyValue kv23 = new KeyValue(clip.translateXProperty(), 0);
        KeyValue kv24 = new KeyValue(clip.translateYProperty(), 0);
        KeyFrame kf21 = new KeyFrame(Duration.millis(500), kv21, kv22, kv23, kv24);
        Timeline shrink = new Timeline(kf21);

        KeyValue kv31 = new KeyValue(content.opacityProperty(), 0);
        KeyFrame kf31 = new KeyFrame(Duration.millis(250), kv31);
        Timeline invisible = new Timeline(kf31);
        invisible.setOnFinished((event) -> {
            this.root.getChildren().remove(this.content);
        });

        invisible.play();
        rounded.play();
        shrink.play();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReadOnlyDoubleProperty centerX() {
        return centerX;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReadOnlyDoubleProperty centerY() {
        return centerY;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Stage getStage() {
        return this;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Region getContent() {
        return content;
    }

}
