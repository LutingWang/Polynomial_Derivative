package poly;

public interface Derivable extends Cloneable {
    String VAR = "x";
    
    boolean isZero();
    
    boolean isOne();
    
    Derivable differentiate();
    
    Derivable mult(Derivable derivable);
    
    static boolean isInstance(Object obj, Class c) {
        if (!(obj instanceof Derivable)) {
            throw new ClassCastException();
        }
        return c.isInstance(obj);
    }
    
    Derivable clone();
}
