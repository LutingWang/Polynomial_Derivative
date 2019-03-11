package poly.element;

import poly.Derivable;
import poly.Factor;
import poly.Item;

public class Triangular extends Element {
    private TypeEnum typeEnum;
    
    public Triangular(TypeEnum t) {
        if (t.equals(TypeEnum.SIN) || t.equals(TypeEnum.COS)) {
            typeEnum = t;
        } else {
            throw new RuntimeException("Triangular function type does not match.");
        }
    }
    
    @Override
    public Derivable differenciate() {
        Derivable derivable = null;
        switch (typeEnum) {
            case SIN:
                derivable = new Triangular(TypeEnum.COS);
                break;
            case COS:
                derivable = new Item(
                        new Factor(new Const(-1)),
                        new Factor(new Triangular(TypeEnum.SIN))
                );
                break;
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
        return obj instanceof Triangular && this.typeEnum.equals(((Triangular) obj).typeEnum);
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
        }
        temp += Derivable.VAR + ")";
        return temp;
    }
    
    @Override
    public Triangular clone() {
        return new Triangular(typeEnum);
    }
}
