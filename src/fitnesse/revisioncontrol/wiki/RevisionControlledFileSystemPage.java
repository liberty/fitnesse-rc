package fitnesse.revisioncontrol.wiki;

import fitnesse.ComponentFactory;
import fitnesse.revisioncontrol.*;
import fitnesse.wiki.*;

import java.io.File;
import java.util.List;
import java.util.Map;

public class RevisionControlledFileSystemPage extends FileSystemPage implements RevisionControllable {
  private static final String REVISION_CONTROLLER = "RevisionController";

  private RevisionController revisioner;

  private static final String ERROR_LOGS_NAME = "ErrorLogs";
  private static final String RECENT_CHANGES_NAME = "RecentChanges";

  private static final String ADD_ON_SAVE_PROP = "RevisionController.addOnSave";
  protected static boolean addOnSave = false;


  public RevisionControlledFileSystemPage(final String path, final String name, 
                                          final ComponentFactory componentFactory) throws Exception {
    super(path, name, null, new NullVersionsController());
    this.revisioner = createRevisionController(componentFactory);
  }

  protected RevisionControlledFileSystemPage(final String path, final String name, final WikiPage parent,
                                          final RevisionController revisionController) throws Exception {
    super(path, name, parent, new NullVersionsController());
    this.revisioner = revisionController;
  }

  public RevisionControlledFileSystemPage(final String path, final String name,
                                          final RevisionController revisionController) throws Exception {
    this(path, name, null, revisionController);
  }

  private static RevisionController createRevisionController(ComponentFactory factory) throws Exception {
    RevisionController revisionController = (RevisionController) factory.createComponent(REVISION_CONTROLLER);
    if (revisionController == null) {
      throw new IllegalStateException("A RevisionController must be configured in the FitNesse properties");
    }
    addOnSave = Boolean.valueOf(factory.getProperty(ADD_ON_SAVE_PROP));
    return revisionController;
  }

  @Override
  public void doCommit(PageData data) throws Exception {
    if (isTransientPage())
    {
      super.doCommit(data);
      return;
    }

    // Check before save since we need to know if it a new page
    boolean needToAddToRevisionControl = addOnSave && !pageExists();

    super.doCommit(data);

    if (needToAddToRevisionControl)
    {
       revisioner.add(getAbsoluteFileSystemPath());
    }
    else if (getState().isCheckedIn() && !hasLocalLock())
    {
      revisioner.lock(getAbsoluteFileSystemPath());
    }
  }

   private boolean pageExists() {
      return getContentFile().exists() && getPropertiesFile().exists();
   }

   @Override
  public PageData makePageData() throws Exception {
    PageData data = super.makePageData();
    if (isDeleted(data))
      data.setContent("!deletedpage");
    return data;
  }

  private boolean isDeleted(PageData data) throws Exception {
    return data.isEmpty() && revisioner != null && getState().isDeleted();
  }

  @Override
  protected WikiPage createChildPage(String name) throws Exception {
    return new RevisionControlledFileSystemPage(getFileSystemPath(), name, this, revisioner);
  }

  @Override
  public void removeChildPage(String name) throws Exception {
    RevisionControlledFileSystemPage childToBeDeleted = (RevisionControlledFileSystemPage) getChildPage(name);

    if (parentAndChildAreUnderRevisionControl(childToBeDeleted)) {
      revisioner.delete(childToBeDeleted.getAbsoluteFileSystemPath());

      if (hasCachedSubpage(name))
        children.remove(name);
    } else {
      super.removeChildPage(name);
    }
  }

  private boolean parentAndChildAreUnderRevisionControl(RevisionControlledFileSystemPage childPage) {
    return getState().isUnderRevisionControl() &&
      childPage.getState().isUnderRevisionControl() &&
      childPage.getState().isCheckedIn();
  }

  @Override
  public List<WikiPageAction> getActions() throws Exception {
    WikiPagePath localPagePath = getPageCrawler().getFullPath(this);
    String localPageName = PathParser.render(localPagePath);

    List<WikiPageAction> actions = super.getActions();
//    addRevisionControlActions(localPageName, actions);
    replaceVersionsActionWithRevisionsAction(localPageName, actions);
    return actions;
  }

//  private void addRevisionControlActions(String localPageName, List<WikiPageAction> actions) throws Exception {
//    actions.addAll(RevisionControlActionsBuilder.getRevisionControlActions(localPageName, getData()));
//  }

  private void replaceVersionsActionWithRevisionsAction(String localPageName, List<WikiPageAction> actions) {
    WikiPageAction revisionControlAction = new WikiPageAction(localPageName, "Revisions");
    int position = actions.indexOf(new WikiPageAction(localPageName, "Versions"));
    if (position > 0) {
      actions.remove(position);
      actions.add(position, revisionControlAction);
    }
    else {
      actions.add(revisionControlAction);
    }
  }

   /**
    * @see fitnesse.revisioncontrol.RevisionControlOperation
    */
   public <R> R execute(final RevisionControlOperation<R> operation) {
     return operation.execute(revisioner, getAbsoluteFileSystemPath());
   }

  /**
   * @see fitnesse.revisioncontrol.RevisionControlOperation
   */
  public <R> R execute(final RevisionControlOperation<R> operation, final Map<String, String> args) {
    return operation.execute(revisioner, getAbsoluteFileSystemPath(), args);
  }

  public boolean isExternallyRevisionControlled() {
    return revisioner.isExternalRevisionControlEnabled();
  }

  public State getState() {
    return revisioner.getState(getAbsoluteFileSystemPath());
  }

   public boolean hasLocalLock() {
      return revisioner.hasLocalLock(getAbsoluteFileSystemPathForContentFile());
   }
   
   public String getAbsoluteFileSystemPathForContentFile() {
     return getContentFile().getAbsolutePath();
   }

   private File getContentFile() {
      return new File(getFileSystemPath() + contentFilename);
   }

   public String getAbsoluteFileSystemPathForPropertiesFile() {
     return getPropertiesFile().getAbsolutePath();
   }

   private File getPropertiesFile() {
      return new File(getFileSystemPath() + propertiesFilename);
   }

   public void clearCachedChildren() {
      children.clear();
   }

   private boolean isTransientPage() throws Exception
   {
      return getName().startsWith(ERROR_LOGS_NAME) || getName().equals(RECENT_CHANGES_NAME);
   }


}
