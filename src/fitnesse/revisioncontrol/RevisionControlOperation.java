package fitnesse.revisioncontrol;

import fitnesse.html.HtmlTag;
import fitnesse.wiki.WikiPageAction;

import java.util.HashMap;
import java.util.Map;

import static fitnesse.revisioncontrol.CheckinOperationHtmlBuilder.COMMIT_MESSAGE;

public abstract class RevisionControlOperation<R> {

  public static final RevisionControlOperation<Results> ADD =
    new RevisionControlOperation<Results>("Add", "addToRevisionControl", "a", "Add this sub-wiki to revision control") {

      @Override
      public Results execute(RevisionController revisionController, String pagePath, Map<String, String> args) {
        return revisionController.add(pagePath);
      }
    };

  public static final RevisionControlOperation<State> SYNC =
    new RevisionControlOperation<State>("Synchronize", "syncRevisionControl", "") {

      @Override
      public State execute(RevisionController revisionController, String pagePath, Map<String, String> args) {
        return revisionController.getState(pagePath);
      }
    };

  public static final RevisionControlOperation<NewRevisionResults> UPDATE =
    new RevisionControlOperation<NewRevisionResults>("Update", "update", "u", "Update this sub-wiki from version control.") {

      @Override
      public NewRevisionResults execute(RevisionController revisionController, String pagePath, Map<String, String> args) {
        return revisionController.update(pagePath);
      }
    };

  public static final RevisionControlOperation<Results> CHECKOUT =
    new RevisionControlOperation<Results>("Checkout", "checkout", "c", "Get updates to this page from version control.") {

      @Override
      public Results execute(RevisionController revisionController, String pagePath, Map<String, String> args) {
        return revisionController.checkout(pagePath);
      }
    };

  public static final RevisionControlOperation<NewRevisionResults> CHECKIN =
    new RevisionControlOperation<NewRevisionResults>("Checkin", "checkin", "i", "Put changes to this page into version control.") {

      @Override
      public NewRevisionResults execute(RevisionController revisionController, String pagePath, Map<String, String> args) {
        return revisionController.checkin(pagePath, args.containsKey(COMMIT_MESSAGE) ? args.get(COMMIT_MESSAGE) : "");
      }

      @Override
      public HtmlTag makeHtml(String resource) {
        return new CheckinOperationHtmlBuilder(this).makeHtml(resource);
      }
    };

  public static final RevisionControlOperation<Results> REVERT =
    new RevisionControlOperation<Results>("Revert", "revert", "", "Discard local changes to this page.") {

      @Override
      public Results execute(RevisionController revisionController, String pagePath, Map<String, String> args) {
        return revisionController.revert(pagePath);
      }
    };

  public static final RevisionControlOperation<StatusResults> STATUS =
    new RevisionControlOperation<StatusResults>("Status", "getStatus", "t", "Get the status of this sub-wiki from version control.") {

      @Override
      public StatusResults execute(RevisionController revisionController, String pagePath, Map<String, String> args) {
        return revisionController.getStatus(pagePath);
      }
    };

  private final String query;
  private final String accessKey;
  private final String name;
  private String description;

  protected RevisionControlOperation(String name, String query, String accessKey) {
    this(name, query, accessKey, null);
  }

   public RevisionControlOperation(String name, String query, String accessKey, String description) {
      this.name = name;
      this.query = query;
      this.accessKey = accessKey;
      this.description = description;
   }

  public WikiPageAction makeAction(String pageName) {
    WikiPageAction action = new WikiPageAction(pageName, name);
    action.setQuery(query);
    action.setShortcutKey(accessKey);
    return action;
  }

  public HtmlTag makeHtml(String resource) {
    return new RevisionControlOperationHtmlBuilder(this).makeHtml(resource);
  }

  public String getName() {
    return name;
  }

  public String getQuery() {
    return query;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return name;
  }

  public R execute(RevisionController revisionController, String pagePath) {
     return execute(revisionController, pagePath, new HashMap<String, String>());

  }

  public abstract R execute(RevisionController revisionController, String pagePath, Map<String, String> args);

}
