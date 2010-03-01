package fitnesse.revisioncontrol.responders;

import fitnesse.FitNesseContext;
import fitnesse.html.HtmlTag;
import fitnesse.http.MockRequest;
import fitnesse.revisioncontrol.NullState;
import fitnesse.revisioncontrol.OperationStatus;
import fitnesse.revisioncontrol.RevisionControlOperation;
import fitnesse.revisioncontrol.RevisionController;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;
import fitnesse.wiki.*;

import java.util.Map;

import static org.easymock.EasyMock.*;

public class RevisionControlResponderTest extends RevisionControlTestCase {
  private final String revisionControlOperation = "Test Revision Control Operation";
  private static final String pageName = "SomePage";

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.root = InMemoryPage.makeRoot("RooT");
    this.context = new FitNesseContext(this.root);
    this.request = new MockRequest();
    this.responder = new TestRevisionControlResponder();
  }

  public void testShouldReturnPageNotFoundMessageWhenPageDoesNotExist() throws Exception {
    replay(this.revisionController);
    final String pageName = "InvalidPageName";
    this.request.setResource(pageName);
    invokeResponderAndCheckResponseContains("The requested resource: <i>" + pageName + "</i> was not found.");
  }

  public void testShouldReturnInvalidWikiPageMessageIfWikiPageDoesNotExistOnFileSystem() throws Exception {
    replay(this.revisionController);
    final String inMemoryPageName = "InMemoryPage";
    this.root.addChildPage(inMemoryPageName);
    this.request.setResource(inMemoryPageName);
    invokeResponderAndCheckResponseContains("The page " + inMemoryPageName + " doesn't support '" + this.revisionControlOperation + "' operation.");
  }

  public void testShouldResolveSymbolicLinkToActualPageAndApplyRevisionControlOperations() throws Exception {
    replay(this.revisionController);
    final String symbolicLinkName = "SymbolicLink";
    final String pageOneName = "PageOne";
    final String symbolicLinkPageName = pageOneName + "." + symbolicLinkName;
    createSymbolicLink(symbolicLinkName, pageOneName);

    this.request.setResource(symbolicLinkPageName);
    invokeResponderAndCheckResponseContains("The page " + symbolicLinkPageName + " doesn't support '" + this.revisionControlOperation + "' operation.");
  }

  public void testShouldReportPerformRevisionControlOperation() throws Exception {
    final String expectedResponse1 = "Attempted to '" + this.revisionControlOperation + "' the page '" + pageName + "'.";
    final String expectedResponse2 = this.revisionControlOperation + " was successful.";
    this.revisionController = createNiceMock(RevisionController.class);
    expect(this.revisionController.getState((String) anyObject())).andStubReturn(NullState.UNKNOWN);
    replay(this.revisionController);
    createExternalRoot();
    this.root.getPageCrawler().addPage(this.root, PathParser.parse(pageName), "Test Page Content");
    this.request.setResource(pageName);

    invokeResponderAndCheckResponseContains(expectedResponse1, expectedResponse2);
    verify(this.revisionController);
  }

  private void createSymbolicLink(final String symbolicLinkName, final String pageOneName) throws Exception {
    final String pageTwoName = "PageTwo";
    final WikiPage pageOne = this.root.addChildPage(pageOneName);
    this.root.addChildPage(pageTwoName);

    final PageData data = pageOne.getData();
    final WikiPageProperties properties = data.getProperties();
    final WikiPageProperty symLinks = getSymLinkProperty(properties);
    symLinks.set(symbolicLinkName, pageTwoName);
    pageOne.commit(data);
  }

  private WikiPageProperty getSymLinkProperty(final WikiPageProperties properties) {
    return properties.set(SymbolicPage.PROPERTY_NAME);
  }

  private class TestRevisionControlResponder extends RevisionControlResponder {
    public TestRevisionControlResponder() {
      super(new RevisionControlOperation(RevisionControlResponderTest.this.revisionControlOperation, "", "") {
         @Override
         public Object execute(RevisionController revisionController, String pagePath, Map args) {
          return OperationStatus.SUCCESS;
         }
      });
    }

    @Override
    protected String createPageLink(final String resource) throws Exception {
      return "End of operation.";
    }

    @Override
    protected void performOperation(final RevisionControlledFileSystemPage page, HtmlTag tag) {
    }

  }
}
