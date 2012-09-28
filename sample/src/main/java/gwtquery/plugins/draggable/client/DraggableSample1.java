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

import gwtquery.plugins.draggable.client.DraggableOptions.DragFunction;
import gwtquery.plugins.draggable.client.events.DragContext;

/**
 * Simple sample
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class DraggableSample1 implements EntryPoint {

  private int startCounter = 0;
  private int dragCounter = 0;
  private int stopCounter = 0;

  public void onModuleLoad() {
    // simpleDraggable div
    DraggableOptions o = createOptionsForSimpleDraggable();
    $("#simpleDraggable").as(Draggable).draggable(o);
  }

  private DraggableOptions createOptionsForSimpleDraggable() {
    DraggableOptions o = new DraggableOptions();
    o.setOnDragStart(new DragFunction() { 
      public void f(DragContext context) {
        startCounter++;
        $("#startCounter").html("" + startCounter);
        
      }
    });

    o.setOnDrag(new DragFunction() {
      public void f(DragContext context) {
        dragCounter++;
        $("#dragCounter").html("" + dragCounter);
      }
    });

    o.setOnDragStop(new DragFunction() {
      public void f(DragContext context) {
        stopCounter++;
        $("#stopCounter").html("" + stopCounter);
      }
    });
    return o;

  }
}
