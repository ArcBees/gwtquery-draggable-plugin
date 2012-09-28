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

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.plugins.events.GqEvent;

import java.util.Arrays;
import java.util.Comparator;

import gwtquery.plugins.draggable.client.DraggableHandler;
import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.events.DragContext;

/**
 * This add-on manage the z-index for the helper while being dragged.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public class StackPlugin implements DraggablePlugin {

  private class ZIndexComparator implements Comparator<Element> {

    public int compare(Element element1, Element element2) {
      int zIndex1 = getZindex(element1);
      int zIndex2 = getZindex(element2);
      return (zIndex1 - zIndex2);

    }
  }

  private static int getZindex(Element element) {
    String zIndex = element.getStyle().getZIndex();
    if (zIndex == null || zIndex.length() == 0) {
      return 0;
    }
    return new Integer(zIndex);
  }

  public String getName() {
    return "stack";
  }

  public boolean hasToBeExecuted(DraggableOptions options) {
    return options.getStack() != null && options.getStack().length() != 0;
  }

  public void onDrag(DraggableHandler info,  DragContext ctx, GqEvent e) {
  }

  public void onStart(DraggableHandler info,  DragContext ctx, GqEvent e) {

    GQuery stackElements = info.getOptions().getStack();
    Element[] sortedElementArray = stackElements.elements();
    Arrays.sort(sortedElementArray, new ZIndexComparator());

    if (sortedElementArray.length == 0) {
      return;
    }

    int zIndexMin = getZindex(sortedElementArray[0]);
    int i = 0;
    for (Element el : sortedElementArray) {
      el.getStyle().setZIndex(zIndexMin + i);
      i++;
    }

    info.getHelper().get(0).getStyle().setZIndex(
        zIndexMin + sortedElementArray.length);

  }

  public void onStop(DraggableHandler info, DragContext ctx, GqEvent e) {
  }

}
