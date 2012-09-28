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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.plugins.events.GqEvent;

import java.util.Collection;

import gwtquery.plugins.draggable.client.events.DragContext;

/**
 * The goal of this class is to manage the interactions between draggable and droppable objects.
 * This implementation specifies the interface needed by this kind of manager but do nothing.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 *
 */
public class DragAndDropManager {

  private static DragAndDropManager INSTANCE = GWT
      .create(DragAndDropManager.class);

  /**
   * return the instance of the manager
   * @return
   */
  public static DragAndDropManager getInstance() {
    return INSTANCE;
  }

  /**
   * Link a droppable with the specified scope <code>scope</code>
   * @param droppable
   * @param scope
   */
  public void addDroppable(Element droppable, String scope) {
  }

  /**
   * Method called when the draggable is being dragged
   * @param draggable
   * @param e
   */
  public void drag(DragContext ctx, GqEvent e) {
  }


  /**
   * Method called when the draggable was dropped
   * @param ctx
   * @param e
   * @return
   */
  public boolean drop(DragContext ctx, GqEvent e) {
    return false;
  }

 
  /**
   * Return the list of droppable elements with the scope <code>scope</code>
   * @param scope
   * @return
   */
  public Collection<Element> getDroppablesByScope(String scope) {
    return null;
  }
  
  public void initialize(DragContext ctx, GqEvent e) {
  }


  public boolean isHandleDroppable(DragContext ctx) {
    return false;
  }

  /**
   * Use this method when droppable elements change dynamically during a drag
   * operation and you want to take into account these changes for the drop
   * operation.
   */
  public void update(DragContext ctx) {
  }

}
