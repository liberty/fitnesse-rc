package fitnesse.revisioncontrol.responders;

import fitnesse.revisioncontrol.Results;
import fitnesse.revisioncontrol.RevisionControlException;
import static util.RegexTestCase.assertSubString;
import static org.easymock.EasyMock.*;

public class CheckoutResponderTest extends RevisionControlTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    responder = new CheckoutResponder();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    verify(revisionController);
  }

  public void testShouldAskRevisionControllerToCheckoutPage() throws Exception {
    expectCheckoutForPage(FS_PARENT_PAGE);
    replay(revisionController);
    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);
    invokeResponderAndCheckSuccessStatus();
  }

  public void testAfterCheckoutShouldGiveEditLink() throws Exception {
    expectCheckoutForPage(FS_PARENT_PAGE);
    replay(revisionController);
    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);
    invokeResponderAndCheckSuccessStatus();
    assertSubString("Click <a href=\"" + FS_PARENT_PAGE + "?edit\">here</a>", response.getContent());
  }

  public void testShouldReportErrorMsgIfCheckoutOperationFails() throws Exception {
    final String errorMsg = "Cannot checkout files to Revision Control";
    revisionController.checkout(filePathFor(FS_PARENT_PAGE));
    expectLastCall().andThrow(new RevisionControlException(errorMsg));
    replay(revisionController);

    createPage(FS_PARENT_PAGE);
    request.setResource(FS_PARENT_PAGE);

    invokeResponderAndCheckSuccessStatus();

    assertSubString(errorMsg, response.getContent());
  }

  private void expectCheckoutForPage(String page) throws Exception {
    expect(revisionController.checkout(filePathFor(page))).andReturn(new Results());
  }
}
