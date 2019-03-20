package poly;

import poly.element.Element;
import poly.element.Const;

import java.util.*;

public class Item extends AbstractCollection<Derivable>
        implements Comparable<Item>, Derivable {
    private Factor con = new Factor(1); // Const
    private Factor var = new Factor(1); // Var
    private HashSet<Factor> tri = new HashSet<>(); // Tri
    private HashSet<Poly> polies = new HashSet<>(); // poly
    
    public Item() {}
    
    public Item(Derivable... derivables) {
        Item item = new Item();
        for (Derivable derivable : derivables) {
            item = item.mult(derivable);
        }
        this.con = item.con;
        this.var = item.var;
        this.tri = item.tri;
        this.polies = item.polies;
    }
    
    @Override
    public boolean isZero() {
        return con.isZero();
    }
    
    private Item setZero() {
        return setConst(new Const(0));
    }
    
    @Override
    public boolean isOne() {
        return size() == 0;
    }
    
    private boolean isConst() {
        return var.isOne() && tri.size() == 0 && polies.size() == 0;
    }
    
    private Const getConst() {
        return con.getConst();
    }
    
    private Item setConst(Const newValue) {
        con = new Factor(newValue);
        return this;
    }
    
    private Factor getVar() {
        return var;
    }
    
    private Item setVar(Factor newValue) {
        var = newValue;
        return this;
    }
    
    public boolean isFactor() {
        return size() <= 1;
    }
    
    /**
     * The two items can be added.
     * @param item The other item to be added
     * @return If the two items can be added
     */
    public boolean equivalent(Item item) {
        return item.var.equals(var)
                && item.tri.equals(tri)
                && item.polies.equals(polies);
    }
    
    public boolean contains(Derivable derivable) {
        return polies.contains(derivable)
                || tri.contains(derivable)
                || con.equals(derivable)
                || var.equals(derivable);
    }
    
    Item add(Item item) {
        assert equivalent(item);
        return clone().setConst(getConst().add(item.getConst()));
    }
    
    private Item mult(Element element) {
        return mult(new Factor(element));
    }
    
    /* Ensures that no factor is zero */
    private Item mult(Factor factor) {
        if (factor.isZero()) {
            return setZero();
        }
        switch (factor.getType()) {
            case CONST:
                con = con.mult(factor);
                break;
            case VAR:
                var = var.mult(factor);
                break;
            case SIN:
            case COS:
                Optional<Factor> tri = this.tri.stream()
                        .filter(factor::equivalent)
                        .findAny();
                if (tri.isPresent()) {
                    this.tri.remove(tri.get());
                    Factor temp = tri.get().mult(factor);
                    if (!temp.isOne()) {
                        this.tri.add(temp);
                    }
                } else {
                    this.tri.add(factor);
                }
                break;
            default:
                break;
        }
        return this;
    }
    
    private Item mult(Item item) {
        con = con.mult(item.con);
        var = var.mult(item.var);
        for (Factor factor : item.tri) {
            mult(factor);
        }
        for (Poly poly : item.polies) {
            mult(poly);
        }
        return this;
    }
    
    /* Ensures that polies are all simplest */
    private Item mult(Poly poly) {
        if (poly.isItem()) {
            return mult(poly.toItem());
        }
        LinkedList<Derivable> linkedList = poly.commonFactors();
        Poly poly1 = poly;
        for (Derivable derivable : linkedList) {
            if (derivable instanceof Item) {
                mult((Item) derivable);
            } else if (derivable instanceof Poly) {
                mult((Poly) derivable);
            } else {
                throw new RuntimeException();
            }
            poly1 = poly1.devide(derivable);
        }
        polies.add(poly1.setSub());
        return this;
    }
    
    public Item mult(Derivable obj) {
        Item item = clone();
        if (obj instanceof Element) {
            return item.mult((Element) obj);
        }
        if (obj instanceof Factor) {
            return item.mult((Factor) obj);
        }
        if (obj instanceof Item) {
            return item.mult((Item) obj);
        }
        if (obj instanceof Poly) {
            return item.mult((Poly) obj);
        }
        throw new ClassCastException();
    }
    
    public Item devide(Derivable derivable) {
        Item item = clone();
        if (con.equals(derivable)) {
            item.con = new Factor(1);
        } else if (var.equals(derivable)) {
            item.var = new Factor(1);
        } else if (tri.contains(derivable)) {
            item.tri.remove(derivable);
        } else if (polies.contains(derivable)) {
            item.polies.remove(derivable);
        } else {
            throw new RuntimeException("Item dividing not exist factor.");
        }
        return item;
    }
    
    @Override
    public Poly differentiate() {
        Poly poly = new Poly();
        for (Derivable derivable : this) {
            poly = poly.add(clone().devide(derivable).mult(derivable.differentiate()));
        }
        return poly;
    }
    
    /* positive if this is greater than the other*/
    @Override
    public int compareTo(Item item) {
        int result =  -this.getConst().compareTo(item.getConst());
        if (result == 0) {
            return -1;
        }
        return result;
    }
    
    @Override
    public Iterator<Derivable> iterator() {
        ArrayList<Derivable> arrayList = new ArrayList<>(tri);
        arrayList.addAll(polies);
        if (!var.isOne()) {
            arrayList.add(0, var);
        }
        if (!con.isOne()) {
            arrayList.add(0, con);
        }
        return arrayList.iterator();
    }
    
    @Override
    public int size() {
        int fcount = 0;
        if (!con.isOne()) {
            fcount++;
        }
        if (!var.isOne()) {
            fcount++;
        }
        fcount += tri.size();
        fcount += polies.size();
        return fcount;
    }
    
    @Override
    public Item clone() {
        return new Item(toArray(new Derivable[0]));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(Derivable.isInstance(obj, this.getClass()))) {
            return false;
        }
        Item item = (Item) obj;
        return con.equals(item.con) && equivalent(item);
    }
    
    @Override
    public int hashCode() {
        return size();
    }
    
    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        if (isConst()) {
            temp.append(con.toString());
            if (con.absIsOne()) {
                temp.append(1);
            }
            return temp.toString();
        }
        temp.append(con);
        if (!con.absIsOne()) {
            temp.append("*");
        }
        if (!var.isOne()) {
            temp.append(var).append("*");
        }
        for (Factor factor : tri) {
            temp.append(factor).append("*");
        }
        for (Poly poly : polies) {
            if (poly.isOne()) {
                continue;
            }
            temp.append(poly.setSub(poly.isItem())).append("*");
        }
        return temp.substring(0, temp.length() - 1);
    }
}