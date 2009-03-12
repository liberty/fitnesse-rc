package fitnesse.responders.revisioncontrol;

import fitnesse.html.HtmlTableListingBuilder;
import fitnesse.html.HtmlTag;
import fitnesse.revisioncontrol.OperationStatus;
import fitnesse.revisioncontrol.Results;
import fitnesse.revisioncontrol.RevisionControlException;
import static fitnesse.revisioncontrol.RevisionControlOperation.ADD;
import fitnesse.revisioncontrol.State;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;
import fitnesse.wiki.FileSystemPage;
import fitnesse.wiki.WikiPage;

public class AddResponder extends RevisionControlResponder {
  public AddResponder() {
    super(ADD);
  }

  @Override
  protected void beforeOperation(FileSystemPage page) {
    verifyParentIsUnderVersionControl(page);
  }

  private void verifyParentIsUnderVersionControl(FileSystemPage page) {
    final WikiPage parent = page.getParent();
    if (page == parent)
      return;

    final RevisionControlledFileSystemPage parentPage = (RevisionControlledFileSystemPage) parent;
    final State parentState = parentPage.getState();
    if (parentState == null || !parentState.isUnderRevisionControl())
      throw new RevisionControlException("A page is being added, but its parent is not under revision control");
    else
      verifyParentIsUnderVersionControl(parentPage);
  }

  @Override
  protected void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag) {
    Results results = page.execute(ADD);
    makeResultsHtml(results, tag);
  }


  private void makeResultsHtml(Results results, HtmlTag tag) {
    if (results.getStatus() == OperationStatus.NOTHING_TO_DO) {
      tag.add("Nothing to add to revision control");
    } else {
      HtmlTableListingBuilder table = new RevisionControlDetailsTableBuilder(results, rootPagePath);
      tag.add(table.getTable());
    }
  }
}
