package fitnesse.revisioncontrol.wiki;

import fitnesse.revisioncontrol.Results;
import static fitnesse.revisioncontrol.NullState.VERSIONED;
import fitnesse.revisioncontrol.responders.RevisionControlTestCase;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPageAction;
import static org.easymock.EasyMock.*;

import java.util.List;

public class RevisionControlledFileSystemPageTest extends RevisionControlTestCase {

  protected void setUp() throws Exception {
    super.setUp();

    createExternalRoot();
  }

  public void testCommitWillLockPageIfUnderRevisionControl() throws Exception {
    expectStateOfPageIs(FS_PARENT_PAGE, VERSIONED);
    expect(revisionController.lock(filePathFor(FS_PARENT_PAGE))).andReturn(new Results());
    replay(revisionController);

    createPage(FS_PARENT_PAGE);

    parentPage.commit(parentPage.getData());
  }

  public void testCreateChildPageWillCreateRevisionControlledPage() throws Exception {
    replay(revisionController);

    createPage(FS_PARENT_PAGE);
    assertTrue(parentPage instanceof RevisionControlledFileSystemPage);

    RevisionControlledFileSystemPage rcPage = (RevisionControlledFileSystemPage) parentPage;
    WikiPage childPage = rcPage.createChildPage(FS_CHILD_PAGE);
    assertTrue(childPage instanceof RevisionControlledFileSystemPage);
  }

  public void testDeleteChildPageWillDeleteChildFromRevisionControl() throws Exception {
    expectStateOfPageIs(FS_CHILD_PAGE, VERSIONED);
    expect(revisionController.delete(filePathFor(FS_CHILD_PAGE))).andReturn(new Results());
    replay(revisionController);

    createPage(FS_CHILD_PAGE);

    parentPage.removeChildPage(FS_CHILD_PAGE);
  }

  public void testVersionsActionIsOverwritten() throws Exception {
    replay(revisionController);

    createPage(FS_PARENT_PAGE);

    WikiPageAction replacedAction = new WikiPageAction(parentPage.getName(), "Versions");
    WikiPageAction expectedAction = new WikiPageAction(parentPage.getName(), "Revisions");

    List<WikiPageAction> actions = parentPage.getActions();
    assertFalse(actions.contains(replacedAction));
    assertTrue(actions.contains(expectedAction));
    WikiPageAction actualAction = actions.get(actions.indexOf(expectedAction));
    assertEquals("revisions", actualAction.getQuery());
  }
}
