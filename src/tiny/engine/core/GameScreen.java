package tiny.engine.core;

import java.awt.*;

/**
 * The GameScreen class represents a screen that is rendered and updated by the
 * internal game loop. The GameScreen class is abstract and is extended by any
 * class wishing to be rendered or updated.
 *
 * @author Damian Strain
 */
public abstract class GameScreen {

    private final Game game;

    /**
     * Constructs a new game screen that has access to all TinyEngine
     * components.
     *
     * @param game the reference to the game
     */
    public GameScreen(Game game) {
        this.game = game;
    }

    /**
     * Updates the current screen's logic. Override this method if you are using
     * the fixed time step loop.
     */
    public void update() {
    }

    /**
     * Updates the current screen's logic. Override this method if you are using
     * the variable time step loop.
     *
     * @param deltaTime the time passed since the last call to update
     */
    public void update(double deltaTime) {
    }

    /**
     * Renders the current screen. Override this method if you are using the
     * fixed time step loop.
     *
     * @param g2d the Graphics context to render with
     * @param interpolation the interpolation between the previous and current
     * state
     */
    public void render(Graphics2D g2d, float interpolation) {
    }

    /**
     * Renders the current screen. Override this method if you are using the
     * variable time step loop.
     *
     * @param g2d the Graphics context to render with
     */
    public void render(Graphics2D g2d) {
    }

    /**
     * Pauses the current screen.
     */
    public abstract void pause();

    /**
     * Resumes the current screen if paused.
     */
    public abstract void resume();

    /**
     * Disposes of the current screen, freeing up resources. This should be
     * called when the current screen is no longer needed.
     */
    public abstract void dispose();
}
