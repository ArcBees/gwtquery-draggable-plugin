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
import static com.google.gwt.query.client.GQuery.body;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.GQuery.Offset;
import com.google.gwt.query.client.plugins.Effects;
import com.google.gwt.query.client.plugins.UiPlugin;
import com.google.gwt.query.client.plugins.UiPlugin.Dimension;
import com.google.gwt.query.client.plugins.effects.PropertiesAnimation.Easing;
import com.google.gwt.query.client.plugins.events.GqEvent;
import com.google.gwt.user.client.Window;

import static gwtquery.plugins.draggable.client.Draggable.DRAGGABLE_HANDLER_KEY;

import gwtquery.plugins.draggable.client.Draggable.CssClassNames;
import gwtquery.plugins.draggable.client.DraggableOptions.AxisOption;
import gwtquery.plugins.draggable.client.DraggableOptions.CursorAt;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.draggable.client.impl.DraggableHandlerImpl;

public class DraggableHandler {

  public static DraggableHandler getInstance(Element draggable) {
    return $(draggable).data(DRAGGABLE_HANDLER_KEY, DraggableHandler.class);
  }

  private DraggableHandlerImpl impl = GWT.create(DraggableHandlerImpl.class);

  private Offset margin;

  private Offset offset;
  private Offset absPosition;
  // from where the click happened relative to the draggable element
  private Offset offsetClick;
  private Offset parentOffset;
  private Offset relativeOffset;
  private int originalEventPageX;
  private int originalEventPageY;
  private Offset position;
  private Offset originalPosition;

  // info from helper
  private String helperCssPosition;
  private GQuery helperScrollParent;
  private GQuery helperOffsetParent;
  private int[] containment;
  private GQuery helper;
  private DraggableOptions options;
  private Dimension helperDimension;
  private boolean cancelHelperRemoval = false;

  // can be instantiate only by Draggable plugin
  DraggableHandler(DraggableOptions options) {
    this.options = options;
  }

  /**
   * convert a relative position to a absolute position and vice versa.
   * 
   * @param absolute if true the position is convert to an absolute position, if
   *          false it is convert in a relative position
   * @param aPosition position to convert
   * @return
   */
  public Offset convertPositionTo(boolean absolute, Offset aPosition) {
    int mod = absolute ? 1 : -1;
    GQuery scroll = getScrollParent();
    boolean scrollIsRootNode = isRootNode(scroll.get(0));

    int top = aPosition.top
        + relativeOffset.top
        * mod
        + parentOffset.top
        * mod
        - ("fixed".equals(helperCssPosition) ? -helperScrollParent.scrollTop()
            : scrollIsRootNode ? 0 : scroll.scrollTop()) * mod;

    int left = aPosition.left
        + relativeOffset.left
        * mod
        + parentOffset.left
        * mod
        - ("fixed".equals(helperCssPosition) ? -helperScrollParent.scrollLeft()
            : scrollIsRootNode ? 0 : scroll.scrollLeft()) * mod;

    return new Offset(left, top);

  }

  public int[] getContainment() {
    return containment;
  }

  public GQuery getHelper() {
    return helper;
  }

  public String getHelperCssPosition() {
    return helperCssPosition;
  }

  public Dimension getHelperDimension() {
    return helperDimension;
  }

  public GQuery getHelperOffsetParent() {
    return helperOffsetParent;
  }

  public GQuery getHelperScrollParent() {
    return helperScrollParent;
  }

  public Offset getMargin() {
    return margin;
  }

  public Offset getOffset() {
    return offset;
  }

  public Offset getOffsetClick() {
    return offsetClick;
  }

  public DraggableOptions getOptions() {
    return options;
  }

  public int getOriginalEventPageX() {
    return originalEventPageX;
  }

  public int getOriginalEventPageY() {
    return originalEventPageY;
  }

  public Offset getOriginalPosition() {
    return originalPosition;
  }

  public Offset getParentOffset() {
    return parentOffset;
  }

  public Offset getPosition() {
    return position;
  }

  public Offset getAbsolutePosition() {
    return absPosition;
  }

  public Offset getRelativeOffset() {
    return relativeOffset;
  }

  public void initialize(Element element, GqEvent e) {

    helperCssPosition = helper.css("position");
    helperScrollParent = helper.as(UiPlugin.GQueryUi).scrollParent();
    helperOffsetParent = helper.offsetParent();

    if ("html".equalsIgnoreCase(helperOffsetParent.get(0).getTagName())) {
      helperOffsetParent = $(body);
    }

    setMarginCache(element);

    absPosition = new Offset(element.getAbsoluteLeft(),
        element.getAbsoluteTop());

    offset = new Offset(absPosition.left - margin.left, absPosition.top
        - margin.top);

    offsetClick = new Offset(e.pageX() - offset.left, e.pageY() - offset.top);

    parentOffset = calculateParentOffset(element);
    relativeOffset = calculateRelativeHelperOffset(element);

    originalEventPageX = e.pageX();
    originalEventPageY = e.pageY();

    position = calculateOriginalPosition(element, e);
    originalPosition = new Offset(position.left, position.top);

    if (options.getCursorAt() != null) {
      adjustOffsetFromHelper(options.getCursorAt());
    }
    calculateContainment();

  }

  private Offset calculateOriginalPosition(Element element, GqEvent e) {
    if (HelperType.ORIGINAL == options.getHelperType()) {
      return impl.getCssPosition(element);
    } else {
      return generatePosition(e, true);
    }
  }

  public boolean isRootNode(Element e) {
    return "html".equalsIgnoreCase(e.getTagName()) || e == body;
  }

  /**
   * 
   * @param firstTime if true, the helper has to be positionned without take
   *          care to the axis options
   */
  public void moveHelper(boolean firstTime) {
    if (helper == null || helper.size() == 0) {
      return;
    }
    AxisOption axis = options.getAxis();
    if (AxisOption.NONE == axis || AxisOption.X_AXIS == axis || firstTime) {
      helper.get(0).getStyle().setLeft(position.left, Unit.PX);
    }
    if (AxisOption.NONE == axis || AxisOption.Y_AXIS == axis || firstTime) {
      helper.get(0).getStyle().setTop(position.top, Unit.PX);
    }
  }

  public void regeneratePositions(GqEvent e) {
    position = generatePosition(e, false);
    offset = convertPositionTo(true, position);
    absPosition = offset.add(margin.left, margin.top);
  }

  public void revertToOriginalPosition(Function function) {
    Properties oldPosition = Properties.create("{top:'"
        + String.valueOf(originalPosition.top) + "px',left:'"
        + String.valueOf(originalPosition.left) + "px'}");
    helper.as(Effects.Effects).animate(oldPosition,
        options.getRevertDuration(), Easing.LINEAR, function);

  }

  public void setHelperDimension(Dimension helperDimension) {
    this.helperDimension = helperDimension;
  }

  public void setMarginCache(Element element) {
    int marginLeft = (int) GQuery.$(element).cur("marginLeft", true);
    int marginTop = (int) GQuery.$(element).cur("marginTop", true);

    margin = new Offset(marginLeft, marginTop);

  }

  public void setOptions(DraggableOptions options) {
    this.options = options;

  }

  public void setPosition(Offset Offset) {
    position = Offset;

  }

  public void setOffsetClick(Offset offsetClick) {
    this.offsetClick = offsetClick;
  }

  void cacheHelperSize() {
    if (helper != null) {
      setHelperDimension(new Dimension(helper.get(0)));
    }

  }

  void clear(Element draggable) {
    if (helper == null) {
      return;
    }
    helper.removeClass(CssClassNames.GWT_DRAGGABLE_DRAGGING);
    if (HelperType.ORIGINAL != options.getHelperType() && !cancelHelperRemoval) {
      impl.removeHelper(helper, options.getHelperType());
    }
    helper = null;
    cancelHelperRemoval = false;

  }

  void createHelper(Element draggable, GqEvent e) {
    helper = options.getHelperType().createHelper(draggable,
        options.getHelper());
    if (!isElementAttached(helper)) {
      if ("parent".equals(options.getAppendTo())) {
        helper.appendTo(draggable.getParentNode());
      } else {
        helper.appendTo(options.getAppendTo());
      }
    }

    if (options.getHelperType() != HelperType.ORIGINAL
        && !helper.css("position").matches("(fixed|absolute)")) {
      helper.css("position", Position.ABSOLUTE.getCssName());
    }

  }

  private void adjustOffsetFromHelper(CursorAt cursorAt) {

    if (cursorAt.getLeft() != null) {
      offsetClick.left = cursorAt.getLeft().intValue() + margin.left;
    }

    if (cursorAt.getRight() != null) {
      offsetClick.left = helperDimension.getWidth()
          - cursorAt.getRight().intValue() + margin.left;
    }

    if (cursorAt.getTop() != null) {
      offsetClick.top = cursorAt.getTop().intValue() + margin.top;
    }

    if (cursorAt.getBottom() != null) {
      offsetClick.top = helperDimension.getHeight()
          - cursorAt.getBottom().intValue() + margin.top;
    }
  }

  private void calculateContainment() {
    String containmentAsString = options.getContainment();
    int[] containmentAsArray = options.getContainmentAsArray();
    GQuery $containement = options.getContainmentAsGQuery();

    if (containmentAsArray == null && containmentAsString == null
        && $containement == null) {
      containment = null;
      return;
    }

    if (containmentAsArray != null) {
      containment = containmentAsArray;
      return;
    }

    if (containmentAsString != null) {
      if ("window".equals(containmentAsString)) {
        containment = new int[]{
            0 /*- relativeOffset.left - parentOffset.left*/,
            0 /*- relativeOffset.top - parentOffset.top*/,
            Window.getClientWidth() - helperDimension.getWidth() - margin.left,
            Window.getClientHeight() - helperDimension.getHeight() - margin.top};

        return;
      }

      if ("parent".equals(containmentAsString)) {
        $containement = $(helper.get(0).getParentElement());
      } else if ("document".equals(containmentAsString)) {
        $containement = $("body");
      } else {
        $containement = $(containmentAsString);
      }
    }

    Element ce = $containement.get(0);
    if (ce == null) {
      return;
    }

    containment = impl.calculateContainment(this, $containement.offset(), ce,
        (!"hidden".equals($containement.css("overflow"))));

  }

  private Offset calculateParentOffset(Element element) {
    Offset position = helperOffsetParent.offset();

    if ("absolute".equals(helperCssPosition)
        && isOffsetParentIncludedInScrollParent()) {
      position = position.add(helperScrollParent.scrollLeft(),
          helperScrollParent.scrollTop());
    }

    if (impl.resetParentOffsetPosition(helperOffsetParent)) {
      position.left = 0;
      position.top = 0;
    }

    position = position.add((int) helperOffsetParent.cur("borderLeftWidth",
        true), (int) helperOffsetParent.cur("borderTopWidth", true));

    return new Offset(position.left, position.top);

  }

  /*
   * This is a relative to absolute position minus the actual position
   * calculation - only used for relative positioned helper
   */
  private Offset calculateRelativeHelperOffset(Element element) {
    if ("relative".equals(helperCssPosition)) {
      return impl.calculateRelativeHelperOffset(element, this);
    }
    return new Offset(0, 0);
  }

  private Offset generatePosition(GqEvent e, boolean initPosition) {

    GQuery scroll = getScrollParent();
    boolean scrollIsRootNode = isRootNode(scroll.get(0));

    int pageX = e.pageX();
    int pageY = e.pageY();

    if (!initPosition) {
      if (containment != null && containment.length == 4) {
        if (e.pageX() - offsetClick.left < containment[0]) {
          pageX = containment[0] + offsetClick.left;
        }
        if (e.pageY() - offsetClick.top < containment[1]) {
          pageY = containment[1] + offsetClick.top;
        }
        if (e.pageX() - offsetClick.left > containment[2]) {
          pageX = containment[2] + offsetClick.left;
        }
        if (e.pageY() - offsetClick.top > containment[3]) {
          pageY = containment[3] + offsetClick.top;
        }
      }

      if (options.getGrid() != null) {
        int[] grid = options.getGrid();
        int roundedTop = originalEventPageY
            + Math.round((pageY - originalEventPageY) / grid[1]) * grid[1];
        int roundedLeft = originalEventPageX
            + Math.round((pageX - originalEventPageX) / grid[0]) * grid[0];

        if (containment != null && containment.length == 4) {
          boolean isOutOfContainment0 = roundedLeft - offsetClick.left < containment[0];
          boolean isOutOfContainment1 = roundedTop - offsetClick.top < containment[1];
          boolean isOutOfContainment2 = roundedLeft - offsetClick.left > containment[2];
          boolean isOutOfContainment3 = roundedTop - offsetClick.top > containment[3];

          pageY = !(isOutOfContainment1 || isOutOfContainment3) ? roundedTop
              : (!isOutOfContainment1) ? roundedTop - grid[1] : roundedTop
                  + grid[1];
          pageX = !(isOutOfContainment0 || isOutOfContainment2) ? roundedLeft
              : (!isOutOfContainment0) ? roundedLeft - grid[0] : roundedLeft
                  + grid[0];

        } else {
          pageY = roundedTop;
          pageX = roundedLeft;
        }

      }
    }

    int top = pageY
        - offsetClick.top
        - relativeOffset.top
        - parentOffset.top
        + ("fixed".equals(helperCssPosition) ? -helperScrollParent.scrollTop()
            : scrollIsRootNode ? 0 : scroll.scrollTop());

    int left = pageX
        - offsetClick.left
        - relativeOffset.left
        - parentOffset.left
        + ("fixed".equals(helperCssPosition) ? -helperScrollParent.scrollLeft()
            : scrollIsRootNode ? 0 : scroll.scrollLeft());
    return new Offset(left, top);
  }

  private GQuery getScrollParent() {
    if ("absolute".equals(helperCssPosition)
        && !(isOffsetParentIncludedInScrollParent())) {
      return helperOffsetParent;
    } else {
      return helperScrollParent;
    }
  }

  private boolean isElementAttached(GQuery $element) {
    // normally this test helper.parents().filter("body").length() == 0 is
    // sufficient but they are a bug in gwtquery in filter function
    // return helper.parents().filter("body").length() == 0;
    GQuery parents = $element.parents();
    for (Element parent : parents.elements()) {
      if (parent == body) {
        return true;
      }
    }
    return false;
  }

  private boolean isOffsetParentIncludedInScrollParent() {
    assert helperOffsetParent != null && helperScrollParent != null;
    return !"html".equalsIgnoreCase(helperScrollParent.get(0).getTagName())
        && UiPlugin.contains(helperScrollParent.get(0),
            helperOffsetParent.get(0));
  }

}
