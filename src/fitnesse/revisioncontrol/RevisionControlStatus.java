package fitnesse.revisioncontrol;

import fitnesse.html.HtmlElement;

public class RevisionControlStatus {
  private String filePath;
  private HtmlElement[] elements;

  public RevisionControlStatus(String filePath, HtmlElement... elements) {
    this.filePath = filePath;
    this.elements = elements;
  }

  public String getFilePath() {
    return filePath;
  }

  public HtmlElement[] getElements() {
    return elements;
  }
}
