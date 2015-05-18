package com.github.pjozsef.componentfx.floatingbubble;

/**
 * This enum is used to add additional functionalities to the given FloatingStage instance.
 */
public enum FloatingStageOption {
    /**
     * DRAGGABLE enables mouse dragging of the shrunk stage.
     */
    DRAGGABLE {
        @Override
        public void apply(FloatingStage stage) {
            
        }
    };
    public abstract void apply(FloatingStage stage);
}
