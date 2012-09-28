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

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventHandler;

/**
 * Event fired when the initialization of the drag before it starts.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public class BeforeDragStartEvent extends
    AbstractDraggableEvent<BeforeDragStartEvent.BeforeDragStartEventHandler> {

  public interface BeforeDragStartEventHandler extends EventHandler {
    public void onBeforeDragStart(BeforeDragStartEvent event);
  }

  public static Type<BeforeDragStartEventHandler> TYPE = new Type<BeforeDragStartEventHandler>();
 
  public BeforeDragStartEvent(DragContext dragContext) {
    super(dragContext);
  }

  @Override
  public Type<BeforeDragStartEventHandler> getAssociatedType() {
    return TYPE;
  }

  /**
   * Helper not yet initialized at this moment.
   */
  @Override
  public Element getHelper() {
    return null;
  }

  @Override
  protected void dispatch(BeforeDragStartEventHandler handler) {
    handler.onBeforeDragStart(this);
  }

}
