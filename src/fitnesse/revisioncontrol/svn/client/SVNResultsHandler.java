package fitnesse.revisioncontrol.svn.client;

import fitnesse.html.HtmlTag;
import fitnesse.revisioncontrol.OperationStatus;
import fitnesse.revisioncontrol.Results;
import fitnesse.revisioncontrol.RevisionControlDetail;
import fitnesse.revisioncontrol.RevisionControlException;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;

public class SVNResultsHandler implements ISVNEventHandler {
  private Results results;

  public SVNResultsHandler(Results results) {
    this.results = results;
  }

  public void handleEvent(SVNEvent event, double progress) {
    SVNErrorMessage errorMessage = event.getErrorMessage();
    if (errorMessage != null) {
      results.setStatus(OperationStatus.FAILURE);
      throw new RevisionControlException(errorMessage.getFullMessage());
    }

    SVNEventAction action = event.getAction();

    if (isCompletedAction(action)) {
      return;
    }

    String path = event.getFile().getAbsolutePath();
    HtmlTag actionName = SVNEventActionTags.getTag(action);
    results.addDetail(new RevisionControlDetail(path, actionName));
  }

  private boolean isCompletedAction(SVNEventAction action) {
    return action == SVNEventAction.COMMIT_COMPLETED ||
      action == SVNEventAction.UPDATE_COMPLETED ||
      action == SVNEventAction.STATUS_COMPLETED;
  }

  public void checkCancelled() throws SVNCancelException {
  }
}