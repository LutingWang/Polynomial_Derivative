package poly.element;

import poly.Derivable;
import poly.Factor;
import poly.Item;

public class Tri extends Element {
    private TypeEnum typeEnum;
    
    public Tri(TypeEnum t) {
        if (t.equals(TypeEnum.SIN) || t.equals(TypeEnum.COS)) {
            typeEnum = t;
        } else {
            throw new RuntimeException("Tri function type does not match.");
        }
    }
    
    @Override
    public Derivable differenciate() {
        Derivable derivable = null;
        switch (typeEnum) {
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
    public int hashCode() {
        return super.getCode(typeEnum);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Derivable)) {
            throw new ClassCastException();
        }
        return obj instanceof Tri && this.typeEnum.equals(((Tri) obj).typeEnum);
    }
    
    @Override
    public String toString() {
        String temp = "";
        switch (typeEnum) {
            case SIN:
                temp += "sin(";
                break;
            case COS:
                temp += "cos(";
                break;
            default:
                throw new RuntimeException();
        }
        temp += Derivable.VAR + ")";
        return temp;
    }
    
    @Override
    public Tri clone() {
        return new Tri(typeEnum);
    }
}
