package poly.element;

import poly.Derivable;
import poly.Factor;
import poly.Item;
import poly.Poly;

public class Tri extends Element {
    private final Poly poly;
    
    public Tri(TypeEnum t, Poly poly) {
        super(t);
        assert t == TypeEnum.SIN || t == TypeEnum.COS;
        this.poly = poly;
    }
    
    @Override
    public Poly differenciate() {
        switch (getType()) {
            case SIN:
                return (Poly) new Item(
                        new Factor(new Tri(TypeEnum.COS, poly))
                ).mult(poly.differenciate());
            case COS:
                return (Poly) new Item(
                        new Factor(new Const(-1)),
                        new Factor(new Tri(TypeEnum.SIN, poly))
                ).mult(poly.differenciate());
            default:
                throw new RuntimeException();
        }
    }
    
    @Override
    public Tri clone() {
        return new Tri(getType(), poly.clone());
    }
    
    @Override
    public String toString() {
        String temp = "";
        switch (getType()) {
            case SIN:
                temp += "sin";
                break;
            case COS:
                temp += "cos";
                break;
            default:
                throw new RuntimeException();
        }
        temp += "(" + poly.toString() + ")";
        return temp;
    }
}
