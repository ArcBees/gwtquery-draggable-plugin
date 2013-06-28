/*
 * Copyright 2010 The gwtquery plugins team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gwtquery.plugins.draggable.client.events;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.draggable.client.events.DragContext.VALUE_KEY;

/**
 * Event fired when a draggable is unselected and when multipe selection is
 * enabled
 *
 * @param <H>
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 */
public class DraggableUnselectedEvent extends
    GwtEvent<DraggableUnselectedEvent.DraggableUnselectedHandler> {

  public interface DraggableUnselectedHandler extends EventHandler {
    public void onDraggableUnselected(DraggableUnselectedEvent event);
  }

  public static Type<DraggableUnselectedHandler> TYPE = new Type<DraggableUnselectedHandler>();

  private Element unselectedDraggable;

  public DraggableUnselectedEvent(Element unselectedDraggable) {
    this.unselectedDraggable = unselectedDraggable;
  }

  @Override
  public Type<DraggableUnselectedHandler> getAssociatedType() {
    return TYPE;
  }

  /**
   * This method allows getting the data object linked to the selected draggable
   * element (a cell) in the context of CellWidget.It return the data object
   * being rendered by the selected cell. Return null if we are not in the
   * context of an drag and drop cell widget.
   *
   * @param <T> the class of the data
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T> T getUnselectedData() {
    return (T) $(unselectedDraggable).data(VALUE_KEY);
  }

  /**
   * @return the current draggable DOM element
   */
  public Element getUnselectedDraggable() {
    return unselectedDraggable;
  }

  /**
   * This method return the widget associated to the unselected DOM element if
   * it exist. It returns null otherwise.
   */
  public DraggableWidget<?> getUnselectedDraggableWidget() {
    return DraggableWidget.get(unselectedDraggable);
  }

  @Override
  protected void dispatch(DraggableUnselectedHandler handler) {
    handler.onDraggableUnselected(this);

  }

}
