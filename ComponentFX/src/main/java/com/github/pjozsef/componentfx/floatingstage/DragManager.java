package com.github.pjozsef.componentfx.floatingstage;

import javafx.scene.input.MouseEvent;

class DragManager {

    private final DraggableFloatingStage<?> stage;

    private double x, y;
    private boolean wasMoved;

    DragManager(DraggableFloatingStage<?> stage) {
        this.stage = stage;
    }

    void onDrag(MouseEvent event) {
        wasMoved = true;
        stage.setX(event.getScreenX() - stage.getScene().getWidth()/2 );
        stage.setY(event.getScreenY() - stage.getScene().getHeight()/2 );
    }

    void onPress(MouseEvent event) {
        updateCoordinates(event);
    }

    boolean wasMoved() {
        if (wasMoved) {
            wasMoved = false;
            return true;
        }
        return wasMoved;
    }

    private void updateCoordinates(MouseEvent event) {
        x = event.getX();
        y = event.getY();
    }
}
