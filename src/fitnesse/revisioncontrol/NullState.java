package fitnesse.revisioncontrol;

import static fitnesse.revisioncontrol.RevisionControlOperation.*;

public abstract class NullState implements State {
  protected String state;

  public static final NullState VERSIONED = new Versioned("Versioned");
  public static final NullState UNKNOWN = new Unknown("Unknown");

  protected NullState(String state) {
    this.state = state;
  }

  public boolean isCheckedOut() {
    return true;
  }

  public boolean isDeleted() {
    return false;
  }
}

class Versioned extends NullState {
  protected Versioned(String state) {
    super(state);
  }

  public RevisionControlOperation[] operations() {
    return new RevisionControlOperation[]{CHECKOUT, UPDATE, STATUS};
  }

  public boolean isUnderRevisionControl() {
    return true;
  }

  public boolean isCheckedIn() {
    return true;
  }
}

class Unknown extends NullState {
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
}
