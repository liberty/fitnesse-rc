package fitnesse.revisioncontrol.wiki;

import fitnesse.ComponentFactory;
import fitnesse.revisioncontrol.*;
import fitnesse.wiki.*;

import java.util.List;

public class RevisionControlledFileSystemPage extends FileSystemPage implements RevisionControllable {
  private static final String REVISION_CONTROLLER = "RevisionController";

  private RevisionController revisioner;

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
    return (RevisionController) factory.createComponent(REVISION_CONTROLLER);
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
    return data.isEmpty() && revisioner != null && getState().isDeleted();
  }

  @Override
  protected WikiPage createChildPage(String name) throws Exception {
    return new RevisionControlledFileSystemPage(getFileSystemPath(), name, this, revisioner);
  }

  @Override
  public void removeChildPage(String name) throws Exception {
    RevisionControlledFileSystemPage pageToBeDeleted = (RevisionControlledFileSystemPage) getChildPage(name);

    if (pageToBeDeleted.getState().isUnderRevisionControl()) {
      revisioner.delete(pageToBeDeleted.getAbsoluteFileSystemPath());
    }

    if (hasCachedSubpage(name))
      children.remove(name);
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

  private void addRevisionControlActions(String localPageName, List<WikiPageAction> actions) throws Exception {
    actions.addAll(RevisionControlActionsBuilder.getRevisionControlActions(localPageName, getData()));
  }

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

  public boolean isExternallyRevisionControlled() {
    return revisioner.isExternalRevisionControlEnabled();
  }

  public State getState() {
    return revisioner.getState(getAbsoluteFileSystemPath());
  }
}
