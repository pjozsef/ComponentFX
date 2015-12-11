package com.github.pjozsef.componentfx.pixelcanvas;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author József Pollák
 */
public class BasicPixelCanvas extends PixelCanvas {
    private final Canvas canvas;
    private Paint[][] state;
    protected final IntegerProperty rows, columns;
    private final DoubleProperty width, height, pixelWidth, pixelHeight;

    public BasicPixelCanvas(DoubleProperty w, DoubleProperty h) {
        this.width = w;
        this.height = h;
        this.setWidth(width.doubleValue());
        this.setHeight(height.doubleValue());
        width.addListener((observable, oldValue, newValue) -> this.setWidth(width.doubleValue()));
        height.addListener((observable, oldValue, newValue) -> this.setHeight(height.doubleValue()));

        this.rows = new SimpleIntegerProperty(1);
        this.columns = new SimpleIntegerProperty(1);
        this.rows.addListener(this::onSizeChange);
        this.columns.addListener(this::onSizeChange);

        this.pixelWidth = new SimpleDoubleProperty();
        this.pixelHeight = new SimpleDoubleProperty();
        this.pixelWidth.bind(widthProperty().divide(columns));
        this.pixelHeight.bind(heightProperty().divide(rows));
        this.pixelWidth.addListener(this::onPropertyChange);
        this.pixelHeight.addListener(this::onPropertyChange);


        this.canvas = new Canvas();
        this.state = new Paint[0][0];


        this.canvas.widthProperty().bind(this.widthProperty());
        this.canvas.heightProperty().bind(this.heightProperty());

        widthProperty().addListener(this::onPropertyChange);
        heightProperty().addListener(this::onPropertyChange);

        getChildren().add(this.canvas);
    }


    private void onPropertyChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        refresh();
    }

    private void onSizeChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        checkNotNull(newValue, "New value of observable must not be null!");
        checkArgument(newValue.intValue() > 0, "New value must be greater than 0!");
        refresh();
    }

    /**
     * {@inheritDoc }
     *
     * @param x
     * @param y
     * @param p
     */
    @Override
    public void drawPixel(int x, int y, Paint p) {
        //lower estimate for x and y offset
        final double offsetX = Math.floor(x * getPixelWidth().get());
        final double offsetY = Math.floor(y * getPixelHeight().get());
        //upper estimate for width and height
        final double rectangleWidth = Math.ceil(getPixelWidth().get());
        final double rectangleHeight = Math.ceil(getPixelHeight().get());
        GraphicsContext gc = getCanvas().getGraphicsContext2D();
        gc.setFill(p);
        gc.fillRect(offsetX, offsetY, rectangleWidth, rectangleHeight);

        state[x][y] = p;
    }

    /**
     * {@inheritDoc }
     *
     * @param indices
     * @param paints
     */
    @Override
    public void drawMatrix(int[][] indices, List<Paint> paints) {
        for (int i = 0; i < indices.length; ++i) {
            for (int j = 0; j < indices[i].length; ++j) {
                drawPixel(i, j, paints.get(indices[i][j]));
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void refresh() {
        drawMatrix(this.state);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void drawMatrix(Paint[][] paintMatrix) {
        for (int i = 0; i < state.length; ++i) {
            for (int j = 0; j < state[i].length; ++j) {
                drawPixel(i, j, paintMatrix[i][j]);
            }
        }
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    public int getRows() {
        return rows.get();
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    public int getColumns() {
        return columns.get();
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    protected Canvas getCanvas() {
        return this.canvas;
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    protected DoubleProperty getPixelWidth() {
        return this.pixelWidth;
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    protected DoubleProperty getPixelHeight() {
        return this.pixelHeight;
    }
}
