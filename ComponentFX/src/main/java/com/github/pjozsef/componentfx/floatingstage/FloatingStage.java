package com.github.pjozsef.componentfx.floatingstage;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * A {@code FloatingStage} represents a special JavaFX
 * {@code Stage} that expands on focus gain and shrinks on focus lost.
 *
 * @param <T> the generic container type that is displayed inside a
 FloatingStage.
 */
public interface FloatingStage<T extends Region> {
    /**
     * Called when the FloatingStage gains focus.
     */
    public void grow();
    /**
     * Called when the FloatingStage looses focus.
     */
    public void shrink();
    /**
     * Returns the observable property of the centerX coordinate.
     * @return the center x coordinate of this FloatingStage
     */
    public ReadOnlyDoubleProperty centerX();
    /**
     * Returns the observable property of the centerY coordinate.
     * @return the center y coordinate of this FloatingStage
     */
    public ReadOnlyDoubleProperty centerY();
    /**
     * Returns the stage of this FloatingStage.
     * @return the stage
     */
    public Stage getStage();
    /**
     * Returns the content of this FloatingStage.
     * @return the content
     */
    public T getContent();
}

