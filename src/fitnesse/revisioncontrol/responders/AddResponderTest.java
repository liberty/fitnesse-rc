package fitnesse.revisioncontrol.responders;

import fitnesse.revisioncontrol.NullState;
import fitnesse.revisioncontrol.Results;
import fitnesse.revisioncontrol.RevisionControlException;
import static fitnesse.testutil.RegexTestCase.assertSubString;
import static org.easymock.EasyMock.*;

public class AddResponderTest extends RevisionControlTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    responder = new AddResponder();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    verify(revisionController);
  }

  public void testShouldAskRevisionControllerToAddPage() throws Exception {
    expectAddCalledForPage(FS_PARENT_PAGE);
    replay(revisionController);
    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);
    invokeResponderAndCheckSuccessStatus();
  }

  public void testShouldReportErrorMsgIfAddOperationFails() throws Exception {
    final String errorMsg = "Cannot add files to Revision Control";
    revisionController.add(filePathFor(FS_PARENT_PAGE));
    expectLastCall().andThrow(new RevisionControlException(errorMsg));
    replay(revisionController);

    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);

    invokeResponderAndCheckSuccessStatus();

    assertSubString(errorMsg, response.getContent());
  }

  public void testShouldReportErrorMsgIfParentIsNotUnderRevisionControl() throws Exception {
    final String errorMsg = "parent is not under revision control";
    expectStateOfPageIs(FS_PARENT_PAGE, NullState.UNKNOWN);
    expectStateOfPageIs(FS_CHILD_PAGE, NullState.VERSIONED);
    replay(revisionController);

    createPage(FS_GRAND_CHILD_PAGE);
    request.setResource(FS_PARENT_PAGE + "." + FS_CHILD_PAGE + "." + FS_GRAND_CHILD_PAGE);

    invokeResponderAndCheckSuccessStatus();

    assertSubString(errorMsg, response.getContent());
  }

  public void testParentRemainsInSameStateIfAlreadyUnderRevisionControl() throws Exception {
    expectStateOfPageIs(FS_PARENT_PAGE, NullState.VERSIONED);
    expectStateOfPageIs(FS_CHILD_PAGE, NullState.VERSIONED);
    expectAddCalledForPage(FS_GRAND_CHILD_PAGE);
    replay(revisionController);

    createPage(FS_GRAND_CHILD_PAGE);
    request.setResource(FS_PARENT_PAGE + "." + FS_CHILD_PAGE + "." + FS_GRAND_CHILD_PAGE);

    invokeResponderAndCheckSuccessStatus();
  }

  private void expectAddCalledForPage(String page) throws Exception {
    expect(revisionController.add(filePathFor(page))).andReturn(new Results());
  }
}
