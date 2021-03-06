package poly.element;

import poly.Derivable;

public final class Var extends Element {
    
    public Var() {
        super(TypeEnum.VAR);
    }
    
    @Override
    public Derivable differentiate() {
        return new Const(1);
    }
    
    @Override
    public Var clone() {
        return new Var();
    }
    
    @Override
    public String toString() {
        return Derivable.VAR;
    }
}
