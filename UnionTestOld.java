// ============================================================================
// OLD / REFERENCE ONLY — original JUnit-based test set.
// This is NOT the version used for submission. The simple, self-contained
// plain-Java test set (no JUnit required) is in UnionTest.java.
// Kept here only to show our original JUnit approach.
//
// To run this one you still need the JUnit jars on the classpath:
//   javac -cp .:junit-4.13.2.jar Union.java UnionTestOld.java
//   java  -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar UnionTestOld
// ============================================================================
import static org.junit.Assert.*;

import java.util.Vector;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // Informs JUnit to run test methods in alphabetical order
public class UnionTestOld {

    /** Helper so every test targets one place (the "System Under Test"). */
    private static Vector sut(Vector a, Vector b) {
        // return Union.union(a, b);          // Part (a) naive  -> several tests fail
        return Union.unionPrecise(a, b);      // Part (d) precise -> all pass
    }

    private static Vector<String> v(String... xs) {
        Vector<String> r = new Vector<>();
        for (String x : xs) r.add(x);
        return r;
    }

    // Prints inputs, results, and expected  output for a test, then asserts equality.
    private static void check(String name, Object a, Object b,
                              Object actual, Object expected) {
        System.out.println("[" + name + "]");
        System.out.println("    input a  = " + a);
        System.out.println("    input b  = " + b);
        System.out.println("    result   = " + actual);
        System.out.println("    expected = " + expected);
        assertEquals(expected, actual);
    }

    // Test 1 Baseline: Disjoint inputs.
    // Fault addressed: Order of the result (elements of a appear before elements of b); also the baseline / normal case.
    @Test
    public void test01_disjoint() {
        Vector<String> a = v("a", "b"), b = v("c", "d");
        check("Test 1: disjoint", a, b, sut(a, b), v("a", "b", "c", "d"));
    }

    // Test 2: element common to both. Rationale: reveals the duplicate vs. non-duplicate semantics ambiguity; naive implentation produces a duplicate ["a","b","b","c"].
    // Fault addressed: Meaning of union (an element in both Vectors should appear once, not twice).
    @Test
    public void test02_overlapAcrossVectors() {
        Vector<String> a = v("a", "b"), b = v("b", "c");
        check("Test 2: overlapAcrossVectors", a, b, sut(a, b), v("a", "b", "c"));
    }

    // Test 3: duplicates within one input. Rationale: unclear whether duplicate elements within a single Vector should be removed. Expected result assumes set semantics.
    // Fault addressed: Meaning of union (duplicate elements already within a single Vector should be removed).
    @Test
    public void test03_duplicateWithinInput() {
        Vector<String> a = v("a", "a", "b"), b = v();
        check("Test 3: duplicateWithinInput", a, b, sut(a, b), v("a", "b"));
    }

    // Test 4: one input is empty. Rationale: union with an empty Vector should return the elements of the non-empty Vector unchanged.
    // Fault addressed: Null vectors (union with an empty Vector is an identity boundary).
    @Test
    public void test04_oneEmpty() {
        Vector<String> a = v("x", "y"), b = v();
        check("Test 4: oneEmpty", a, b, sut(a, b), v("x", "y"));
    }

    // Test 5: null ARGUMENT is rejected. Rationale: a null Vector is not the same as an empty Vector.
    // Fault addressed: Null vectors (a null argument throws NullPointerException).
    @Test
    @SuppressWarnings("unchecked")
    public void test05_nullArgumentRejected() {
        System.out.println("[Test 5: nullArgumentRejected]");
        System.out.println("    input a  = null");
        System.out.println("    input b  = " + v("a"));
        System.out.println("    expected = NullPointerException");
        try {
            Vector result = sut(null, v("a"));
            fail("expected NullPointerException but got result " + result);
        } catch (NullPointerException e) {
            System.out.println("    result   = threw NullPointerException"
                    + (e.getMessage() != null ? " (\"" + e.getMessage() + "\")" : ""));
        }
    }

    // Test 6: null element is a legal value. Rationale: contract permits null, at most one null in the result.
    // Fault addressed: Null elements (null is allowed and de-duplicated like any other value).
    @Test
    public void test06_nullElementDeDuplicated() {
        Vector<String> a = v(); a.add(null);
        Vector<String> b = v(); b.add(null);
        Vector result = sut(a, b);
        System.out.println("[Test 6: nullElementDeDuplicated]");
        System.out.println("    input a  = " + a);
        System.out.println("    input b  = " + b);
        System.out.println("    result   = " + result + "   size=" + result.size());
        System.out.println("    expected = [null] (size 1)");
        assertEquals(1, result.size());
        assertNull(result.get(0));
    }

    // Test 7: both inputs are empty. Rationale: the union of two empty Vectors contains no elements, so the result should be an empty Vector rather than null.
    // Fault addressed: Null result (two empty inputs yield an empty Vector, not null).
    @Test
    public void test07_bothEmpty() {
        Vector<String> a = v(), b = v();
        check("Test 7: bothEmpty", a, b, sut(a, b), v());
    }

    // Test 8: equal values in distinct objects. Rationale: checks whether duplicate detection uses == (object identity) or .equals() (value equality).
    // Fault addressed: equality of elements
    @Test
    public void test08_equalityByValueNotIdentity() {
        Vector<String> a = v(); a.add(new String("k"));
        Vector<String> b = v(); b.add(new String("k"));
        Vector result = sut(a, b);
        System.out.println("[Test 8: equalityByValueNotIdentity]");
        System.out.println("    input a  = " + a + "  (distinct String object, value \"k\")");
        System.out.println("    input b  = " + b + "  (distinct String object, value \"k\")");
        System.out.println("    result   = " + result + "   size=" + result.size());
        System.out.println("    expected = size 1 (equal by value)");
        assertEquals(1, result.size());
    }

    // Test 9: inputs must not be mutated. Rationale: no side effects on args.
    // Fault addressed: Modification of the input vectors (a and b are left unchanged).
    @Test
    public void test09_inputsNotModified() {
        Vector<String> a = v("a", "b"), b = v("b", "c");
        Vector<String> result = sut(a, b);
        System.out.println("[Test 9: inputsNotModified]");
        System.out.println("    input a  = " + a + "   (must stay [a, b])");
        System.out.println("    input b  = " + b + "   (must stay [b, c])");
        System.out.println("    result   = " + result);
        assertEquals("a was modified", v("a", "b"), a);
        assertEquals("b was modified", v("b", "c"), b);
    }

    // Test 10: aliased inputs (a == b). Rationale: same object passed twice must still yield a set with no duplicates.
    // Fault addressed: Same vectors passed twice (elements are treated as one collection, not added twice).
    @Test
    public void test10_aliasedInputs() {
        Vector<String> a = v("a", "b");
        check("Test 10: aliasedInputs (same Vector passed as both args)",
                a, a, sut(a, a), v("a", "b"));
    }

    /** Important! The type of elements is not exercised by a runtime unit test. The raw-Vector typing concern is a
     *  compile-time issue, resolved by the generic signature in Part (d) rather than by a JUnit case. */

    // Test 11: same printed value but different types (Integer 1 vs String "1").
    // Fault addressed: Equality of elements + Type of elements (equality uses equals(), so
    // Integer 1 and String "1" are NOT duplicates and both are kept, even though both print as 1).
    @Test
    public void test11_intVsStringSameValue() {
        Vector<Object> a = new Vector<>(); a.add(Integer.valueOf(1)); // Integer 1
        Vector<Object> b = new Vector<>(); b.add("1");                // String "1"
        Vector result = sut(a, b);
        System.out.println("[Test 11: intVsStringSameValue]");
        System.out.println("    input a  = " + a + "   (Integer 1)");
        System.out.println("    input b  = " + b + "   (String \"1\")");
        System.out.println("    result   = " + result + "   size=" + result.size());
        System.out.println("    expected = [1, 1] size 2  (Integer 1 != String \"1\")");
        assertEquals(2, result.size());
        assertEquals(Integer.valueOf(1), result.get(0));
        assertEquals("1", result.get(1));
    }

    // Runner: prints a PASS / FAIL line per test with the test's name
    public static void main(String[] args) {
        JUnitCore core = new JUnitCore();
        core.addListener(new RunListener() {
            private boolean failed;

            @Override public void testStarted(Description d) {
                failed = false;
            }
            @Override public void testFailure(Failure f) {
                failed = true;
                System.out.println("    >>> FAIL: " + f.getMessage());
            }
            @Override public void testFinished(Description d) {
                System.out.println("    >>> " + (failed ? "FAIL" : "PASS")
                        + ": " + d.getMethodName());
                System.out.println();
            }
        });
        Result r = core.run(UnionTestOld.class);
        System.out.println("==================================================");
        System.out.println("Tests run: " + r.getRunCount()
                + ",  Passed: " + (r.getRunCount() - r.getFailureCount())
                + ",  Failed: " + r.getFailureCount());
        System.exit(r.wasSuccessful() ? 0 : 1);
    }
}
