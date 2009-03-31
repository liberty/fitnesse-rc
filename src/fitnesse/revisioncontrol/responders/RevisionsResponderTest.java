package fitnesse.revisioncontrol.responders;

import static fitnesse.revisioncontrol.NullState.UNKNOWN;
import static fitnesse.revisioncontrol.NullState.VERSIONED;
import fitnesse.revisioncontrol.RevisionController;
import fitnesse.revisioncontrol.RevisionControlOperation;
import fitnesse.wiki.PageData;
import static org.easymock.EasyMock.*;

public class RevisionsResponderTest extends RevisionControlTestCase {
  private final RevisionController revisionController = createMock(RevisionController.class);

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
    expect(revisionController.isExternalRevisionControlEnabled()).andReturn(false);
    replay(revisionController);

    request.setResource(root.getName());
    invokeResponderAndCheckResponseContains();
  }

  public void testShouldNotMakeRevisionControlActionsIfPageIsNotEditableNorImported() throws Exception {
    expectStateOfPageIs(FS_PARENT_PAGE, VERSIONED);
    replay(revisionController);

    createPage(FS_PARENT_PAGE);

    final PageData pageData = parentPage.getData();
    pageData.removeAttribute("Edit");
    parentPage.commit(pageData);

    request.setResource(FS_PARENT_PAGE);
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
    assertActionIsNotPresent(RevisionControlOperation.UPDATE);
    assertActionIsNotPresent(RevisionControlOperation.REVERT);
    assertActionIsNotPresent(RevisionControlOperation.STATUS);
  }

  private void assertActionIsPresent(RevisionControlOperation<?> operation) {
    assertTrue(response.getContent().contains(operation.getDescription()));
    assertTrue(response.getContent().contains(operation.getName()));
  }

  private void assertActionIsNotPresent(RevisionControlOperation<?> operation) {
    assertFalse(response.getContent().contains(operation.getDescription()));
    assertFalse(response.getContent().contains(operation.getName()));
  }
}