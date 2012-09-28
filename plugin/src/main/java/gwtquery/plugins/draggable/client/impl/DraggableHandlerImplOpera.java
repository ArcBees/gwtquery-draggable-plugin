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
package gwtquery.plugins.draggable.client.impl;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.GQuery.Offset;

import gwtquery.plugins.draggable.client.DraggableHandler;

/**
 * Specific code for Opera
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public class DraggableHandlerImplOpera extends DraggableHandlerImpl {

  @Override
  public Offset calculateRelativeHelperOffset(Element element,
      DraggableHandler draggableHandler) {
    // With Opera we have to remove borderTop and borderLeft of the offsetParent (included in offsetLeft and offsetTop of the element)
    Offset relativeHelperOffset = super.calculateRelativeHelperOffset(element, draggableHandler);
    Element offsetParent = GQuery.$(element).offsetParent().get(0);
    
    int offsetParentBorderLeft = (int)  GQuery.$(offsetParent).cur("borderLeftWidth", true);
    int offsetParentBorderTop = (int)  GQuery.$(offsetParent).cur("borderTopWidth", true);
    
    return new Offset(relativeHelperOffset.left - offsetParentBorderLeft, relativeHelperOffset.top - offsetParentBorderTop);
  }
  
}

