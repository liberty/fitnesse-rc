package fitnesse.revisioncontrol.responders;

import fitnesse.responders.NotFoundResponder;
import fitnesse.responders.BasicResponder;
import fitnesse.authentication.SecureOperation;
import fitnesse.authentication.AlwaysSecureOperation;
import fitnesse.http.Response;
import fitnesse.http.Request;
import fitnesse.http.SimpleResponse;
import fitnesse.FitNesseContext;
import fitnesse.revisioncontrol.State;
import fitnesse.revisioncontrol.RevisionControllable;
import fitnesse.revisioncontrol.RevisionControlOperation;
import fitnesse.html.*;
import fitnesse.wiki.WikiPagePath;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;

public class RevisionsResponder extends BasicResponder {
  private static final String TITLE = "Revision Control";

  private String resource;

  public SecureOperation getSecureOperation() {
    return new AlwaysSecureOperation();
  }

  public Response makeResponse(FitNesseContext context, Request request) throws Exception {
    resource = request.getResource();

    WikiPagePath path = PathParser.parse(resource);
    WikiPage page = context.root.getPageCrawler().getPage(context.root, path);
    if (page == null) {
      return new NotFoundResponder().makeResponse(context, request);
    }

    SimpleResponse response = new SimpleResponse();
    response.setContent(makeHtml(context, page).html());
    return response;
  }

  protected HtmlElement makeHtml(FitNesseContext context, WikiPage wikiPage) throws Exception {
    HtmlPage htmlPage = context.htmlPageFactory.newPage();
    htmlPage.title.use(TITLE + ": " + resource);
    htmlPage.header.use(HtmlUtil.makeBreadCrumbsWithPageType(resource, TITLE));
    htmlPage.main.use(makePageHtml(wikiPage));
    return htmlPage;
  }

  protected HtmlTag makePageHtml(WikiPage wikiPage) {
    TagGroup group = new TagGroup();

    final State state = ((RevisionControllable) wikiPage).getState();
    final RevisionControlOperation[] operations = state.operations();
    for (final RevisionControlOperation operation : operations) {
      group.add(operation.makeHtml(resource));
    }
    return group;
  }
}
