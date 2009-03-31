package fitnesse.revisioncontrol.responders;

import fitnesse.revisioncontrol.Results;
import fitnesse.revisioncontrol.RevisionControlException;
import static util.RegexTestCase.assertSubString;
import static org.easymock.EasyMock.*;

public class RevertResponderTest extends RevisionControlTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    responder = new RevertResponder();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    verify(revisionController);
  }

  public void testShouldAskRevisionControllerToRevertPage() throws Exception {
    expectRevertForPage(FS_PARENT_PAGE);
    replay(revisionController);

    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);

    invokeResponderAndCheckSuccessStatus();
  }

  public void testShouldReportErrorMsgIfRevertOperationFails() throws Exception {
    final String errorMsg = "Cannot revert files from Revision Control";
    revisionController.revert(filePathFor(FS_PARENT_PAGE));
    expectLastCall().andThrow(new RevisionControlException(errorMsg));
    replay(revisionController);

    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);

    invokeResponderAndCheckSuccessStatus();

    assertSubString(errorMsg, response.getContent());
  }

  public void testShouldOnlyRevertCurrentPage() throws Exception {
    expectRevertForPage(FS_CHILD_PAGE);
    replay(revisionController);

    createPage(FS_GRAND_CHILD_PAGE);
    request.setResource(FS_PARENT_PAGE + "." + FS_CHILD_PAGE);

    invokeResponderAndCheckSuccessStatus();
  }

  private void expectRevertForPage(String page) throws Exception {
    expect(revisionController.revert(filePathFor(page))).andReturn(new Results());
  }
}
