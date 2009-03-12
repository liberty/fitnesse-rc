package fitnesse.revisioncontrol.svn.client;

import fitnesse.html.HtmlElement;
import fitnesse.html.HtmlTag;
import static fitnesse.revisioncontrol.ActionStyle.*;
import fitnesse.revisioncontrol.RevisionControlDetail;
import fitnesse.revisioncontrol.RevisionControlHtmlUtils;
import fitnesse.revisioncontrol.StatusResults;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNStatus;

public class SVNStatusResultsHandler implements ISVNStatusHandler {
  private StatusResults results;

  public SVNStatusResultsHandler(StatusResults results) {
    this.results = results;
  }

  public void handleStatus(SVNStatus status) {
    HtmlTag localFileLabel = SVNStatusEventTags.getTag(status.getContentsStatus());
    HtmlTag localPropertiesLabel = SVNStatusEventTags.getTag(status.getPropertiesStatus());

    HtmlTag remoteFileLabel = SVNStatusEventTags.getTag(status.getRemoteContentsStatus());
    HtmlTag remotePropertiesLabel = SVNStatusEventTags.getTag(status.getRemotePropertiesStatus());

    HtmlTag lockLabel = getLockLabel(status);

    // Obtains the working revision number of the item
    HtmlElement workingRevisionLabel = getRevisionLabel(status.getRevision().getNumber());

    // Obtains the number of the revision when the item was last changed
    HtmlElement lastRevisionLabel = getRevisionLabel(status.getCommittedRevision().getNumber());

    // Obtains the number of the latest revision in the repository; only returned if different
    // than working copy revision numbers
    HtmlElement remoteRevisionLabel = getRemoteRevisionLabel(status);

    HtmlElement authorLabel = RevisionControlHtmlUtils.makeTextTag((status.getAuthor() != null ? status.getAuthor() : "?"));

    results.addDetail(new RevisionControlDetail(status.getFile().getAbsolutePath(),
      localFileLabel, localPropertiesLabel, remoteFileLabel, remotePropertiesLabel,
      lockLabel, workingRevisionLabel, lastRevisionLabel, remoteRevisionLabel, authorLabel));

    if (!hasOkStyle(localFileLabel) || !hasOkStyle(localPropertiesLabel) ||
      !hasOkStyle(remoteFileLabel) || !hasOkStyle(remotePropertiesLabel)||
      !hasOkStyle(lockLabel)) {
      results.setAlertsFound(true);
    }
  }

  private HtmlElement getRevisionLabel(long revision) {
    return RevisionControlHtmlUtils.makeTextTag((revision >= 0 ? String.valueOf(revision) : "?"));
  }

  private HtmlElement getRemoteRevisionLabel(SVNStatus status) {
    if (status.getRemoteRevision() != null)
      return getRevisionLabel(status.getRemoteRevision().getNumber());
    else
      return RevisionControlHtmlUtils.makeTextTag("", "");
  }

  private HtmlTag getLockLabel(SVNStatus status) {
    SVNLock localLock = status.getLocalLock();
    SVNLock remoteLock = status.getRemoteLock();
    HtmlTag lockLabel = RevisionControlHtmlUtils.makeTextTag("", "");

    if (localLock != null) {
      /*
      * at first suppose the file is locKed
      */
      lockLabel = RevisionControlHtmlUtils.makeTextTag("Local", OK);

      if (remoteLock != null && !remoteLock.getID().equals(localLock.getID())) {
        /*
        * if the lock-token of the local lock differs from the lock-token of the
        * remote lock - the lock was sTolen!
        */
        lockLabel = RevisionControlHtmlUtils.makeTextTag("Stolen:" + remoteLock.getOwner(), ALERT);
      }

      if (remoteLock == null) {
        /*
        * the local lock presents but there's no lock in the
        * repository - the lock was Broken. This is true only if
        * doStatus() was invoked with remote=true.
        */
        lockLabel = RevisionControlHtmlUtils.makeTextTag("Broken", WARNING);
      }
    } else if (remoteLock != null) {
      /*
      * the file is not locally locked but locked in the repository -
      * the lock token is in some Other working copy.
      */
      lockLabel = RevisionControlHtmlUtils.makeTextTag("Owner:" + remoteLock.getOwner(), WARNING);
    }
    return lockLabel;
  }

  private boolean hasOkStyle(HtmlTag localFileLabel) {
    String attr = localFileLabel.getAttribute("class");
    return attr == null || attr.length() == 0 || attr.equals(OK.getStyle());
  }
}