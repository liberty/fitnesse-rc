package fitnesse.revisioncontrol.svn;

import fitnesse.revisioncontrol.RevisionControlOperation;
import static fitnesse.revisioncontrol.RevisionControlOperation.*;
import static fitnesse.revisioncontrol.svn.SVNState.*;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SVNStateTest {

  @Test
  public void canPerformAddOperationIfStateIsUnknown() throws Exception {
    final RevisionControlOperation[] operations = SVNState.UNKNOWN.operations();
    assertOperationCount(operations, 1, UNKNOWN);
    assertEquals(ADD, operations[0]);
  }

  @Test
  public void canPerformCheckInUpdateRevertOperationsIfStateIsVersioned() throws Exception {
    final RevisionControlOperation[] operations = VERSIONED.operations();
    assertOperationCount(operations, 4, VERSIONED);
    assertContains(operations, CHECKIN, UPDATE, REVERT, STATUS);
  }

  @Test
  public void canPerformCheckInAndRevertOperationsIfStateIsDeleted() throws Exception {
    final RevisionControlOperation[] operations = SVNState.DELETED.operations();
    assertOperationCount(operations, 3, DELETED);
    assertContains(operations, CHECKIN, REVERT, STATUS);
  }

  @Test
  public void canPerformCheckInAndRevertOperationsIfStateIsAdded() throws Exception {
    final RevisionControlOperation[] operations = SVNState.ADDED.operations();
    assertOperationCount(operations, 3, ADDED);
    assertContains(operations, CHECKIN, REVERT, STATUS);
  }

  @Test
  public void testIsUnderRevisionControl() throws Exception {
    assertFalse("Files in Unknown State should not be under revision control", SVNState.UNKNOWN.isUnderRevisionControl());
    assertTrue("Files in Checked In State should be under revision control", VERSIONED.isUnderRevisionControl());
    assertTrue("Files in Added State should be under revision control", SVNState.ADDED.isUnderRevisionControl());
    assertTrue("Files in Deleted State should be under revision control", SVNState.DELETED.isUnderRevisionControl());
  }

  @Test
  public void testIsCheckedIn() throws Exception {
    assertTrue("Files in Checked In State should be checked in", VERSIONED.isCheckedIn());
    assertFalse("Files in Unknown State should not be checked in", SVNState.UNKNOWN.isCheckedIn());
    assertTrue("Files in Deleted State should be checked in", SVNState.DELETED.isCheckedIn());
    assertFalse("Files in Added State should not be checked in", SVNState.ADDED.isCheckedIn());
  }

  @Test
  public void testIsCheckedOut() throws Exception {
    assertTrue("Versioned Files should be checked out", VERSIONED.isCheckedOut());
    assertFalse("Files in Unknown State should not be checked out", SVNState.UNKNOWN.isCheckedOut());
    assertTrue("Files in Deleted State should be checked out", SVNState.DELETED.isCheckedOut());
    assertTrue("Files in Added State should not be checked out", SVNState.ADDED.isCheckedOut());
  }

  private void assertContains(RevisionControlOperation[] operations, RevisionControlOperation... expectedOperations) {
    final List<RevisionControlOperation> ops = Arrays.asList(operations);
    for (final RevisionControlOperation operation : expectedOperations)
      assertTrue(ops.contains(operation));
  }

  private void assertOperationCount(RevisionControlOperation[] operations, int operationCount, SVNState state) {
    assertEquals("Only " + operationCount + " operations should be allowed in " +
      state + " state:", operationCount, operations.length);
  }
}
