package fitnesse.revisioncontrol;

import fitnesse.wiki.WikiPageAction;

public abstract class RevisionControlOperation<R> {
  public static final RevisionControlOperation<Results> ADD = new RevisionControlOperation<Results>("Add", "addToRevisionControl", "a") {

    @Override
    public Results execute(RevisionController revisionController, String pagePath) {
      return revisionController.add(pagePath);
    }
  };
  public static final RevisionControlOperation<State> SYNC = new RevisionControlOperation<State>("Synchronize", "syncRevisionControl", "") {

    @Override
    public State execute(RevisionController revisionController, String pagePath) {
      return revisionController.getState(pagePath);
    }
  };
  public static final RevisionControlOperation<NewRevisionResults> UPDATE = new RevisionControlOperation<NewRevisionResults>("Update", "update", "u") {

    @Override
    public NewRevisionResults execute(RevisionController revisionController, String pagePath) {
      return revisionController.update(pagePath);
    }
  };
  public static final RevisionControlOperation<Results> CHECKOUT = new RevisionControlOperation<Results>("Checkout", "checkout", "c") {

    @Override
    public Results execute(RevisionController revisionController, String pagePath) {
      return revisionController.checkout(pagePath);
    }
  };
  public static final RevisionControlOperation<NewRevisionResults> CHECKIN = new RevisionControlOperation<NewRevisionResults>("Checkin", "checkin", "i") {

    @Override
    public NewRevisionResults execute(RevisionController revisionController, String pagePath) {
      return revisionController.checkin(pagePath);
    }
  };
  public static final RevisionControlOperation<Results> DELETE = new RevisionControlOperation<Results>("Delete", "deleteFromRevisionControl", "d") {

    @Override
    public Results execute(RevisionController revisionController, String pagePath) {
      return revisionController.delete(pagePath);
    }
  };
  public static final RevisionControlOperation<Results> REVERT = new RevisionControlOperation<Results>("Revert", "revert", "") {

    @Override
    public Results execute(RevisionController revisionController, String pagePath) {
      return revisionController.revert(pagePath);
    }
  };
  public static final RevisionControlOperation<StatusResults> STATUS = new RevisionControlOperation<StatusResults>("Status", "getStatus", "t") {

    @Override
    public StatusResults execute(RevisionController revisionController, String pagePath) {
      return revisionController.getStatus(pagePath);
    }
  };

  private final String query;
  private final String accessKey;
  private final String name;

  protected RevisionControlOperation(String name, String query, String accessKey) {
    this.name = name;
    this.query = query;
    this.accessKey = accessKey;
  }

  public WikiPageAction makeAction(String pageName) {
    WikiPageAction action = new WikiPageAction(pageName, name);
    action.setQuery(query);
    action.setShortcutKey(accessKey);
    return action;
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

  @Override
  public String toString() {
    return name;
  }

  public abstract R execute(RevisionController revisionController, String pagePath);
}
