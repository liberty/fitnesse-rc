package fitnesse.revisioncontrol;

import static fitnesse.revisioncontrol.NullState.UNKNOWN;
import static fitnesse.revisioncontrol.NullState.VERSIONED;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;
import fitnesse.util.FileUtil;
import fitnesse.wiki.PageData;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPageAction;
import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.util.List;

public class RevisionControlActionsBuilderTest extends TestCase {
  private static final String ROOT = "testDir";
  private WikiPage root;
  private final RevisionController revisionController = createMock(RevisionController.class);

  @Override
  protected void setUp() throws Exception {
    FileUtil.createDir(ROOT);
    expect(revisionController.getState((String) anyObject())).andStubReturn(VERSIONED);
    expect(revisionController.add((String) anyObject())).andStubReturn(new Results());
  }

  @Override
  protected void tearDown() throws Exception {
    FileUtil.deleteFileSystemDirectory(ROOT);
    verify(revisionController);
  }

  public void testShouldNotMakeRevisionControlActionsIfWikiIsNotUnderRevisionControl() throws Exception {
    expect(revisionController.isExternalRevisionControlEnabled()).andReturn(false);
    replay(revisionController);

    final String pageName = "EditablePage";
    List<WikiPageAction> actions = getActions(pageName);
    assertRevisionControlItemsNotDisplayed(pageName, actions);
  }

  public void testShouldNotMakeRevisionControlActionsIfPageIsNotEditableNorImported() throws Exception {
    final String pageName = "NonEditablePage";

    expect(revisionController.lock(ROOT + "/ExternalRoot/" + pageName)).andReturn(new Results());
    replay(revisionController);

    createRoot();
    final WikiPage testPage = root.addChildPage(pageName);
    final PageData pageData = testPage.getData();
    pageData.removeAttribute("Edit");
    testPage.commit(pageData);

    List<WikiPageAction> actions = testPage.getActions();
    assertRevisionControlItemsNotDisplayed(pageName, actions);
  }

  public void testShouldMakeAddToRevisionControlActionForPages() throws Exception {
    final String pageName = "NotUnderVersionControlPage";
    expect(revisionController.isExternalRevisionControlEnabled()).andReturn(true);
    expect(revisionController.getState(contentAndPropertiesFilePath(ROOT + "/ExternalRoot/" + pageName))).
      andReturn(UNKNOWN).atLeastOnce();
    replay(revisionController);

    List<WikiPageAction> actions = getActions(pageName);
    assertActionIsPresent(pageName, actions, RevisionControlOperation.ADD);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.CHECKIN);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.CHECKIN);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.REVERT);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.DELETE);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.STATUS);
  }

  public void testShouldDisplayAssociatedRevisionControlActionForPages() throws Exception {
    final String pageName = "CheckedInPage";
    expect(revisionController.isExternalRevisionControlEnabled()).andReturn(true);
    expect(revisionController.getState(contentAndPropertiesFilePath(ROOT + "/ExternalRoot/" + pageName))).andReturn(VERSIONED);
    replay(revisionController);

    List<WikiPageAction> actions = getActions(pageName);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.ADD);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.CHECKIN);
    assertActionIsPresent(pageName, actions, RevisionControlOperation.UPDATE);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.REVERT);
    assertActionIsPresent(pageName, actions, RevisionControlOperation.DELETE);
    assertActionIsPresent(pageName, actions, RevisionControlOperation.STATUS);
  }

  public void testShouldNotDisplayRevertActionForLocalUnchangedPages() throws Exception {
    final String pageName = "UnchangedPage";
    expect(revisionController.isExternalRevisionControlEnabled()).andReturn(true);
    expect(revisionController.getState(contentAndPropertiesFilePath(ROOT + "/ExternalRoot/" + pageName))).andReturn(VERSIONED);
    replay(revisionController);

    List<WikiPageAction> actions = getActions(pageName);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.CHECKIN);
    assertActionIsPresent(pageName, actions, RevisionControlOperation.UPDATE);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.REVERT);
    assertActionIsPresent(pageName, actions, RevisionControlOperation.DELETE);
    assertActionIsPresent(pageName, actions, RevisionControlOperation.STATUS);
  }

  private void createRoot() throws Exception {
    root = RevisionControlledFileSystemPage.makeRoot(ROOT, "ExternalRoot", revisionController);
  }

  private List<WikiPageAction> getActions(String pageName) throws Exception {
    createRoot();
    WikiPage page = root.addChildPage(pageName);
    return page.getActions();
  }

  private void assertRevisionControlItemsNotDisplayed(String pageName, List<WikiPageAction> actions) throws Exception {
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.ADD);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.CHECKIN);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.UPDATE);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.REVERT);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.DELETE);
    assertActionIsNotPresent(pageName, actions, RevisionControlOperation.STATUS);
  }

  private String contentAndPropertiesFilePath(String basePath) {
    return new File(basePath).getAbsolutePath();
  }

  private void assertActionIsPresent(String pageName, List<WikiPageAction> actions, RevisionControlOperation<?> operation) {
    assertTrue(actions.contains(operation.makeAction(pageName)));
  }

  private void assertActionIsNotPresent(String pageName, List<WikiPageAction> actions, RevisionControlOperation<?> operation) {
    assertFalse(actions.contains(operation.makeAction(pageName)));
  }
}
