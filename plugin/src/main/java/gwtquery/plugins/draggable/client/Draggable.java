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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.plugins.MousePlugin;
import com.google.gwt.query.client.plugins.Plugin;
import com.google.gwt.query.client.plugins.events.GqEvent;
import gwtquery.plugins.draggable.client.DraggableOptions.DragFunction;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.DraggableOptions.SelectFunction;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent;
import gwtquery.plugins.draggable.client.events.DragContext;
import gwtquery.plugins.draggable.client.events.DragEvent;
import gwtquery.plugins.draggable.client.events.DragStartEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.DraggableSelectedEvent;
import gwtquery.plugins.draggable.client.events.DraggableUnselectedEvent;
import gwtquery.plugins.draggable.client.plugins.CursorPlugin;
import gwtquery.plugins.draggable.client.plugins.DraggablePlugin;
import gwtquery.plugins.draggable.client.plugins.GroupSelectedPlugin;
import gwtquery.plugins.draggable.client.plugins.OpacityPlugin;
import gwtquery.plugins.draggable.client.plugins.ScrollPlugin;
import gwtquery.plugins.draggable.client.plugins.SnapPlugin;
import gwtquery.plugins.draggable.client.plugins.StackPlugin;
import gwtquery.plugins.draggable.client.plugins.ZIndexPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Draggable plugin for GwtQuery
 */
public class Draggable extends MousePlugin {

  /**
   * Interface containing all css classes used in this plug-in
   */
  public static interface CssClassNames {
    String GWT_DRAGGABLE = "gwtQuery-draggable";
    String GWT_DRAGGABLE_DISABLED = "gwtQuery-draggable-disabled";
    String GWT_DRAGGABLE_DRAGGING = "gwtQuery-draggable-dragging";
  }

  private class DragCaller extends StartCaller {

    public DragCaller(DragContext ctx, DraggableHandler dragHandler, GqEvent e) {
      super(ctx, dragHandler, e);
    }

    public void call(DraggablePlugin plugin) {
      plugin.onDrag(dragHandler, ctx, e);
    }
  }

  private static interface PluginCaller {
    void call(DraggablePlugin plugin);
  }

  private class StartCaller implements PluginCaller {
    protected DragContext ctx;
    protected DraggableHandler dragHandler;
    protected GqEvent e;

    public StartCaller(DragContext ctx, DraggableHandler dragHandler, GqEvent e) {
      this.ctx = ctx;
      this.dragHandler = dragHandler;
      this.e = e;
    }

    public void call(DraggablePlugin plugin) {
      plugin.onStart(dragHandler, ctx, e);
    }
  }

  private class StopCaller extends StartCaller {

    public StopCaller(DragContext ctx, DraggableHandler dragHandler, GqEvent e) {
      super(ctx, dragHandler, e);
    }

    public void call(DraggablePlugin plugin) {
      plugin.onStop(dragHandler, ctx, e);
    }
  }

  public static final Class<Draggable> Draggable = Draggable.class;

  public static final String DRAGGABLE_HANDLER_KEY = "draggableHandler";

  static List<Element> selectedDraggables;

  private static Map<String, DraggablePlugin> draggablePlugins;

  // Register the plugin in GQuery
  static {
    GQuery.registerPlugin(Draggable.class, new Plugin<Draggable>() {
      public Draggable init(GQuery gq) {
        return new Draggable(gq);
      }
    });

    // register the different draggable plugins
    registerDraggablePlugin(new OpacityPlugin());
    registerDraggablePlugin(new ScrollPlugin());
    registerDraggablePlugin(new CursorPlugin());
    registerDraggablePlugin(new ZIndexPlugin());
    registerDraggablePlugin(new StackPlugin());
    registerDraggablePlugin(new SnapPlugin());
    registerDraggablePlugin(new GroupSelectedPlugin());

    selectedDraggables = new ArrayList<Element>();
  }

  /**
   * Register a draggable plugin that will be called during the drag operation
   *
   * @param plugin
   */
  public static void registerDraggablePlugin(DraggablePlugin plugin) {
    if (draggablePlugins == null) {
      draggablePlugins = new HashMap<String, DraggablePlugin>();
    }
    draggablePlugins.put(plugin.getName(), plugin);
  }

  private static void trigger(GwtEvent<?> e, DragFunction callback,
                              DragContext dragContext, HasHandlers handlerManager) {
    if (handlerManager != null && e != null) {
      handlerManager.fireEvent(e);
    }
    if (callback != null) {
      callback.f(dragContext);
    }
  }

  private boolean dragStart = false;


  /**
   * Constructor
   *
   * @param gq
   */
  protected Draggable(GQuery gq) {
    super(gq);
  }

  /**
   * Remove the draggable behavior to the selected elements. This method
   * releases resources used by the plugin and should be called when an element
   * is removed of the DOM.
   *
   * @return
   */
  public Draggable destroy() {

    for (Element e : elements()) {
      selectedDraggables.remove(e);

      $(e).removeData(DRAGGABLE_HANDLER_KEY).removeClass(
          CssClassNames.GWT_DRAGGABLE, CssClassNames.GWT_DRAGGABLE_DISABLED,
          CssClassNames.GWT_DRAGGABLE_DRAGGING);
    }
    destroyMouseHandler();
    return this;
  }

  /**
   * Make the selected elements draggable with default options
   *
   * @return
   */
  public Draggable draggable() {
    return draggable(new DraggableOptions(), null);
  }

  /**
   * Make the selected elements draggable by using the <code>options</code>
   *
   * @param options options to use during the drag operation
   * @return
   */
  public Draggable draggable(DraggableOptions options) {
    return draggable(options, null);
  }

  /**
   * Make the selected elements draggable by using the <code>options</code>. All
   * drag events will be fired on the <code>eventBus</code>
   *
   * @param options  options to use during the drag operation
   * @param eventBus The eventBus to use to fire events.
   * @return
   */
  public Draggable draggable(DraggableOptions options, HasHandlers eventBus) {

    this.eventBus = eventBus;

    initMouseHandler(options);

    for (Element e : elements()) {
      if (options.getHelperType() == HelperType.ORIGINAL
          && !positionIsFixedAbsoluteOrRelative(e.getStyle().getPosition())) {
        e.getStyle().setPosition(Position.RELATIVE);
      }
      e.addClassName(CssClassNames.GWT_DRAGGABLE);

      if (options.isDisabled()) {
        e.addClassName(CssClassNames.GWT_DRAGGABLE_DISABLED);
      }
      DraggableHandler handler = new DraggableHandler(options);
      $(e).data(DRAGGABLE_HANDLER_KEY, handler);
    }

    return this;
  }

  /**
   * Make the selected elements draggable with default options. All drag events
   * will be fired on the <code>eventBus</code>
   *
   * @param eventBus The eventBus to use to fire events.
   * @return
   */
  public Draggable draggable(HasHandlers eventBus) {
    return draggable(new DraggableOptions(), eventBus);
  }

  /**
   * Get the {@link DraggableOptions} for the first element.
   *
   * @return
   */
  public DraggableOptions options() {

    DraggableHandler handler = data(DRAGGABLE_HANDLER_KEY,
        DraggableHandler.class);
    if (handler != null) {
      return handler.getOptions();
    }

    return null;
  }

  /**
   * Set the DraggableOptions on each element.
   *
   * @param options
   * @return
   */
  public Draggable options(DraggableOptions options) {

    for (Element e : elements()) {
      DraggableHandler handler = $(e).data(DRAGGABLE_HANDLER_KEY,
          DraggableHandler.class);
      if (handler != null) {
        handler.setOptions(options);
      }
    }
    return this;
  }

  @Override
  protected String getPluginName() {
    return "draggable";
  }

  @Override
  protected boolean mouseCapture(Element draggable, GqEvent event) {

    DraggableHandler handler = $(draggable).data(DRAGGABLE_HANDLER_KEY,
        DraggableHandler.class);
    return handler != null && handler.getHelper() == null
        && !handler.getOptions().isDisabled()
        && isHandleClicked(draggable, event);
  }

  @Override
  protected boolean mouseClick(Element element, GqEvent event) {
    // react on click event only if no metakey is pressed, if no drag occurs and
    // if more than one element are selected

    if (!event.isMetaKeyPressed() && !dragStart
        && selectedDraggables.size() > 1) {
      DraggableHandler dragHandler = DraggableHandler.getInstance(element);
      DraggableOptions options = dragHandler.getOptions();
      unselectAll();
      select(element, options.getSelectedClassName());
    }

    dragStart = false;

    return !event.isMetaKeyPressed();
  }

  @Override
  protected boolean mouseDown(Element draggable, GqEvent event) {

    DraggableHandler dragHandler = DraggableHandler.getInstance(draggable);
    DraggableOptions options = dragHandler.getOptions();

    if (!options.isMultipleSelection()) {
      // ensure all previously selected element are unselected
      unselectAll();

    } else {

      if (event.isMetaKeyPressed()) {

        if (selectedDraggables.contains(draggable)) {

          unselect(draggable);

        } else if (canBeSelected(draggable, dragHandler)) {
          select(draggable, options.getSelectedClassName());
        }
      } else if (!selectedDraggables.contains(draggable)) {
        // if no meta key pressed and if the draggable is not selected ,
        // deselect all and select the draggable.
        unselectAll();
        select(draggable, options.getSelectedClassName());

      }
    }
    return super.mouseDown(draggable, event) && !event.isMetaKeyPressed();
  }

  @Override
  protected boolean mouseDrag(Element currentDraggable, GqEvent event) {
    dragStart = true;

    boolean result = false;

    DragContext ctx = new DragContext(currentDraggable, currentDraggable,
        selectedDraggables);
    result |= mouseDragImpl(ctx,
        DraggableHandler.getInstance(currentDraggable), event, false);

    for (Element draggable : selectedDraggables) {
      if (draggable != currentDraggable) {
        ctx = new DragContext(draggable, currentDraggable, selectedDraggables);
        result |= mouseDragImpl(ctx, DraggableHandler.getInstance(draggable),
            event, false);
      }

    }

    return result;
  }

  @Override
  protected boolean mouseStart(Element currentDraggable, GqEvent event) {

    boolean result = false;

    DraggableHandler dragHandler = getHandler(currentDraggable);
    DraggableOptions options = dragHandler.getOptions();
    // if the currentDraggable have not the same scope has the other selected
    // draggable or doesn't accept multi selection, unselect all
    if (!canBeSelected(currentDraggable, dragHandler)
        || !options.isMultipleSelection()) {
      unselectAll();
    }

    // if the currentDraggable is not yet selected and can be selected,
    // select it
    if (!selectedDraggables.contains(currentDraggable)
        && canBeSelected(currentDraggable, dragHandler)
        && options.isMultipleSelection()) {
      GWT.log("select element");
      select(currentDraggable, options.getSelectedClassName());
    }

    // select other draggable elements if select options is set
    SelectFunction selectFunction = options.getSelect();
    if (selectFunction != null) {
      GQuery followers = selectFunction.selectElements();
      followers.each(new Function() {
        @Override
        public void f(Element e) {
          DraggableHandler handler = DraggableHandler.getInstance(e);
          if (handler != null) {
            GWT.log("Select automatic selected element " + e.getId());
            select(e, handler.getOptions().getSelectedClassName());
          }
        }
      });
    }

    // first call mouseStart for the initial draggable
    DragContext ctx = new DragContext(currentDraggable, currentDraggable,
        selectedDraggables);
    result |= mouseStartImpl(ctx, event);

    // call mouseStartImpl for the others
    for (Element draggable : selectedDraggables) {
      if (draggable != currentDraggable) {
        ctx = new DragContext(draggable, currentDraggable, selectedDraggables);
        result |= mouseStartImpl(ctx, event);
      }
    }

    return result;
  }

  @Override
  protected boolean mouseStop(Element initialDraggable, final GqEvent event) {
    boolean result = false;

    DragContext ctx = new DragContext(initialDraggable, initialDraggable,
        selectedDraggables);
    result |= mouseStopImpl(ctx, event);

    for (Element draggable : selectedDraggables) {
      if (draggable != initialDraggable) {
        ctx = new DragContext(draggable, initialDraggable, selectedDraggables);
        result |= mouseStopImpl(ctx, event);
      }

    }

    DraggableOptions options = getOptions(initialDraggable);

    // deselect automatic selected elements
    // select other draggable elements if select options is set
    SelectFunction selectFunction = options.getSelect();
    if (selectFunction != null) {
      GQuery followers = selectFunction.selectElements();
      for (Element e : followers.elements()) {
        unselect(e);
      }
    }

    return result;
  }

  private void callPlugins(PluginCaller caller, DraggableOptions options) {
    for (DraggablePlugin plugin : draggablePlugins.values()) {
      if (plugin.hasToBeExecuted(options)) {
        caller.call(plugin);
      }
    }
  }

  private boolean canBeSelected(Element draggable, DraggableHandler handler) {
    if (selectedDraggables.isEmpty()) {

      return true;
    }

    String selectedScope = DraggableHandler.getInstance(
        selectedDraggables.get(0)).getOptions().getScope();
    String currentScope = handler.getOptions().getScope();

    return currentScope.equals(selectedScope);

  }

  private DragAndDropManager getDragAndDropManager() {
    return DragAndDropManager.getInstance();
  }

  private DraggableHandler getHandler(Element draggable) {

    return DraggableHandler.getInstance(draggable);
  }

  private DraggableOptions getOptions(Element draggable) {
    DraggableHandler handler = getHandler(draggable);
    return handler != null ? handler.getOptions() : null;
  }

  private boolean isHandleClicked(Element draggable, final GqEvent event) {
    DraggableOptions options = getOptions(draggable);
    // if no handle or if specified handle is not inside the draggable element,
    // continue
    if (options.getHandle() == null
        || $(options.getHandle(), draggable).length() == 0) {
      return true;
    }

    // OK, we have a valid handle, check if we are clicking on the handle object
    // or one of its descendants
    GQuery handleAndDescendant = $(options.getHandle(), draggable).find("*").andSelf();
    for (Element e : handleAndDescendant.elements()) {
      if (e == event.getEventTarget().cast()) {
        return true;
      }
    }
    return false;
  }

  /**
   * implementation of mouse drag
   */
  private boolean mouseDragImpl(DragContext ctx, DraggableHandler dragHandler,
                                GqEvent event, boolean noPropagation) {
    Element draggable = ctx.getDraggable();

    dragHandler.regeneratePositions(event);

    if (!noPropagation) {

      callPlugins(new DragCaller(ctx, dragHandler, event),
          dragHandler.getOptions());

      try {
        trigger(new DragEvent(ctx), dragHandler.getOptions().getOnDrag(), ctx);
      } catch (UmbrellaException e) {
        for (Throwable t : e.getCauses()) {
          if (t instanceof StopDragException) {
            mouseStop(draggable, event);
            return false;
          }
        }

      }
    }

    dragHandler.moveHelper(noPropagation);

    if (getDragAndDropManager().isHandleDroppable(ctx)) {
      getDragAndDropManager().drag(ctx, event);
    }

    return false;
  }

  private boolean mouseStartImpl(final DragContext ctx, final GqEvent event) {

    Element draggable = ctx.getDraggable();
    final DraggableHandler dragHandler = DraggableHandler.getInstance(draggable);
    DraggableOptions options = dragHandler.getOptions();

    try {
      trigger(new BeforeDragStartEvent(ctx), options.getOnBeforeDragStart(),
          ctx);
    } catch (UmbrellaException e) {
      for (Throwable t : e.getCauses()) {
        if (t instanceof StopDragException) {
          return false;
        }
      }

    }

    dragHandler.createHelper(draggable, event);
    dragHandler.cacheHelperSize();

    dragHandler.initialize(draggable, event);
    callPlugins(new StartCaller(ctx, dragHandler, event), options);

    try {
      trigger(new DragStartEvent(ctx), options.getOnDragStart(), ctx);
    } catch (UmbrellaException e) {
      for (Throwable t : e.getCauses()) {
        if (t instanceof StopDragException) {
          mouseStop(draggable, event);
          return false;
        }
      }

    }

    dragHandler.cacheHelperSize();

    if (getDragAndDropManager().isHandleDroppable(ctx)) {
      getDragAndDropManager().initialize(ctx, event);
    }

    dragHandler.getHelper().addClass(CssClassNames.GWT_DRAGGABLE_DRAGGING);
    // defer the mouseDragImpl to be sure that all selected draggable are
    // initialized
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {

      public void execute() {
        mouseDragImpl(ctx, dragHandler, event, true);

      }
    });

    return true;
  }

  private boolean mouseStopImpl(final DragContext ctx, final GqEvent event) {
    final Element draggable = ctx.getDraggable();
    final DraggableHandler handler = getHandler(draggable);
    final DraggableOptions options = handler.getOptions();

    boolean dropped = isDropped(ctx, event);

    if (draggable == null) {
      return false;
    }

    RevertOption revertOption = options.getRevert();
    if (revertOption.doRevert(dropped)) {
      handler.revertToOriginalPosition(new Function() {
        @Override
        public void f(Element e) {
          callPlugins(new StopCaller(ctx, handler, event), options);
          triggerDragStop(ctx, options);

          handler.clear(draggable);
        }
      });
      return false;
    }

    callPlugins(new StopCaller(ctx, handler, event), options);
    triggerDragStop(ctx, options);

    handler.clear(draggable);

    return false;
  }

  private boolean isDropped(DragContext ctx, GqEvent event) {

    boolean dropped = false;

    if (ctx.getDraggable() == ctx.getInitialDraggable()) {

      if (getDragAndDropManager().isHandleDroppable(ctx)) {
        dropped = getDragAndDropManager().drop(ctx, event);
      }

      $(ctx.getInitialDraggable()).data("_is_dropped", dropped);

    } else {
      dropped = $(ctx.getInitialDraggable()).data("_is_dropped", Boolean.class);

    }
    return dropped;
  }

  private native boolean positionIsFixedAbsoluteOrRelative(String position) /*-{
      return (/^(?:r|a|f)/).test(position);
  }-*/;

  private void select(Element draggable, String selectedCssClass) {
    if (selectedDraggables.contains(draggable)) {
      return;
    }
    selectedDraggables.add(draggable);

    if (selectedCssClass != null) {
      draggable.addClassName(selectedCssClass);
    }
    GWT.log("trigger DraggableSelectedEvent");
    trigger(new DraggableSelectedEvent(draggable),
        getOptions(draggable).getOnSelected(), draggable);
  }

  private void unselect(Element draggable) {

    DraggableHandler handler = DraggableHandler.getInstance(draggable);
    DraggableOptions options = handler.getOptions();

    if (options.getSelectedClassName() != null) {
      draggable.removeClassName(options.getSelectedClassName());
    }
    GWT.log("trigger DraggableUnselectedEvent");
    selectedDraggables.remove(draggable);
    trigger(new DraggableUnselectedEvent(draggable), options.getOnUnselected(),
        draggable);
  }

  private void trigger(GwtEvent<?> e, DragFunction callback,
                       DragContext dragContext) {
    trigger(e, callback, dragContext, eventBus);
  }

  /**
   * Use a deferred command to be sure that this event is trigger after the
   * possible drop event.
   *
   * @param draggable
   * @param options
   */
  private void triggerDragStop(final DragContext ctx,
                               final DraggableOptions options) {
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {

      public void execute() {
        trigger(new DragStopEvent(ctx), options.getOnDragStop(), ctx);

      }
    });
  }

  private void unselectAll() {
    // TODO concurent modification list !
    while (selectedDraggables.size() != 0) {
      unselect(selectedDraggables.get(0));
    }

  }

}
