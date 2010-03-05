package fitnesse.revisioncontrol.responders;

import fitnesse.FitNesseContext;
import fitnesse.html.HtmlTableListingBuilder;
import fitnesse.html.HtmlTag;
import fitnesse.http.Request;
import fitnesse.http.Response;
import fitnesse.revisioncontrol.NewRevisionResults;
import fitnesse.revisioncontrol.OperationStatus;
import fitnesse.revisioncontrol.RevisionControlDetail;
import fitnesse.revisioncontrol.svn.client.SVNEventActionTags;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;
import org.tmatesoft.svn.core.wc.SVNEventAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fitnesse.revisioncontrol.CheckinOperationHtmlBuilder.CHECKIN_FOR_DELETED_PAGE;
import static fitnesse.revisioncontrol.CheckinOperationHtmlBuilder.COMMIT_MESSAGE;
import static fitnesse.revisioncontrol.RevisionControlOperation.CHECKIN;
import static org.tmatesoft.svn.core.wc.SVNEventAction.COMMIT_DELETED;

public class CheckinResponder extends RevisionControlResponder {
  private String commitMessage;
  private boolean checkinForDeletedPage;

   public CheckinResponder() {
    super(CHECKIN);
  }

   @Override
   public Response makeResponse(FitNesseContext context, Request request) throws Exception {
     commitMessage = (String) request.getInput(COMMIT_MESSAGE);
     checkinForDeletedPage = isCheckinForDeletedPage(request);
     return super.makeResponse(context, request);
   }

   @Override
  protected void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag) {
    Map<String, String> checkinArgs = new HashMap<String, String>();
    checkinArgs.put(COMMIT_MESSAGE, commitMessage != null ? commitMessage : "");
    NewRevisionResults results = page.execute(CHECKIN, checkinArgs);
    if (anyChildPageDeleteCheckedIn(results)) {
       page.clearCachedChildren();
    }
    if (checkinForDeletedPage) {
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

   private boolean isCheckinForDeletedPage(Request request) {
      return "yes".equalsIgnoreCase((String) request.getInput(CHECKIN_FOR_DELETED_PAGE));
   }

   private boolean anyChildPageDeleteCheckedIn(NewRevisionResults results) {
     List<RevisionControlDetail> revisionControlDetailList = results.getDetails();
     for (RevisionControlDetail detail : revisionControlDetailList) {
       if (detail.getActionTags().contains(SVNEventActionTags.getTag(COMMIT_DELETED))) {
         return true;
       }
     }
     return false;
   }

}
