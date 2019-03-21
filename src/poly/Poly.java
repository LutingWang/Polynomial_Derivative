package poly;

import java.util.AbstractCollection;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Optional;

public class Poly implements Derivable {
    private TreeSet<Item> expression = new TreeSet<>(Item::compareTo);
    private boolean sub = false;
    
    public Poly(Item... items) {
        for (Item item : items) {
            add(item.clone());
        }
    }
    
    private Poly(AbstractCollection<Item> items) {
        for (Item item : items) {
            add(item.clone());
        }
    }
    
    public Poly setSub(boolean sub) {
        this.sub = sub;
        return this;
    }
    
    public Poly setSub() {
        return setSub(true);
    }
    
    private Optional<Item> firstItem() {
        return expression.stream().findFirst();
    }
    
    @Override
    public boolean isZero() {
        return expression.size() == 0;
    }
    
    @Override
    public boolean isOne() {
        return isItem() && firstItem().get().isZero();
    }
    
    public boolean isFactor() {
        return isItem() && firstItem().get().isFactor();
    }
    
    boolean isItem() {
        return expression.size() == 1;
    }
    
    Item toItem() {
        assert isItem();
        return firstItem().get();
    }
    
    /* Ensures that no item is zero */
    private Poly add(Item item) {
        if (item.isZero()) {
            return this;
        }
        Optional<Item> item1 = expression.stream()
                .filter(item::equivalent)
                .findAny();
        if (item1.isPresent()) {
            Item temp = item1.get();
            expression.remove(temp);
            temp = item.add(temp);
            if (!temp.isZero()) {
                expression.add(temp);
            }
        } else {
            expression.add(item);
        }
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
        if (!item.isZero()) {
            for (Item item1 : expression) {
                poly.add(item.mult(item1));
            }
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
        if (derivable instanceof Item) {
            return mult((Item) derivable);
        }
        if (derivable instanceof Poly) {
            return mult((Poly) derivable);
        }
        throw new ClassCastException();
    }
    
    Poly devide(Derivable derivable) {
        LinkedList<Item> linkedList = new LinkedList<>();
        for (Item item : expression) {
            linkedList.add(item.devide(derivable));
        }
        return new Poly(linkedList);
    }
    
    LinkedList<Derivable> commonFactors() {
        LinkedList<Derivable> result = new LinkedList<>();
        Optional<Item> first = firstItem();
        if (!first.isPresent()) {
            return result;
        }
        for (Derivable derivable : first.get()) {
            if (expression.stream()
                    .allMatch(item -> item.contains(derivable))) {
                result.add(derivable);
            }
        }
        return result;
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
        Poly poly = new Poly(expression);
        return poly.setSub(sub);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(Derivable.isInstance(obj, this.getClass()))) {
            return false;
        }
        return ((Poly) obj).expression.equals(expression);
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
        if (sub) {
            s.insert(0, "(").append(")");
        }
        return s.toString();
    }
}