import java.util.Vector;

public class Union {

    // Part A (a): naive implementation (intentionally under-specified)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Vector union(Vector a, Vector b) {
        Vector result = new Vector();
        for (int i = 0; i < a.size(); i++) {
            result.add(a.get(i));
        }
        for (int i = 0; i < b.size(); i++) {
            result.add(b.get(i));
        }
        return result;
    }

    // Part A (d): precise implementation matching the tightened contract.
    public static <E> Vector<E> unionPrecise(Vector<E> a, Vector<E> b) {
        if (a == null || b == null) {
            throw new NullPointerException("union arguments must not be null");
        }
        Vector<E> result = new Vector<>();
        for (E e : a) {
            if (!result.contains(e)) {   // Vector.contains uses equals(), null-safe
                result.add(e);
            }
        }
        for (E e : b) {
            if (!result.contains(e)) {
                result.add(e);
            }
        }
        return result;
    }
}
