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

import com.google.gwt.core.client.EntryPoint;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.draggable.client.Draggable.Draggable;

/**
 * Simple sample
 *
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 */
public class StackSample implements EntryPoint {

  public void onModuleLoad() {
    DraggableOptions o = new DraggableOptions();
    o.setStack(".ui-widget-content");
    $(".ui-widget-content").as(Draggable).draggable(o);
  }

}
