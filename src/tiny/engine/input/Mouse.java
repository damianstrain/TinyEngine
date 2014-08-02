package tiny.engine.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * The Mouse Class handles mouse and mouse motion events. The last event is used
 * to determine the mouse location.
 *
 * @author Damian Strain
 */
public final class Mouse extends EventQueue<MouseEvent> implements MouseListener, MouseMotionListener {

    private int x;
    private int y;

    private boolean leftButton;
    private boolean middleButton;
    private boolean rightButton;

    /**
     * Returns the x coordinate of the mouse cursor.
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the mouse cursor.
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns whether the left mouse button is currently pressed.
     *
     * @return true is the left mouse button is pressed, false otherwise
     */
    public boolean isLeftButtonPressed() {
        return leftButton;
    }

    /**
     * Returns whether the middle mouse button is currently pressed.
     *
     * @return true is the middle mouse button is pressed, false otherwise
     */
    public boolean isMiddleButtonPressed() {
        return middleButton;
    }

    /**
     * Returns whether the right mouse button is currently pressed.
     *
     * @return true is the right mouse button is pressed, false otherwise
     */
    public boolean isRightButtonPressed() {
        return rightButton;
    }

    /**
     * Adds a mouse clicked event to the queue.
     *
     * @param mouseEvent the mouse event to be added
     */
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        add(mouseEvent);
    }

    /**
     * Adds a mouse pressed event to the queue.
     *
     * @param mouseEvent the mouse event to be added
     */
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        add(mouseEvent);
    }

    /**
     * Adds a mouse released event to the queue.
     *
     * @param mouseEvent the mouse event to be added
     */
    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        add(mouseEvent);
    }

    /**
     * Adds a mouse entered event to the queue.
     *
     * @param mouseEvent the mouse event to be added
     */
    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        add(mouseEvent);
    }

    /**
     * Adds a mouse existed event to the queue.
     *
     * @param mouseEvent the mouse event to be added
     */
    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        add(mouseEvent);
    }

    /**
     * Adds a mouse dragged event to the queue.
     *
     * @param mouseEvent the mouse event to be added
     */
    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        add(mouseEvent);
    }

    /**
     * Adds a mouse moved event to the queue.
     *
     * @param mouseEvent the mouse event to be added
     */
    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        add(mouseEvent);
    }

    /**
     * This method processes the mouse event passed into it. If a mouse button
     * has been pressed, it is marked true.
     *
     * @param event the event to be processed
     */
    @Override
    public void processEvent(MouseEvent event) {
        x = event.getX();
        y = event.getY();

        // If the mouse button was pressed, update the correct button
        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                leftButton = true;
            }

            if (event.getButton() == MouseEvent.BUTTON2) {
                middleButton = true;
            }

            if (event.getButton() == MouseEvent.BUTTON3) {
                rightButton = true;
            }
        }

        // If the mouse button was released, update the correct button
        if (event.getID() == MouseEvent.MOUSE_RELEASED) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                leftButton = false;
            }

            if (event.getButton() == MouseEvent.BUTTON2) {
                middleButton = false;
            }

            if (event.getButton() == MouseEvent.BUTTON3) {
                rightButton = false;
            }
        }
    }
}
