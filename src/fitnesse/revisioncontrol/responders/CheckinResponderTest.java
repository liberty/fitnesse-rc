package fitnesse.revisioncontrol.responders;

import fitnesse.revisioncontrol.NewRevisionResults;
import fitnesse.revisioncontrol.OperationStatus;
import fitnesse.revisioncontrol.RevisionControlDetail;
import fitnesse.revisioncontrol.RevisionControlException;
import static util.RegexTestCase.assertSubString;
import static org.easymock.EasyMock.*;

import java.util.Arrays;
import java.util.List;

public class CheckinResponderTest extends RevisionControlTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    responder = new CheckinResponder();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    verify(revisionController);
  }

  public void testShouldAskRevisionControllerToCheckinPage() throws Exception {
    expect(revisionController.checkin(filePathFor(FS_PARENT_PAGE), "")).
      andReturn(new TestNewRevisionResults(1234, OperationStatus.SUCCESS));
    replay(revisionController);
    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);
    invokeResponderAndCheckSuccessStatus();

    assertSubString(FS_PARENT_PAGE, response.getContent());
    assertSubString("Checked In", response.getContent());
    assertSubString("At revision 1234", response.getContent());
  }

  public void testShouldAskRevisionControllerToCheckinPageWithNothingToDo() throws Exception {
    expect(revisionController.checkin(filePathFor(FS_PARENT_PAGE), "")).
      andReturn(new TestNewRevisionResults(-1, OperationStatus.NOTHING_TO_DO));
    replay(revisionController);
    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);
    invokeResponderAndCheckSuccessStatus();

    assertSubString("No changes to check in", response.getContent());
  }

  public void testShouldReportErrorMsgIfCheckinOperationFails() throws Exception {
    final String errorMsg = "Cannot checkin files to Revision Control";
    revisionController.checkin(filePathFor(FS_PARENT_PAGE), "");
    expectLastCall().andThrow(new RevisionControlException(errorMsg));
    replay(revisionController);

    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);

    invokeResponderAndCheckSuccessStatus();

    assertSubString(errorMsg, response.getContent());
  }

  private static class TestNewRevisionResults extends NewRevisionResults {
    public TestNewRevisionResults(int newRevision, OperationStatus status) {
      setNewRevision(newRevision);
      setStatus(status);
    }

    public List<RevisionControlDetail> getDetails() {
      return Arrays.asList(new RevisionControlDetail(FS_PARENT_PAGE, "Checked In"));
    }
  }
}
