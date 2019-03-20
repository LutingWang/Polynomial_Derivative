package poly.element;

import poly.Derivable;
import poly.Item;
import poly.Poly;

public class Tri extends Element {
    private final Poly poly; // poly is never zero
    
    /**
     * Constructs an instance with specified type and nesting poly.
     * For the reason that poly is a mutable class, it is automatically
     * cloned with in the constructor.
     * @param t Type of this instance, e.g. SIN
     * @param poly Nesting poly
     */
    public Tri(TypeEnum t, Poly poly) {
        super(t);
        assert t == TypeEnum.SIN || t == TypeEnum.COS;
        this.poly = poly.clone().setSub();
    }
    
    @Override
    public boolean isZero() {
        return getType() == TypeEnum.SIN && poly.isZero();
    }
    
    @Override
    public Derivable differentiate() {
        switch (getType()) {
            case SIN:
                return new Item(
                        new Tri(TypeEnum.COS, poly),
                        poly.differentiate());
            case COS:
                return new Item(
                        new Const(-1),
                        new Tri(TypeEnum.SIN, poly),
                        poly.differentiate());
            default:
                throw new RuntimeException();
        }
    }
    
    @Override
    public Tri clone() {
        return new Tri(getType(), poly);
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && poly.equals(((Tri) obj).poly);
    }
    
    @Override
    public String toString() { // TODO: modify print
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
        String print = poly.print();
        if (!poly.isFactor() && poly.isItem()) {
            print = "(" + print + ")";
        }
        temp += "(" + print + ")";
        return temp;
    }
}
