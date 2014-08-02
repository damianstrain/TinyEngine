package tiny.engine.input;

import java.util.Collection;
import java.util.LinkedList;

/**
 * The EventQueue Class provides basic thread-safe and deterministic event
 * handling for input. Events that occur between update calls are placed into
 * the event queue. When update is called, each event is processed, then the
 * event queue is moved to the publicly accessible events List.
 *
 * @author Damian Strain
 */
public abstract class EventQueue<T> {

    private LinkedList<T> events = new LinkedList<T>();
    private LinkedList<T> eventQueue = new LinkedList<T>();

    /**
     * Adds an event to the event queue.
     *
     * @param newEvent the event to be added
     */
    public final synchronized void add(T newEvent) {
        eventQueue.add(newEvent);
    }

    /**
     * Adds events to the event queue. The events will be processed during the
     * next update.
     *
     * @param newEvents the events to be added
     */
    public final synchronized void addEvents(Collection<T> newEvents) {
        eventQueue.addAll(newEvents);
    }

    /**
     * Returns a list of events that occurred before the last update.
     *
     * @return a List of events
     */
    public final LinkedList<T> getEvents() {
        return events;
    }

    /**
     * Override to perform any custom processing for events. When update() is
     * called, this will be called for each event in the queue.
     *
     * @param event the event to be processed
     */
    protected abstract void processEvent(T event);

    /**
     * This method processes the event queue. The processEvent() method is
     * called for each event in the queue. Events are then set to the eventQueue
     * List, and a new eventQueue is created.
     * <p/>
     * As events are no longer needed, the user is free to modify them. If a
     * reference to events is not saved before the next update, the contents
     * will be lost.
     */
    public synchronized void update() {
        for (T event : eventQueue) {
            processEvent(event);
        }
        events = eventQueue;
        eventQueue = new LinkedList<T>();
    }
}
