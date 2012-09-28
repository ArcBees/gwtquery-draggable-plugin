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
import static com.google.gwt.query.client.GQuery.document;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.GQuery.Offset;
import com.google.gwt.query.client.plugins.events.GqEvent;
import com.google.gwt.user.client.Window;

import gwtquery.plugins.draggable.client.DragAndDropManager;
import gwtquery.plugins.draggable.client.DraggableHandler;
import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.AxisOption;
import gwtquery.plugins.draggable.client.events.DragContext;

/**
 * This add-on handle scrolling of parent element.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public class ScrollPlugin implements DraggablePlugin {

  private static String OVERFLOW_OFFSET_KEY = "overflowOffset";

  public String getName() {
    return "scroll";
  }

  public boolean hasToBeExecuted(DraggableOptions options) {
    return options.isScroll();
  }

  public void onDrag(DraggableHandler handler,  DragContext ctx, GqEvent e) {
    DraggableOptions options = handler.getOptions();
    Element draggableElement = ctx.getDraggable();
    GQuery scrollParent = handler.getHelperScrollParent();
    Element scrollParentElement = scrollParent.get(0);
    if (scrollParentElement == null) {
      return;
    }

    AxisOption axis = options.getAxis();
    Offset overflowOffset = $(draggableElement).data(OVERFLOW_OFFSET_KEY,
        Offset.class);
    int scrollSensitivity = options.getScrollSensitivity();
    int scrollSpeed = options.getScrollSpeed();

    boolean scrolled = false;

    if (scrollParentElement != null
        && scrollParentElement != $(GQuery.document).get(0)
        && !"html".equalsIgnoreCase(scrollParentElement.getTagName())) {
      if (AxisOption.NONE == axis || AxisOption.Y_AXIS == axis) {
        // test if we have to scroll down...
        if ((overflowOffset.top + scrollParentElement.getOffsetHeight())
            - e.pageY() < scrollSensitivity) {
          scrollParentElement.setScrollTop(scrollParentElement.getScrollTop()
              + scrollSpeed);
          scrolled = true;
          // test if we have to scroll up...
        } else if (e.pageY() - overflowOffset.top < scrollSensitivity) {
          scrollParentElement.setScrollTop(scrollParentElement.getScrollTop()
              - scrollSpeed);
          scrolled = true;
        }
      }

      if (AxisOption.NONE == axis || AxisOption.X_AXIS == axis) {
        // test if we have to scroll left...
        if ((overflowOffset.left + scrollParentElement.getOffsetWidth())
            - e.pageX() < scrollSensitivity) {
          scrollParentElement.setScrollLeft(scrollParentElement.getScrollLeft()
              + scrollSpeed);
          scrolled = true;
          // test if we have to scroll right...
        } else if (e.pageX() - overflowOffset.left < scrollSensitivity) {
          scrollParentElement.setScrollLeft(scrollParentElement.getScrollLeft()
              - scrollSpeed);
          scrolled = true;
        }
      }

    } else {
      if (AxisOption.NONE == axis || AxisOption.Y_AXIS == axis) {
        if (e.pageY() - document.getScrollTop() < scrollSensitivity) {
          document.setScrollTop(document.getScrollTop() - scrollSpeed);
          scrolled = true;
        } else if (Window.getClientHeight()
            - (e.pageY() - document.getScrollTop()) < scrollSensitivity) {
          document.setScrollTop(document.getScrollTop() + scrollSpeed);
          scrolled = true;
        }
      }

      if (AxisOption.NONE == axis || AxisOption.X_AXIS == axis) {
        if (e.pageX() - document.getScrollLeft() < scrollSensitivity) {
          document.setScrollLeft(document.getScrollLeft() - scrollSpeed);
          scrolled = true;
        } else if (Window.getClientWidth()
            - (e.pageX() - document.getScrollLeft()) < scrollSensitivity) {
          document.setScrollLeft(document.getScrollLeft() + scrollSpeed);
          scrolled = true;
        }
      }

    }

    if (scrolled && DragAndDropManager.getInstance().isHandleDroppable(ctx)) {
      DragAndDropManager.getInstance().initialize(ctx,e);
    }

  }

  public void onStart(DraggableHandler handler,  DragContext ctx,
      GqEvent e) {

    GQuery scrollParent = handler.getHelperScrollParent();
    Element scrollParentElement = scrollParent.get(0);
    if (scrollParentElement != null
        && scrollParentElement != $(GQuery.document).get(0)
        && !"html".equalsIgnoreCase(scrollParentElement.getTagName())) {
      Offset scrollParentOffset = scrollParent.offset();
      $(ctx.getDraggable()).data(OVERFLOW_OFFSET_KEY, scrollParentOffset);
    }
  }

  public void onStop(DraggableHandler handler,  DragContext ctx, GqEvent e) {
    $(ctx.getDraggable()).removeData(OVERFLOW_OFFSET_KEY);

  }

}
