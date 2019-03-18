package poly;

import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.ListIterator;

public class Poly implements Derivable {
    private LinkedList<Item> expression = new LinkedList<>();
    private boolean sub = false;
    
    public Poly(Item... items) {
        for (Item item : items) {
            add(item.clone());
        }
    }
    
    public Poly(ArrayList<Item> items) {
        for (Item item : items) {
            add(item.clone());
        }
    }
    
    public Poly setSub() {
        sub = true;
        return this;
    }
    
    public boolean isFactor() {
        return expression.size() == 1 && expression.get(0).isFactor();
    }
    
    public Poly sort() {
        Poly poly = clone();
        poly.expression.sort(Item::compareTo);
        Collections.reverse(poly.expression);
        return poly;
    }
    
    public String print() {
        StringBuilder toPrint = new StringBuilder();
        Poly poly = sort();
        try {
            for (int i = 0; i < poly.expression.size(); i++) {
                ArrayList<Item> sub = new ArrayList<>();
                sub.add(poly.expression.get(i).clone());
                Factor common = null;
                for (int j = i + 1; j < poly.expression.size(); j++) {
                    if (common == null) {
                        sub.add(poly.expression.get(j).clone());
                        Factor temp = commonFactor(sub);
                        if (temp == null) {
                            sub.remove(1);
                        } else {
                            common = temp;
                            poly.expression.remove(i--);
                            poly.expression.remove(--j);
                            j--; // removed two items
                        }
                    } else if (poly.expression.get(j).containsFactor(common)) {
                        sub.add(poly.expression.remove(j--).clone());
                    }
                }
                if (common != null) {
                    Item item = new Item();
                    for (; common != null; common = commonFactor(sub)) {
                        item = (Item) item.mult(common);
                        for (Item item1 : sub) {
                            item1.devide(common);
                        }
                    }
                    toPrint.append(item).append("*")
                            .append(new Poly(sub).setSub().print());
                }
            }
            if (poly.isZero()) {
                if ('+' == toPrint.charAt(0)) {
                    toPrint.deleteCharAt(0);
                }
                return toPrint.toString();
            } else {
                return poly + toPrint.toString();
            }
        } catch (Throwable t) {
            return poly.toString();
        }
    }
    
    private static Factor commonFactor(ArrayList<Item> expression) {
        if (expression.size() <= 1) {
            return null;
        }
        for (Factor factor : expression.get(0)) {
            if (factor.isOne()) {
                continue;
            }
            int i;
            for (i = 1; i < expression.size()
                    && expression.get(i).containsFactor(factor); i++) {}
            if (i == expression.size()) {
                return factor;
            }
        }
        return null;
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
    
    @Override
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
        poly.sub = sub;
        for (Item item : expression) {
            poly.add(item);
        }
        return poly;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(Derivable.isInstance(obj, this.getClass()))) {
            return false;
        }
        HashSet<Item> hashSet = new HashSet<>(((Poly) obj).clone().expression);
        return hashSet.equals(new HashSet<>(expression));
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
        Poly poly = clone().sort();
        StringBuilder s = new StringBuilder();
        for (Item item : poly.expression) {
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
        if (sub && !isFactor()) {
            s.insert(0, "(").append(")");
        }
        return s.toString();
    }
}