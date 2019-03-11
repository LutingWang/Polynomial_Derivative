package poly;

import poly.element.Const;
import poly.element.Element;

import java.math.BigInteger;

public class Factor implements Derivable, Comparable<Factor> {
    private BigInteger exp;
    private Element element;
    
    public Factor(Element element, BigInteger exp) {
        this.element = element;
        this.exp = exp;
    }
    
    public Factor(Element element) {
        this(element, BigInteger.ONE);
    }
    
    public Element getElement() {
        return element;
    }
    
    public BigInteger getExp() {
        return exp;
    }
    
    public Factor mult(Factor factor) {
        if (hashCode() != factor.hashCode()) {
            throw new ClassCastException();
        }
        if (isConst()) {
            ((Const) element).mult((Const) factor.getElement());
        } else {
            exp = exp.add(factor.getExp());
        }
        return this;
    }
    
    public boolean isConst() {
        return element instanceof Const;
    }
    
    @Override
    public Item differenciate() {
        return new Item(
                new Factor(new Const(exp)),
                new Factor(element.clone(), exp.subtract(BigInteger.ONE))
        ).mult(element.differenciate());
    }
    
    public Factor merge() {
        if (exp.equals(BigInteger.ZERO)) {
            exp = BigInteger.ONE;
            element = new Const(1);
        }
        return this;
    }
    
    @Override
    public int hashCode() {
        return element.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Factor)) {
            throw new ClassCastException();
        }
        Factor factor = (Factor) obj;
        return hashCode() == factor.hashCode() && exp.equals(factor.getExp());
    }
    
    @Override
    public int compareTo(Factor factor) {
        return Integer.compare(hashCode(), factor.hashCode());
    }
    
    @Override
    public String toString() {
        if (exp.equals(BigInteger.ZERO)) {
            return "";
        }
        String temp = element.toString();
        if (!exp.equals(BigInteger.ONE)) {
            temp += "^" + exp.toString();
        }
        return temp + "*";
    }
    
    @Override
    public Factor clone() {
        return new Factor(element.clone(), new BigInteger(exp.toString()));
    }
}
