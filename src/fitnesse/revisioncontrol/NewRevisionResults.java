package fitnesse.revisioncontrol;

public class NewRevisionResults extends Results {
  protected long newRevision;

  public long getNewRevision() {
    return newRevision;
  }

  public void setNewRevision(long newRevision) {
    this.newRevision = newRevision;
  }
}
