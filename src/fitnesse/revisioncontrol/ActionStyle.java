package fitnesse.revisioncontrol;

public enum ActionStyle {
  OK("pass"),
  WARNING("error"),
  ALERT("fail");

  private final String style;

  ActionStyle(String style) {
    this.style = style;
  }

  public String getStyle() {
    return style;
  }
}
