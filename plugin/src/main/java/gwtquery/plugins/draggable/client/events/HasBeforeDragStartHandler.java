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

import com.google.gwt.event.shared.HandlerRegistration;

import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent.BeforeDragStartEventHandler;

/**
 * A widget that implements this interface provides registration for
 * {@link BeforeDragStartEventHandler} instances.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public interface HasBeforeDragStartHandler {

  /**
   * Adds a {@link BeforeDragStartEventHandler} handler.
   * 
   * @param handler
   *          the before drag handler
   * @return {@link BeforeDragStartEventHandler} used to remove this handler
   */
  HandlerRegistration addBeforeDragHandler(BeforeDragStartEventHandler handler);

}
