package poly.element;

import poly.Derivable;

public final class Var extends Element {
    
    @Override
    public Derivable differenciate() {
        return new Const(1);
    }
    
    @Override
    public int hashCode() {
        return super.getCode(TypeEnum.VAR);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Derivable)) {
            throw new ClassCastException();
        }
        return obj instanceof Var;
    }
    
    @Override
    public String toString() {
        return "x";
    }
    
    @Override
    public Var clone() {
        return new Var();
    }
}
