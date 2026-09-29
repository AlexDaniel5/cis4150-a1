import java.util.Vector;

public class UnionTest {

    private static int passed = 0;
    private static int failed = 0;

    /** Use commentation to decide which implementation to test */
    private static Vector sut(Vector a, Vector b) {
        // return Union.union(a, b);          // Part (a) naive  -> several tests fail
        return Union.unionPrecise(a, b);      // Part (d) precise -> all pass
    }

    /** Builds a Vector<String> from the given values. */
    private static Vector<String> v(String... xs) {
        Vector<String> r = new Vector<>();
        for (String x : xs) r.add(x);
        return r;
    }

    /** Records and prints a PASS/FAIL line for a test. */
    private static void report(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println("    >>> " + (ok ? "PASS" : "FAIL") + ": " + name);
        System.out.println();
    }

    /** Prints the input/result/expected block, then compares with equals() and records the result. */
    private static void check(String name, Object a, Object b, Object actual, Object expected) {
        System.out.println("[" + name + "]");
        System.out.println("    input a  = " + a);
        System.out.println("    input b  = " + b);
        System.out.println("    result   = " + actual);
        System.out.println("    expected = " + expected);
        report(name, expected == null ? actual == null : expected.equals(actual));
    }

    public static void main(String[] args) {

        // Test 1: disjoint inputs (baseline / normal case).
        // Fault addressed: Order of the result (elements of a appear before elements of b).
        {
            Vector<String> a = v("a", "b"), b = v("c", "d");
            check("Test 1: disjoint", a, b, sut(a, b), v("a", "b", "c", "d"));
        }

        // Test 2: element common to both inputs.
        // Fault addressed: Meaning of union (an element in both Vectors should appear once, not twice).
        {
            Vector<String> a = v("a", "b"), b = v("b", "c");
            check("Test 2: overlapAcrossVectors", a, b, sut(a, b), v("a", "b", "c"));
        }

        // Test 3: duplicates within one input.
        // Fault addressed: Meaning of union (duplicate elements within a single Vector should be removed).
        {
            Vector<String> a = v("a", "a", "b"), b = v();
            check("Test 3: duplicateWithinInput", a, b, sut(a, b), v("a", "b"));
        }

        // Test 4: one input is empty (union with an empty Vector returns the other unchanged).
        // Fault addressed: empty inputs.
        {
            Vector<String> a = v("x", "y"), b = v();
            check("Test 4: oneEmpty", a, b, sut(a, b), v("x", "y"));
        }

        // Test 5: a null argument is rejected (null Vector != empty Vector).
        // Fault addressed: Null vectors (a null argument throws NullPointerException).
        {
            System.out.println("[Test 5: nullArgumentRejected]");
            System.out.println("    input a  = null");
            System.out.println("    input b  = " + v("a"));
            System.out.println("    expected = NullPointerException");
            boolean ok;
            try {
                Vector result = sut(null, v("a"));
                System.out.println("    result   = returned " + result + " (no exception)");
                ok = false;
            } catch (NullPointerException e) {
                System.out.println("    result   = threw NullPointerException"
                        + (e.getMessage() != null ? " (\"" + e.getMessage() + "\")" : ""));
                ok = true;
            }
            report("Test 5: nullArgumentRejected", ok);
        }

        // Test 6: null element is a legal value (at most one null in the result).
        // Fault addressed: Null elements.
        {
            Vector<String> a = v(); a.add(null);
            Vector<String> b = v(); b.add(null);
            Vector result = sut(a, b);
            System.out.println("[Test 6: nullElementDeDuplicated]");
            System.out.println("    input a  = " + a);
            System.out.println("    input b  = " + b);
            System.out.println("    result   = " + result + "   size=" + result.size());
            System.out.println("    expected = [null] (size 1)");
            report("Test 6: nullElementDeDuplicated", result.size() == 1 && result.get(0) == null);
        }

        // Test 7: both inputs are empty (result is an empty Vector, not null).
        // Fault addressed: Null result.
        {
            Vector<String> a = v(), b = v();
            check("Test 7: bothEmpty", a, b, sut(a, b), v());
        }

        // Test 8: equal-but-distinct objects (equality by value, not identity).
        // Fault addressed: Equality of elements.
        {
            Vector<String> a = v(); a.add(new String("k"));
            Vector<String> b = v(); b.add(new String("k"));
            Vector result = sut(a, b);
            System.out.println("[Test 8: equalityByValueNotIdentity]");
            System.out.println("    input a  = " + a + "  (distinct String object, value \"k\")");
            System.out.println("    input b  = " + b + "  (distinct String object, value \"k\")");
            System.out.println("    result   = " + result + "   size=" + result.size());
            System.out.println("    expected = size 1 (equal by value)");
            report("Test 8: equalityByValueNotIdentity", result.size() == 1);
        }

        // Test 9: aliased inputs (a == b) still yield a set with no duplicates.
        // Fault addressed: Same vectors passed twice.
        {
            Vector<String> a = v("a", "b");
            check("Test 9: aliasedInputs (same Vector passed as both args)", a, a, sut(a, a), v("a", "b"));
        }

        // Test 10: same printed value but different types (Integer 1 vs String "1").
        // Fault addressed: Ensure that Integer 1 and String "1" remain separate in the result Vector, following equals() behavior.
        {
            Vector<Object> a = new Vector<>(); a.add(Integer.valueOf(1)); // Integer 1
            Vector<Object> b = new Vector<>(); b.add("1");                // String "1"
            Vector result = sut(a, b);
            System.out.println("[Test 10: intVsStringSameValue]");
            System.out.println("    input a  = " + a + "   (Integer 1)");
            System.out.println("    input b  = " + b + "   (String \"1\")");
            System.out.println("    result   = " + result + "   size=" + result.size());
            System.out.println("    expected = [1, 1] size 2  (Integer 1 != String \"1\")");
            boolean ok = result.size() == 2
                    && Integer.valueOf(1).equals(result.get(0))
                    && "1".equals(result.get(1));
            report("Test 10: intVsStringSameValue", ok);
        }

        System.out.println("==================================================");
        System.out.println("Tests run: " + (passed + failed)
                + ",  Passed: " + passed + ",  Failed: " + failed);
        System.exit(failed == 0 ? 0 : 1);
    }
}
