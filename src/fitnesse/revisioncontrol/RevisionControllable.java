package fitnesse.revisioncontrol;

import java.util.Map;


public interface RevisionControllable {
  <R> R execute(RevisionControlOperation<R> operation);

  <R> R execute(RevisionControlOperation<R> operation, Map<String, String> operationArgs);

  boolean isExternallyRevisionControlled();

  State getState();
}