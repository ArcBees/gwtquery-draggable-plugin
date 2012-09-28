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
package gwtquery.plugins.draggable.client;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.plugins.MouseOptions;
import com.google.gwt.user.client.ui.Widget;

import gwtquery.plugins.draggable.client.Draggable.CssClassNames;
import gwtquery.plugins.draggable.client.events.DragContext;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;

import java.util.List;

/**
 * This class is used to configure the drag operation.
 * 
 * See <a href=
 * "http://code.google.com/p/gwtquery-plugins/wiki/DraggablePluginGettingStarted"
 * >the wiki page</a> for more documentation and samples
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public class DraggableOptions extends MouseOptions {

  /**
   * Axis options. Constraint the drag operation to either the horizontal (
   * <code>X_AXIS</code>) or vertical (<code>Y_AXIS</code>) axis.
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   * 
   */
  public static enum AxisOption {
    /**
     * No axis constraint
     */
    NONE,

    /**
     * X_AXIS constraints the drag operation to the horizontal axis
     */
    X_AXIS,

    /**
     * Y_AXIS constraints the drag operation to the vertical axis
     */
    Y_AXIS;
  }

  /**
   * Object used to specify the cursorAt options. This options allows you to set
   * the offset of the dragging helper relative to the mouse cursor.
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   * 
   */
  public static class CursorAt {

    private Integer bottom;
    private Integer left;
    private Integer right;
    private Integer top;

    /**
     * Specify coordinates by giving one or two parameters. If you define more
     * than two argument, the bottom parameter has priority over the top
     * parameter and the right parameter has priority over left parameter
     * 
     * @param top
     * @param left
     * @param bottom
     * @param right
     */
    public CursorAt(Integer top, Integer left, Integer bottom, Integer right) {
      if (bottom != null) {
        this.bottom = bottom;
      } else if (top != null) {
        this.top = top;
      }

      if (right != null) {
        this.right = right;
      } else if (left != null) {
        this.left = left;
      }
    }

    public Integer getBottom() {
      return bottom;
    }

    public Integer getLeft() {
      return left;
    }

    public Integer getRight() {
      return right;
    }

    public Integer getTop() {
      return top;
    }

  }

  /**
   * Object use as callback function
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   * 
   */
  public interface DragFunction {
    public void f(DragContext context);
  }

  /**
   * Determine the mode used to group selected draggable during a drag operation
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   */
  public static enum GroupingMode {
    /**
     * place the helper under the initial current helper
     */
    DOWN,
    /**
     * place the helper to the left of the initial current helper
     */
    LEFT,
    /**
     * No grouping
     */
    NONE,
    /**
     * place the helper to the right of the initial current helper
     */
    RIGHT,
    /**
     * place the helper above the initial current helper
     */
    UP;

  }

  /**
   * Specify which element is used as dragging display
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   * 
   */
  public static enum HelperType {
    /**
     * CLONE : a clone of the original element will be used as dragging display
     */
    CLONE {
      @Override
      public GQuery createHelper(Element original, GQuery helperFromOptions) {
        return $(original).clone();
      }
    },

    /**
     * ORIGINAL : an other element will be used as dragging display
     */
    ELEMENT {
      @Override
      public GQuery createHelper(Element original, GQuery helperFromOptions) {
        return helperFromOptions;
      }
    },

    /**
     * ORIGINAL : the original element will be used as dragging display
     */
    ORIGINAL {
      @Override
      public GQuery createHelper(Element original, GQuery helperFromOptions) {
        return $(original);
      }
    };

    public abstract GQuery createHelper(Element original,
        GQuery helperFromOptions);
  }

  /**
   * Define what the dragging helper do a the end of the drag operation.
   */
  public static enum RevertOption {
    /**
     * ALWAYS : the helper will return to its start position when dragging
     * stops.
     */
    ALWAYS,

    /**
     * NEVER : the draggable will not return to its start position when dragging
     * stops.
     */
    NEVER,

    /**
     * ON_INVALID_DROP :revert will only occur if the draggable has not been
     * dropped (useful with droppable plug-in)
     */
    ON_INVALID_DROP,

    /**
     * ON_VALID_DROP : revert will only occur if the draggable has been dropped
     * (useful with droppable plug-in)
     */
    ON_VALID_DROP;

    public boolean doRevert(boolean dropped) {
      return this == ALWAYS || (this == ON_INVALID_DROP && !dropped)
          || (this == ON_VALID_DROP && dropped);
    }
  }

  /**
   * Function object used to select other draggable elements that will follow
   * the initial draggable when this one will drag.
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   * 
   */
  public class SelectFunction {
    public GQuery selectElements() {
      return $(selectWidgets().toArray(new Widget[0]));
    }

    public List<DraggableWidget<?>> selectWidgets() {
      throw new RuntimeException(
          "You must override at least selectElements() function or selectWidgets() function");
    }
  }

  /**
   * Determines which edges of snap elements the helper will snap to.
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   * 
   */
  public static enum SnapMode {
    /**
     * The helper will snap the inner and the outer edges of snap elements.
     */
    BOTH,

    /**
     * The helper will snap the inner edges of snap elements.
     */
    INNER,

    /**
     * The helper will snap the outer edges of snap elements.
     */
    OUTER;
  }

  public static final String DEFAULT_SCOPE = "default";

  private GQuery $containment;
  private GQuery $snap;
  private String appendTo;
  private AxisOption axis;
  private String containment;
  // private boolean connectToSortable;
  private int[] containmentAsArray;
  private Cursor cursor;
  private CursorAt cursorAt;
  private boolean disabled;
  private int[] grid;
  private GroupingMode groupingMode;
  private int groupSpacing;
  private String handle;
  private GQuery helper;
  private HelperType helperType;
  private boolean multipleSelection;
  private DragFunction onBeforeDragStart;
  private DragFunction onDrag;
  private DragFunction onDragStart;
  private DragFunction onDragStop;
  private Function onSelected;
  private Function onUnselected;
  private Float opacity;
  private RevertOption revert;
  private int revertDuration;
  private String scope;
  private boolean scroll;
  private int scrollSensitivity;
  private int scrollSpeed;
  private String selectedClassName;
  private SelectFunction selectFunction;
  private String snap;
  private SnapMode snapMode;
  private int snapTolerance;
  private GQuery stack;
  private Integer zIndex;

  /**
   * @return the css selector used to select the element where the helper will
   *         be appended
   */
  public String getAppendTo() {
    return appendTo;
  }

  /**
   * 
   * @return the {@link AxisOption}
   */
  public AxisOption getAxis() {
    if (axis == null) {
      return AxisOption.NONE;
    }
    return axis;
  }

  /**
   * 
   * @return the css selector used to select the element that will constraint
   *         dragging within it
   */
  public String getContainment() {
    return containment;
  }

  /**
   * 
   * @return an array of 4 int determining the area used to constraint dragging
   *         within it
   */
  public int[] getContainmentAsArray() {
    return containmentAsArray;
  }

  /**
   * 
   * @return the {@link GQuery} object used to select the element that will
   *         constraint dragging within it
   */
  public GQuery getContainmentAsGQuery() {
    return $containment;
  }

  /**
   * 
   * @return the {@link Cursor} used during the drag operation
   */
  public Cursor getCursor() {
    return cursor;
  }

  /**
   * 
   * @return the {@link CursorAt}
   */
  public CursorAt getCursorAt() {
    return cursorAt;
  }

  /**
   * 
   * @return The array of int defining the dimension of the cell of the snap
   *         grid.
   */
  public int[] getGrid() {
    return grid;
  }

  /**
   * @return the {@link GroupingMode}
   * 
   */
  public GroupingMode getGroupingMode() {
    return groupingMode;
  }

  public int getGroupSpacing() {
    return groupSpacing;
  }

  /**
   * 
   * @return the css selector used to select the element used as drag handle.
   *         The drag operations starts only when the user clicks on it
   */
  public String getHandle() {
    return handle;
  }

  /**
   * 
   * @return the {@link GQuery} object used as dragging display
   */
  public GQuery getHelper() {
    return helper;
  }

  /**
   * 
   * @return the {@link HelperType}
   */
  public HelperType getHelperType() {
    return helperType;
  }

  /**
   * 
   * @return the {@link DragFunction} called before the drag start
   */
  public DragFunction getOnBeforeDragStart() {
    return onBeforeDragStart;
  }

  /**
   * 
   * @return the {@link DragFunction} called when the helper is being dragged
   */
  public DragFunction getOnDrag() {
    return onDrag;
  }

  /**
   * 
   * @return the {@link DragFunction} called when the drag start
   */
  public DragFunction getOnDragStart() {
    return onDragStart;
  }

  /**
   * 
   * @return the {@link DragFunction} called before the drag stop
   */
  public DragFunction getOnDragStop() {
    return onDragStop;
  }

  /**
   * @return the function called when a draggable is selected
   * 
   */
  public Function getOnSelected() {
    return onSelected;
  }

  /**
   * @return the function called when a draggable is unselected
   * 
   */
  public Function getOnUnselected() {
    return onUnselected;    
  }

  /**
   * 
   * @return the value of the opacity set to the helper while being dragged
   */
  public Float getOpacity() {
    return opacity;
  }

  /**
   * 
   * @return the {@link RevertOption}
   */
  public RevertOption getRevert() {
    return revert;
  }

  /**
   * 
   * @return The duration of the revert animation, in milliseconds.
   */
  public int getRevertDuration() {
    return revertDuration;
  }

  /**
   * 
   * @return the scope of the draggable. The scope is used to group sets of
   *         draggable and droppable items, in addition to droppable's accept
   *         option. A draggable with the same scope value as a droppable will
   *         be accepted by the droppable.
   */
  public String getScope() {
    return scope;
  }

  /**
   * 
   * @return the distance in pixels from the edge of the viewport after which
   *         the viewport should scroll. Distance is relative to pointer, not to
   *         the draggable.
   */
  public int getScrollSensitivity() {
    return scrollSensitivity;
  }

  /**
   * 
   * @return The speed at which the window should scroll once the mouse pointer
   *         gets within the scrollSensitivity distance.
   */
  public int getScrollSpeed() {
    return scrollSpeed;
  }

  public SelectFunction getSelect() {
    return selectFunction;
  }

  /**
   * @return Css class which will be added to the draggable when it will be
   *         selected
   */
  public String getSelectedClassName() {
    return selectedClassName;
  }

  /**
   * 
   * @return the css selector used to identify the snap elements.
   */
  public String getSnap() {
    return snap;
  }

  /**
   * 
   * @return the {@link GQuery} used to identify the snap elements.
   */
  public GQuery getSnap_$() {
    return $snap;
  }

  /**
   * 
   * @return the {@link SnapMode}
   */
  public SnapMode getSnapMode() {
    return snapMode;
  }

  /**
   * 
   * @return The distance in pixels from the snap element edges at which
   *         snapping should occur.
   */
  public int getSnapTolerance() {
    return snapTolerance;
  }

  /**
   * 
   * @return the {@link GQuery}
   */
  public GQuery getStack() {
    return stack;
  }

  /**
   * 
   * @return return the z-index set to the helper while being dragged
   */
  public Integer getZIndex() {
    return zIndex;
  }

  /**
   * 
   * @return if the drag is disabled.
   */
  public boolean isDisabled() {
    return disabled;
  }

  /**
   * 
   * @return if the draggable can be part of a selection for a multi-drag
   */
  public boolean isMultipleSelection() {
    return multipleSelection;
  }

  /**
   * 
   * @return if the container scroll while dragging
   */
  public boolean isScroll() {
    return scroll;
  }

  /**
   * 
   * @return if the helper will snap the edges of the other draggable elements.
   */
  public boolean isSnap() {
    return snap != null;
  }

  /**
   * The element selected by the appendTo option will be used as the draggable
   * helper's container during dragging. By default, the helper is appended to
   * the same container as the draggable.
   * 
   * @param appendTo
   *          the selector defining the element where the helper will be added
   */
  public void setAppendTo(String appendTo) {
    this.appendTo = appendTo;
  }

  /**
   * set the {@link AxisOption}
   * 
   * @param axis
   */
  public void setAxis(AxisOption axis) {
    this.axis = axis;
  }

  /**
   * Constrains dragging to within the bounds of the specified element (called
   * the container) defining by the selector.
   * 
   * @param containment
   *          selector defining the container element. Possible string values: a
   *          css selector 'parent' the container will be the parent element of
   *          the element, 'document' the container will the document, 'window'
   *          the container will be the browser area
   * @see sample at
   *      http://gwtquery-plugins.googlecode.com/svn/trunk/draggable/demo
   *      /DraggableSample2/DraggableSample2.html
   * 
   */
  public void setContainment(GQuery containment) {
    this.$containment = containment;
    // mutually exclusive
    this.containmentAsArray = null;
    this.containment = null;
  }

  /**
   * Constrains dragging to within the bounds of the specified region. The
   * region is defined by a array of integer : {left, top, right, bottom}
   * 
   * @param containment
   *          array of int defining the region.
   * 
   *          e.g. options.setContainment(new int[] { 300, 500, 600, 800 })
   *          Constrains the dragging in the following region : 300px from the
   *          left of the document 500px from the top of the document 600px from
   *          the right of the document 800px from the bottom of the document
   */
  public void setContainment(int[] containment) {
    this.containmentAsArray = containment;
    // mutually exclusive
    this.containment = null;
  }

  /**
   * Constrains dragging to within the bounds of the specified element (called
   * the container) defining by the selector.
   * 
   * @param containment
   *          selector defining the container element. Possible string values: a
   *          css selector 'parent' the container will be the parent element of
   *          the element, 'document' the container will the document, 'window'
   *          the container will be the browser area
   * @see sample at
   *      http://gwtquery-plugins.googlecode.com/svn/trunk/draggable/demo
   *      /DraggableSample2/DraggableSample2.html
   * 
   */
  public void setContainment(String containment) {
    this.containment = containment;
    // mutually exclusive
    this.containmentAsArray = null;
  }

  /**
   * Specify the css cursor to use during the drag operation.
   * 
   * @param cursor
   */
  public void setCursor(Cursor cursor) {
    this.cursor = cursor;
  }

  /**
   * set the {@link CursorAt}
   * 
   * @param cursorAt
   */
  public void setCursorAt(CursorAt cursorAt) {
    this.cursorAt = cursorAt;
  }

  /**
   * Disables (true) or enables (false) the drag operation.
   * 
   * @param disabled
   */
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  /**
   * Snaps the dragging helper to a grid defining by the <code>grid</code>
   * parameter.
   * 
   * @param grid
   *          array of int defining the dimension of the cell of the snapped
   *          grid.
   * 
   *          e.g. options.setGrid(new int[]{40, 60}); The draggable will be
   *          moved by 40 pixel horizontally and by 60px vertically
   * 
   */
  public void setGrid(int[] grid) {
    this.grid = grid;
  }

  /**
   * set the {@link GroupingMode}
   * 
   * @param groupingMode
   */
  public void setGroupingMode(GroupingMode groupingMode) {
    this.groupingMode = groupingMode;
  }

  /**
   * Set the space in px applied when it will be grouped to other draggable
   * 
   * @param groupSpacing
   */
  public void setGroupSpacing(int groupSpacing) {
    this.groupSpacing = groupSpacing;
  }

  /**
   * If specified, restricts drag start when the user clicks on the specified
   * element(s).
   * 
   * @param selector
   *          css selector defining element(s) allowing starting of the drag
   */
  public void setHandle(String selector) {
    this.handle = selector;
  }

  /**
   * Set an element as helper. This element will be used as dragging display
   * during the drag operation instead of the original draggable.
   * 
   * If the element is not attached to the DOM, it will be attached at the start
   * of the drag and removed after.
   * 
   * @param helper
   *          element used as helper during the drag
   */
  public void setHelper(Element helper) {
    this.helper = $(helper);
    this.helperType = HelperType.ELEMENT;
  }

  /**
   * Set the first element of the GQuery object as helper. This element will be
   * used as dragging display during the drag operation instead of the original
   * draggable.
   * 
   * If the element is not attached to the DOM, it will be attached at the start
   * of the drag and removed after.
   * 
   * @param helper
   *          GQuery object containing the element that will be used as helper
   *          during the drag
   */
  public void setHelper(GQuery helper) {
    this.helper = helper;
    this.helperType = HelperType.ELEMENT;
  }

  /**
   * Set the {@link HelperType}
   * 
   * @param helperType
   *          the helper type
   */
  public void setHelper(HelperType helperType) {
    this.helperType = helperType;
  }

  /**
   * Specify a css selector defining the element that will be used as helper.
   * This element will be used as dragging display during the drag operation
   * instead of the original draggable.
   * 
   * If the element is not attached to the DOM, it will be attached at the start
   * of the drag and removed after.
   * 
   * @param helper
   *          css selector defining the element that will be used as helper
   *          during the drag
   */
  public void setHelper(String selector) {
    this.helper = $(helper);
    this.helperType = HelperType.ELEMENT;
  }

  /**
   * 
   * @param multipleSelection
   */
  public void setMultipleSelection(boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }

  /**
   * Set the {@link DragFunction} called before the initialization of the drag
   * operation.
   * 
   * @param onDragStart
   *          function called before the initialization of the drag operation.
   * 
   */
  public void setOnBeforeDragStart(DragFunction onBeforeDragStart) {
    this.onBeforeDragStart = onBeforeDragStart;
  }

  /**
   * Set the {@link DragFunction} called when the drag operation is dragging.
   * 
   * @param onDragStart
   *          function called when the drag operation is dragging.
   */
  public void setOnDrag(DragFunction onDrag) {
    this.onDrag = onDrag;
  }

  /**
   * Set the {@link DragFunction} called when the drag operation starts.
   * 
   * @param onDragStart
   *          function called when the drag operation starts.
   */
  public void setOnDragStart(DragFunction onDragStart) {
    this.onDragStart = onDragStart;
  }

  /**
   * Set the {@link DragFunction} called when the drag operation ends.
   * 
   * @param onDragStart
   *          function called when the drag operation ends.
   */
  public void setOnDragStop(DragFunction onDragStop) {
    this.onDragStop = onDragStop;
  }

  /**
   * Set the {@link Function} called when a draggable is selected.
   * 
   * @param onSelected
   *          callback function called when a draggable is selected.
   */
  public void setOnSelected(Function onSelected) {
    this.onSelected = onSelected;
  }

  /**
   * Set the {@link Function} called when a draggable is unselected.
   * 
   * @param onUnselected
   *          callback function called when a draggable is unselected.
   */
  public void setOnUnselected(Function onUnselected) {
    this.onUnselected = onUnselected;
  }

  /**
   * Set the opacity of the helper during the drag.
   * 
   * @param opacity
   *          a float between 0 and 1
   */
  public void setOpacity(Float opacity) {
    this.opacity = opacity;
  }

  /**
   * Set the {@link RevertOption}
   * 
   * @param revert
   */
  public void setRevert(RevertOption revert) {
    this.revert = revert;
  }

  /**
   * The duration of the revert animation, in milliseconds.
   * 
   * @param revertDuration
   */
  public void setRevertDuration(int revertDuration) {
    this.revertDuration = revertDuration;
  }

  /**
   * Used to group sets of draggable and droppable items, in addition to
   * droppable's accept option. A draggable with the same scope value as a
   * droppable will be accepted by the droppable.
   * 
   * @param scope
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

  /**
   * Define if the container scroll while dragging
   * 
   * @param scroll
   */
  public void setScroll(boolean scroll) {
    this.scroll = scroll;
  }

  /**
   * Distance in pixels from the edge of the viewport after which the viewport
   * should scroll. Distance is relative to pointer, not to the draggable.
   * 
   * @param scrollSensitivity
   */
  public void setScrollSensitivity(int scrollSensitivity) {
    this.scrollSensitivity = scrollSensitivity;
  }

  /**
   * The speed at which the window should scroll once the mouse pointer gets
   * within the scrollSensitivity distance.
   * 
   * @param scrollSpeed
   */
  public void setScrollSpeed(int scrollSpeed) {
    this.scrollSpeed = scrollSpeed;
  }

  public void setSelect(final GQuery query) {
    selectFunction = new SelectFunction() {
      @Override
      public GQuery selectElements() {
        return query;
      }
    };
  }

  public void setSelect(SelectFunction selectFunction) {
    this.selectFunction = selectFunction;
  }

  public void setSelect(final String selector) {
    selectFunction = new SelectFunction() {
      @Override
      public GQuery selectElements() {
        return $(selector);
      }
    };
  }

  /**
   * Set the css class which will be added to the draggable when it will be
   * selected
   */
  public void setSelectedClassName(String selectedClassName) {
    this.selectedClassName = selectedClassName;
  }

  /**
   * Define if the draggable will snap to the edges of the other draggable
   * elements when it is near an edge of these elements.
   * 
   * @param snap
   */
  public void setSnap(boolean snap) {
    if (snap) {
      this.snap = "." + CssClassNames.GWT_DRAGGABLE;
    } else {
      this.snap = null;
    }
  }

  /**
   * Define snap elements by a GQuery object. The draggable will snap to the
   * edges of the selected elements when near an edge of the element.
   * 
   * @param $
   */
  public void setSnap(GQuery $) {
    $snap = $;
    snap = null;

  }

  /**
   * Define snap element as being the selected elements that match the selector.
   * The draggable will snap to the edges of the selected elements when near an
   * edge of the element.
   * 
   * @param snapSelector
   */
  public void setSnap(String snapSelector) {
    this.snap = snapSelector;
    $snap = null;
  }

  /**
   * Determines which edges of snap elements the draggable will snap to.
   * Possible values: INNER, OUTER, BOTH
   * 
   * @param snapMode
   */
  public void setSnapMode(SnapMode snapMode) {
    this.snapMode = snapMode;
  }

  /**
   * The distance in pixels from the snap element edges at which snapping should
   * occur.
   * 
   * @param snapTolerance
   */
  public void setSnapTolerance(int snapTolerance) {
    this.snapTolerance = snapTolerance;
  }

  /**
   * Controls the z-Index of the selected elements, always brings to front the
   * dragged item. Very useful in things like window managers.
   * 
   * @param stack
   */
  public void setStack(GQuery stack) {
    this.stack = stack;
  }

  /**
   * Controls the z-Index of the selected elements that match the selector,
   * always brings to front the dragged item. Very useful in things like window
   * managers.
   * 
   * @param stack
   */
  public void setStack(String selector) {
    this.stack = $(selector);
  }

  /**
   * z-index for the helper while being dragged.
   * 
   * @param zIndex
   */
  public void setZIndex(Integer zIndex) {
    this.zIndex = zIndex;
  }

  @Override
  protected void initDefault() {
    super.initDefault();
    appendTo = "parent";
    axis = AxisOption.NONE;
    revert = RevertOption.NEVER;
    cursor = Cursor.AUTO;
    helperType = HelperType.ORIGINAL;
    scope = DEFAULT_SCOPE;
    revertDuration = 500;
    scroll = true;
    scrollSensitivity = 20;
    scrollSpeed = 20;
    snapMode = SnapMode.BOTH;
    snapTolerance = 20;
    multipleSelection = false;
    selectedClassName = null;
    groupingMode = GroupingMode.NONE;
    groupSpacing = 2;
  }

}
