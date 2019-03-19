package poly.element;

import poly.Factor;
import poly.Item;
import poly.Poly;

public class Tri extends Element {
    private final Poly poly; // poly is never zero
    
    public Tri(TypeEnum t, Poly poly) {
        super(t);
        assert t == TypeEnum.SIN || t == TypeEnum.COS;
        this.poly = poly.clone().setSub();
    }
    
    @Override
    public Item differentiate() {
        switch (getType()) {
            case SIN:
                return (Item) new Item(
                        new Factor(new Tri(TypeEnum.COS, poly))
                ).mult(poly.differentiate());
            case COS:
                return (Item) new Item(
                        new Factor(new Const(-1)),
                        new Factor(new Tri(TypeEnum.SIN, poly))
                ).mult(poly.differentiate());
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
        String print = poly.print();
        if (!poly.isFactor() && poly.isItem()) {
            print = "(" + print + ")";
        }
        temp += "(" + print + ")";
        return temp;
    }
}
