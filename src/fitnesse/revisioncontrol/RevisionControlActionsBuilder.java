package fitnesse.revisioncontrol;

import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;
import fitnesse.wiki.PageData;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPageAction;

import java.util.ArrayList;
import java.util.List;

public class RevisionControlActionsBuilder {
  public static List<WikiPageAction> getRevisionControlActions(String pageName, PageData pageData) throws Exception {
    List<WikiPageAction> actions = new ArrayList<WikiPageAction>();
    if (pageData.hasAttribute("Edit") || pageData.hasAttribute("WikiImport")) {
      final WikiPage wikiPage = pageData.getWikiPage();
      if (shouldDisplayRevisionControlActions(wikiPage)) {
        actions.add(new WikiPageAction(null, "Revision Control"));
        final State state = ((RevisionControllable) wikiPage).getState();
        final RevisionControlOperation[] operations = state.operations();
        for (final RevisionControlOperation operation : operations)
          actions.add(operation.makeAction(pageName));
      }
    }
    return actions;
  }

  private static boolean shouldDisplayRevisionControlActions(WikiPage wikiPage) throws Exception {
    return wikiPage instanceof RevisionControlledFileSystemPage &&
      ((RevisionControllable) wikiPage).isExternallyRevisionControlled();
  }
}
