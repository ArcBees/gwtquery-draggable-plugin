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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import gwtquery.plugins.draggable.client.events.DragEvent;
import gwtquery.plugins.draggable.client.events.DragEvent.DragEventHandler;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;

import java.util.Date;

/**
 * Sample of the integration of draggable plugin and GWT
 *
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 */
public class GWTIntegrationSample implements EntryPoint {

  interface Image extends ClientBundle {
    Image INSTANCE = GWT.create(Image.class);

    ImageResource gwtLogo();
  }

  /**
   * Handler that catch DragEvent and DragStopEvent to fill the information
   * message
   *
   * @author Julien Dramaix (julien.dramaix@gmail.com)
   */
  private class DragHandlerImpl implements DragEventHandler,
      DragStopEventHandler {

    // only used to display msg
    private String draggableDescription;

    public DragHandlerImpl(String draggableDescription) {
      this.draggableDescription = draggableDescription;
    }

    public void onDrag(DragEvent event) {
      String msg = "Component " + draggableDescription + " is dragging";
      infoMsg.setHTML(msg);

    }

    public void onDragStop(DragStopEvent event) {
      infoMsg.setHTML(DEFAULT_MSG);

    }

  }

  private static String DEFAULT_MSG = "No drag operation";
  private HTML infoMsg;

  public void addInfoMsg() {
    infoMsg = new HTML(DEFAULT_MSG);
    infoMsg.addStyleName("infoMessage");
    RootPanel.get("gwtIntegrationSampleMsg").add(infoMsg);

  }

  public <T extends Widget> DraggableWidget<T> makeDraggable(T widget,
                                                             String draggableDescription) {

    DraggableWidget<T> dragWidget = new DraggableWidget<T>(widget);

    DragHandlerImpl dragHandler = new DragHandlerImpl(draggableDescription);
    dragWidget.addDragHandler(dragHandler);
    dragWidget.addDragStopHandler(dragHandler);

    return dragWidget;

  }

  public void onModuleLoad() {

    addInfoMsg();

    RootPanel.get("gwtIntegrationSampleDiv").add(
        makeDraggable(createMenuBar(), "menu"));
    RootPanel.get("gwtIntegrationSampleDiv").add(
        makeDraggable(createDecoratedForm(), "form"));
    RootPanel.get("gwtIntegrationSampleDiv").add(
        makeDraggable(createDatePanel(), "datePicker"));
    RootPanel.get("gwtIntegrationSampleDiv").add(
        makeDraggable(createTabPanel(), "tabPanel"));
    RootPanel.get("gwtIntegrationSampleDiv").add(
        makeDraggable(createDynamicTree(), "tree"));


  }

  /**
   * Create a Date picker. The code comes from the GWT show case :
   * http://gwt.google.com/samples/Showcase/Showcase.html#!CwDatePicker@
   *
   * @return
   */
  private VerticalPanel createDatePanel() {
    // Create a basic date picker
    DatePicker datePicker = new DatePicker();
    final Label text = new Label();

    // Set the value in the text box when the user selects a date
    datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
      public void onValueChange(ValueChangeEvent<Date> event) {
        Date date = event.getValue();
        String dateString = DateTimeFormat.getMediumDateFormat().format(date);
        text.setText(dateString);
      }
    });

    // Set the default value
    datePicker.setValue(new Date(), true);

    // Combine the widgets into a panel and return them
    VerticalPanel vPanel = new VerticalPanel();
    vPanel.add(new HTML("Permanent DatePicker:"));
    vPanel.add(text);
    vPanel.add(datePicker);
    return vPanel;

  }

  /**
   * Create a Decorated Form The code comes from the GWT show case :
   * http://gwt.google.com/samples/Showcase/Showcase.html#!CwDecoratorPanel
   *
   * @return
   */
  private DecoratorPanel createDecoratedForm() {
    // Create a table to layout the form options
    FlexTable layout = new FlexTable();
    layout.setCellSpacing(6);
    FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();

    // Add a title to the form
    layout.setHTML(0, 0, "Enter Search Criteria");
    cellFormatter.setColSpan(0, 0, 2);
    cellFormatter.setHorizontalAlignment(0, 0,
        HasHorizontalAlignment.ALIGN_CENTER);

    // Add some standard form options
    layout.setHTML(1, 0, "Name");
    layout.setWidget(1, 1, new TextBox());
    layout.setHTML(2, 0, "Description");
    layout.setWidget(2, 1, new TextBox());

    // Wrap the content in a DecoratorPanel
    DecoratorPanel decPanel = new DecoratorPanel();
    decPanel.setWidget(layout);
    return decPanel;

  }

  /**
   * Create a Dynamic tree. The code comes from the GWT show case :
   * http://gwt.google.com/samples/Showcase/Showcase.html#!CwTree
   *
   * @return
   */
  private Widget createDynamicTree() {
    // Create a new tree
    Tree dynamicTree = new Tree();

    // Add some default tree items
    for (int i = 0; i < 5; i++) {
      TreeItem item = dynamicTree.addItem(SafeHtmlUtils.fromString("Item " + i));

      // Temporarily add an item so we can expand this node
      item.addItem(SafeHtmlUtils.fromString(""));
    }

    // Add a handler that automatically generates some children
    dynamicTree.addOpenHandler(new OpenHandler<TreeItem>() {
      public void onOpen(OpenEvent<TreeItem> event) {
        TreeItem item = event.getTarget();
        if (item.getChildCount() == 1) {
          // Close the item immediately
          item.setState(false, false);

          // Add a random number of children to the item
          String itemText = item.getText();
          int numChildren = Random.nextInt(5) + 2;
          for (int i = 0; i < numChildren; i++) {
            TreeItem child = item.addItem(SafeHtmlUtils.fromString(itemText + "." + i));
            child.addItem(SafeHtmlUtils.fromString(""));
          }

          // Remove the temporary item when we finish loading
          item.getChild(0).remove();

          // Reopen the item
          item.setState(true, false);
        }
      }
    });

    // Return the tree (decorated)
    DecoratorPanel decPanel = new DecoratorPanel();
    decPanel.setWidget(dynamicTree);
    return decPanel;
  }

  /**
   * Create a menu bar. The code comes from the GWT show case :
   * http://gwt.google.com/samples/Showcase/Showcase.html#!CwMenuBar
   *
   * @return
   */
  private MenuBar createMenuBar() {
    // Create a command that will execute on menu item selection
    Command menuCommand = new Command() {
      private int curPhrase = 0;
      private final String[] phrases = new String[]{
          "Thank you for selecting a menu item", "A fine selection indeed",
          "Don't you have anything better to do than select menu items?",
          "Try something else", "this is just a menu!", "Another wasted click"};

      public void execute() {
        Window.alert(phrases[curPhrase]);
        curPhrase = (curPhrase + 1) % phrases.length;
      }
    };

    // Create a menu bar
    MenuBar menu = new MenuBar();
    menu.setAutoOpen(false);
    menu.setWidth("500px");
    menu.setAnimationEnabled(true);

    // Create a sub menu of recent documents
    MenuBar recentDocsMenu = new MenuBar(true);
    String[] recentDocs = new String[]{"Fishing in the desert.txt",
        "How to tame a wild parrot", "Idiots Guide to Emu Farms"};
    for (int i = 0; i < recentDocs.length; i++) {
      recentDocsMenu.addItem(recentDocs[i], menuCommand);
    }

    // Create the file menu
    MenuBar fileMenu = new MenuBar(true);
    fileMenu.setAnimationEnabled(true);
    menu.addItem(new MenuItem("File", fileMenu));
    String[] fileOptions = new String[]{"New", "Open", "Close", "Recents",
        "Exit"};

    for (int i = 0; i < fileOptions.length; i++) {
      if (i == 3) {
        fileMenu.addSeparator();
        fileMenu.addItem(fileOptions[i], recentDocsMenu);
        fileMenu.addSeparator();
      } else {
        fileMenu.addItem(fileOptions[i], menuCommand);
      }
    }

    // Create the edit menu
    MenuBar editMenu = new MenuBar(true);
    menu.addItem(new MenuItem("Edit", editMenu));
    String[] editOptions = new String[]{"Undo", "Redo", "Copy", "Cut",
        "Paste"};

    for (int i = 0; i < editOptions.length; i++) {
      editMenu.addItem(editOptions[i], menuCommand);
    }

    // Create the GWT menu
    MenuBar gwtMenu = new MenuBar(true);
    menu.addItem(new MenuItem("GWT", true, gwtMenu));
    String[] gwtOptions = new String[]{"Download", "Examples", "Source code",
        "GWT wit' the program"};

    for (int i = 0; i < gwtOptions.length; i++) {
      gwtMenu.addItem(gwtOptions[i], menuCommand);
    }

    // Create the help menu
    MenuBar helpMenu = new MenuBar(true);
    menu.addSeparator();
    menu.addItem(new MenuItem("Help", helpMenu));
    String[] helpOptions = new String[]{"Contents", "Fortune cookies",
        "About GWT"};

    for (int i = 0; i < helpOptions.length; i++) {
      helpMenu.addItem(helpOptions[i], menuCommand);
    }

    // Return the menu
    menu.ensureDebugId("cwMenuBar");
    return menu;

  }

  /**
   * Create a Dynamic tree. The code comes from the GWT show case :
   * http://gwt.google.com/samples/Showcase/Showcase.html#!CwTabPanel
   */
  @SuppressWarnings("deprecation")
  private DecoratedTabPanel createTabPanel() {
    // Create a tab panel
    DecoratedTabPanel tabPanel = new DecoratedTabPanel();
    tabPanel.setWidth("400px");
    tabPanel.setAnimationEnabled(true);

    // Add a home tab
    String[] tabTitles = {"Home", "GWT Logo", "More info"};
    HTML homeText = new HTML(
        "Click one of the tabs to see more content. <br/> You can drag me now !");
    tabPanel.add(homeText, tabTitles[0]);

    // Add a tab with an image
    /*
     * VerticalPanel vPanel = new VerticalPanel(); vPanel.add(new
     * Image(Showcase.images.gwtLogo()));
     */
    // TODO add gwt logo
    VerticalPanel vPanel = new VerticalPanel();
    vPanel.add(new com.google.gwt.user.client.ui.Image(Image.INSTANCE.gwtLogo()));
    tabPanel.add(new com.google.gwt.user.client.ui.Image(Image.INSTANCE.gwtLogo()), tabTitles[1]);

    // Add a tab
    HTML moreInfo = new HTML("Tabs are highly customizable using CSS.");
    tabPanel.add(moreInfo, tabTitles[2]);

    // Return the content
    tabPanel.selectTab(0);
    tabPanel.ensureDebugId("cwTabPanel");
    return tabPanel;

  }

}
