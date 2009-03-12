package fitnesse.revisioncontrol.responders;

import fitnesse.html.HtmlElement;
import fitnesse.html.HtmlTableListingBuilder;
import fitnesse.revisioncontrol.Results;
import fitnesse.revisioncontrol.RevisionControlDetail;
import fitnesse.revisioncontrol.RevisionControlHtmlUtils;

import java.util.ArrayList;
import java.util.List;

public class RevisionControlDetailsTableBuilder extends HtmlTableListingBuilder {
  public RevisionControlDetailsTableBuilder(Results results, String rootPagePath) {
    super();
    makeTable(results, rootPagePath);
  }

  private void makeTable(Results results, String rootPagePath) {
    if (results.getDetails().size() > 0) {
      addHeadingRow(results);
      addDetailRows(results, rootPagePath);
    }
  }

  private void addHeadingRow(Results results){
    if (results.getDetailLabels() != null)
      addRow(results.getDetailLabels().toArray(new HtmlElement[results.getDetailLabels().size()]));
  }

  private void addDetailRows(Results results, String rootPagePath) {
    for (RevisionControlDetail detail : results.getDetails()) {
      List<HtmlElement> elements = new ArrayList<HtmlElement>(detail.getActionTags());
      elements.add(0, RevisionControlHtmlUtils.makePathLabel(detail.getFilePath(), rootPagePath));
      addRow(elements.toArray(new HtmlElement[detail.getActionTags().size()]));
    }
  }
}
