/*
 * Copyright (c) 2006 Sabre Holdings. All Rights Reserved.
 */

package fitnesse.revisioncontrol.svn.client;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.wc.SVNEventAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SVNEventActionTagsTest extends TestCase {
  public void testAllConstantsAreRegistered() throws Exception {
    Set<SVNEventAction> types = ConstantEnumUtil.getEnumsWhichConstantNameStartsWith(SVNEventAction.class, "");
    assertNotNull(types);
    assertTrue(types.size() > 0);
    assertSetsMatch(SVNEventActionTags.getTagsByType().keySet(), types);
  }

  private void assertSetsMatch(Set<SVNEventAction> actual, Set<SVNEventAction> expected) {
    List<SVNEventAction> extra = cullSet(expected, actual);
    if (extra.size() > 0)
      fail("extra: " + extra);

    List<SVNEventAction> missing = cullSet(actual, expected);
    if (missing.size() > 0)
      fail("missing: " + missing);
  }

  private List<SVNEventAction> cullSet(Set<SVNEventAction> expected, Set<SVNEventAction> actual) {
    List<SVNEventAction> testActions = new ArrayList<SVNEventAction>(expected);
    testActions.removeAll(actual);
    return testActions;
  }
}