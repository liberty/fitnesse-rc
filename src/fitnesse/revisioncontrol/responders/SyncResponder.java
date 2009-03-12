package fitnesse.responders.revisioncontrol;

import fitnesse.html.HtmlTag;
import static fitnesse.revisioncontrol.RevisionControlOperation.SYNC;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;

public class SyncResponder extends RevisionControlResponder {
  public SyncResponder() {
    super(SYNC);
  }

  @Override
  protected void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag) {
    page.execute(SYNC);
  }
}
