package poly;

import poly.element.Const;
import poly.element.Element;
import poly.element.TypeEnum;

import java.math.BigInteger;

public class Factor implements Derivable, Comparable<Factor> {
    /**
     * No exponent should exceed this value.
     */
    public static final BigInteger EXP_UPPER_BOUND =
            new BigInteger("10000");
    
    private final BigInteger exp; // never zero
    private final Element element;
    
    public Factor(Element element, BigInteger exp) {
        if (exp.equals(BigInteger.ZERO)) {
            this.element = new Const(1);
            this.exp = BigInteger.ONE;
        } else if (exp.abs().compareTo(EXP_UPPER_BOUND) <= 0) {
            // check sin(0)
            if (element.isZero() && element.getType() != TypeEnum.CONST) {
                this.element = new Const(0);
            } else {
                this.element = element.clone();
            }
            this.exp = exp;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public Factor(Element element) {
        this(element, BigInteger.ONE);
    }
    
    Factor(int num) {
        this(new Const(num));
    }
    
    TypeEnum getType() {
        return element.getType();
    }
    
    @Override
    public boolean isZero() {
        return element.isZero();
    }
    
    @Override
    public boolean isOne() {
        return element.isOne();
    }
    
    boolean absIsOne() {
        return isConst() && ((Const) element).abs().isOne();
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
    
    /**
     * The two factors can be multiplied.
     * @param factor The factor to be multiplied with this instance
     * @return if this two factors can be multiplied
     */
    boolean equivalent(Factor factor) {
        return this.isOne() || factor.isOne() || element.equals(factor.element);
    }
    
    private Factor mult(Element element) {
        return mult(new Factor(element));
    }
    
    private Factor mult(Factor factor) {
        // if either is one, return the other
        if (isOne()) {
            return factor.clone();
        } else if (factor.isOne()) {
            return clone();
        }
        // if neither is one, two factors must have the same type
        if (this.getType() != factor.getType()) {
            throw new ClassCastException();
        }
        if (isConst()) { // constant multiplication need to invoke its method
            return new Factor((Const) this.element.mult(factor.element));
        } else { // others add exponent
            return new Factor(element, exp.add(factor.exp));
        }
    }
    
    /**
     * Factors can only multiply {@code Element} or {@code Factor}.
     */
    @Override
    public Factor mult(Derivable derivable) {
        if (derivable instanceof Element) {
            return mult((Element) derivable);
        }
        if (derivable instanceof Factor) {
            return mult((Factor) derivable);
        }
        throw new ClassCastException();
    }
    
    @Override
    public Derivable differentiate() {
        return new Item(
                new Const(exp),
                new Factor(element, exp.subtract(BigInteger.ONE)),
                element.differentiate());
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
