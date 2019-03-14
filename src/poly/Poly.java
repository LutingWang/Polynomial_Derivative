package poly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Poly implements Derivable {
    private LinkedList<Item> expression = new LinkedList<>();
    
    public Poly sort() {
        expression.sort(Item::compareTo);
        Collections.reverse(expression);
        return this;
    }
    
    private Poly add(Item item) {
        for (Item item1 : expression) {
            if (item.equals(item1)) {
                item1.add(item);
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
        if (derivable instanceof Item) {
            return add((Item) derivable);
        }
        if (derivable instanceof Poly) {
            return add((Poly) derivable);
        }
        throw new ClassCastException();
    }

    public Poly differenciate() {
        Poly poly = new Poly();
        for (Item it : expression) {
            poly.add(it.differenciate());
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
        for (Item it : expression) {
            if (!it.isZero()) {
                s.append(it.toString());
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
    
    @Override
    public Poly clone() {
        Poly poly = new Poly();
        for (Item item : expression) {
            poly.add(item);
        }
        return poly;
    }
}