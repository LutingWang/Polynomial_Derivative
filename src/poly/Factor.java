package poly;

import poly.element.Const;
import poly.element.Element;
import poly.element.TypeEnum;

import java.math.BigInteger;

public class Factor implements Derivable, Comparable<Factor> {
    private static final BigInteger EXP_UPPER_BOUND =
            new BigInteger("10000");
    
    private final BigInteger exp; // never zero
    private final Element element;
    
    public Factor(Element element, BigInteger exp) {
        if (exp.equals(BigInteger.ZERO)) {
            this.element = new Const(1);
            this.exp = BigInteger.ONE;
        } else if (exp.abs().compareTo(EXP_UPPER_BOUND) <= 0) {
            this.element = element.clone();
            this.exp = exp;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public Factor(Element element) {
        this(element, BigInteger.ONE);
    }
    
    TypeEnum getType() {
        return element.getType();
    }
    
    private boolean isConst() {
        return element instanceof Const;
    }
    
    Const getConst() {
        if (!isConst()) {
            throw new ClassCastException();
        }
        return (Const) element;
    }
    
    public boolean isZero() {
        return element.isZero();
    }
    
    boolean isOne() {
        return  isConst() && element.isOne();
    }
    
    boolean absIsOne() {
        return isConst() && ((Const) element).abs().isOne();
    }
    
    public boolean equivalent(Factor factor) {
        return this.isOne() || factor.isOne() || element.equals(factor.element);
    }
    
    private Factor mult(Factor factor) {
        if (isOne()) {
            return factor.clone();
        } else if (factor.isOne()) {
            return clone();
        }
        if (this.getType() != factor.getType()) {
            throw new ClassCastException();
        }
        if (isConst()) {
            return new Factor((Const) this.element.mult(factor.element));
        } else {
            return new Factor(element, exp.add(factor.exp));
        }
    }
    
    @Override
    public Factor mult(Derivable derivable) {
        if (!(derivable instanceof Factor)) {
            throw new ClassCastException();
        }
        return mult((Factor) derivable);
    }
    
    @Override
    public Derivable differentiate() {
        return new Item(
                new Factor(new Const(exp)),
                new Factor(element, exp.subtract(BigInteger.ONE))
        ).mult(element.differentiate());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!Derivable.isInstance(obj, this.getClass())
                || this.getType() != ((Factor) obj).getType()) {
            return false;
        }
        Factor factor = (Factor) obj;
        return element.equals(factor.element) && exp.equals(factor.exp);
    }
    
    @Override
    public int compareTo(Factor factor) {
        return Integer.compare(
                this.element.hashCode(), factor.element.hashCode());
    }
    
    @Override
    public int hashCode() {
        return element.hashCode();
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
        return new Factor(element, exp);
    }
}
