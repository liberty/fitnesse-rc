package fitnesse.revisioncontrol.responders;

import fitnesse.html.HtmlTableListingBuilder;
import fitnesse.html.HtmlTag;
import fitnesse.revisioncontrol.NewRevisionResults;
import static fitnesse.revisioncontrol.RevisionControlOperation.UPDATE;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;

public class UpdateResponder extends RevisionControlResponder {
  public UpdateResponder() {
    super(UPDATE);
  }

  @Override
  protected void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag) {
    NewRevisionResults results = page.execute(UPDATE);
    makeResultsHtml(results, tag);
  }

  private void makeResultsHtml(NewRevisionResults results, HtmlTag tag) {
    if (results.getDetails().size() == 0) {
      tag.add("At revision " + results.getNewRevision());
    }
    else {
      HtmlTableListingBuilder table = new RevisionControlDetailsTableBuilder(results, rootPagePath);
      tag.add(table.getTable());

      tag.add("Updated to revision " + results.getNewRevision());
    }
  }
}
