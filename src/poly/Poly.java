package poly;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

public class Poly implements Derivable {
    private LinkedList<Item> expression = new LinkedList<>();
    
    public Poly(Item... items) {
        for (Item item : items) {
            add(item.clone());
        }
    }
    
    public Poly sort() {
        expression.sort(Item::compareTo);
        Collections.reverse(expression);
        return this;
    }
    
    public boolean isZero() {
        return expression.size() == 0;
    }
    
    private Poly add(Item item) {
        if (item.isZero()) {
            return this;
        }
        for (ListIterator<Item> it = expression.listIterator(); it.hasNext();) {
            Item item1 = it.next();
            if (item.equivalent(item1)) {
                it.set(item1.add(item));
                if (item1.isZero()) {
                    it.remove();
                }
                return this;
            }
        }
        expression.add(item);
        return this;
    }
    
    private Poly add(Poly poly) {
        for (Item item : poly.expression) {
            add(item);
        }
        return this;
    }
    
    public Poly add(Derivable derivable) {
        Poly poly = clone();
        if (derivable instanceof Item) {
            return poly.add((Item) derivable);
        }
        if (derivable instanceof Poly) {
            return poly.add((Poly) derivable);
        }
        throw new ClassCastException();
    }
    
    private Poly mult(Item item) {
        Poly poly = new Poly();
        for (Item item1 : expression) {
            poly.add((Item) item.mult(item1));
        }
        return poly;
    }
    
    private Poly mult(Poly poly) {
        Poly poly1 = new Poly();
        for (Item item : poly.expression) {
            poly1.add(mult(item));
        }
        return poly1;
    }
    
    public Poly mult(Derivable derivable) {
        Poly poly = clone();
        if (derivable instanceof Item) {
            return poly.mult((Item) derivable);
        }
        if (derivable instanceof Poly) {
            return poly.mult((Poly) derivable);
        }
        throw new ClassCastException();
    }
    
    public Poly differentiate() {
        Poly poly = new Poly();
        for (Item it : expression) {
            poly.add(it.differentiate());
        }
        return poly;
    }
    
    @Override
    public Poly clone() {
        Poly poly = new Poly();
        for (Item item : expression) {
            poly.add(item);
        }
        return poly;
    }
    
    /**
     * Transforms the polynomial to a string representation. The sequence of
     * items is decided by their coefficients, specifically decreasing. The
     * purpose is to try to set the leading term with a positive coefficient.
     *
     * @return a string representation of the polynomial.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Item item : expression) {
            if (!item.isZero()) {
                s.append(item.toString());
            }
        }
        if (s.length() == 0) {
            return "0";
        }
        if ('+' == s.charAt(0)) {
            s.deleteCharAt(0);
        }
        return s.toString();
    }
}