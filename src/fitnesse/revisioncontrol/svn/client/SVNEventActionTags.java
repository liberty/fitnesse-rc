/*
 * Copyright (c) 2006 Sabre Holdings. All Rights Reserved.
 */

package fitnesse.revisioncontrol.svn.client;

import fitnesse.html.HtmlTag;
import fitnesse.revisioncontrol.ActionStyle;
import static fitnesse.revisioncontrol.ActionStyle.*;
import fitnesse.revisioncontrol.RevisionControlHtmlUtils;
import org.tmatesoft.svn.core.wc.SVNEventAction;

import java.util.HashMap;
import java.util.Map;

public class SVNEventActionTags {
  private static Map<SVNEventAction, HtmlTag> tagsByAction = new HashMap<SVNEventAction, HtmlTag>();

  static {
    registerActionTag(SVNEventAction.PROGRESS, "Progress", ALERT);
    registerActionTag(SVNEventAction.ADD, "Added", OK);
    registerActionTag(SVNEventAction.COPY, "Copied", OK);
    registerActionTag(SVNEventAction.DELETE, "Deleted", OK);
    registerActionTag(SVNEventAction.RESTORE, "Restored", OK);
    registerActionTag(SVNEventAction.REVERT, "Reverted", OK);
    registerActionTag(SVNEventAction.FAILED_REVERT, "Revert failed", ALERT);
    registerActionTag(SVNEventAction.RESOLVED, "Resolved", OK);
    registerActionTag(SVNEventAction.SKIP, "Skipped", WARNING);
    registerActionTag(SVNEventAction.UPDATE_DELETE, "Deleted", OK);
    registerActionTag(SVNEventAction.UPDATE_ADD, "Added", OK);
    registerActionTag(SVNEventAction.UPDATE_UPDATE, "Updated", OK);
    tagsByAction.put(SVNEventAction.UPDATE_NONE, RevisionControlHtmlUtils.makeTextTag(" "));
    tagsByAction.put(SVNEventAction.UPDATE_COMPLETED, RevisionControlHtmlUtils.makeTextTag(" "));
    registerActionTag(SVNEventAction.UPDATE_EXTERNAL, "Externally updated", OK);
    registerActionTag(SVNEventAction.UPDATE_REPLACE, "Replaced", OK);
    registerActionTag(SVNEventAction.UPDATE_EXISTS, "Existing", WARNING);
    registerActionTag(SVNEventAction.STATUS_COMPLETED, "Completed", OK);
    registerActionTag(SVNEventAction.STATUS_EXTERNAL, "External", OK);
    registerActionTag(SVNEventAction.COMMIT_MODIFIED, "Modified", OK);
    registerActionTag(SVNEventAction.COMMIT_ADDED, "Added", OK);
    registerActionTag(SVNEventAction.COMMIT_DELETED, "Deleted", OK);
    registerActionTag(SVNEventAction.COMMIT_REPLACED, "Replaced", OK);
    registerActionTag(SVNEventAction.COMMIT_DELTA_SENT, "Delta sent", OK);
    registerActionTag(SVNEventAction.COMMIT_COMPLETED, "Completed", OK);
    registerActionTag(SVNEventAction.CHANGELIST_SET, "Added to changelist", OK);
    registerActionTag(SVNEventAction.CHANGELIST_MOVED, "Moved changelists", OK);
    registerActionTag(SVNEventAction.CHANGELIST_CLEAR, "Removed from changelist", OK);
    registerActionTag(SVNEventAction.ANNOTATE, "Annotating", OK);
    registerActionTag(SVNEventAction.LOCKED, "Locked", OK);
    registerActionTag(SVNEventAction.UNLOCKED, "Unlocked", OK);
    registerActionTag(SVNEventAction.LOCK_FAILED, "Lock Failed", ALERT);
    registerActionTag(SVNEventAction.UNLOCK_FAILED, "Unlock Failed", ALERT);
    registerActionTag(SVNEventAction.MERGE_BEGIN, "Merge began", OK);
    registerActionTag(SVNEventAction.FOREIGN_MERGE_BEGIN, "Foreign merge began", OK);
    registerActionTag(SVNEventAction.UPGRADE, "Working copy upgraded", OK);
  }

  private static void registerActionTag(SVNEventAction action, String description, ActionStyle style) {
    tagsByAction.put(action, RevisionControlHtmlUtils.makeTextTag(description, style.getStyle()));
  }

  public static void verifyActionKnown(SVNEventAction action) {
    if (tagsByAction.get(action) == null)
      System.err.println("\n" +
        "###########################################################################\n" +
        "########### " + action + " not recognized! The fitnesse svn integration and svnkit are not compatible!\n" +
        "###########################################################################\n" +
        "\n");
  }

  public static Map<SVNEventAction, HtmlTag> getTagsByType() {
    return tagsByAction;
  }

  public static HtmlTag getTag(SVNEventAction action) {
    verifyActionKnown(action);
    return tagsByAction.get(action);
  }
}