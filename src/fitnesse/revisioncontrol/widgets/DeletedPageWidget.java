package fitnesse.revisioncontrol.widgets;

import fitnesse.html.HtmlElement;
import fitnesse.html.HtmlTag;
import fitnesse.html.HtmlUtil;
import fitnesse.html.TagGroup;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPagePath;
import fitnesse.wikitext.WikiWidget;
import fitnesse.wikitext.widgets.ParentWidget;

public class DeletedPageWidget extends WikiWidget {
  public static final String REGEXP = "^!deletedpage";

  private static final String DELETED_MESSAGE = "This page has been deleted, but the deletion " +
    "has not been checked into revision control.";

  public DeletedPageWidget(ParentWidget parent, String text) throws Exception {
    super(parent);
  }

  public String render() throws Exception {
    TagGroup group = new TagGroup();
    group.add(makeDeletedMessage());
    group.add(makeActionLinks());
    return group.html();
  }

  private HtmlElement makeDeletedMessage() {
    HtmlTag divTag = HtmlUtil.makeDivTag("centered");
    HtmlTag headTag = new HtmlTag("H3");
    headTag.add(DELETED_MESSAGE);
    divTag.add(headTag);
    return divTag;
  }

  private HtmlElement makeActionLinks() throws Exception {
    WikiPage wikiPage = getWikiPage();
    WikiPagePath fullPath = wikiPage.getPageCrawler().getFullPath(wikiPage);
    String renderedPath = PathParser.render(fullPath);

    HtmlTag divTag = HtmlUtil.makeDivTag("centered");
    divTag.add(HtmlUtil.makeLink(renderedPath + "?checkin", "Checkin"));
    divTag.add(" or ");
    divTag.add(HtmlUtil.makeLink(renderedPath + "?revert", "Revert"));
    divTag.add(" the deletion.");
    return divTag;
  }
}