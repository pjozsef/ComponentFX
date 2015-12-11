package com.github.pjozsef.componentfx.floatingstage;

import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Duration;

class DragManager {

    private static final long SAMPLE_RATE = 50;
    private static final double DISTANCE_THRESHOLD = 10;
    private static final double DISTANCE_MAX = 40;
    private static final int CYCLE_COUNT_RATIO = 60;

    private final DraggableFloatingStage<? extends Region> stage;
    private long sampleTime;
    private boolean wasMoved;
    private Point2D sampled, current;

    DragManager(DraggableFloatingStage<? extends Region> stage) {
        this.stage = stage;
    }

    void onDrag(MouseEvent event) {
        if (System.currentTimeMillis() - sampleTime > SAMPLE_RATE) {
            updateCoordinates(event);
        }
        wasMoved = true;
        stage.setX(event.getScreenX() - stage.getScene().getWidth() / 2);
        stage.setY(event.getScreenY() - stage.getScene().getHeight() / 2);
    }

    void onPress(MouseEvent event) {
        initCoordinates(event);
    }

    void onRelease(MouseEvent event) {
        System.out.println(sampled.distance(current));
        double distance = getDistance(sampled, current);
        if (distance > DISTANCE_THRESHOLD) {
            int cycleCount = (int)Math.pow(distance, 2)/CYCLE_COUNT_RATIO;
            final ArrayList<Double> slideRatios = new ArrayList<>(cycleCount);
            for(int i=cycleCount-1; i!=0 ; --i){
                System.out.println(Math.sqrt(i)/cycleCount);
                slideRatios.add(Math.sqrt(i)/cycleCount);
            }
            Timeline dragTimeline = new Timeline(
                    new KeyFrame(
                            Duration.millis(33),
                            e -> {
                                double ratio = slideRatios.get(0);
                                Point2D slide = current.subtract(sampled);
                                stage.setX(stage.getX() + slide.getX()*ratio);
                                stage.setY(stage.getY() + slide.getY()*ratio);
                                slideRatios.remove(0);
                            }));
            dragTimeline.setCycleCount(cycleCount-1);
            dragTimeline.play();
        }
    }

    boolean wasMoved() {
        if (wasMoved) {
            wasMoved = false;
            return true;
        }
        return wasMoved;
    }

    private void initCoordinates(MouseEvent e) {
        sampled = new Point2D(e.getX(), e.getY());
        current = new Point2D(e.getX(), e.getY());
    }

    private void updateCoordinates(MouseEvent e) {
        Point2D temp = new Point2D(e.getX(), e.getY());
        if(sampled.distance(temp)>DISTANCE_THRESHOLD){
            sampled = current;
            current = temp;
        }
    }

    private double getDistance(Point2D sampled, Point2D current) {
        double distance = sampled.distance(current);
        return distance > DISTANCE_MAX ? DISTANCE_MAX : distance;
    }
}
