package poly;

import java.util.*;

public class Poly implements Derivable {
    private TreeSet<Item> expression = new TreeSet<>(Item::compareTo);
    private boolean sub = false;
    
    public Poly(Item... items) {
        for (Item item : items) {
            add(item.clone());
        }
    }
    
    public Poly(AbstractCollection<Item> items) {
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
    
    public boolean isItem() {
        return expression.size() == 1;
    }
    
    public Item toItem() {
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
    
    public Poly devide(Derivable derivable) {
        LinkedList<Item> linkedList = new LinkedList<>();
        for (Item item : expression) {
            linkedList.add(item.devide(derivable));
        }
        return new Poly(linkedList);
    }
    
    public LinkedList<Derivable> commonFactors() {
        LinkedList<Derivable> result = new LinkedList<>();
        for (Derivable derivable : firstItem().get()) {
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
    /*
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
            } else {
                if (toPrint.length() == 0) {
                    poly.unSetSub();
                }
                toPrint.insert(0, poly);
            }
            if (sub && !isFactor()) {
                toPrint.insert(0, '(');
                toPrint.append(')');
            }
            if (!equals(new PolyBuild(toPrint.toString()).parsePoly())) {
                return toString();
            } else {
                return toPrint.toString();
            }
        } catch (Throwable t) {
            return toString();
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
    */
}