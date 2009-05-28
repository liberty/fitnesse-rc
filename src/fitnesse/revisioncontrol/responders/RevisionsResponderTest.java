package fitnesse.revisioncontrol.responders;

import static fitnesse.revisioncontrol.NullState.UNKNOWN;
import static fitnesse.revisioncontrol.NullState.VERSIONED;
import fitnesse.revisioncontrol.RevisionControlOperation;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class RevisionsResponderTest extends RevisionControlTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.responder = new RevisionsResponder();
  }

  @Override
  protected void tearDown() throws Exception {
    verify(revisionController);
  }

  public void testShouldNotMakeRevisionControlFormIfWikiIsNotUnderRevisionControl() throws Exception {
    replay(revisionController);

    request.setResource(root.getName());
    invokeResponderAndCheckResponseContains();
    assertRevisionControlItemsNotDisplayed();
  }

  public void testShouldMakeAddToRevisionControlActionForPages() throws Exception {
    expectStateOfPageIs(FS_PARENT_PAGE, UNKNOWN);
    replay(revisionController);

    createPage(FS_PARENT_PAGE);

    request.setResource(FS_PARENT_PAGE);
    invokeResponderAndCheckResponseContains();
    assertActionIsPresent(RevisionControlOperation.ADD);
    assertActionIsNotPresent(RevisionControlOperation.CHECKIN);
    assertActionIsNotPresent(RevisionControlOperation.CHECKIN);
    assertActionIsNotPresent(RevisionControlOperation.REVERT);
    assertActionIsNotPresent(RevisionControlOperation.STATUS);
  }

  public void testShouldDisplayAssociatedRevisionControlActionForPages() throws Exception {
    expectStateOfPageIs(FS_PARENT_PAGE, VERSIONED);
    replay(revisionController);

    createPage(FS_PARENT_PAGE);

    request.setResource(FS_PARENT_PAGE);
    invokeResponderAndCheckResponseContains();
    assertActionIsNotPresent(RevisionControlOperation.ADD);
    assertActionIsNotPresent(RevisionControlOperation.CHECKIN);
    assertActionIsPresent(RevisionControlOperation.UPDATE);
    assertActionIsNotPresent(RevisionControlOperation.REVERT);
    assertActionIsPresent(RevisionControlOperation.STATUS);
  }

  public void testShouldNotDisplayRevertActionForLocalUnchangedPages() throws Exception {
    expectStateOfPageIs(FS_PARENT_PAGE, VERSIONED);
    replay(revisionController);

    createPage(FS_PARENT_PAGE);

    request.setResource(FS_PARENT_PAGE);
    invokeResponderAndCheckResponseContains();
    assertActionIsNotPresent(RevisionControlOperation.CHECKIN);
    assertActionIsPresent(RevisionControlOperation.UPDATE);
    assertActionIsNotPresent(RevisionControlOperation.REVERT);
    assertActionIsPresent(RevisionControlOperation.STATUS);
  }

  private void assertRevisionControlItemsNotDisplayed() throws Exception {
    assertActionIsNotPresent(RevisionControlOperation.ADD);
    assertActionIsNotPresent(RevisionControlOperation.CHECKIN);
//    assertActionIsNotPresent(RevisionControlOperation.UPDATE);
    assertActionIsNotPresent(RevisionControlOperation.REVERT);
//    assertActionIsNotPresent(RevisionControlOperation.STATUS);
  }

  private void assertActionIsPresent(RevisionControlOperation<?> operation) {
    assertResponseContent(operation.getDescription(), true);
    assertResponseContent(operation.getName(), true);
  }

  private void assertActionIsNotPresent(RevisionControlOperation<?> operation) {
    assertResponseContent(operation.getDescription(), false);
    assertResponseContent(operation.getName(), false);
  }

  private void assertResponseContent(String s, boolean shouldContain) {
    if (shouldContain)
      assertTrue("Response should contain '" + s + "'", responseContains(s));
    else
      assertFalse("Response should not contain '" + s + "'", responseContains(s));
  }

  private boolean responseContains(String s) {
    return response.getContent().contains(s);
  }
}