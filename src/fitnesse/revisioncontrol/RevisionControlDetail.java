package fitnesse.revisioncontrol;

import fitnesse.html.HtmlElement;
import fitnesse.html.HtmlTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RevisionControlDetail {
  private String filePath;
  private List<HtmlElement> actionTags;

  public RevisionControlDetail(String filePath, String... actions) {
    this.filePath = filePath;
    this.actionTags = new ArrayList<HtmlElement>(actions.length);
    for (String action : actions) {
      actionTags.add(new HtmlTag(action));
    }
  }

  public RevisionControlDetail(String filePath, HtmlElement... actionTags) {
    this.filePath = filePath;
    this.actionTags = Arrays.asList(actionTags);
  }

  public String getFilePath() {
    return filePath;
  }

  public List<HtmlElement> getActionTags() {
    return Collections.unmodifiableList(actionTags);
  }
}
