/*
 * Copyright (c) 2006 Sabre Holdings. All Rights Reserved.
 */

package fitnesse.revisioncontrol;

import fitnesse.html.HtmlElement;
import fitnesse.html.HtmlTag;
import fitnesse.html.HtmlUtil;
import fitnesse.html.TagGroup;

import java.util.ArrayList;
import java.util.List;

public class RevisionControlOperationHtmlBuilder {
   protected RevisionControlOperation operation;

   public RevisionControlOperationHtmlBuilder(RevisionControlOperation operation) {
      this.operation = operation;
   }

   public HtmlTag makeHtml(String resource) {
     TagGroup group = new TagGroup();
     group.add(new HtmlTag("h3", operation.getName()));
     group.add(operation.getDescription());
     group.add(makeForm(resource));
     group.add(HtmlUtil.HR);
     return group;
   }

   protected HtmlTag makeForm(String resource) {
      HtmlTag form = HtmlUtil.makeFormTag("post", resource);
      form.add(HtmlUtil.makeInputTag("hidden", "responder", operation.getQuery()));
      for (HtmlTag tag : getHtmlTagsToAddToForm()) {
         form.add(tag);
      }
      return form;
   }

   protected List<HtmlTag> getHtmlTagsToAddToForm() {
      List<HtmlTag> tags = new ArrayList<HtmlTag>();
      tags.add(HtmlUtil.makeInputTag("submit", "", operation.getName()));
      return tags;
   }

}
