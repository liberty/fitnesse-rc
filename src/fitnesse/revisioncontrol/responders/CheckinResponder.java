package fitnesse.revisioncontrol.responders;

import fitnesse.FitNesseContext;
import fitnesse.html.HtmlTableListingBuilder;
import fitnesse.html.HtmlTag;
import fitnesse.http.Request;
import fitnesse.http.Response;
import fitnesse.revisioncontrol.NewRevisionResults;
import fitnesse.revisioncontrol.OperationStatus;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;

import java.util.HashMap;
import java.util.Map;

import static fitnesse.revisioncontrol.CheckinOperationHtmlBuilder.COMMIT_MESSAGE;
import static fitnesse.revisioncontrol.RevisionControlOperation.CHECKIN;

public class CheckinResponder extends RevisionControlResponder {
  private String commitMessage;

   public CheckinResponder() {
    super(CHECKIN);
  }

   @Override
   public Response makeResponse(FitNesseContext context, Request request) throws Exception {
     commitMessage = (String) request.getInput(COMMIT_MESSAGE);
     return super.makeResponse(context, request);
   }

   @Override
  protected void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag) {
    Map<String, String> checkinArgs = new HashMap<String, String>();
    checkinArgs.put(COMMIT_MESSAGE, commitMessage != null ? commitMessage : "");
    NewRevisionResults results = page.execute(CHECKIN, checkinArgs);
    makeResultsHtml(results, tag);
  }

  private void makeResultsHtml(NewRevisionResults results, HtmlTag tag) {
    if (results.getStatus().equals(OperationStatus.NOTHING_TO_DO)) {
      tag.add("No changes to check in");
    } else {
      HtmlTableListingBuilder table = new RevisionControlDetailsTableBuilder(results, rootPagePath);
      tag.add(table.getTable());
      tag.add("At revision " + results.getNewRevision());
    }
  }
}
