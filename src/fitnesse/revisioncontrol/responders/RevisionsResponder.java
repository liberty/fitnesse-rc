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
import fitnesse.wikitext.Utils;
import fitnesse.html.*;
import fitnesse.wiki.WikiPagePath;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;

public class RevisionsResponder extends BasicResponder {
  public static final String COMMENT_FIELD = "comment";

  private static final String TITLE = "Revision Control";

  private String resource;

  public SecureOperation getSecureOperation() {
    return new AlwaysSecureOperation();
  }

  public Response makeResponse(FitNesseContext context, Request request) throws Exception {
    resource = request.getResource();

    String resource = request.getResource();
    WikiPagePath path = PathParser.parse(resource);
    WikiPage page = context.root.getPageCrawler().getPage(context.root, path);
    if (page == null) {
      return new NotFoundResponder().makeResponse(context, request);
    }

    SimpleResponse response = new SimpleResponse();
    response.setContent(makeHtml(context, page));
    return response;
  }

  public String makeHtml(FitNesseContext context, WikiPage wikiPage) throws Exception {
    HtmlPage html = context.htmlPageFactory.newPage();
    html.title.use(TITLE + ": " + resource);
    html.header.use(HtmlUtil.makeBreadCrumbsWithPageType(resource, TITLE));
    html.main.use(makePageContent(wikiPage));
    return html.html();
  }

  private HtmlTag makePageContent(WikiPage wikiPage) {
    TagGroup group = new TagGroup();

    final State state = ((RevisionControllable) wikiPage).getState();
    final RevisionControlOperation[] operations = state.operations();
    for (final RevisionControlOperation operation : operations)
      group.add(makeHtmlForOperation(operation));

    return group;
  }

  public TagGroup makeHtmlForOperation(RevisionControlOperation operation) {
    TagGroup group = makeContent(operation.getName(), operation.getDescription());
    group.add(makeForm(operation.getQuery(), operation.getName()));
    group.add(HtmlUtil.HR);
    return group;
  }

  private HtmlTag makeForm(String responderName, String buttonCaption) {
    HtmlTag form = HtmlUtil.makeFormTag("post", resource);
    form.add(HtmlUtil.makeInputTag("hidden", "responder", responderName));
    form.add(HtmlUtil.makeInputTag("submit", "", buttonCaption));
    return form;
  }

  private HtmlTag makeCheckinContent() throws Exception {
    TagGroup group = makeContent("Checkin:", "Put changes to this sub-wiki into version control.");
    group.add(makeCheckinForm());
    return group;
  }

  private HtmlTag makeCheckinForm() throws Exception {
    HtmlTag form = HtmlUtil.makeFormTag("post", resource);
    form.add(HtmlUtil.makeInputTag("hidden", "responder", "versioncommit"));

    HtmlTag table = makeTable();
    addTableRow(table, new HtmlElement[]{new RawHtml("Checkin Comment: "), makeCommitTextarea()});
    form.add(table);

    form.add(HtmlUtil.makeInputTag("submit", "", "Commit"));

    return form;
  }

  private TagGroup makeContent(String header, String description) {
    TagGroup group = new TagGroup();
    group.add(new HtmlTag("h3", header));
    group.add(description);
    return group;
  }

  private HtmlTag makeTable() {
    HtmlTag table = new HtmlTag("table");
    table.addAttribute("border", "0");
    table.addAttribute("cellspacing", "0");
    table.addAttribute("class", "dirListing");
    return table;
  }

  private void addTableRow(HtmlTag table, HtmlElement[] rowItems) {
    HtmlTag row = new HtmlTag("tr");

    for (HtmlElement rowItem : rowItems) {
      HtmlTag cell = new HtmlTag("td", rowItem);
      row.add(cell);
    }
    table.add(row);
  }

  private HtmlTag makeCommitTextarea() {
    HtmlTag textarea = new HtmlTag("textarea");
    textarea.addAttribute("name", COMMENT_FIELD);
    textarea.addAttribute("rows", "3");
    textarea.addAttribute("cols", "50");
    textarea.add(Utils.escapeHTML(""));

    return textarea;
  }
}
