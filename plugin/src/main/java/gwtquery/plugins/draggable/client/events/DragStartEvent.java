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
package gwtquery.plugins.draggable.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Event fired when the drag operation stops.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public class DragStartEvent extends
    AbstractDraggableEvent<DragStartEvent.DragStartEventHandler> {

  public interface DragStartEventHandler extends EventHandler {
    public void onDragStart(DragStartEvent event);
  }

  public static Type<DragStartEventHandler> TYPE = new Type<DragStartEventHandler>();

  public DragStartEvent(DragContext dragContext) {
    super(dragContext);
  }

  @Override
  public Type<DragStartEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DragStartEventHandler handler) {
    handler.onDragStart(this);
  }

}
