package poly.element;

import poly.Derivable;

import java.math.BigInteger;

/**
 * Encapsulates java {@code BigInteger} in order to provide convenient access
 * to other classes.
 */
public final class Const extends Element implements Comparable<Const> {
    private final BigInteger value;
    
    public Const(BigInteger v) {
        super(TypeEnum.CONST);
        value = v;
    }
    
    public Const(int v) {
        this(BigInteger.valueOf(v));
    }
    
    @Override
    public boolean isZero() {
        return value.equals(BigInteger.ZERO);
    }
    
    @Override
    public boolean isOne() {
        return value.equals(BigInteger.ONE);
    }
    
    public Const abs() {
        return new Const(value.abs());
    }
    
    public Const negate() {
        return new Const(value.negate());
    }
    
    public Const add(Const c) {
        return new Const(value.add(c.value));
    }
    
    @Override
    public Derivable mult(Derivable derivable) {
        if (derivable instanceof Const) {
            return new Const(value.multiply(((Const) derivable).value));
        } else {
            return super.mult(derivable);
        }
    }
    
    @Override
    public Derivable differentiate() {
        return new Const(0);
    }
    
    @Override
    public int compareTo(Const c) {
        return value.compareTo(c.value);
    }
    
    @Override
    public Const clone() {
        return new Const(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && this.value.equals(((Const) obj).value);
    }
    
    @Override
    public String toString() {
        String temp = "";
        if (value.compareTo(BigInteger.ZERO) >= 0) {
            temp += "+";
        } else {
            temp += "-";
        }
        if (!value.abs().equals(BigInteger.ONE)) {
            temp += value.abs().toString();
        }
        return temp;
    }
}
