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
import static com.google.gwt.query.client.GQuery.body;

import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.plugins.events.GqEvent;

import gwtquery.plugins.draggable.client.DraggableHandler;
import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.events.DragContext;

/**
 * This add-on handles the css cursor to display during drag operation.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public class CursorPlugin implements DraggablePlugin {

  private static String OLD_CURSOR_KEY = "oldCursor";
  private static String CURSOR_CSS = "cursor";
  private boolean isStarting=false;

  public String getName() {
    return "cursor";
  }

  public boolean hasToBeExecuted(DraggableOptions options) {
    return options.getCursor() != null;
  }

  public void onDrag(DraggableHandler handler,  DragContext ctx, GqEvent e) {
    // nothing to do
  }

  public void onStart(DraggableHandler handler,  DragContext ctx,
      GqEvent e) {

    if (ctx.getInitialDraggable() == ctx.getDraggable() && !isStarting){
      isStarting = true;
      GQuery $body = $(body);
      String oldCursor = $body.css(CURSOR_CSS);
      if (oldCursor != null) {
        $body.data(OLD_CURSOR_KEY, oldCursor);
      }
      $body.css(CURSOR_CSS, handler.getOptions().getCursor().getCssName());
    }

  }

  public void onStop(DraggableHandler handler,  DragContext ctx, GqEvent e) {
    if (ctx.getInitialDraggable() != ctx.getDraggable()){
      return;
    }
    GQuery $body = $(body);
    String oldCursor = $body.data(OLD_CURSOR_KEY, String.class);
    $body.css(CURSOR_CSS, oldCursor);
    isStarting=false;
  }

}
