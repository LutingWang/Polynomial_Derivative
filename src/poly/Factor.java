package poly;

import poly.element.Const;
import poly.element.Element;
import poly.element.TypeEnum;

import java.math.BigInteger;

public class Factor implements Derivable, Comparable<Factor> {
    private final BigInteger exp; // never zero
    private final Element element;
    
    public Factor(Element element, BigInteger exp) {
        if (exp.equals(BigInteger.ZERO)) {
            this.element = new Const(1);
            this.exp = BigInteger.ONE;
        } else {
            this.element = element;
            this.exp = exp;
        }
    }
    
    public Factor(Element element) {
        this(element, BigInteger.ONE);
    }
    
    public TypeEnum getType() {
        return element.getType();
    }
    
    public boolean isConst() {
        return element instanceof Const;
    }
    
    public Const getConst() {
        if (!isConst()) {
            throw new ClassCastException();
        }
        return (Const) element;
    }
    
    public boolean isZero() {
        return isConst() && ((Const) element).isZero();
    }
    
    public boolean isOne() {
        return isConst() && ((Const) element).isOne();
    }
    
    public Factor mult(Factor factor) {
        if (isOne()) {
            return factor.clone();
        }
        if (this.getType() != factor.getType()) {
            throw new ClassCastException();
        }
        if (isConst()) {
            return new Factor(
                    ((Const) this.element).mult((Const) factor.element));
        } else {
            return new Factor(
                    element.clone(), exp.add(factor.exp));
        }
    }
    
    @Override
    public Item differenciate() {
        return new Item(
                new Factor(new Const(exp)),
                new Factor(element.clone(), exp.subtract(BigInteger.ONE))
        ).mult(element.differenciate());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Derivable)) {
            throw new ClassCastException();
        }
        if (!(obj instanceof Factor)) {
            return false;
        }
        Factor factor = (Factor) obj;
        return this.getType() == (factor).getType()
                && exp.equals(factor.exp);
    }
    
    @Override
    public int compareTo(Factor factor) {
        return Integer.compare(
                this.element.hashCode(), factor.element.hashCode());
    }
    
    @Override
    public String toString() {
        String temp = element.toString();
        if (!exp.equals(BigInteger.ONE)) {
            temp += "^" + exp.toString();
        }
        return temp;
    }
    
    @Override
    public Factor clone() {
        return new Factor(element.clone(), new BigInteger(exp.toString()));
    }
}
