package fitnesse.revisioncontrol.responders;

import static fitnesse.revisioncontrol.NullState.VERSIONED;
import fitnesse.revisioncontrol.RevisionControlException;
import static util.RegexTestCase.assertSubString;
import static org.easymock.EasyMock.*;

public class SyncResponderTest extends RevisionControlTestCase {
  public void testShouldAskRevisionControllerToSyncronizePage() throws Exception {
    expectStateOfPageIs(FS_PARENT_PAGE, VERSIONED);
    replay(revisionController);

    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);

    invokeResponderAndCheckSuccessStatus();
  }

  public void testShouldReportErrorMsgIfSyncronizationFails() throws Exception {
    final String errorMsg = "Cannot synchronize files from Revision Control";
    expect(revisionController.getState(filePathFor(FS_PARENT_PAGE))).andThrow(new RevisionControlException(errorMsg));
    replay(revisionController);

    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);

    invokeResponderAndCheckSuccessStatus();

    assertSubString(errorMsg, response.getContent());
  }

  public void testShouldSyncronizeAllChildPage() throws Exception {
    expectStateOfPageIs(FS_PARENT_PAGE, VERSIONED);
    replay(revisionController);

    createPage(FS_GRAND_CHILD_PAGE);
    request.setResource(FS_PARENT_PAGE);

    invokeResponderAndCheckSuccessStatus();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    responder = new SyncResponder();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    verify(revisionController);
  }
}
