package com.github.pjozsef.componentfx.floatingbubble;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Builder;

/**
 * The {@code FloatingStage} class is a special circular draggable JavaFX
 * {@code Stage} that expands on focus gain and shrinks on focus lost.
 */
public class FloatingStage extends Stage {
    private boolean small;
    private StackPane root;
    private Rectangle rectangle;
    private Scene scene;
    private Timeline growContent, shrinkContent;
    private double diameter, bigSize, difference;

    /**
     * Creates a FloatingStage with a specific radius and background color.
     *
     * @param diameter the diameter of the floating circle
     * @param fill the fill of the floating circle
     */
    @Builder
    public FloatingStage(final double diameter, Paint fill) {
        this.diameter = diameter;
        this.bigSize = diameter * 5;
        this.difference = bigSize-diameter;

        root = new StackPane();
        root.setBackground(Background.EMPTY);
        
        small=true;

        rectangle = new Rectangle(diameter, diameter, fill);
        rectangle.setArcWidth(diameter);
        rectangle.setArcHeight(diameter);
        root.getChildren().add(rectangle);

        KeyValue v1 = new KeyValue(rectangle.arcHeightProperty(), 0);
        KeyValue v2 = new KeyValue(rectangle.arcWidthProperty(), 0);
        KeyValue v3 = new KeyValue(rectangle.widthProperty(), bigSize);
        KeyValue v4 = new KeyValue(rectangle.heightProperty(), bigSize);
        KeyFrame kf1 = new KeyFrame(Duration.millis(1000), v1, v2, v3, v4);
        growContent = new Timeline(kf1);

        KeyValue v5 = new KeyValue(rectangle.arcHeightProperty(), diameter);
        KeyValue v6 = new KeyValue(rectangle.arcWidthProperty(), diameter);
        KeyValue v7 = new KeyValue(rectangle.widthProperty(), diameter);
        KeyValue v8 = new KeyValue(rectangle.heightProperty(), diameter);
        KeyFrame kf2 = new KeyFrame(Duration.millis(1000), v5, v6, v7, v8);
        shrinkContent = new Timeline(kf2);
        shrinkContent.setOnFinished((event) -> {
            shrinkStage();
        });
        
        rectangle.setOnMouseClicked(event ->{
            if (small) {
                grow();
            } else {
                shrink();
            }
        });

        initStage();
    }

    private AnchorPane createBackground(double radius, Paint fill) {
        AnchorPane background = new AnchorPane();
        background.setBackground(Background.EMPTY);

        double size = radius * 2;
        rectangle = new Rectangle(size, size, fill);
        rectangle.setArcWidth(size);
        rectangle.setArcHeight(size);

        background.getChildren().add(rectangle);
        return background;
    }

    private void initStage() {
        this.scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        this.setScene(scene);
        this.initStyle(StageStyle.TRANSPARENT);

        
    }

    private void grow() {
        small=false;
        growStage();
        this.growContent.play();
    }

    private void growStage() {
        this.setWidth(bigSize);
        this.setHeight(bigSize);
        this.setMinWidth(bigSize);
        this.setMinHeight(bigSize);
        this.setX(getX()-difference/2.);
        this.setY(getY()-difference/2.);
    }

    private void shrink() {
        small=true;
        this.shrinkContent.play();
    }

    private void shrinkStage() {//called in shrink animation callback
        this.setX(getX()+difference/2.);
        this.setY(getY()+difference/2.);
        this.setMaxWidth(diameter);
        this.setMaxHeight(diameter);
        this.setMinWidth(diameter);
        this.setMinHeight(diameter);
    }

}
