package fitnesse.revisioncontrol.svn;

import fitnesse.revisioncontrol.*;
import fitnesse.revisioncontrol.svn.client.SVNClient;

import java.io.File;
import java.util.Properties;

public class SVNRevisionController implements RevisionController {
  private final SVNClient client;
  private boolean debug;

  public SVNRevisionController() {
    this(new Properties());
  }

  public SVNRevisionController(final Properties properties) {
    client = new SVNClient(properties);
    debug = Boolean.parseBoolean(properties.getProperty("RevisionControlDebug"));
  }

  public Results add(final String pagePath) {
    try {
      File file = getFileFromPath(pagePath);
      Results results = new SVNResults();
      if (!isUnderVersionControl(file)) {
        debug("add", file);
        client.doAdd(file, results);
      }
      else {
        results.setStatus(OperationStatus.NOTHING_TO_DO);
      }
      return results;
    } catch (Exception e) {
      throw revisionControlException("add", pagePath, e);
    }
  }

  public NewRevisionResults checkin(final String pagePath, String commitMessage) {
    try {
      File file = getFileFromPath(pagePath);
      NewRevisionResults results = new SVNNewRevisionResults();
      debug("checkin", file);
      client.doCommit(file, (commitMessage == null || commitMessage.isEmpty()) ? "Auto Commit" : commitMessage, results);
      return results;
    } catch (Exception e) {
      throw revisionControlException("checkin", pagePath, e);
    }
  }

  public Results checkout(final String pagePath) {
    throw new RevisionControlException("This operation is currently not supported");
  }

  public Results delete(final String pagePath) {
    try {
      File file = getFileFromPath(pagePath);
      Results results = new SVNResults();
      debug("delete", file);
      client.doDelete(file, false, results);
      return results;
    } catch (Exception e) {
      throw revisionControlException("delete", pagePath, e);
    }
  }

  public Results revert(final String pagePath) {
    try {
      final File file = getFileFromPath(pagePath);
      Results results = new SVNResults();
      debug("revert", file);
      client.doRevert(file, results);
      client.doUnlock(file);
      return results;
    } catch (Exception e) {
      throw revisionControlException("revert", pagePath, e);
    }
  }

  public NewRevisionResults update(final String pagePath) {
    try {
      final File file = getFileFromPath(pagePath);
      NewRevisionResults results = new SVNNewRevisionResults();
      debug("update", file);
      client.doUpdate(file, results);
      return results;
    } catch (Exception e) {
      throw revisionControlException("update", pagePath, e);
    }
  }

  public Results lock(String pagePath) {
    try {
      final File file = getFileFromPath(pagePath);
      Results results = new SVNResults();
      debug("lock", file);
      client.doLock(file, results);
      return results;
    } catch (Exception e) {
      throw revisionControlException("lock", pagePath, e);
    }
  }

  public Results unlock(String pagePath) {
    try {
      final File file = getFileFromPath(pagePath);
      Results results = new SVNResults();
      debug("unlock", file);
      client.doUnlock(file, results);
      return results;
    } catch (Exception e) {
      throw revisionControlException("unlock", pagePath, e);
    }
  }

  public StatusResults getStatus(String pagePath) {
    try {
      final File file = getFileFromPath(pagePath);
      StatusResults results = new SVNStatusResults();
      debug("status", file);
      client.doStatus(file, results);
      return results;
    } catch (Exception e) {
      throw revisionControlException("status", pagePath, e);
    }
  }

   public boolean hasLocalLock(String pagePath) {
      try {
        final File file = getFileFromPath(pagePath);
        debug("hasLocalLock", file);
        return client.hasLocalLock(file);
      } catch (Exception e) {
        throw revisionControlException("hasLocalLock", pagePath, e);
      }
   }
   
  public OperationStatus move(final File src, final File dest) {
    try {
      client.doMove(src, dest);
      return OperationStatus.SUCCESS;
    } catch (Exception e) {
      throw new RevisionControlException("Unable to move file : " + src.getAbsolutePath() + " to location " + dest.getAbsolutePath(), e);
    }
  }

  public boolean isExternalRevisionControlEnabled() {
    return true;
  }

  private File getFileFromPath(String pagePath) {
    return new File(pagePath);
  }

  public State getState(final String pagePath) {
    File file = getFileFromPath(pagePath);
    debug("get state for", file);
    return client.getState(file);
  }

  private void debug(String operation, File file) {
    if (debug)
      System.out.println("About to " + operation + " page: " + file.getAbsolutePath());
  }

  private RevisionControlException revisionControlException(String operation, String pagePath, Exception e) {
    return new RevisionControlException("Unable to " + operation + " page '" + pagePath + "':" + e.getMessage(), e);
  }

  private boolean isUnderVersionControl(final File file) {
    return getState(file.getAbsolutePath()).isUnderRevisionControl();
  }

   class SVNResults extends Results {
    SVNResults() {
      setDetailLabels("Name", "Actions");
    }
  }

  class SVNNewRevisionResults extends NewRevisionResults {
    SVNNewRevisionResults() {
      setDetailLabels("Name", "Actions");
    }
  }

  class SVNStatusResults extends StatusResults {
    SVNStatusResults() {
      setDetailLabels("Name", "File", "Props", "RemoteFile", "RemoteProps",
      "LockStatus", "WCRev", "LastRev", "RemoteRev", "Author");
    }
  }
}