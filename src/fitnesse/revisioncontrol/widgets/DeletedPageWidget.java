package fitnesse.revisioncontrol.widgets;

import fitnesse.html.HtmlElement;
import fitnesse.html.HtmlTag;
import fitnesse.html.HtmlUtil;
import fitnesse.html.TagGroup;
import fitnesse.revisioncontrol.CheckinOperationHtmlBuilder;
import fitnesse.revisioncontrol.RevisionControlOperation;
import fitnesse.revisioncontrol.responders.CheckinResponder;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPagePath;
import fitnesse.wikitext.WikiWidget;
import fitnesse.wikitext.widgets.ParentWidget;

import java.util.List;

import static fitnesse.revisioncontrol.CheckinOperationHtmlBuilder.CLEAR_FROM_PARENT_CACHE;
import static fitnesse.revisioncontrol.RevisionControlOperation.CHECKIN;

public class DeletedPageWidget extends WikiWidget {
  public static final String REGEXP = "^!deletedpage";

  private static final String DELETED_MESSAGE = "This page has been deleted, but the deletion " +
    "has not been checked into revision control.<br/>You must checkin or revert.";

  public DeletedPageWidget(ParentWidget parent, String text) throws Exception {
    super(parent);
  }

  public String render() throws Exception {
    TagGroup group = new TagGroup();
    group.add(makeDeletedMessage());
    group.add(HtmlUtil.HR);
    group.add(makeCheckinHtml());
    group.add(makeRevertHtml(group));
    return group.html();
  }

   private HtmlTag makeCheckinHtml() throws Exception {
      return new DeleteCheckinOperationHtmlBuilder().makeHtml(getPagePath());
   }

   private HtmlTag makeRevertHtml(TagGroup group) throws Exception {
      return RevisionControlOperation.REVERT.makeHtml(getPagePath());
   }

   private HtmlElement makeDeletedMessage() {
    HtmlTag divTag = HtmlUtil.makeDivTag("centered");
    HtmlTag headTag = new HtmlTag("H3");
    headTag.add(DELETED_MESSAGE);
    divTag.add(headTag);
    return divTag;
  }

   private String getPagePath() throws Exception {
      WikiPage wikiPage = getWikiPage();
      WikiPagePath fullPath = wikiPage.getPageCrawler().getFullPath(wikiPage);
      return PathParser.render(fullPath);
   }

   static class DeleteCheckinOperationHtmlBuilder extends CheckinOperationHtmlBuilder {

      public DeleteCheckinOperationHtmlBuilder() {
         super(CHECKIN);
      }

      @Override
      protected List<HtmlTag> getHtmlTagsToAddToForm() {
         List<HtmlTag> tags = super.getHtmlTagsToAddToForm();
         tags.add(HtmlUtil.makeInputTag("hidden", CLEAR_FROM_PARENT_CACHE, "yes"));
         return tags;
      }
   }
}