package com.github.pjozsef.componentfx.floatingbubble;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
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
 *
 * @param <T> the generic Node parameter that is displayed inside a
 * FloatingStage
 */
public class FloatingStage<T extends Region> extends Stage {

    BooleanProperty shrunk;
    StackPane root;
    Rectangle background;
    Scene scene;
    Timeline growContent, shrinkContent;
    double diameter, expandedWidth, expandedHeight, diffWidth, diffHeight;
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
    public FloatingStage(final double diameter, Paint fill, T content, FloatingStageOption[] options) {
        this.diameter = diameter;
        this.expandedWidth = content.getPrefWidth();
        this.expandedHeight = content.getPrefHeight();
        this.diffWidth = expandedWidth - diameter;
        this.diffHeight = expandedHeight - diameter;
        this.root = new StackPane();
        this.shrunk = new SimpleBooleanProperty(true);
        this.background = new Rectangle(diameter, diameter, fill);
        this.content = content;
        this.options = options;

        initBackground();
        initContent();
        initStage();
        applyOptions();
    }

    private void initStage() {
        root.setBackground(Background.EMPTY);
        root.getChildren().addAll(background, this.content);
        this.scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        this.setScene(scene);
        this.initStyle(StageStyle.TRANSPARENT);
        this.setAlwaysOnTop(true);
        this.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean nowFocused) {
                if (!nowFocused) {
                    shrink();
                }
            }

        });
    }

    private void initContent() {
        this.content.visibleProperty().bind(shrunk.not());
    }

    private void initBackground() {
        background.setArcWidth(diameter);
        background.setArcHeight(diameter);

        KeyValue v1 = new KeyValue(background.arcHeightProperty(), 0);
        KeyValue v2 = new KeyValue(background.arcWidthProperty(), 0);
        KeyValue v3 = new KeyValue(background.widthProperty(), expandedWidth);
        KeyValue v4 = new KeyValue(background.heightProperty(), expandedHeight);
        KeyFrame kf1 = new KeyFrame(Duration.millis(1000), v1, v2, v3, v4);
        growContent = new Timeline(kf1);
        growContent.setOnFinished((event) -> {
            shrunk.set(false);
        });

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
            if (shrunk.get()) {
                grow();
            } else {
                shrink();
            }
        });
    }

    private void applyOptions() {
        for (FloatingStageOption option : options) {
            option.apply(this);
        }
    }

    public void grow() {
        growStage();
        this.growContent.play();
    }

    public void shrink() {
        shrunk.set(true);
        this.shrinkContent.play();
    }

    private void growStage() {
        this.setWidth(expandedWidth);
        this.setHeight(expandedHeight);
        this.setMinWidth(expandedWidth);
        this.setMinHeight(expandedHeight);
        this.setX(getX() - diffWidth / 2.);
        this.setY(getY() - diffHeight / 2.);
    }

    private void shrinkStage() {//called in shrink animation callback
        this.setX(getX() + diffWidth / 2.);
        this.setY(getY() + diffHeight / 2.);
        this.setMaxWidth(diameter);
        this.setMaxHeight(diameter);
        this.setMinWidth(diameter);
        this.setMinHeight(diameter);
    }

}
