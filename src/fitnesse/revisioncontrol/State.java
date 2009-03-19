package fitnesse.revisioncontrol;

public interface State {
  String REVISION_CONTROL_STATE = "RevisionControlState";

  RevisionControlOperation[] operations();

  boolean isUnderRevisionControl();

  String toString();

  boolean isCheckedOut();

  boolean isCheckedIn();

  boolean isDeleted();
}
