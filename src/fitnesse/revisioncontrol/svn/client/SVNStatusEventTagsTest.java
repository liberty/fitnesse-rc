package fitnesse.revisioncontrol.svn.client;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SVNStatusEventTagsTest extends TestCase {
  public void testAllConstantsAreRegistered() throws Exception {
    Set<SVNStatusType> types = ConstantEnumUtil.getEnumsWhichConstantNameStartsWith(SVNStatusType.class, "");
    assertNotNull(types);
    assertTrue(types.size() > 0);
    assertSetsMatch(SVNStatusEventTags.getTagsByType().keySet(), types);
  }

  private void assertSetsMatch(Set<SVNStatusType> actual, Set<SVNStatusType> expected) {
    List<SVNStatusType> extra = cullSet(expected, actual);
    if (extra.size() > 0)
      fail("extra: " + extra);

    List<SVNStatusType> missing = cullSet(actual, expected);
    if (missing.size() > 0)
      fail("missing: " + missing);
  }

  private List<SVNStatusType> cullSet(Set<SVNStatusType> expected, Set<SVNStatusType> actual) {
    List<SVNStatusType> testActions = new ArrayList<SVNStatusType>(expected);
    testActions.removeAll(actual);
    return testActions;
  }
}