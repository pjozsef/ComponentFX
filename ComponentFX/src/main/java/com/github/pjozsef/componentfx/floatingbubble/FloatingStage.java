package com.github.pjozsef.componentfx.floatingbubble;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Scene;
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
 * @param <T> the generic Node parameter that is displayed inside a FloatingStage
 */
public class FloatingStage<T extends Node> extends Stage {

    boolean shrunk;
    StackPane root;
    Rectangle background;
    Scene scene;
    Timeline growContent, shrinkContent;
    double diameter, bigSize, difference;
    final T content;
    final FloatingStageOption[] options;

    /**
     * Creates a FloatingStage with a specific radius, background color and the
     * specified options.
     *
     * @param diameter the diameter of this FloatingStage
     * @param fill the fill of this FloatingStage
     * @param content the content displayed in this FloatingStage
     * @param options the {@link FloatingStageOption}s that provide extra
     * functionalities to this FloatingStage
     */
    @Builder
    @SuppressWarnings("LeakingThisInConstructor")
    public FloatingStage(final double diameter, Paint fill, T content, FloatingStageOption[] options) {
        this.diameter = diameter;
        this.bigSize = diameter * 5;
        this.difference = bigSize - diameter;

        root = new StackPane();
        root.setBackground(Background.EMPTY);

        shrunk = true;
        
        background = new Rectangle(diameter, diameter, fill);
        initBackground();

        initStage();
        this.content = content;
        
        this.options = options;
        for (FloatingStageOption option : options) {
            option.apply(this);
        }
    }

    private void initStage() {
        this.scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        this.setScene(scene);
        this.initStyle(StageStyle.TRANSPARENT);

    }

    private void initBackground() {
        background.setArcWidth(diameter);
        background.setArcHeight(diameter);
        root.getChildren().add(background);

        KeyValue v1 = new KeyValue(background.arcHeightProperty(), 0);
        KeyValue v2 = new KeyValue(background.arcWidthProperty(), 0);
        KeyValue v3 = new KeyValue(background.widthProperty(), bigSize);
        KeyValue v4 = new KeyValue(background.heightProperty(), bigSize);
        KeyFrame kf1 = new KeyFrame(Duration.millis(1000), v1, v2, v3, v4);
        growContent = new Timeline(kf1);

        KeyValue v5 = new KeyValue(background.arcHeightProperty(), diameter);
        KeyValue v6 = new KeyValue(background.arcWidthProperty(), diameter);
        KeyValue v7 = new KeyValue(background.widthProperty(), diameter);
        KeyValue v8 = new KeyValue(background.heightProperty(), diameter);
        KeyFrame kf2 = new KeyFrame(Duration.millis(1000), v5, v6, v7, v8);
        shrinkContent = new Timeline(kf2);
        shrinkContent.setOnFinished((event) -> {
            shrinkStage();
        });

        background.setOnMouseClicked(event -> {
            if (shrunk) {
                grow();
            } else {
                shrink();
            }
        });
    }

    public void grow() {
        shrunk = false;
        growStage();
        this.growContent.play();
    }

    public void shrink() {
        shrunk = true;
        this.shrinkContent.play();
    }
    
    private void growStage() {
        this.setWidth(bigSize);
        this.setHeight(bigSize);
        this.setMinWidth(bigSize);
        this.setMinHeight(bigSize);
        this.setX(getX() - difference / 2.);
        this.setY(getY() - difference / 2.);
    }

    private void shrinkStage() {//called in shrink animation callback
        this.setX(getX() + difference / 2.);
        this.setY(getY() + difference / 2.);
        this.setMaxWidth(diameter);
        this.setMaxHeight(diameter);
        this.setMinWidth(diameter);
        this.setMinHeight(diameter);
    }

}
