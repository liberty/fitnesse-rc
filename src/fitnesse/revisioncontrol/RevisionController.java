package fitnesse.revisioncontrol;

import java.io.File;

public interface RevisionController {
  public Results add(String pagePath);

  public NewRevisionResults checkin(String pagePath, String commitMessage);

  public Results checkout(String pagePath);

  public Results delete(String pagePath);

  public Results revert(String pagePath);

  public NewRevisionResults update(String pagePath);

  public Results lock(String pagePath);

  public StatusResults getStatus(String pagePath);

  public OperationStatus move(File src, File dest);

  public State getState(String pagePath);

  public boolean isExternalRevisionControlEnabled();

   public boolean hasLocalLock(String pagePath);
}
