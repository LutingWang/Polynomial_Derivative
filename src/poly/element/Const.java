package poly.element;

import poly.Derivable;

import java.math.BigInteger;

public final class Const extends Element implements Comparable<Const> {
    private final BigInteger value;
    
    public Const(BigInteger v) {
        super(TypeEnum.CONST);
        value = v;
    }
    
    public Const(int v) {
        this(BigInteger.valueOf(v));
    }
    
    public BigInteger getValue() {
        return value;
    }
    
    public boolean isZero() {
        return value.equals(BigInteger.ZERO);
    }
    
    public boolean isOne() {
        return value.equals(BigInteger.ONE);
    }
    
    public Const negate() {
        return new Const(value.negate());
    }
    
    public Const add(Const c) {
        return new Const(value.add(c.getValue()));
    }
    
    public Const mult(Const c) {
        return new Const(value.multiply(c.getValue()));
    }
    
    @Override
    public Const differenciate() {
        return new Const(0);
    }
    
    @Override
    public int compareTo(Const c) {
        return value.compareTo(c.getValue());
    }
    
    @Override
    public Const clone() {
        return new Const(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return this.value.equals(((Const) obj).value);
        }
        return false;
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
