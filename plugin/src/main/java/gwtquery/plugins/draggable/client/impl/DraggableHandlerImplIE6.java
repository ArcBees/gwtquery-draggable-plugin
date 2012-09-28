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

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.GQuery.Offset;
import com.google.gwt.query.client.plugins.UiPlugin.Dimension;

import gwtquery.plugins.draggable.client.DraggableHandler;

/**
 * Specific code for IE
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public class DraggableHandlerImplIE6 extends DraggableHandlerImplIE {

  @Override
  public int[] calculateContainment(DraggableHandler draggableHandler, Offset containerOffset,
	      Element containerElement,  boolean overflow) {
	  Dimension helperDimension = draggableHandler.getHelperDimension();
    // don't substract margin in ie 6 and 7
    return new int[] {
        containerOffset.left
            + (int) $(containerElement).cur("borderLeftWidth", true)
            + (int) $(containerElement).cur("paddingLeft", true),
        containerOffset.top
            + (int) $(containerElement).cur("borderTopWidth", true)
            + (int) $(containerElement).cur("paddingTop", true),
        containerOffset.left
            + (overflow ? Math.max(containerElement.getScrollWidth(),
                containerElement.getOffsetWidth()) : containerElement
                .getOffsetWidth())
            - (int) $(containerElement).cur("borderLeftWidth", true)
            - (int) $(containerElement).cur("paddingRight", true)
            - helperDimension.getWidth(),
        containerOffset.top
            + (overflow ? Math.max(containerElement.getScrollHeight(),
                containerElement.getOffsetHeight()) : containerElement
                .getOffsetHeight())
            - (int) $(containerElement).cur("borderTopWidth", true)
            - (int) $(containerElement).cur("paddingBottom", true)
            - helperDimension.getHeight()};
  }
  
}


