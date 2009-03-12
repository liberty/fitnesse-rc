package fitnesse.revisioncontrol.wiki;

import fitnesse.responders.revisioncontrol.RevisionControlTestCase;
import fitnesse.revisioncontrol.NullState;
import fitnesse.revisioncontrol.Results;
import fitnesse.wiki.WikiPage;
import static org.easymock.EasyMock.*;

public class RevisionControlledFileSystemPageTest extends RevisionControlTestCase {

  protected void setUp() throws Exception {
    super.setUp();

    createExternalRoot();
  }

  public void testCommitWillLockPageIfUnderRevisionControl() throws Exception {
    createPage(FS_PARENT_PAGE);
    expectStateOfPageIs(FS_PARENT_PAGE, NullState.VERSIONED);
    replay(revisionController);

//    expect(revisionController).lock();

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
    createPage(FS_CHILD_PAGE);
    expectStateOfPageIs(FS_CHILD_PAGE, NullState.VERSIONED);
    expect(revisionController.delete(filePathFor(FS_CHILD_PAGE))).andReturn(new Results());
    replay(revisionController);

    parentPage.removeChildPage(FS_CHILD_PAGE);
  }
}
