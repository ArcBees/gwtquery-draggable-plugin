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

import java.util.ArrayList;
import java.util.List;

import gwtquery.plugins.draggable.client.DraggableHandler;
import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.SnapMode;
import gwtquery.plugins.draggable.client.events.DragContext;

/**
 * Add-on allow the draggable to snap other elements
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 *
 */
public class SnapPlugin implements DraggablePlugin {

  private static class SnapElement {

    Offset offset;
    int width;
    int height;

    public SnapElement(Offset o, int width, int height) {
      offset = o;
      this.width = width;
      this.height = height;
    }

    public int getHeight() {
      return height;
    }

    public Offset getOffset() {
      return offset;
    }

    public int getWidth() {
      return width;
    }

  }

  private static String SNAP_ELEMENTS_KEY = "snapElements";

  public String getName() {
    return "snap";
  }

  public boolean hasToBeExecuted(DraggableOptions options) {
    return options.isSnap();
  }

  @SuppressWarnings("unchecked")
  public void onDrag(DraggableHandler handler,  DragContext ctx, GqEvent e) {

    List<SnapElement> snapElements = $(ctx.getDraggable()).data(
        SNAP_ELEMENTS_KEY, ArrayList.class);

    int snapTolerance = handler.getOptions().getSnapTolerance();
    SnapMode snapMode = handler.getOptions().getSnapMode();

    int helperLeft = handler.getAbsolutePosition().left;
    int helperRight = helperLeft + handler.getHelperDimension().getWidth();
    int helperTop = handler.getAbsolutePosition().top;
    int helperBottom = helperTop + handler.getHelperDimension().getHeight();

    for (SnapElement snapElement : snapElements) {
      int snapElementLeft = snapElement.getOffset().left;
      int snapElementRight = snapElementLeft + snapElement.getWidth();
      int snapElementTop = snapElement.getOffset().top;
      int snapElementBottom = snapElementTop + snapElement.getHeight();

      if (!((snapElementLeft - snapTolerance < helperLeft
          && helperLeft < snapElementRight + snapTolerance
          && snapElementTop - snapTolerance < helperTop && helperTop < snapElementBottom
          + snapTolerance)
          || (snapElementLeft - snapTolerance < helperLeft
              && helperLeft < snapElementRight + snapTolerance
              && snapElementTop - snapTolerance < helperBottom && helperBottom < snapElementBottom
              + snapTolerance)
          || (snapElementLeft - snapTolerance < helperRight
              && helperRight < snapElementRight + snapTolerance
              && snapElementTop - snapTolerance < helperTop && helperTop < snapElementBottom
              + snapTolerance) || (snapElementLeft - snapTolerance < helperRight
          && helperRight < snapElementRight + snapTolerance
          && snapElementTop - snapTolerance < helperBottom && helperBottom < snapElementBottom
          + snapTolerance))) {
        // no snapping !!
        /*
         * if (snapElement.isSnapping()){ //TODO trigger event
         * sanpReleaseEvent... //handler.triggerEvent()
         * snapElement.setSnapping(false); continue; }
         */

        continue;
      }
      Offset newTopDimension = null;
      Offset newLeftDimension = null;
      if (SnapMode.INNER != snapMode) {
        boolean snapTop = Math.abs(snapElementTop - helperBottom) <= snapTolerance;
        boolean snapBottom = Math.abs(snapElementBottom - helperTop) <= snapTolerance;
        boolean snapLeft = Math.abs(snapElementLeft - helperRight) <= snapTolerance;
        boolean snapRight = Math.abs(snapElementRight - helperLeft) <= snapTolerance;

        if (snapTop) {
          newTopDimension = handler.convertPositionTo(false,
              new Offset(0, snapElementTop
                  - handler.getHelperDimension().getHeight()));
        } else if (snapBottom) {
          newTopDimension = handler.convertPositionTo(false,
              new Offset(0, snapElementBottom));
        }

        if (snapLeft) {
          newLeftDimension = handler.convertPositionTo(false,
              new Offset(snapElementLeft
                  - handler.getHelperDimension().getWidth(), 0));
        } else if (snapRight) {
          newLeftDimension = handler.convertPositionTo(false,
              new Offset(snapElementRight, 0));
        }
      }

      if (SnapMode.OUTER != snapMode) {
        boolean snapTop = Math.abs(snapElementTop - helperTop) <= snapTolerance;
        boolean snapBottom = Math.abs(snapElementBottom - helperBottom) <= snapTolerance;
        boolean snapLeft = Math.abs(snapElementLeft - helperLeft) <= snapTolerance;
        boolean snapRight = Math.abs(snapElementRight - helperRight) <= snapTolerance;

        if (snapTop) {
          newTopDimension = handler.convertPositionTo(false,
              new Offset(0, snapElementTop));
        } else if (snapBottom) {
          newTopDimension = handler.convertPositionTo(false,
              new Offset(0, snapElementBottom
                  - handler.getHelperDimension().getHeight()));
        }

        if (snapLeft) {
          newLeftDimension = handler.convertPositionTo(false,
              new Offset(snapElementLeft, 0));
        } else if (snapRight) {
          newLeftDimension = handler.convertPositionTo(false,
              new Offset(snapElementRight
                  - handler.getHelperDimension().getWidth(), 0));
        }
      }

      if (newTopDimension != null) {
        int newTop = newTopDimension.top- handler.getMargin().top;
        int newLeft = handler.getPosition().left;
        handler.setPosition(new Offset(newLeft, newTop));

      }

      if (newLeftDimension != null) {
        int newTop = handler.getPosition().top;
        int newLeft = newLeftDimension.left
            - handler.getMargin().left;
        handler.setPosition(new Offset(newLeft, newTop));

      }

    }
  }

  public void onStart(DraggableHandler handler,  DragContext ctx,
      GqEvent e) {
    Element draggableElement = ctx.getDraggable();
    List<SnapElement> snapElements = new ArrayList<SnapElement>();
    GQuery snap = (handler.getOptions().getSnap_$() != null ? handler
        .getOptions().getSnap_$() : $(handler.getOptions().getSnap()));

    for (Element element : snap.elements()) {
      if (element != draggableElement) {
        GQuery $element = $(element);
        snapElements.add(new SnapElement($element.offset(), $element.outerWidth(),
            $element.outerHeight()));
      }
    }
    $(draggableElement).data(SNAP_ELEMENTS_KEY, snapElements);

  }
  
  public void onStop(DraggableHandler handler,  DragContext ctx, GqEvent e) {
    // nothing to do
  }

}

