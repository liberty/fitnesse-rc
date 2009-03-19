package fitnesse.revisioncontrol.wiki;

import fitnesse.ComponentFactory;
import fitnesse.revisioncontrol.*;
import fitnesse.wiki.*;

import java.io.File;
import java.util.List;
import java.util.Properties;

public class RevisionControlledFileSystemPage extends FileSystemPage implements RevisionControllable {
  private static final String REVISION_CONTROLLER = "RevisionController";

  private RevisionController revisioner;

  protected RevisionControlledFileSystemPage(final String path, final String name, final WikiPage parent) throws Exception {
    super(path, name, parent, new NullVersionsController());
  }

  protected RevisionControlledFileSystemPage(final String path, final String name, final WikiPage parent,
                                             final RevisionController revisionController) throws Exception {
    super(path, name, parent, new NullVersionsController());
    this.revisioner = revisionController;
  }

  public static WikiPage makeRoot(final String path, final String name,
                                  final RevisionController revisionController) throws Exception {
    return new RevisionControlledFileSystemPage(path, name, null, revisionController);
  }

  public static WikiPage makeRoot(final String path, final String name,
                                  final Properties properties) throws Exception {
    ComponentFactory factory = new ComponentFactory(properties);
    RevisionController revisionController =
      (RevisionController) factory.createComponent(REVISION_CONTROLLER);
    return new RevisionControlledFileSystemPage(path, name, null, revisionController);
  }

  @Override
  public void doCommit(PageData data) throws Exception {
    super.doCommit(data);

    if (getState().isUnderRevisionControl()) {
      revisioner.lock(getAbsoluteFileSystemPath());
    }
  }

  @Override
  public PageData makePageData() throws Exception {
    PageData data = super.makePageData();
    if (isDeleted(data))
      data.setContent("!deletedpage");
    return data;
  }

  private boolean isDeleted(PageData data) throws Exception {
    return data.isEmpty() && getState().isDeleted();
  }

  @Override
  protected WikiPage createChildPage(String name) throws Exception {
    final RevisionControlledFileSystemPage newPage =
      new RevisionControlledFileSystemPage(getFileSystemPath(), name, this, this.revisioner);
    final File baseDir = new File(newPage.getFileSystemPath());
    baseDir.mkdirs();
    return newPage;
  }

  @Override
  public void removeChildPage(String name) throws Exception {
    RevisionControlledFileSystemPage pageToBeDeleted = (RevisionControlledFileSystemPage) getChildPage(name);

    if (pageToBeDeleted.getState().isUnderRevisionControl()) {
      revisioner.delete(getAbsoluteFileSystemPath());
    }

    if (hasCachedSubpage(name))
      children.remove(name);
  }

  @Override
  public List<WikiPageAction> getActions() throws Exception {
    WikiPagePath localPagePath = getPageCrawler().getFullPath(this);
    String localPageName = PathParser.render(localPagePath);

    List<WikiPageAction> actions = super.getActions();
    actions.addAll(RevisionControlActionsBuilder.getRevisionControlActions(localPageName, getData()));
    return actions;
  }

  /**
   * @see fitnesse.revisioncontrol.RevisionControlOperation
   */

  public <R> R execute(final RevisionControlOperation<R> operation) {
    return operation.execute(this.revisioner, getAbsoluteFileSystemPath());
  }

  public boolean isExternallyRevisionControlled() {
    return this.revisioner.isExternalRevisionControlEnabled();
  }

  public State getState() {
    return this.revisioner.getState(getAbsoluteFileSystemPath());
  }
}
