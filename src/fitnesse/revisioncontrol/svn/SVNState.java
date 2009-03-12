package fitnesse.revisioncontrol.svn;

import fitnesse.revisioncontrol.RevisionControlOperation;
import static fitnesse.revisioncontrol.RevisionControlOperation.*;
import fitnesse.revisioncontrol.State;

public abstract class SVNState implements State {
  String state;

  public static final SVNState VERSIONED = new Versioned("Versioned");
  public static final SVNState UNKNOWN = new Unknown("Unknown");
  public static final SVNState DELETED = new Deleted("Deleted");
  public static final SVNState ADDED = new Added("Added");

  protected SVNState(String state) {
    this.state = state;
  }

  public boolean isCheckedOut() {
    return true;
  }

  @Override
  public String toString() {
    return state;
  }

  protected boolean contains(String msg, String searchString) {
    return msg.indexOf(searchString) != -1;
  }
}

class Versioned extends SVNState {
  protected Versioned(String state) {
    super(state);
  }

  public RevisionControlOperation[] operations() {
    return new RevisionControlOperation[]{CHECKIN, UPDATE, REVERT, DELETE, STATUS};
  }

  public boolean isUnderRevisionControl() {
    return true;
  }

  public boolean isCheckedIn() {
    return true;
  }
}

class Unknown extends SVNState {
  protected Unknown(String state) {
    super(state);
  }

  public RevisionControlOperation[] operations() {
    return new RevisionControlOperation[]{ADD};
  }

  public boolean isUnderRevisionControl() {
    return false;
  }

  public boolean isCheckedIn() {
    return false;
  }

  @Override
  public boolean isCheckedOut() {
    return false;
  }
}

class Deleted extends SVNState {
  protected Deleted(String state) {
    super(state);
  }

  public RevisionControlOperation[] operations() {
    return new RevisionControlOperation[]{CHECKIN, REVERT, STATUS};
  }

  public boolean isUnderRevisionControl() {
    return true;
  }

  public boolean isCheckedIn() {
    return true;
  }
}

class Added extends SVNState {
  protected Added(String state) {
    super(state);
  }

  public RevisionControlOperation[] operations() {
    return new RevisionControlOperation[]{CHECKIN, REVERT, STATUS};
  }

  public boolean isUnderRevisionControl() {
    return true;
  }

  public boolean isCheckedIn() {
    return false;
  }
}
