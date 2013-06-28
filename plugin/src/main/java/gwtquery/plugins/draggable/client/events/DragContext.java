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
import com.google.gwt.query.client.GQuery.Offset;
import gwtquery.plugins.draggable.client.DraggableHandler;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;

import java.util.List;

import static com.google.gwt.query.client.GQuery.$;

/**
 * Object containing useful information on the drag operation.
 *
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 */
public class DragContext {

  static final String VALUE_KEY = "__dragAndDropCellAssociatedValue";

  private Element draggable;

  private List<Element> selectedDraggables;

  private Element initialDraggable;


  /**
   * Constructor
   *
   * @param draggable the draggable element
   */
  public DragContext(Element draggable, Element initialDraggable,
                     List<Element> selectedDraggable) {
    this.draggable = draggable;
    this.selectedDraggables = selectedDraggable;
    this.initialDraggable = initialDraggable;
  }

  public DragContext(DragContext ctx) {
    this(ctx.getDraggable(), ctx.getInitialDraggable(), ctx
        .getSelectedDraggables());
  }

  /**
   * @return the draggable DOM element
   */
  public Element getDraggable() {
    return draggable;
  }

  /**
   * This method allows getting the data object linked to the draggable element
   * (a cell) in the context of CellWidget.It return the data object being
   * rendered by the dragging cell. Return null if we are not in the context of
   * an drag and drop cell widget.
   *
   * @param <T> the class of the data
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T> T getDraggableData() {
    return (T) $(getDraggable()).data(VALUE_KEY);
  }

  /**
   * This method return the widget associated to the draggable DOM element if it
   * exist. It returns null otherwise.
   */
  public DraggableWidget<?> getDraggableWidget() {
    if (getDraggable() != null) {
      return DraggableWidget.get(getDraggable());
    }
    return null;
  }

  /**
   * @return the DOM element used for dragging display
   */
  public Element getHelper() {
    DraggableHandler handler = DraggableHandler.getInstance(draggable);

    if (handler.getHelper() != null) {
      return handler.getHelper().get(0);
    }
    return null;
  }

  /**
   * @return the {@link Offset} of the helper element.
   */
  public Offset getHelperPosition() {
    return DraggableHandler.getInstance(draggable).getPosition();
  }

  /**
   * @return the list of selected draggables.
   */
  public List<Element> getSelectedDraggables() {
    return selectedDraggables;
  }

  /**
   * @return the draggable element that initiate the drag operation (i.e. the
   *         clicked element)
   */
  public Element getInitialDraggable() {
    return initialDraggable;
  }


}
