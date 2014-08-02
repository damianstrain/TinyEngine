package tiny.engine.input;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * The MouseWheel Class handles wheel rotation events.
 *
 * @author Damian Strain
 */
public final class MouseWheel extends EventQueue<MouseWheelEvent> implements MouseWheelListener {

    private int wheelRotation = 0;

    /**
     * Returns the total wheel rotation between the last two updates. A negative
     * rotation is away from the user, and a positive is towards.
     *
     * @return the total wheel rotation
     */
    public int getWheelRotation() {
        return wheelRotation;
    }

    /**
     * Because we want the difference in the mouse rotation, not a cumulative
     * tally, the wheel rotation needs to be reset each update. To do this,
     * update() is overridden. First the wheel count is reset, then we call the
     * EventQueue’s update as normal.
     */
    @Override
    public synchronized void update() {
        // Reset the wheel rotation
        wheelRotation = 0;
        super.update();
    }

    /**
     * Adds a mouseWheelEvent event to the queue.
     *
     * @param mouseWheelEvent the event to be added
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        add(mouseWheelEvent);
    }

    /**
     * This method processes the mouse wheel event passed into it.
     *
     * @param event the event to be processed
     */
    @Override
    public void processEvent(MouseWheelEvent event) {
        // Increment the wheel rotation
        wheelRotation += event.getWheelRotation();
    }
}
