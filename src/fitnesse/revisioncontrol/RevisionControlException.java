package fitnesse.revisioncontrol;

public class RevisionControlException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public RevisionControlException(Throwable cause) {
    super(cause);
  }

  public RevisionControlException(String errorMsg) {
    super(errorMsg);
  }

  public RevisionControlException(String errorMsg, Exception e) {
    super(errorMsg, e);
  }
}
