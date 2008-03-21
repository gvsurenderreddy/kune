package org.ourproject.kune.platf.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.ui.Widget;

public class SplitterListenerCollection extends ArrayList<SplitterListener> {

    private static final long serialVersionUID = 1L;

    /**
     * Fires a event to all listeners.
     * 
     * @param sender
     *                the widget sending the event.
     */
    public void fireStartResizing(final Widget sender) {
        for (Iterator<SplitterListener> it = iterator(); it.hasNext();) {
            SplitterListener listener = it.next();
            listener.onStartResizing(sender);
        }
    }

    /**
     * Fires a event to all listeners.
     * 
     * @param sender
     *                the widget sending the event.
     */
    public void fireStopResizing(final Widget sender) {
        for (Iterator<SplitterListener> it = iterator(); it.hasNext();) {
            SplitterListener listener = it.next();
            listener.onStopResizing(sender);
        }
    }

}