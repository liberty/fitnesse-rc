package fitnesse.revisioncontrol;


public interface RevisionControllable {
  <R> R execute(RevisionControlOperation<R> operation);

  boolean isExternallyRevisionControlled();

  State getState();
}