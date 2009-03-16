package fitnesse.revisioncontrol.responders;

import fitnesse.html.HtmlTableListingBuilder;
import fitnesse.html.HtmlTag;
import fitnesse.html.HtmlUtil;
import fitnesse.revisioncontrol.OperationStatus;
import fitnesse.revisioncontrol.Results;
import static fitnesse.revisioncontrol.RevisionControlOperation.DELETE;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;

public class DeleteResponder extends RevisionControlResponder {
  public DeleteResponder() {
    super(DELETE);
  }

  @Override
  protected String createPageLink(String resource) throws Exception {
    String parentResource = "";
    int lastIndexOfDot = resource.lastIndexOf('.');
    if (lastIndexOfDot != -1)
      parentResource = resource.substring(0, lastIndexOfDot);
    return "Click " + HtmlUtil.makeLink(parentResource, "here").html() + " to view the parent page.";
  }

  @Override
  protected void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag) {
    Results results = page.execute(DELETE);
    makeResultsHtml(results, tag);
  }

  private void makeResultsHtml(Results results, HtmlTag tag) {
    if (results.getStatus() == OperationStatus.NOTHING_TO_DO) {
      tag.add("Nothing to delete");
    } else {
      HtmlTableListingBuilder table = new RevisionControlDetailsTableBuilder(results, rootPagePath);
      tag.add(table.getTable());
    }
  }
}
