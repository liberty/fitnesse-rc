package fitnesse.revisioncontrol.responders;

import fitnesse.revisioncontrol.Results;
import fitnesse.revisioncontrol.RevisionControlException;
import static fitnesse.testutil.RegexTestCase.assertSubString;
import static org.easymock.EasyMock.*;

public class DeleteResponderTest extends RevisionControlTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.responder = new DeleteResponder();
  }

  public void testShouldAskRevisionControllerToDeletePage() throws Exception {
    expectDeleteForPage(FS_GRAND_CHILD_PAGE);
    replay(this.revisionController);

    createPage(FS_GRAND_CHILD_PAGE);
    this.request.setResource(FS_PARENT_PAGE + "." + FS_CHILD_PAGE + "." + FS_GRAND_CHILD_PAGE);

    invokeResponderAndCheckSuccessStatus();
  }

  private void expectDeleteForPage(String page) throws Exception {
    expect(revisionController.delete(filePathFor(page))).andReturn(new Results());
  }

  public void testShouldRemovePageReferenceFromParentAfterDeletingChildPage() throws Exception {
    expectDeleteForPage(FS_GRAND_CHILD_PAGE);
    replay(this.revisionController);

    createPage(FS_GRAND_CHILD_PAGE);
    this.request.setResource(FS_PARENT_PAGE + "." + FS_CHILD_PAGE + "." + FS_GRAND_CHILD_PAGE);

    invokeResponderAndCheckSuccessStatus();
  }

  public void testShouldDeleteAllChildPages() throws Exception {
    expectDeleteForPage(FS_PARENT_PAGE);
    replay(this.revisionController);

    createPage(FS_GRAND_CHILD_PAGE);
    this.request.setResource(FS_PARENT_PAGE);
    invokeResponderAndCheckSuccessStatus();
  }

  public void testShouldReportErrorMsgIfDeleteOperationFails() throws Exception {
    final String errorMsg = "Cannot delete files from Revision Control";
    this.revisionController.delete(filePathFor(FS_PARENT_PAGE));
    expectLastCall().andThrow(new RevisionControlException(errorMsg));
    replay(this.revisionController);

    createPage(FS_PARENT_PAGE);
    this.request.setResource(FS_PARENT_PAGE);

    invokeResponderAndCheckSuccessStatus();

    assertSubString(errorMsg, this.response.getContent());
  }

  public void testAfterDeletingPageShouldProvideLinkToParentPage() throws Exception {
    expectDeleteForPage(FS_CHILD_PAGE);
    replay(this.revisionController);

    createPage(FS_CHILD_PAGE);
    this.request.setResource(FS_PARENT_PAGE + "." + FS_CHILD_PAGE);

    invokeResponderAndCheckSuccessStatus();

    assertSubString("Click <a href=\"" + FS_PARENT_PAGE + "\">here</a>", this.response.getContent());
  }

  public void testAfterDeletingTopMostPageShouldProvideLinkToWikiRootPage() throws Exception {
    expectDeleteForPage(FS_PARENT_PAGE);
    replay(this.revisionController);

    createPage(FS_PARENT_PAGE);
    this.request.setResource(FS_PARENT_PAGE);

    invokeResponderAndCheckSuccessStatus();

    assertSubString("Click <a href=\"\">here</a>", this.response.getContent());
  }

  public void testShouldReportErrorMsgIfChildPagesAreLockedOrCheckedOut() throws Exception {
    final String errorMsg = "Child Page cannot be deleted from Revision Control";
    this.revisionController.delete(filePathFor(FS_PARENT_PAGE));
    expectLastCall().andThrow(new RevisionControlException(errorMsg));
    replay(this.revisionController);

    createPage(FS_CHILD_PAGE);
    this.request.setResource(FS_PARENT_PAGE);

    invokeResponderAndCheckSuccessStatus();

    assertSubString(errorMsg, this.response.getContent());
  }
}
