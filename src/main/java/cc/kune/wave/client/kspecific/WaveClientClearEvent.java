/*
 *
 * Copyright (C) 2007-2012 The kune development team (see CREDITS for details)
 * This file is part of kune.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package cc.kune.wave.client.kspecific;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class WaveClientClearEvent extends GwtEvent<WaveClientClearEvent.WaveClientClearHandler> {

  public interface HasWaveClientClearHandlers extends HasHandlers {
    HandlerRegistration addWaveClientClearHandler(WaveClientClearHandler handler);
  }

  public interface WaveClientClearHandler extends EventHandler {
    public void onWaveClientClear(WaveClientClearEvent event);
  }

  private static final Type<WaveClientClearHandler> TYPE = new Type<WaveClientClearHandler>();

  public static void fire(final HasHandlers source) {
    source.fireEvent(new WaveClientClearEvent());
  }

  public static Type<WaveClientClearHandler> getType() {
    return TYPE;
  }

  public WaveClientClearEvent() {
  }

  @Override
  protected void dispatch(final WaveClientClearHandler handler) {
    handler.onWaveClientClear(this);
  }

  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  @Override
  public Type<WaveClientClearHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return "WaveClientClearEvent[" + "]";
  }
}
