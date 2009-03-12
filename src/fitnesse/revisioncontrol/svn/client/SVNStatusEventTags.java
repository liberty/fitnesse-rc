package fitnesse.revisioncontrol.svn.client;

import fitnesse.html.HtmlTag;
import fitnesse.revisioncontrol.ActionStyle;
import static fitnesse.revisioncontrol.ActionStyle.*;
import fitnesse.revisioncontrol.RevisionControlHtmlUtils;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import java.util.HashMap;
import java.util.Map;

public class SVNStatusEventTags {
  private static Map<SVNStatusType, HtmlTag> tagsByType = new HashMap<SVNStatusType, HtmlTag>();

  static {
    registerStatusType(SVNStatusType.STATUS_NONE, "", OK);
    registerStatusType(SVNStatusType.STATUS_NORMAL, "", OK);
    registerStatusType(SVNStatusType.STATUS_MODIFIED, "Modified", OK);
    registerStatusType(SVNStatusType.STATUS_CONFLICTED, "Conflicted", ALERT);
    registerStatusType(SVNStatusType.STATUS_DELETED, "Deleted", OK);
    registerStatusType(SVNStatusType.STATUS_ADDED, "Added", OK);
    registerStatusType(SVNStatusType.STATUS_UNVERSIONED, "Unversioned", WARNING);
    registerStatusType(SVNStatusType.STATUS_EXTERNAL, "External", WARNING);
    registerStatusType(SVNStatusType.STATUS_IGNORED, "Ignored", WARNING);
    registerStatusType(SVNStatusType.STATUS_MISSING, "Missing", ALERT);
    registerStatusType(SVNStatusType.STATUS_MERGED, "Merged", ALERT);
    registerStatusType(SVNStatusType.STATUS_INCOMPLETE, "Incomplete", ALERT);
    registerStatusType(SVNStatusType.STATUS_OBSTRUCTED, "Obstructed", ALERT);
    registerStatusType(SVNStatusType.STATUS_REPLACED, "Replaced", ALERT);

    registerStatusType(SVNStatusType.INAPPLICABLE, "Inapplicable", ALERT);
    registerStatusType(SVNStatusType.UNKNOWN, "Unknown", ALERT);
    tagsByType.put(SVNStatusType.UNCHANGED, RevisionControlHtmlUtils.makeTextTag(" "));
    registerStatusType(SVNStatusType.MISSING, "Missing", WARNING);
    registerStatusType(SVNStatusType.OBSTRUCTED, "Obstructed", ALERT);
    registerStatusType(SVNStatusType.CHANGED, "Updated", OK);
    registerStatusType(SVNStatusType.MERGED, "Merged", WARNING);
    registerStatusType(SVNStatusType.CONFLICTED, "Conflicted", ALERT);
    registerStatusType(SVNStatusType.CONFLICTED_UNRESOLVED, "Conflicted", ALERT);

    registerStatusType(SVNStatusType.LOCK_INAPPLICABLE, "Lock inapplicable", ALERT);
    registerStatusType(SVNStatusType.LOCK_UNKNOWN, "Lock unkown", ALERT);
    registerStatusType(SVNStatusType.LOCK_UNCHANGED, "Lock unchanged", OK);
    registerStatusType(SVNStatusType.LOCK_LOCKED, "File locked", ALERT);
    registerStatusType(SVNStatusType.LOCK_UNLOCKED, "File unlocked", ALERT);

  }

  private static void registerStatusType(SVNStatusType type, String title, ActionStyle style) {
    tagsByType.put(type, RevisionControlHtmlUtils.makeTextTag(title, style.getStyle()));
  }

  public static void verifyStatusKnown(SVNStatus status) {
    verifyStatusTypeKnown(status.getContentsStatus());
    verifyStatusTypeKnown(status.getPropertiesStatus());
  }

  public static void verifyStatusTypeKnown(SVNStatusType statusType) {
    if (tagsByType.get(statusType) == null)
      System.err.println(
          "\n" +
              "###########################################################################\n" +
              "########### " + statusType + " not recognized! The fitnesse svn plugin and svnkit are not compatible!\n" +
              "###########################################################################\n" +
              "\n");
  }

  public static Map<SVNStatusType, HtmlTag> getTagsByType() {
    return tagsByType;
  }

  public static HtmlTag getTag(SVNStatusType type) {
    verifyStatusTypeKnown(type);
    return tagsByType.get(type);
  }
}