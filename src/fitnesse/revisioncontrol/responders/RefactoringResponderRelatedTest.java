package fitnesse.revisioncontrol.responders;

import fitnesse.responders.refactoring.DeletePageResponder;
import static fitnesse.revisioncontrol.NullState.UNKNOWN;
import static fitnesse.revisioncontrol.NullState.VERSIONED;
import fitnesse.revisioncontrol.Results;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

public class RefactoringResponderRelatedTest extends RevisionControlTestCase {
  public void testShouldDeleteVersionedPageFromRevisionControll() throws Exception {
    super.setUp();
    this.responder = new DeletePageResponder();
    expectStateOfPageIs(FS_GRAND_CHILD_PAGE, VERSIONED);
    expect(this.revisionController.delete(filePathFor(FS_GRAND_CHILD_PAGE))).andReturn(new Results());
    replay(this.revisionController);

    createPage(FS_GRAND_CHILD_PAGE);

    this.request.setResource(FS_PARENT_PAGE + "." + FS_CHILD_PAGE + "." + FS_GRAND_CHILD_PAGE);
    this.request.addInput("confirmed", "yes");

    invokeResponderAndCheckStatusIs(303);
  }

  public void testShouldNotDeleteNonVersionedPageFromRevisionControll() throws Exception {
    this.responder = new DeletePageResponder();
    expectStateOfPageIs(FS_GRAND_CHILD_PAGE, UNKNOWN);
    replay(this.revisionController);

    createPage(FS_GRAND_CHILD_PAGE);

    this.request.setResource(FS_PARENT_PAGE + "." + FS_CHILD_PAGE + "." + FS_GRAND_CHILD_PAGE);
    this.request.addInput("confirmed", "yes");

    invokeResponderAndCheckStatusIs(303);
  }
}
