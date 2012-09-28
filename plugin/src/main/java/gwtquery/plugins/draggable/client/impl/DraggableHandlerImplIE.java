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

import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.GQuery;

/**
 * Specific code for IE
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public class DraggableHandlerImplIE extends DraggableHandlerImpl {

	@Override
	public boolean resetParentOffsetPosition(GQuery helperOffsetParent) {
		return super.resetParentOffsetPosition(helperOffsetParent)
				|| helperOffsetParent.get(0) == GQuery.document.cast();
	}

	@Override
	public void removeHelper(GQuery helper, HelperType helperType) {
		if (helperType == HelperType.CLONE) {
			// in IE, the clone helper has the same hashcode than the draggable
			// don't call remove method on it because all dragable's data will be cleared also.
			// TODO maybe add an issue in GQuery to discuss about this problem !
			//      problem comes maybe from GWT directly !
			Element helperElement = helper.get(0);
			helperElement.getParentNode().removeChild(helperElement);
		} else {
			super.removeHelper(helper, helperType);
		}
	}
}

