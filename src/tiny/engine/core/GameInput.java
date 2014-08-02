package tiny.engine.core;

import tiny.engine.input.Keyboard;
import tiny.engine.input.Mouse;
import tiny.engine.input.MouseWheel;

/**
 * The GameInput class encapsulates and provides access to the underlying input
 * component. It provides methods to returns instances of the keyboard, mouse or
 * mouse wheel.
 *
 * @author Damian Strain
 */
public final class GameInput {

    private final Keyboard keyboard;
    private final Mouse mouse;
    private final MouseWheel mouseWheel;

    /**
     * Constructs and initialises the input component.
     */
    public GameInput() {
        keyboard = new Keyboard();
        mouse = new Mouse();
        mouseWheel = new MouseWheel();
    }

    /**
     * Returns a reference to the keyboard.
     *
     * @return a keyboard reference
     */
    public Keyboard getKeyboard() {
        return keyboard;
    }

    /**
     * Returns a reference to the mouse.
     *
     * @return a mouse reference
     */
    public Mouse getMouse() {
        return mouse;
    }

    /**
     * Returns a reference to the mouse wheel.
     *
     * @return a mouse wheel reference
     */
    public MouseWheel getMouseWheel() {
        return mouseWheel;
    }
}
