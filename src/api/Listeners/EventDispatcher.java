package api.Listeners;

import api.Inventory;
import api.Listeners.Experience.ExperienceEvent;
import api.Listeners.Experience.ExperienceEventSource;
import api.Listeners.Experience.ExperienceListener;
import api.Listeners.Inventory.InventoryEvent;
import api.Listeners.Inventory.InventoryEventSource;
import api.Listeners.Inventory.InventoryListener;
import org.powerbot.script.rt4.ClientContext;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 11/16/15
 */

public class EventDispatcher {

    private final List<EventListener> listeners;
    private final Object syncLock = new Object();
    private volatile boolean running;

    public EventDispatcher(final ClientContext ctx) {
        this.listeners = new ArrayList<EventListener>();
        this.running = true;

        new Thread(new InventoryEventSource(this, ctx)).start();
        new Thread(new ExperienceEventSource(this, ctx)).start();
    }

    public void addListener(EventListener listener) {
        synchronized (syncLock) {
            listeners.add(listener);
        }
    }

    public void removeListener(EventListener listener) {
        synchronized (syncLock) {
            listeners.remove(listener);
        }
    }

    public void clearListeners() {
        synchronized (syncLock) {
            listeners.clear();
        }
    }

    public void fireEvent(EventObject event) {
        synchronized (syncLock) {
            for (EventListener listener : listeners) {
                if (listener instanceof ExperienceListener && event instanceof ExperienceEvent) {
                    ((ExperienceListener) listener).onExperienceChanged((ExperienceEvent) event);
                }
                if (listener instanceof InventoryListener && event instanceof InventoryEvent) {
                    ((InventoryListener) listener).onInventoryChange((InventoryEvent) event);
                }
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

}