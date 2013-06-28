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
package gwtquery.plugins.draggable.client.gwt;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.js.JsNodeArray;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.AxisOption;
import gwtquery.plugins.draggable.client.DraggableOptions.CursorAt;
import gwtquery.plugins.draggable.client.DraggableOptions.GroupingMode;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.DraggableOptions.SelectFunction;
import gwtquery.plugins.draggable.client.DraggableOptions.SnapMode;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent.BeforeDragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragEvent;
import gwtquery.plugins.draggable.client.events.DragEvent.DragEventHandler;
import gwtquery.plugins.draggable.client.events.DragStartEvent;
import gwtquery.plugins.draggable.client.events.DragStartEvent.DragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.draggable.client.events.DraggableSelectedEvent;
import gwtquery.plugins.draggable.client.events.DraggableSelectedEvent.DraggableSelectedHandler;
import gwtquery.plugins.draggable.client.events.DraggableUnselectedEvent;
import gwtquery.plugins.draggable.client.events.DraggableUnselectedEvent.DraggableUnselectedHandler;
import gwtquery.plugins.draggable.client.events.HasAllDragHandler;

import java.util.List;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.draggable.client.Draggable.Draggable;

/**
 * Wrapper widget that wrap an GWT widget and allows dragging it.
 * <p/>
 * This class can be used as a wrapper or subclassed.
 *
 * @param <T>
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 */
public class DraggableWidget<T extends Widget> extends Composite implements
    HasAllDragHandler {

  private final static String DRAGGABLE_WIDGET_KEY = "__draggableWidget";

  /**
   * This method return the widget associated to a draggable DOM element if it
   * exist. It returns null otherwise.
   *
   * @param e a draggable DOM element
   * @return
   */
  public static DraggableWidget<?> get(Element e) {
    return $(e).data(DRAGGABLE_WIDGET_KEY, DraggableWidget.class);
  }

  private SimpleEventBus dragHandlerManager;
  private DraggableOptions options;

  /**
   * Constructor wrapping a existing widget.
   *
   * @param w the widget that you want to make draggable
   */
  public DraggableWidget(T w) {
    this(w, new DraggableOptions(), DraggableOptions.DEFAULT_SCOPE);
  }

  /**
   * Constructor wrapping a existing widget.
   *
   * @param w       the widget that you want to make draggable
   * @param options options to use during the drag operation
   */
  public DraggableWidget(T w, DraggableOptions options) {
    this(w, options, DraggableOptions.DEFAULT_SCOPE);

  }

  /**
   * Constructor wrapping a existing widget.
   *
   * @param w       the widget that you want to make draggable
   * @param options options to use during the drag operation
   * @param scope   Used to group sets of draggable and droppable Widget, in addition
   *                to droppable's accept option. A draggable with the same scope
   *                value as a droppable will be accepted.
   */
  public DraggableWidget(T w, DraggableOptions options, String scope) {
    initWidget(w);
    this.options = options;
    this.options.setScope(scope);
  }

  /**
   * Constructor wrapping a existing widget.
   *
   * @param w     the widget that you want to make draggable
   * @param scope Used to group sets of draggable and droppable Widget, in addition
   *              to droppable's accept option. A draggable with the same scope
   *              value as a droppable will be accepted.
   */
  public DraggableWidget(T w, String scope) {
    this(w, new DraggableOptions(), scope);
  }

  /**
   * Constructor allowing subclassing of this class
   * <p/>
   * As {@link DraggableWidget} extends {@link Composite}, don't forget to call
   * {@link #initWidget(Widget)} method !
   */
  protected DraggableWidget() {
    this.options = new DraggableOptions();

  }

  /**
   * Add a handler object that will manage the {@link BeforeDragStartEvent}
   * event. This kind of event is fired before the initialization of the drag
   * operation.
   */
  public HandlerRegistration addBeforeDragHandler(
      BeforeDragStartEventHandler handler) {
    return addDragHandler(handler, BeforeDragStartEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DraggableSelectedEvent}
   * event. This kind of event is fired when the widget is selected.
   */
  public HandlerRegistration addDraggableSelectedHandler(
      DraggableSelectedHandler handler) {
    return addDragHandler(handler, DraggableSelectedEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DraggableUnselectedEvent}
   * event. This kind of event is fired when the widget is unselected.
   */
  public HandlerRegistration addDraggableUnselectedHandler(
      DraggableUnselectedHandler handler) {
    return addDragHandler(handler, DraggableUnselectedEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DragEvent} event. this
   * kind of event is fired during the move of the widget.
   */
  public HandlerRegistration addDragHandler(DragEventHandler handler) {
    return addDragHandler(handler, DragEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DragStartEvent} event.
   * This kind of event is fired when the drag operation starts.
   */
  public HandlerRegistration addDragStartHandler(DragStartEventHandler handler) {
    return addDragHandler(handler, DragStartEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DragStopEvent} event. This
   * kind of event is fired when the drag operation stops.
   */
  public HandlerRegistration addDragStopHandler(DragStopEventHandler handler) {
    return addDragHandler(handler, DragStopEvent.TYPE);
  }

  /**
   * Return the drag and drop scope. A draggable widget with the same scope than
   * a droppable widget will be accepted by this droppable.
   *
   * @return the scope
   */
  public String getDragAndDropScope() {
    return options.getScope();
  }

  /**
   * @return the options currently use for the drag operation
   */
  public DraggableOptions getDraggableOptions() {
    return options;
  }

  /**
   * Get the wrapped original widget
   *
   * @return
   */
  @SuppressWarnings("unchecked")
  public T getOriginalWidget() {
    return (T) getWidget();
  }

  /**
   * Tell if a clone of the widget is used as display during the drag operation
   */
  public boolean isCloneUsedAsHelper() {
    return HelperType.CLONE == options.getHelperType();
  }

  public boolean isDragDisabled() {
    return options.isDisabled();
  }

  /**
   * Tell if this widget is used as display during the drag operation
   */
  public boolean isOriginalUsedWidgetAsHelper() {
    return HelperType.ORIGINAL == options.getHelperType();
  }

  /**
   * Tell if an other widget is used as display during the drag operation
   */
  public boolean isOtherWidgetUsedAsHelper() {
    return HelperType.ELEMENT == options.getHelperType();

  }

  /**
   * The element selected by the appendTo option will be used as the draggable
   * helper's container during dragging. By default, the helper is appended to
   * the same container as the draggable.
   *
   * @param appendTo a css selector defining the element where the helper will be added
   *                 <p/>
   *                 e.g. myDraggableWidget.setAppendTo("body");
   *                 myDraggableWidget.setAppendTo("#idOfContainer");
   */
  public void setAppendTo(String appendTo) {
    options.setAppendTo(appendTo);
  }

  /**
   * set the {@link AxisOption}
   *
   * @param axis
   */

  public void setAxis(AxisOption axis) {
    options.setAxis(axis);
  }

  /**
   * Prevents dragging from starting on specified elements.
   *
   * @param cancelSelectors array of css selectors defining cancel elements
   */
  public void setCancel(String... cancelSelectors) {
    options.setCancel(cancelSelectors);
  }

  /**
   * Constrains dragging to within the bounds of the specified region. The
   * region is defined by a array of integer : {left, top, right, bottom}
   *
   * @param containment array of int defining the region.
   *                    <p/>
   *                    e.g. options.setContainment(new int[] { 300, 500, 600, 800 })
   *                    Constrains the dragging in the following region : 300px from the
   *                    left of the document 500px from the top of the document 600px from
   *                    the right of the document 800px from the bottom of the document
   */
  public void setContainment(int[] containment) {
    options.setContainment(containment);
  }

  /**
   * Constrains dragging to within the bounds of the specified element (called
   * the container) defining by the selector.
   *
   * @param containment selector defining the container element. Possible string values: a
   *                    css selector 'parent' the container will be the parent element of
   *                    the element, 'document' the container will the document, 'window'
   *                    the container will be the browser area
   * @see sample at
   *      http://gwtquery-plugins.googlecode.com/svn/trunk/draggable/demo
   *      /DraggableSample2/DraggableSample2.html
   */
  public void setContainment(String containment) {
    options.setContainment(containment);
  }

  /**
   * Constrains dragging to within the bounds of the specified widget (called
   * the container) defining by the selector.
   *
   * @param containment Widget takes as the container element.
   */
  public void setContainment(Widget container) {
    GQuery $container = $(container.getElement());
    options.setContainment($container);
  }

  /**
   * Moves the dragging helper so the cursor always appears to drag from the
   * same position.
   *
   * @param cursorAt
   */
  public void setCursorAt(CursorAt cursorAt) {
    options.setCursorAt(cursorAt);
  }

  /**
   * Set the time in milliseconds to define when the drag should start.
   *
   * @param delay
   */
  public void setDelay(int delay) {
    options.setDelay(delay);
  }

  /**
   * Disables (true) or enables (false) the drag operation.
   *
   * @param disabled
   */
  public void setDisabledDrag(boolean disabled) {
    options.setDisabled(disabled);
  }

  /**
   * Set the tolerance, in pixels, for when the drag should start. If specified,
   * drag will not start until after mouse is dragged beyond distance.
   *
   * @param distance
   */
  public void setDistance(int distance) {
    options.setDistance(distance);
  }

  /**
   * Used to group sets of draggable and droppable widget, in addition to
   * droppable's accept option. A DraggableWidget with the same scope value than
   * a DroppableWidget will be accepted by this last.
   *
   * @param scope
   */
  public void setDragAndDropScope(String scope) {
    options.setScope(scope);
  }

  /**
   * Set the options for the draggable
   *
   * @param options
   */
  public void setDraggableOptions(DraggableOptions options) {
    this.options = options;
    if (isAttached()) {
      $(getElement()).as(Draggable).options(options);
    }
  }

  /**
   * Specify the css cursor to use during the drag operation.
   *
   * @param cursor
   */
  public void setDraggingCursor(Cursor cursor) {
    options.setCursor(cursor);
  }

  /**
   * Set the opacity of the helper during the drag.
   *
   * @param opacity a float between 0 and 1
   */
  public void setDraggingOpacity(Float opacity) {
    options.setOpacity(opacity);
  }

  /**
   * z-index for the helper while being dragged.
   *
   * @param zIndex
   */
  public void setDraggingZIndex(Integer zIndex) {
    options.setZIndex(zIndex);
  }

  /**
   * Snaps the dragging helper to a grid defining by the <code>grid</code>
   * parameter.
   *
   * @param grid array of int defining the dimension of the cell of the snapped
   *             grid.
   *             <p/>
   *             e.g. myDraggableWidget.setGrid(new int[]{40, 60}); The widget will
   *             be moved by 40 pixel horizontally and by 60px vertically
   */
  public void setGrid(int[] grid) {
    options.setGrid(grid);
  }

  public void setGroupingMode(GroupingMode groupingMode) {
    options.setGroupingMode(groupingMode);
  }

  public void setGroupSpacing(int spacing) {
    options.setGroupSpacing(spacing);
  }

  /**
   * If specified, restricts drag start when the user clicks on the specified
   * element(s).
   *
   * @param selector css selector defining element(s) allowing starting of the drag
   */
  public void setHandle(String selector) {
    options.setHandle(selector);
  }

  public void setMultipleSelection(boolean enabled) {
    options.setMultipleSelection(enabled);
  }

  /**
   * Set the revert options
   * <p/>
   * ALWAYS : the widget will return to its start position when dragging stops.
   * <p/>
   * NEVER : the widget will not return to its start position when dragging
   * stops.
   * <p/>
   * ON_VALID_DROP :revert will only occur if the widget has been dropped
   * (useful with droppable plug-in)
   * <p/>
   * ON_INVALID_DROP :revert will only occur if the widget has not been dropped
   * (useful with droppable plug-in)
   *
   * @param revert
   */
  public void setRevert(RevertOption revert) {
    options.setRevert(revert);
  }

  /**
   * The duration of the revert animation, in milliseconds.
   *
   * @param revertDuration
   */
  public void setRevertDuration(int revertDuration) {
    options.setRevertDuration(revertDuration);
  }

  /**
   * Define if the container scroll while dragging
   *
   * @param scroll
   */
  public void setScroll(boolean scroll) {
    options.setScroll(scroll);
  }

  /**
   * Distance in pixels from the edge of the viewport after which the viewport
   * should scroll. Distance is relative to pointer, not to the widget.
   *
   * @param scrollSensitivity
   */
  public void setScrollSensitivity(int scrollSensitivity) {
    options.setScrollSensitivity(scrollSensitivity);
  }

  /**
   * The speed at which the window should scroll once the mouse pointer gets
   * within the scrollSensitivity distance.
   *
   * @param scrollSpeed
   */
  public void setScrollSpeed(int scrollSpeed) {
    options.setScrollSpeed(scrollSpeed);
  }

  public void setSelectedClassName(String selectedClassName) {
    options.setSelectedClassName(selectedClassName);
  }


  public void setSelectFunction(SelectFunction selectFunction) {
    options.setSelect(selectFunction);
  }

  /**
   * Define if this DragableWidget will snap to the edges of the other
   * DraggableWidget when it is near an edge of these widget.
   *
   * @param snap
   */
  public void setSnap(boolean snap) {
    options.setSnap(snap);
  }

  /**
   * Define if this DragableWidget will snap to the edges of the other widget
   * when it is near an edge of these elements.
   *
   * @param snap
   */
  public void setSnap(List<Widget> snapWidgets) {
    if (snapWidgets == null) {
      return;
    }
    JsNodeArray snapElements = JsNodeArray.create();
    for (Widget w : snapWidgets) {
      snapElements.addNode(w.getElement());
    }
    options.setSnap($(snapElements));
  }

  /**
   * Determines which edges of snap widget the DraggableWidget will snap to.
   * Possible values: INNER, OUTER, BOTH
   *
   * @param snapMode
   */
  public void setSnapMode(SnapMode snapMode) {
    options.setSnapMode(snapMode);
  }

  /**
   * The distance in pixels from the snap widget edges at which snapping should
   * occur.
   *
   * @param snapTolerance
   */
  public void setSnapTolerance(int snapTolerance) {
    options.setSnapTolerance(snapTolerance);
  }

  /**
   * Controls the z-Index of the selected Widget during the dragging, always
   * brings to front the dragged item. Very useful in things like window
   * managers.
   *
   * @param stack
   */
  public void setStack(List<Widget> stackWidgets) {
    if (stackWidgets == null) {
      return;
    }
    JsNodeArray stackElements = JsNodeArray.create();
    for (Widget w : stackWidgets) {
      stackElements.addNode(w.getElement());
    }
    options.setStack($(stackElements));
  }

  /**
   * Controls the z-Index of the selected elements that match the selector,
   * always brings to front the dragged item. Very useful in things like window
   * managers.
   *
   * @param stack
   */
  public void setStack(String selector) {
    options.setStack(selector);
  }

  /**
   * A clone of the widget will Be used as dragging display during the drag
   * operation instead of moving the original widget
   */
  public void useCloneAsHelper() {
    options.setHelper(HelperType.CLONE);
  }

  /**
   * The widget will Be used as dragging display during the drag operation
   */
  public void useOriginalWidgetAsHelper() {
    options.setHelper(HelperType.ORIGINAL);
  }

  /**
   * THE WIDGET <code>widget</code> will be used as dragging display during the
   * drag operation instead of moving the original widget
   */
  public void useOtherWidgetAsHelper(Widget widget) {
    assert widget != null;
    options.setHelper(widget.getElement());
  }

  protected final <H extends EventHandler> HandlerRegistration addDragHandler(
      H handler, Type<H> type) {
    return ensureDragHandlers().addHandler(type, handler);
  }

  protected EventBus ensureDragHandlers() {
    return dragHandlerManager == null ? dragHandlerManager = new SimpleEventBus()
        : dragHandlerManager;
  }

  protected EventBus getDragHandlerManager() {
    return dragHandlerManager;
  }

  @Override
  protected void onLoad() {
    super.onLoad();
    $(getElement()).as(Draggable).draggable(options, ensureDragHandlers())
        .data(DRAGGABLE_WIDGET_KEY, this);
  }

  @Override
  protected void onUnload() {
    super.onUnload();
    $(getElement()).as(Draggable).destroy().removeData(DRAGGABLE_WIDGET_KEY);
  }

}
