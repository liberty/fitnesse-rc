package fitnesse.responders.revisioncontrol;

import fitnesse.html.HtmlTag;
import fitnesse.html.HtmlUtil;
import static fitnesse.revisioncontrol.RevisionControlOperation.CHECKOUT;
import fitnesse.revisioncontrol.wiki.RevisionControlledFileSystemPage;

public class CheckoutResponder extends RevisionControlResponder {
  public CheckoutResponder() {
    super(CHECKOUT);
  }

  @Override
  protected String createPageLink(String resource) throws Exception {
    return "Click " + HtmlUtil.makeLink(resource + "?edit", "here").html() + " to edit the page.";
  }

  @Override
  protected void performOperation(RevisionControlledFileSystemPage page, HtmlTag tag) {
    page.execute(CHECKOUT);
  }
}
