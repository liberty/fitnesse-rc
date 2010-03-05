package fitnesse.revisioncontrol.svn.client;

import fitnesse.revisioncontrol.*;
import fitnesse.revisioncontrol.svn.SVNState;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;
import java.util.*;

public class SVNClient {
  private final SVNClientManager clientManager;
  private final Map<SVNStatusType, State> states = new HashMap<SVNStatusType, State>();
  private final Map<SVNStatusType, String> errorMsgs = new HashMap<SVNStatusType, String>();

  public SVNClient(Properties properties) {
    initializeRepositories();
    clientManager = initializeClientManager(properties);
    initializeSVNStatusTypeToStateMap();
    initializeUnhandledSVNStatusTypeToErrorMsgsMap();
  }

  private void initializeRepositories() {
    // for DAV (over http and https)
    DAVRepositoryFactory.setup();
    // for svn (over svn and svn+ssh)
    SVNRepositoryFactoryImpl.setup();
    // for local (file)
    FSRepositoryFactory.setup();
  }

  private SVNClientManager initializeClientManager(Properties properties) {
    String userName = properties.getProperty("SvnUser");
    String password = properties.getProperty("SvnPassword");
    if (userName == null || password == null) {
      return SVNClientManager.newInstance();
    } else {
      return SVNClientManager.newInstance(null, userName, password);
    }
  }

  public void doUpdate(File wcPath, NewRevisionResults results) throws SVNException {
    SVNUpdateClient client = clientManager.getUpdateClient();
    client.setIgnoreExternals(true);
    setEventHandler(results, client);

    long revision = client.doUpdate(wcPath, SVNRevision.HEAD, SVNDepth.INFINITY, false, true);
    results.setNewRevision(revision);

    clearEventHandler(client);
  }

  public void doCommit(File wcPath, String commitMessage, NewRevisionResults results) throws SVNException {
    SVNCommitClient client = clientManager.getCommitClient();
    setEventHandler(results, client);

    SVNCommitInfo svnInfo = client.doCommit(new File[] {wcPath},
      false, commitMessage, null, null, false, false, SVNDepth.INFINITY);

    long newRevision = svnInfo.getNewRevision();
    if (newRevision == -1)
      results.setStatus(OperationStatus.NOTHING_TO_DO);
    else
      results.setNewRevision(newRevision);

    clearEventHandler(client);
  }

  public void doAdd(File wcPath, Results results) throws SVNException {
    SVNWCClient client = clientManager.getWCClient();
    setEventHandler(results, client);

    client.doAdd(wcPath, false, false, false, SVNDepth.INFINITY, false, false);

    clearEventHandler(client);
  }

  public void doDelete(File wcPath, boolean force, Results results) throws SVNException {
    SVNWCClient client = clientManager.getWCClient();
    setEventHandler(results, client);

    client.doDelete(wcPath, force, false);

    clearEventHandler(client);
  }

  public List doLog(File wcPath) throws SVNException {
    LogEntryHandler handler = new LogEntryHandler();

    clientManager.getLogClient().doLog(new File[]{wcPath},
      SVNRevision.BASE, SVNRevision.HEAD,
      false, false, 100, handler);

    return handler.logEntries;
  }

  public void doRevert(File wcPath, Results results) throws SVNException {
    SVNWCClient client = clientManager.getWCClient();
    setEventHandler(results, client);

    client.doRevert(new File[]{wcPath}, SVNDepth.INFINITY, null);

    clearEventHandler(client);

    if (results.getDetails().size() == 0)
      results.setStatus(OperationStatus.NOTHING_TO_DO);
  }

  public void doStatus(File wcPath, StatusResults results) throws SVNException {
    SVNStatusClient client = clientManager.getStatusClient();
    SVNStatusResultsHandler handler = new SVNStatusResultsHandler(results);
    client.doStatus(wcPath, SVNRevision.HEAD, SVNDepth.INFINITY,
      true, true, false, false, handler, null);
  }

  private SVNStatus doLocalStatus(File wcPath) throws SVNException {
    return clientManager.getStatusClient().doStatus(wcPath, false);
  }

  public void doLock(File wcPath, Results results) throws SVNException {
    SVNWCClient client = clientManager.getWCClient();
    setEventHandler(results, client);

    client.doLock(FileUtils.getPathsFromRoot(wcPath, false), false, null);

    clearEventHandler(client);
  }

  public void doUnlock(File wcPath) throws SVNException {
    SVNWCClient client = clientManager.getWCClient();
    client.doUnlock(FileUtils.getPathsFromRoot(wcPath, false), true);
  }

  public void doUnlock(File wcPath, Results results) throws SVNException {
    SVNWCClient client = clientManager.getWCClient();
    setEventHandler(results, client);

    client.doUnlock(FileUtils.getPathsFromRoot(wcPath, false), true);

    clearEventHandler(client);
  }

  public void doMove(File src, File dest) throws SVNException {
    clientManager.getMoveClient().doMove(src, dest);
  }

   private static class LogEntryHandler implements ISVNLogEntryHandler {
    public List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();

    public void handleLogEntry(SVNLogEntry logEntry) {
      logEntries.add(logEntry);
    }
  }

  public State getState(File pagePath) {
    SVNStatusType status;
    try {
       SVNStatus svnStatus = doLocalStatus(pagePath);
       if (svnStatus == null) {
         return SVNState.UNKNOWN;
       }
       status = svnStatus.getContentsStatus();
    } catch (SVNException e) {
      return SVNState.UNKNOWN;
    }

    State state = this.states.get(status);
    if (state != null) {
      return state;
    }
    throwExceptionForUnhandledStatuses(status, pagePath);
    throw new RevisionControlException(pagePath + " is in an unknown state. Please update the file and try again.");
  }

   public boolean hasLocalLock(File pagePath) {
      try {
         SVNStatus svnStatus = doLocalStatus(pagePath);
         return svnStatus != null && svnStatus.getLocalLock() != null;
      } catch (SVNException e) {
         throw new RevisionControlException(pagePath + " is in an unknown state. Can not determine local lock ownership.");
      }
   }

  private void throwExceptionForUnhandledStatuses(final SVNStatusType status, final File fileName) {
    String errorMsg = this.errorMsgs.get(status);
    if (errorMsg != null) {
      throw new RevisionControlException(fileName.getAbsolutePath() + errorMsg);
    }
  }

  private void setEventHandler(Results results, SVNBasicClient client) {
    SVNResultsHandler handler = new SVNResultsHandler(results);
    client.setEventHandler(handler);
  }

  private void clearEventHandler(SVNBasicClient client) {
    client.setEventHandler(null);
  }

  private void initializeSVNStatusTypeToStateMap() {
    this.states.put(null, SVNState.UNKNOWN);
    this.states.put(SVNStatusType.STATUS_UNVERSIONED, SVNState.UNKNOWN);
    this.states.put(SVNStatusType.STATUS_NONE, SVNState.UNKNOWN);
    this.states.put(SVNStatusType.STATUS_ADDED, SVNState.ADDED);
    this.states.put(SVNStatusType.STATUS_DELETED, SVNState.DELETED);
    this.states.put(SVNStatusType.STATUS_NORMAL, SVNState.VERSIONED);
    this.states.put(SVNStatusType.STATUS_MODIFIED, SVNState.VERSIONED);
    this.states.put(SVNStatusType.STATUS_REPLACED, SVNState.VERSIONED);
    this.states.put(SVNStatusType.STATUS_IGNORED, SVNState.UNKNOWN);
    this.states.put(SVNStatusType.MERGED, SVNState.VERSIONED);
  }

  private void initializeUnhandledSVNStatusTypeToErrorMsgsMap() {
    this.errorMsgs.put(SVNStatusType.STATUS_CONFLICTED, " has conflicts");
    this.errorMsgs.put(SVNStatusType.STATUS_MISSING, " is missing from the working copy");
    this.errorMsgs.put(SVNStatusType.STATUS_IGNORED, " is marked to be Ignored by SVN. Cannot perform SVN operations on ignored files");
    this.errorMsgs.put(SVNStatusType.STATUS_EXTERNAL, " is an SVN External File. Cannot perform local SVN operatiosn on external files");
    this.errorMsgs.put(SVNStatusType.STATUS_INCOMPLETE, " is marked as incomplete by SVN. Please update the file and try again");
    this.errorMsgs.put(SVNStatusType.STATUS_OBSTRUCTED, " is marked as obstructed by SVN. Please clean the working copy and try again");
  }
}