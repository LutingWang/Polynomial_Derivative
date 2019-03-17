package poly;

public interface Derivable {
    String VAR = "x";
    
    boolean isZero();
    
    Derivable differentiate();
    
    Derivable mult(Derivable derivable);
    
    static boolean isInstance(Object obj, Class c) {
        if (!(obj instanceof Derivable)) {
            throw new ClassCastException();
        }
        return c.isInstance(obj);
    }
}
