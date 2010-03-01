package fitnesse.revisioncontrol.responders;

import fitnesse.FitNesseContext;
import fitnesse.html.HtmlPage;
import fitnesse.html.HtmlTableListingBuilder;
import fitnesse.html.HtmlTag;
import fitnesse.html.HtmlUtil;
import fitnesse.http.Request;
import fitnesse.http.Response;
import fitnesse.http.SimpleResponse;
import fitnesse.revisioncontrol.OperationStatus;
import fitnesse.revisioncontrol.Results;
import static fitnesse.revisioncontrol.RevisionControlOperation.REVERT;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPagePath;

import java.util.List;

public class RevertResponder extends RevisionControlResponder {
   public static final String PARAM_CONFIRMED = "confirmed";

   public RevertResponder() {
    super(REVERT);
  }


   @Override
  public Response makeResponse(FitNesseContext context, Request request) throws Exception {
      String confirmedString = (String) request.getInput(PARAM_CONFIRMED);
    if ("yes".equals(confirmedString)) {
      return super.makeResponse(context, request);
    }
    else {
      return makeConfirmationResponse(context, request);
    }
  }

   @Override
  protected void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag) {
    Results results = page.execute(REVERT);
    makeResultsHtml(results, tag);
  }

  private void makeResultsHtml(Results results, HtmlTag tag) {
    if (results.getStatus() == OperationStatus.NOTHING_TO_DO) {
      tag.add("Nothing to revert");
    } else {
      HtmlTableListingBuilder table = new RevisionControlDetailsTableBuilder(results, rootPagePath);
      tag.add(table.getTable());
    }
  }

  private Response makeConfirmationResponse(FitNesseContext context, Request request) throws Exception {
    SimpleResponse response = new SimpleResponse();
    response.setMaxAge(0);

    String pageName = request.getResource();
    HtmlPage html = context.htmlPageFactory.newPage();
    html.title.use(getPageTitle() + ": " + pageName);
    html.header.use(HtmlUtil.makeBreadCrumbsWithPageType(pageName, getPageTitle()));
    html.main.use(makeConfirmationContent(context.root, pageName));
    response.setContent(html.html());
    return response;
  }

  private String getPageTitle() {
    return REVERT.getName();
  }

  private String makeConfirmationContent(WikiPage root, String qualifiedPageName) throws Exception {
    WikiPagePath path = PathParser.parse(qualifiedPageName);
    WikiPage pageToRevert = root.getPageCrawler().getPage(root, path);
    List children = pageToRevert.getChildren();
    boolean addSubPageWarning = true;
    if (children == null || children.size() == 0) {
      addSubPageWarning = false;
    }

    HtmlTag divTag = HtmlUtil.makeDivTag("centered");
    divTag.add(makeHeadingTag(addSubPageWarning, qualifiedPageName));
    divTag.add(HtmlUtil.BR);
    divTag.add(HtmlUtil.makeLink(qualifiedPageName + "?responder=revert&confirmed=yes", "Yes"));
    divTag.add("&nbsp;&nbsp;&nbsp;&nbsp;");
    divTag.add(HtmlUtil.makeLink(qualifiedPageName, "No"));

    return divTag.html();
  }

  private HtmlTag makeHeadingTag(boolean addSubPageWarning, String qualifiedPageName) {
    HtmlTag h3Tag = new HtmlTag("H3");
    if (addSubPageWarning)
    {
       h3Tag.add("Warning, this page contains one or more subpages.");
       h3Tag.add(HtmlUtil.BR);
    }
    h3Tag.add("Are you sure you want to discard local changes to " + qualifiedPageName + "?");
    return h3Tag;
  }
}
