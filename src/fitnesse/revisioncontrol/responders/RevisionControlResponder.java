package fitnesse.revisioncontrol.responders;

import fitnesse.FitNesseContext;
import fitnesse.html.HtmlPage;
import fitnesse.html.HtmlTag;
import fitnesse.html.HtmlUtil;
import fitnesse.html.TagGroup;
import fitnesse.http.Request;
import fitnesse.http.Response;
import fitnesse.http.SimpleResponse;
import fitnesse.responders.BasicResponder;
import fitnesse.responders.NotFoundResponder;
import fitnesse.revisioncontrol.RevisionControlException;
import fitnesse.revisioncontrol.RevisionControlOperation;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;
import fitnesse.wiki.*;

import java.io.File;
import java.util.List;

public abstract class RevisionControlResponder extends BasicResponder {
  private final RevisionControlOperation operation;
  protected String rootPagePath;
  protected String comment;

  public static final String COMMENT_FIELD = "comment";

  protected RevisionControlResponder(RevisionControlOperation operation) {
    this.operation = operation;
  }

  public Response makeResponse(FitNesseContext context, Request request) throws Exception {
    rootPagePath = new File(context.rootPagePath).getAbsolutePath();
    comment = (String) request.getInput(COMMENT_FIELD);

    WikiPage root = context.root;
    PageCrawler crawler = root.getPageCrawler();
    String resource = request.getResource();
    WikiPagePath path = PathParser.parse(resource);

    WikiPage page = crawler.getPage(root, path);
    if (page == null)
      return new NotFoundResponder().makeResponse(context, request);

    page = resolveSymbolicLinks(page);

    SimpleResponse response = new SimpleResponse();
    response.setMaxAge(0);
    if (!(page instanceof RevisionControlledFileSystemPage)) {
      response.setContent(makeHtml(resource, context, invalidWikiPageContent(resource)));
      return response;
    }
    String returnMsg = executeRevisionControlOperation((RevisionControlledFileSystemPage) page);
    response.setContent(makeHtml(resource, context, content(resource, returnMsg)));

    return response;
  }

  private WikiPage resolveSymbolicLinks(WikiPage page) throws Exception {
    while (page instanceof SymbolicPage)
      page = ((SymbolicPage) page).getRealPage();
    return page;
  }

  protected String executeRevisionControlOperation(RevisionControlledFileSystemPage page) {
    String returnMsg;
    try {
      beforeOperation(page);

      TagGroup group = new TagGroup();

      performOperation(page, group);

      if (group.childTags.size() == 0)
        returnMsg = operation.getName() + " was successful.";
      else
        returnMsg = group.html();

    } catch (RevisionControlException e) {
      returnMsg = operation.getName() + " failed. Following exception occurred:<pre>\n";
      returnMsg += buildExceptionMessage(e);
      if (e.getCause() != null)
        returnMsg += ":\n" + buildExceptionMessage(e.getCause());
      returnMsg += "</pre>";
    }
    return returnMsg;
  }

  private String buildExceptionMessage(Throwable e) {
    return e.getClass().getName() + ": " + e.getMessage();
  }

  protected void beforeOperation(FileSystemPage page) {
  }

  protected abstract void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag);

  protected String createPageLink(String resource) throws Exception {
    return "View the " + HtmlUtil.makeLink(resource, "page").html() + ".";
  }

  private String makeHtml(String resource, FitNesseContext context, String content) throws Exception {
    HtmlPage html = context.htmlPageFactory.newPage();
    html.title.use(operation.getName() + " " + resource);
    html.header.use(HtmlUtil.makeBreadCrumbsWithPageType(resource, operation.getName() + " Page"));
    html.main.use(content);
    return html.html();
  }

  private String content(String resource, String result) throws Exception {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Attempted to '").append(operation.getName()).append("' the page '").
      append(resource).append("'.<br/><hr><br/>");
    buffer.append(result);
    buffer.append("<br/><hr><br/>");
    buffer.append(createPageLink(resource));
    return buffer.toString();
  }

  private String invalidWikiPageContent(String resource) {
    return "The page " + resource + " doesn't support '" + operation.getName() + "' operation.";
  }

  protected List<WikiPage> getChildren(FileSystemPage page) {
    try {
      return page.getChildren();
    } catch (Exception e) {
      throw new RevisionControlException(e);
    }
  }
}
