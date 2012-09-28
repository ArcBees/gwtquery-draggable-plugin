package gwtquery.plugins.draggable.client;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import gwtquery.plugins.draggable.client.DraggableOptions.AxisOption;
import gwtquery.plugins.draggable.client.DraggableOptions.CursorAt;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;

import java.util.HashMap;
import java.util.Map;

public class DraggableOptionsPanel extends Composite {

  @UiTemplate(value = "DraggableOptionsPanel.ui.xml")
  interface DraggableOptionsPanelUiBinder extends
      UiBinder<Widget, DraggableOptionsPanel> {
  }

  private static DraggableOptionsPanelUiBinder uiBinder = GWT
      .create(DraggableOptionsPanelUiBinder.class);

  private static Map<String, Object> contaimentOptions;
  private static Map<String, CursorAt> cursorAtOptions;

  static {
    cursorAtOptions = new HashMap<String, CursorAt>();
    cursorAtOptions.put("None", null);
    cursorAtOptions.put("at top left", new CursorAt(0, 0, null, null));
    cursorAtOptions.put("at top right", new CursorAt(0, null, null, 0));
    cursorAtOptions.put("at bottom left", new CursorAt(null, 0, 0, null));
    cursorAtOptions.put("at bottom right", new CursorAt(null, null, 0, 0));
    cursorAtOptions.put("at center", new CursorAt(75, 75, null, null));

    contaimentOptions = new HashMap<String, Object>();
    contaimentOptions.put("None", null);
    contaimentOptions.put("parent", "parent");
    contaimentOptions.put("demo box", ".demo");
    // contaimentOptions.put("body", "body");
    contaimentOptions.put("window", "window");
    contaimentOptions.put("document", "document");
    contaimentOptions.put(
        "on a virtual box (position:left=300px,top=500px,height=width=300px) ",
        new int[] { 300, 500, 600, 800 });

  }

  private Element draggable;

  @UiField
  ListBox helperListBox;
  @UiField
  ListBox axisListBox;
  @UiField
  ListBox containmentListBox;
  @UiField
  ListBox cursorAtListBox;
  @UiField
  ListBox cursorListBox;
  @UiField
  ListBox gridListBox;
  @UiField
  TextBox delayBox;
  @UiField
  TextBox distanceBox;
  @UiField
  CheckBox disabledCheckBox;
  @UiField
  CheckBox handleCheckBox;
  @UiField
  TextBox opacityBox;
  @UiField
  CheckBox scrollCheckBox;
  @UiField
  TextBox scrollSensivityBox;
  @UiField
  TextBox scrollSpeedBox;
  @UiField
  ListBox revertListBox;
  @UiField
  TextBox revertDurationTextBox;

  public DraggableOptionsPanel(Element draggable) {
    this.draggable = draggable;
    initWidget(uiBinder.createAndBindUi(this));
    // use a deferred command to ensure to init the object when the element is
    // draggabel
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        init();
      }
    });

  }

  @UiHandler(value = "axisListBox")
  public void onAxisChange(ChangeEvent e) {
    AxisOption axis = AxisOption.valueOf(axisListBox.getValue(axisListBox
        .getSelectedIndex()));
    getOptions().setAxis(axis);
  }

  @UiHandler(value = "containmentListBox")
  public void onContainmentChange(ChangeEvent e) {
    String containment = containmentListBox.getValue(containmentListBox
        .getSelectedIndex());
    Object realContainment = contaimentOptions.get(containment);
    if (realContainment instanceof String) {
      getOptions().setContainment((String) realContainment);
    } else {
      getOptions().setContainment((int[]) realContainment);
    }
  }

  @UiHandler(value = "cursorAtListBox")
  public void onCursorAtChange(ChangeEvent e) {
    String cursorAt = cursorAtListBox.getValue(cursorAtListBox
        .getSelectedIndex());

    getOptions().setCursorAt(cursorAtOptions.get(cursorAt));

  }

  @UiHandler(value = "cursorListBox")
  public void onCursorChange(ChangeEvent e) {
    Cursor c = Cursor.valueOf(cursorListBox.getValue(cursorListBox
        .getSelectedIndex()));
    getOptions().setCursor(c);

  }

  @UiHandler(value = "delayBox")
  public void onDelayChange(ValueChangeEvent<String> e) {
    getOptions().setDelay(new Integer(e.getValue()));
  }

  @UiHandler(value = "disabledCheckBox")
  public void onDisabledChange(ValueChangeEvent<Boolean> e) {
    getOptions().setDisabled(e.getValue());
  }

  @UiHandler(value = "distanceBox")
  public void onDistanceChange(ValueChangeEvent<String> e) {
    Integer distance;
    try {
      distance = new Integer(e.getValue());
    } catch (NumberFormatException ex) {
      Window.alert("Please specify a correct number for distance");
      return;
    }
    getOptions().setDistance(distance);
  }

  @UiHandler(value = "gridListBox")
  public void onGridChange(ChangeEvent e) {
    String grid = gridListBox.getValue(gridListBox.getSelectedIndex());
    if ("None".equals(grid)) {
      getOptions().setGrid(null);
    } else {
      String[] dimension = grid.split(",");
      getOptions().setGrid(
          new int[] { new Integer(dimension[0]), new Integer(dimension[1]) });
    }
  }

  @UiHandler(value = "helperListBox")
  public void onHelperChange(ChangeEvent e) {
    HelperType type = HelperType.valueOf(helperListBox.getValue(helperListBox
        .getSelectedIndex()));

    if (type == HelperType.ELEMENT) {
      GQuery helper = $("<div class=\"myHelper\" style=\"width: 150px;height: 150px;\">I'm a custom helper</div>");
      getOptions().setHelper(helper);
    } else {
      getOptions().setHelper(type);
    }
  }

  @UiHandler(value = "handleCheckBox")
  public void onMultiSelectChange(ValueChangeEvent<Boolean> e) {
    if (e.getValue()) {
      getOptions().setHandle("#handle");
    } else {
      getOptions().setHandle(null);
    }
  }

  @UiHandler(value = "opacityBox")
  public void onOpacityChange(ValueChangeEvent<String> e) {
    String opacityString = e.getValue();

    Float opacity;
    if (opacityString == null || opacityString.length() == 0) {
      opacity = null;
    } else {
      try {
        opacity = new Float(e.getValue());
      } catch (NumberFormatException ex) {
        Window.alert("Please specify a correct number for opacity");
        return;
      }
    }
    if (opacity != null && opacity > 1) {
      Window.alert("Opacity must be below than 1.");
      return;
    }
    getOptions().setOpacity(opacity);
  }

  @UiHandler(value = "revertDurationTextBox")
  public void onRevertDurationChange(ValueChangeEvent<String> e) {
    String revertDuration = e.getValue();
    Integer revertDurationInt;
    if (revertDuration == null || revertDuration.length() == 0) {
      revertDurationInt = null;
    } else {
      try {
        revertDurationInt = new Integer(e.getValue());
      } catch (NumberFormatException ex) {
        Window.alert("Please specify a correct number for the revert duration");
        return;
      }
    }
    getOptions().setRevertDuration(revertDurationInt);
  }

  @UiHandler(value = "revertListBox")
  public void onRevertChange(ChangeEvent e) {
    String revert = revertListBox.getValue(revertListBox.getSelectedIndex());
    getOptions().setRevert(RevertOption.valueOf(revert));
  }

  @UiHandler(value = "scrollCheckBox")
  public void onScrollChange(ValueChangeEvent<Boolean> e) {
    boolean scroll = e.getValue();
    getOptions().setScroll(scroll);
    scrollSensivityBox.setEnabled(scroll);
    scrollSpeedBox.setEnabled(scroll);

  }

  @UiHandler(value = "scrollSensivityBox")
  public void onScrollSensitivityChange(ValueChangeEvent<String> e) {
    Integer scrollSensitivity;
    try {
      scrollSensitivity = new Integer(e.getValue());
    } catch (NumberFormatException ex) {
      Window.alert("Please specify a correct number for scrollSensitivity");
      return;
    }
    getOptions().setScrollSensitivity(scrollSensitivity);
  }

  @UiHandler(value = "scrollSpeedBox")
  public void onScrollSpeedChange(ValueChangeEvent<String> e) {
    Integer scrollSpeed;
    try {
      scrollSpeed = new Integer(e.getValue());
    } catch (NumberFormatException ex) {
      Window.alert("Please specify a correct number for scrollSpeed");
      return;
    }
    getOptions().setScrollSpeed(scrollSpeed);
  }

  private DraggableOptions getOptions() {
    return $(draggable).as(Draggable.Draggable).options();
  }

  private void init() {
    DraggableOptions options = getOptions();
    int i = 0;
    for (HelperType h : HelperType.values()) {
      helperListBox.addItem(h.name());
      if (h == options.getHelperType()) {
        helperListBox.setSelectedIndex(i);
      }
      i++;
    }

    delayBox.setValue("" + options.getDelay(), false);

    distanceBox.setValue("" + options.getDistance(), false);

    disabledCheckBox.setValue(options.isDisabled(), false);

    handleCheckBox.setValue(false, false);

    axisListBox.addItem(AxisOption.NONE.name());
    axisListBox.addItem(AxisOption.X_AXIS.name());
    axisListBox.addItem(AxisOption.Y_AXIS.name());
    axisListBox.setSelectedIndex(0);

    if (options.getOpacity() != null) {
      opacityBox.setValue("" + options.getOpacity());
    }

    scrollCheckBox.setValue(options.isScroll());
    scrollSensivityBox.setValue("" + options.getScrollSensitivity());
    scrollSpeedBox.setValue("" + options.getScrollSpeed());

    i = 0;
    for (Cursor c : Cursor.values()) {
      cursorListBox.addItem(c.name());
      if (c == options.getCursor()) {
        cursorListBox.setSelectedIndex(i);
      }
      i++;
    }

    i = 0;
    for (String s : contaimentOptions.keySet()) {
      containmentListBox.addItem(s);
      if (s.equals("None")) {
        containmentListBox.setSelectedIndex(i);
      }
      i++;
    }

    i = 0;
    for (String s : cursorAtOptions.keySet()) {
      cursorAtListBox.addItem(s);
      if (s.equals("None")) {
        cursorAtListBox.setSelectedIndex(i);
      }
      i++;
    }

    gridListBox.addItem("None", "None");
    gridListBox.addItem("snap draggable to a 20x20 grid", "20,20");
    gridListBox.addItem("snap draggable to a 40x40 grid", "40,40");
    gridListBox.addItem("snap draggable to a 80x80 grid", "80,80");
    gridListBox.addItem("snap draggable to a 100x100 grid", "100,100");
    gridListBox.setSelectedIndex(0);

    revertListBox.addItem("never", RevertOption.NEVER.name());
    revertListBox.addItem("always", RevertOption.ALWAYS.name());
    revertListBox.addItem("on valid drop (useful with droppable plug-in)",
        RevertOption.ON_VALID_DROP.name());
    revertListBox.addItem("on invalid drop (useful with droppable plug-in)",
        RevertOption.ON_INVALID_DROP.name());
    revertListBox.setSelectedIndex(0);

    revertDurationTextBox.setValue("" + options.getRevertDuration());
  }

}