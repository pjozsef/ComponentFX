package com.github.pjozsef.componentfx.pixelcanvas;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import lombok.Builder;

import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * A PixelCanvas object is a wrapper around a Canvas that contains a set of rows and
 * columns of pixels. These pixels can easily be changed by the 
 * convenient drawPixel and drawMatrix methods.
 */
public abstract class PixelCanvas extends AnchorPane {
    @Builder(
            builderMethodName = "simpleCanvas",
            buildMethodName = "build",
            builderClassName = "BasicPixelCanvasBuilder")
    private static PixelCanvas buildCanvas(double width, double height) {
        return new BasicPixelCanvas(new SimpleDoubleProperty(width), new SimpleDoubleProperty(height));
    }

    /**
     * Draws a pixel on the canvas.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param p the paint to use
     */
    public abstract void drawPixel(int x, int y, Paint p);
    
    /**
     * Draws a whole matrix on the canvas
     * @param paintMatrix the matrix to draw
     */
    public abstract void drawMatrix(Paint[][] paintMatrix);

    /**
     * Draws a whole matrix on the canvas.
     * The index matrix must have the dimension of the PixelCanvas and its values are indices 
     * that are mapped to the elements in the paints list.
     * 
     * For example:
     * If the value of the element at (0,0) in the indices parameter is 1,
     * then the element with the 1th index in the list will be used
     * to color the (0,0)th pixel on the PixelCanvas.
     * 
     * @param indices the index matrix
     * @param paints the List of Paints to use
     */
    public abstract void drawMatrix(int[][] indices, List<Paint> paints);

    /**
     * Repaints the canvas.
     */
    public abstract void refresh();

    /**
     * Sets the number of pixels in a row and in a column in the canvas.
     * @param row the number of pixels in a row
     * @param col the number of pixels in a column
     */
    public abstract void reize(int row, int col);
    
    /**
     * Returns the column count of the canvas
     * @return the column count of the canvas
     */
    public abstract int getColumns();
    
    /**
     * Returns the row count of the canvas
     * @return the row count of the canvas
     */
    public abstract int getRows();

    /**
     * Returns the underlying Canvas object
     * @return the underlying Canvas
     */
    protected abstract Canvas getCanvas();

    /**
     * Returns the width of a pixel depending on the size of the widget and the number of columns.
     * @return the width of a pixel
     */
    protected abstract ReadOnlyDoubleProperty getPixelWidth();

    /**
     * Returns the height of a pixel depending on the size of the widget and the number of rows.
     * @return the height of a pixel
     */
    protected abstract ReadOnlyDoubleProperty getPixelHeight();

}
