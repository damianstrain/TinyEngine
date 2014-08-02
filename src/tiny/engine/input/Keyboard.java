package tiny.engine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The Keyboard Class handles key input events. The only commonly needed public
 * method is isKeyPressed(int). This simply returns whether a key is currently
 * pressed (i.e. the key has been pressed, but not yet released).
 *
 * @author Damian Strain
 */
public final class Keyboard extends EventQueue<KeyEvent> implements KeyListener {

    private static final int KEY_CODE_MAX = 256;
    private final boolean[] keys;

    /**
     * Constructs a new Keyboard instance and initialises the keys array. The
     * keys array holds simple booleans representing whether a key has been
     * pressed or not. The array can hold no more than 256 values.
     */
    public Keyboard() {
        keys = new boolean[KEY_CODE_MAX];
    }

    /**
     * Adds a key typed event to the queue.
     *
     * @param keyEvent the key event to be added
     */
    @Override
    public void keyTyped(KeyEvent keyEvent) {
        add(keyEvent);
    }

    /**
     * Adds a key pressed event to the queue.
     *
     * @param keyEvent the key event to be added
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        add(keyEvent);
    }

    /**
     * Adds a key released event to the queue.
     *
     * @param keyEvent the key event to be added
     */
    @Override
    public void keyReleased(KeyEvent keyEvent) {
        add(keyEvent);
    }

    /**
     * Returns whether a given key is currently pressed or not.
     *
     * @param keyCode the code for the given key
     * @return true if the key is pressed, false otherwise
     */
    public boolean isKeyPressed(int keyCode) {
        // Check if the key is a fast key
        return (keyCode >= 0) && (keyCode < KEY_CODE_MAX) && keys[keyCode];
    }

    /**
     * This method processes the key event passed into it. If the key has been
     * pressed, true is added to the keys array and false if the key is
     * released.
     *
     * @param event the event to be processed
     */
    @Override
    public void processEvent(KeyEvent event) {
        if (event.getID() == KeyEvent.KEY_PRESSED) {
            if ((event.getKeyCode() >= 0) && (event.getKeyCode() < KEY_CODE_MAX)) {
                keys[event.getKeyCode()] = true;
            }
        } else if (event.getID() == KeyEvent.KEY_RELEASED) {
            if ((event.getKeyCode() >= 0) && (event.getKeyCode() < KEY_CODE_MAX)) {
                keys[event.getKeyCode()] = false;
            }
        }
    }
}
