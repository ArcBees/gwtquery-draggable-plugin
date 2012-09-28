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

import java.util.List;

/**
 * Abstract class for all drag events.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 * @param <H>
 */
public abstract class AbstractDraggableEvent<H extends EventHandler> extends
    GwtEvent<H> {

  private DragContext context;

  public AbstractDraggableEvent() {

  }

  public AbstractDraggableEvent(DragContext dragContext) {
    context = dragContext;
  }

  /**
   * This method allows getting the data object linked to the draggable element
   * (a cell) in the context of CellWidget.It return the data object being
   * rendered by the dragging cell. Return null if we are not in the context of
   * an drag and drop cell widget.
   * 
   * @param <T>
   *          the class of the data
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T> T getDraggableData() {
    assert context != null : "Drag context cannot be null";
    return (T) context.getDraggableData();
  }

  /**
   * This method return the widget associated to the draggable DOM element if it
   * exist. It returns null otherwise.
   * 
   */
  public DraggableWidget<?> getDraggableWidget() {
    assert context != null : "Drag context cannot be null";
    return context.getDraggableWidget();
  }

  /**
   * @return the current draggable DOM element
   * 
   */
  public Element getDraggable() {
    assert context != null : "Drag context cannot be null";
    return context.getDraggable();
  }
  
  /**
   * @return the draggable element that initiate the drag operation (i.e. the
   *         clicked element)
   */
  public Element getInitialDraggable() {
    assert context != null : "Drag context cannot be null";
    return context.getInitialDraggable();
  }
  
  public List<Element> getSelectedDraggables() {
    assert context != null : "Drag context cannot be null";
    return context.getSelectedDraggables();
  }

  /**
   * 
   * @return the DOM element used for dragging display
   */
  public Element getHelper() {
    assert context != null : "Drag context cannot be null";
    return context.getHelper();
  }
}

