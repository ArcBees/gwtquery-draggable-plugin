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

import com.google.gwt.dom.client.Style;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.plugins.events.GqEvent;
import gwtquery.plugins.draggable.client.DraggableHandler;
import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.draggable.client.events.DragContext;

import static com.google.gwt.query.client.GQuery.$;

/**
 * This add-on manage the z-index for the helper while being dragged.
 *
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 */
public class ZIndexPlugin implements DraggablePlugin {

  private static String OLD_ZINDEX_KEY = "oldZIndex";
  private static String ZINDEX_CSS = "zIndex";

  public String getName() {
    return "zIndex";
  }

  public boolean hasToBeExecuted(DraggableOptions options) {
    return options.getZIndex() != null;
  }

  public void onDrag(DraggableHandler handler, DragContext ctx, GqEvent e) {
  }

  public void onStart(DraggableHandler handler, DragContext ctx,
                      GqEvent e) {
    GQuery $element = (handler.getOptions().getHelperType() == HelperType.ORIGINAL) ? $(ctx.getDraggable()) : handler.getHelper();
    if ($element == null || $element.length() == 0) {
      return;
    }
    //String oldZIndex = $element.css(ZINDEX_CSS);
    String oldZIndex = getZIndex($element.get(0).getStyle());
    if (oldZIndex != null) {
      $element.data(OLD_ZINDEX_KEY, oldZIndex);
    }
    $element.css(ZINDEX_CSS, handler.getOptions().getZIndex().toString());

  }

  public void onStop(DraggableHandler handler, DragContext ctx, GqEvent e) {
    //helper can be null if the draggableElement was unloaded and after loaded
    GQuery $element = (handler.getOptions().getHelperType() == HelperType.ORIGINAL) ? handler.getHelper() : $(ctx.getDraggable());
    if ($element == null || $element.length() == 0) {
      return;
    }
    String oldZIndex = $element.data(OLD_ZINDEX_KEY, String.class);
    $element.css(ZINDEX_CSS, oldZIndex);
  }

  /**
   * Force the zIndex property to be a String object
   * Under IE, the zIndex property is returned as an Integer
   * See <a href="http://code.google.com/p/google-web-toolkit/issues/detail?id=5548">Issue 5548</a> of GWT
   *
   * @param style
   * @return
   */
  private native String getZIndex(Style style) /*-{
      return "" + style["zIndex"];
  }-*/;

}
