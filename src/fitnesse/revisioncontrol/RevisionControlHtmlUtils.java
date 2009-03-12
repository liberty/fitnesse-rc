package fitnesse.revisioncontrol;

import fitnesse.html.HtmlElement;
import fitnesse.html.HtmlTag;

public class RevisionControlHtmlUtils {
  static final String CONTENT_FILE_NAME = ".content.txt";
  static final String CONTENT_LABEL = " - <i>content</i>";
  static final String PROPERTIES_FILE_NAME = ".properties.xml";
  static final String PROPERTIES_LABEL = " - <i>properties</i>";

  public static HtmlTag makeTextTag(String text) {
    return new HtmlTag("span", text);
  }

  public static HtmlTag makeTextTag(String text, String style) {
    HtmlTag tag = new HtmlTag("label", " " + text + " ");
    tag.addAttribute("class", style);
    return tag;
  }

  public static HtmlTag makeTextTag(String text, ActionStyle style) {
    return makeTextTag(text, style.getStyle());
  }

  public static HtmlElement makePathLabel(String path, String rootPagePath) {
    if (path.startsWith(rootPagePath)) {
      path = path.substring(rootPagePath.length() + 1);
    }

    path = path.replaceAll("\\\\", ".");
    path = path.replaceAll("/", ".");
    path = path.replaceFirst(CONTENT_FILE_NAME, CONTENT_LABEL);
    path = path.replaceFirst(PROPERTIES_FILE_NAME, PROPERTIES_LABEL);
    return makeTextTag(path);
  }
}