package fitnesse.responders.revisioncontrol;

import fitnesse.html.HtmlTableListingBuilder;
import fitnesse.html.HtmlTag;
import static fitnesse.revisioncontrol.RevisionControlOperation.STATUS;
import fitnesse.revisioncontrol.StatusResults;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;

public class StatusResponder extends RevisionControlResponder {
  public StatusResponder() {
    super(STATUS);
  }

  @Override
  protected void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag) {
    StatusResults results = page.execute(STATUS);
    makeResultsHtml(results, tag);
  }

  private void makeResultsHtml(StatusResults results, HtmlTag tag) {
    HtmlTableListingBuilder table = new RevisionControlDetailsTableBuilder(results, rootPagePath);
    tag.add(table.getTable());
  }
}