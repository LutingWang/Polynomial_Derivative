package poly.element;

import poly.Derivable;

import java.math.BigInteger;

public final class Const extends Element implements Comparable<Const> {
    private BigInteger value;
    
    public Const(BigInteger v) {
        value = v;
    }
    
    public Const(int v) {
        this(BigInteger.valueOf(v));
    }
    
    public BigInteger getValue() {
        return value;
    }
    
    public Const negate() {
        value = value.negate();
        return this;
    }
    
    public Const add(Const c) {
        value = value.add(c.getValue());
        return this;
    }
    
    public Const mult(Const c) {
        value = value.multiply(c.getValue());
        return this;
    }
    
    @Override
    public Const differenciate() {
        return new Const(0);
    }
    
    @Override
    public int hashCode() {
        return super.getCode(TypeEnum.CONST);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Derivable)) {
            throw new ClassCastException();
        }
        return obj instanceof Const && this.value.equals(((Const) obj).value);
    }
    
    @Override
    public int compareTo(Const c) {
        return value.compareTo(c.getValue());
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
    
    @Override
    public Const clone() {
        return new Const(value);
    }
}
