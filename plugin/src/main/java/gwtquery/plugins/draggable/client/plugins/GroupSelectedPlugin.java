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
package gwtquery.plugins.draggable.client.plugins;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.GQuery.Offset;
import com.google.gwt.query.client.plugins.events.GqEvent;

import gwtquery.plugins.draggable.client.DraggableHandler;
import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.GroupingMode;
import gwtquery.plugins.draggable.client.events.DragContext;

/**
 * Plugin used when multi-draggable is on to group helper together.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public class GroupSelectedPlugin implements DraggablePlugin {

  private static class HelperInfo {
    private int height;

    private Offset offset;
    private int width;

    public HelperInfo(Offset offset, GQuery helper) {
      this.offset = offset;
      this.height = helper.outerHeight();
      this.width = helper.outerWidth();
    }

  }

  private static String LAST_DOWN = "__GroupPluginLastDown";
  private static String LAST_LEFT = "__GroupPluginLastLeft";
  private static String LAST_RIGHT = "__GroupPluginLastRight";
  private static String LAST_UP = "__GroupPluginLastUp";

  public String getName() {
    return "GroupSelectedPlugin";
  }

  public boolean hasToBeExecuted(DraggableOptions options) {
    return options.getGroupingMode() != null;
  }

  public void onDrag(DraggableHandler handler, DragContext ctx, GqEvent e) {
  }

  public void onStart(DraggableHandler handler, DragContext ctx, GqEvent e) {

    Element initialDraggable = ctx.getInitialDraggable();

    if (initialDraggable == ctx.getDraggable()) {
      return;
    }

    DraggableHandler initialDragHandler = DraggableHandler
        .getInstance(initialDraggable);
    GQuery $initialDraggable = $(initialDraggable);

    GroupingMode groupingMode = handler.getOptions().getGroupingMode();
    int groupSpacing = handler.getOptions().getGroupSpacing();

    Offset newPosition = null;

    switch (groupingMode) {
    case DOWN:
      HelperInfo lastDown = $initialDraggable.data(LAST_DOWN, HelperInfo.class);
      if (lastDown == null) {
        lastDown = new HelperInfo(initialDragHandler.getAbsolutePosition(),
            initialDragHandler.getHelper());
      }

      newPosition = lastDown.offset.add(0, lastDown.height + groupSpacing);
      HelperInfo newLastDown = new HelperInfo(newPosition, handler.getHelper());
      $initialDraggable.data(LAST_DOWN, newLastDown);

      break;

    case UP:
      HelperInfo lastUp = $initialDraggable.data(LAST_UP, HelperInfo.class);
      if (lastUp == null) {
        lastUp = new HelperInfo(initialDragHandler.getAbsolutePosition(),
            initialDragHandler.getHelper());
      }

      newPosition = lastUp.offset.add(0, -handler.getHelperDimension()
          .getHeight()
          - groupSpacing);
      HelperInfo newLastUp = new HelperInfo(newPosition, handler.getHelper());
      $initialDraggable.data(LAST_UP, newLastUp);
      break;
    case LEFT:
      HelperInfo lastLeft = $initialDraggable.data(LAST_LEFT, HelperInfo.class);
      if (lastLeft == null) {
        lastLeft = new HelperInfo(initialDragHandler.getAbsolutePosition(),
            initialDragHandler.getHelper());
      }

      newPosition = lastLeft.offset.add(-handler.getHelperDimension()
          .getWidth()
          - groupSpacing, 0);
      HelperInfo newLastLeft = new HelperInfo(newPosition, handler.getHelper());
      $initialDraggable.data(LAST_LEFT, newLastLeft);

      break;

    case RIGHT:
      HelperInfo lastRight = $initialDraggable.data(LAST_RIGHT,
          HelperInfo.class);
      if (lastRight == null) {
        lastRight = new HelperInfo(initialDragHandler.getAbsolutePosition(),
            initialDragHandler.getHelper());
      }

      newPosition = lastRight.offset.add(lastRight.width + groupSpacing, 0);
      HelperInfo newLastRight = new HelperInfo(newPosition, handler.getHelper());
      $initialDraggable.data(LAST_RIGHT, newLastRight);

      break;

    default:
      break;
    }

    if (newPosition != null) {

      Offset actualPosition = handler.getAbsolutePosition();
      Offset clickOffset = handler.getOffsetClick();
      Offset newClickOffset = clickOffset.add(actualPosition.left
          - newPosition.left, actualPosition.top - newPosition.top);
      handler.setOffsetClick(newClickOffset);
    }

  }

  public void onStop(DraggableHandler handler, DragContext ctx, GqEvent e) {
    Element initialDraggable = ctx.getInitialDraggable();

    if (initialDraggable == ctx.getDraggable()) {
      $(initialDraggable).removeData(LAST_DOWN).removeData(LAST_LEFT)
          .removeData(LAST_RIGHT).removeData(LAST_UP);
    }
  }

}
