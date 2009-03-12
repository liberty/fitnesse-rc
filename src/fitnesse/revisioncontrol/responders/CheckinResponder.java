package fitnesse.responders.revisioncontrol;

import fitnesse.html.HtmlTableListingBuilder;
import fitnesse.html.HtmlTag;
import fitnesse.revisioncontrol.NewRevisionResults;
import fitnesse.revisioncontrol.OperationStatus;
import static fitnesse.revisioncontrol.RevisionControlOperation.CHECKIN;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;

public class CheckinResponder extends RevisionControlResponder {
  public CheckinResponder() {
    super(CHECKIN);
  }

  @Override
  protected void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag) {
    NewRevisionResults results = page.execute(CHECKIN);
    makeResultsHtml(results, tag);
  }

  private void makeResultsHtml(NewRevisionResults results, HtmlTag tag) {
    if (results.getStatus().equals(OperationStatus.NOTHING_TO_DO)) {
      tag.add("No changes to check in");
    } else {
      HtmlTableListingBuilder table = new RevisionControlDetailsTableBuilder(results, rootPagePath);
      tag.add(table.getTable());
      tag.add("At revision " + results.getNewRevision());
    }
  }
}
