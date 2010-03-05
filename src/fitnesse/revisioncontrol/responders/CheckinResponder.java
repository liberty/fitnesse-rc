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

import static fitnesse.revisioncontrol.CheckinOperationHtmlBuilder.CLEAR_FROM_PARENT_CACHE;
import static fitnesse.revisioncontrol.CheckinOperationHtmlBuilder.COMMIT_MESSAGE;
import static fitnesse.revisioncontrol.RevisionControlOperation.CHECKIN;

public class CheckinResponder extends RevisionControlResponder {
  private String commitMessage;
  private boolean clearCacheChildrenInParent;

   public CheckinResponder() {
    super(CHECKIN);
  }

   @Override
   public Response makeResponse(FitNesseContext context, Request request) throws Exception {
     commitMessage = (String) request.getInput(COMMIT_MESSAGE);
     clearCacheChildrenInParent = isClearParentCacheRequested(request);
     return super.makeResponse(context, request);
   }

   @Override
  protected void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag) {
    Map<String, String> checkinArgs = new HashMap<String, String>();
    checkinArgs.put(COMMIT_MESSAGE, commitMessage != null ? commitMessage : "");
    NewRevisionResults results = page.execute(CHECKIN, checkinArgs);
    if (clearCacheChildrenInParent) {
       clearCachedChildrenForParent(page);
    }
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

   private void clearCachedChildrenForParent(RevisionControlledFileSystemPage page) {
      try {
         if (page.getParent() instanceof RevisionControlledFileSystemPage) {
            ((RevisionControlledFileSystemPage) page.getParent()).clearCachedChildren();
         }
      } catch (Exception e) {
         // ok, nothing to clear
      }
   }

   private boolean isClearParentCacheRequested(Request request) {
      String clearParentCache = (String) request.getInput(CLEAR_FROM_PARENT_CACHE);
      return "yes".equalsIgnoreCase(clearParentCache);
   }

}
