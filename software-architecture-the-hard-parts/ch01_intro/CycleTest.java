
// Example 1-1. Fitness function to detect component cycles
//
public class CycleTest {
  private JDepend jdepend;

  // an architect uses the metrics tool JDepend to check the dependencies between
  // packages. The tool understands the structure of Java packages and fails the
  // test if any cycles exist. An architect can wire this test into the continuous
  // build on a project and stop worrying about the accidental introduction of
  // cycles by triggerhappy developers. This is a great example of a fitness
  // function
  @BeforeEach
  void init() {
    jdepend = new JDepend();
    jdepend.addDirectory("/path/to/project/persistence/classes");
    jdepend.addDirectory("/path/to/project/web/classes");
    jdepend.addDirectory("/path/to/project/thirdpartyjars");
  }

  @Test
  void testAllPackages() {
    Collection packages = jdepend.analyze();
    assertEquals("Cycles exist", false, jdepend.containsCycles());
  }
}
