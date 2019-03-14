package poly.element;

import poly.Derivable;
import poly.Factor;
import poly.Item;

public class Tri extends Element {
    
    public Tri(TypeEnum t) {
        super(t);
        assert t == TypeEnum.SIN || t == TypeEnum.COS;
    }
    
    @Override
    public TypeEnum type() {
        return super.type();
    }
    
    public boolean isSin() {
        return type() == TypeEnum.SIN;
    }
    
    public boolean isCos() {
        return type() == TypeEnum.COS;
    }
    
    @Override
    public Derivable differenciate() {
        Derivable derivable;
        switch (type()) {
            case SIN:
                derivable = new Tri(TypeEnum.COS);
                break;
            case COS:
                derivable = new Item(
                        new Factor(new Const(-1)),
                        new Factor(new Tri(TypeEnum.SIN))
                );
                break;
            default:
                throw new RuntimeException();
        }
        return derivable;
    }
    
    @Override
    public Tri clone() {
        return new Tri(type());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Derivable)) {
            throw new ClassCastException();
        }
        return obj instanceof Tri && type() == ((Tri) obj).type();
    }
    
    @Override
    public String toString() {
        String temp = "";
        switch (type()) {
            case SIN:
                temp += "sin";
                break;
            case COS:
                temp += "cos";
                break;
            default:
                throw new RuntimeException();
        }
        temp += "(" + Derivable.VAR + ")";
        return temp;
    }
}
