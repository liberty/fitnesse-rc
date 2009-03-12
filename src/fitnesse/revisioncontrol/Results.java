package fitnesse.revisioncontrol;

import fitnesse.html.HtmlElement;
import fitnesse.html.HtmlTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Results {
  private OperationStatus status = OperationStatus.SUCCESS;
  private List<RevisionControlDetail> details = new ArrayList<RevisionControlDetail>();
  private List<HtmlElement> detailLabels;

  public OperationStatus getStatus() {
    return status;
  }

  public void setStatus(OperationStatus status) {
    this.status = status;
  }

  public List<RevisionControlDetail> getDetails() {
    return Collections.unmodifiableList(details);
  }

  public void addDetail(RevisionControlDetail detail) {
    details.add(detail);
  }

  public List<HtmlElement> getDetailLabels() {
    return detailLabels;
  }

  public void setDetailLabels(HtmlElement... detailLabels) {
    this.detailLabels = Arrays.asList(detailLabels);
  }

  public void setDetailLabels(String... detailLabels) {
    this.detailLabels = new ArrayList<HtmlElement>();
    for (String detailLabel : detailLabels) {
      this.detailLabels.add(new HtmlTag("strong", detailLabel));
    }
  }
}
