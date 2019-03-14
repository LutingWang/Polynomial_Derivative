package poly.element;

import poly.Derivable;

public final class Var extends Element {
    
    public Var() {
        super(TypeEnum.VAR);
    }
    
    @Override
    public Derivable differenciate() {
        return new Const(1).toFactor();
    }
    
    @Override
    public Var clone() {
        return new Var();
    }
    
    @Override
    public String toString() {
        return "x";
    }
}
