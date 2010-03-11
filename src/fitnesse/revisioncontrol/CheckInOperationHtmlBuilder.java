/*
 * Copyright (c) 2006 Sabre Holdings. All Rights Reserved.
 */

package fitnesse.revisioncontrol;

import fitnesse.html.HtmlElement;
import fitnesse.html.HtmlTag;
import fitnesse.html.RawHtml;
import fitnesse.revisioncontrol.responders.RevisionControlResponder;
import fitnesse.wikitext.Utils;

import java.util.List;

public class CheckinOperationHtmlBuilder extends RevisionControlOperationHtmlBuilder {
   public static final String COMMIT_MESSAGE = "commitMessage";
   public static final String CHECKIN_FOR_DELETED_PAGE = "checkinForDeletedPage";


   public CheckinOperationHtmlBuilder(RevisionControlOperation operation) {
      super(operation);
   }

   @Override
   protected List<HtmlTag> getHtmlTagsToAddToForm() {
      List<HtmlTag> tags = super.getHtmlTagsToAddToForm();
      HtmlTag table = makeTable();
      addTableRow(table, new HtmlElement[]{new RawHtml("Comment: "), makeCommentTextarea()});
      tags.add(0, table);
      return tags;
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

   private HtmlTag makeCommentTextarea() {
     HtmlTag textarea = new HtmlTag("textarea");
     textarea.addAttribute("name", COMMIT_MESSAGE);
     textarea.addAttribute("rows", "3");
     textarea.addAttribute("cols", "50");
     textarea.add(Utils.escapeHTML(""));

     return textarea;
   }

}