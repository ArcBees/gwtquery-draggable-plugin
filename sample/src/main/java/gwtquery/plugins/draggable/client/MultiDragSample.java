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
import static gwtquery.plugins.draggable.client.Draggable.Draggable;

import com.google.gwt.core.client.EntryPoint;

import gwtquery.plugins.draggable.client.DraggableOptions.GroupingMode;

/**
 * Simple sample
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class MultiDragSample implements EntryPoint {

  public void onModuleLoad() {
    
    $(".multi-draggable").as(Draggable).draggable(createOptions(null, "basicScope", true));

    $("#up").as(Draggable).draggable(
        createOptions(GroupingMode.UP, "group-position", true));
    $("#down").as(Draggable).draggable(
        createOptions(GroupingMode.DOWN, "group-position", true));
    $("#left").as(Draggable).draggable(
        createOptions(GroupingMode.LEFT, "group-position", true));
    $("#right").as(Draggable).draggable(
        createOptions(GroupingMode.RIGHT, "group-position", true));
    $("#none").as(Draggable).draggable(
        createOptions(GroupingMode.NONE, "group-position", true));

    DraggableOptions options = createOptions(GroupingMode.NONE, "selectScope",
        false);
    options.setSelect("#draggable2");
    $("#draggable1").as(Draggable).draggable(options);

    options = createOptions(GroupingMode.NONE, "selectScope", false);
    options.setSelect("#draggable3");
    $("#draggable2").as(Draggable).draggable(options);

    options = createOptions(GroupingMode.NONE, "selectScope", false);
    options.setSelect("#draggable1, #draggable2");
    $("#draggable3").as(Draggable).draggable(options);

  }

  private DraggableOptions createOptions(GroupingMode groupingMode,
      String scope, boolean multipleSelection) {
    DraggableOptions o = new DraggableOptions();
    o.setContainment(".demo");
    o.setZIndex(100);
    if (multipleSelection) {
      o.setMultipleSelection(true);
      o.setSelectedClassName("selected");
    }
    if (groupingMode != null){
      o.setGroupingMode(groupingMode);
      //it's better to set a distance to 5px to avoid that the drag start during the selection
      o.setDistance(5);
    }

    o.setScope(scope);
    return o;
  }

}
