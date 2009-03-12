package fitnesse.revisioncontrol;

import static fitnesse.revisioncontrol.NullState.VERSIONED;
import static fitnesse.revisioncontrol.RevisionControlOperation.*;
import static org.easymock.EasyMock.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RevisionControlOperationTest {
  private final String filePath = "SomeFilePath";
  private final RevisionController revisionController = createMock(RevisionController.class);

  @Before
  public void init() {
    reset(revisionController);
  }

  @After
  public void verifyMocks() {
    verify(revisionController);
  }

  @Test
  public void addShouldDelegateCallToRevisionController() throws Exception {
    expect(revisionController.add(filePath)).andReturn(new Results());
    replay(revisionController);
    ADD.execute(revisionController, filePath);
  }

  @Test
  public void checkinShouldDelegateCallToRevisionController() throws Exception {
    expect(revisionController.checkin(filePath)).andReturn(new NewRevisionResults());
    replay(revisionController);
    CHECKIN.execute(revisionController, filePath);
  }

  @Test
  public void checkoutShouldDelegateCallToRevisionController() throws Exception {
    expect(revisionController.checkout(filePath)).andReturn(new Results());
    replay(revisionController);
    CHECKOUT.execute(revisionController, filePath);
  }

  @Test
  public void deleteShouldDelegateCallToRevisionController() throws Exception {
    expect(revisionController.delete(filePath)).andReturn(new Results());
    replay(revisionController);
    DELETE.execute(revisionController, filePath);
  }

  @Test
  public void revertShouldDelegateCallToRevisionController() throws Exception {
    expect(revisionController.revert(filePath)).andReturn(new Results());
    replay(revisionController);
    REVERT.execute(revisionController, filePath);
  }

  @Test
  public void updateShouldDelegateCallToRevisionController() throws Exception {
    expect(revisionController.update(filePath)).andReturn(new NewRevisionResults());
    replay(revisionController);
    UPDATE.execute(revisionController, filePath);
  }

  @Test
  public void syncShouldDelegateCallToRevisionController() throws Exception {
    expect(revisionController.getState(filePath)).andReturn(VERSIONED);
    replay(revisionController);
    SYNC.execute(revisionController, filePath);
  }
}
