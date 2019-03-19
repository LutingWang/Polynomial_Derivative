package poly;

import poly.element.Element;
import poly.element.Const;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;

public class Item implements Comparable<Item>, Iterable<Factor>, Derivable {
    private Factor con = new Factor(new Const(1)); // Const
    private Factor var = new Factor(new Const(1)); // Var
    private ArrayList<Factor> tri = new ArrayList<>(); // Tri
    private ArrayList<Poly> polies = new ArrayList<>(); // poly
    
    public Item(Factor... factors) {
        for (Factor factor : factors) {
            mult(factor.clone());
        }
    }
    
    @Override
    public boolean isZero() {
        boolean flag = false;
        for (Poly poly : polies) {
            flag = flag || poly.isZero();
        }
        return con.isZero() || flag;
    }
    
    public boolean isOne() {
        return hashCode() == 0;
    }
    
    public boolean isFactor() {
        return hashCode() <= 1;
    }
    
    private boolean isConst() {
        return var.equals(new Factor(new Const(1)))
                && tri.size() == 0
                && polies.size() == 0;
    }
    
    private Const getConst() {
        return con.getConst();
    }
    
    private Const setConst(Const newValue) {
        Const oldValue = getConst();
        con = new Factor(newValue);
        return oldValue;
    }
    
    public boolean equivalent(Item item) {
        if (!var.equals(item.var)) {
            return false;
        }
        HashSet<Factor> factorHashSet = new HashSet<>(item.tri);
        HashSet<Poly> polyHashSet = new HashSet<>(item.polies);
        return factorHashSet.equals(new HashSet<>(tri))
                && polyHashSet.equals(new HashSet<>(polies));
    }
    
    public boolean containsFactor(Factor factor) {
        return tri.indexOf(factor) != -1
                || con.equals(factor) || var.equals(factor);
    }
    
    Item add(Item item) {
        assert equals(item);
        Item item1 = clone();
        item1.setConst(item1.getConst().add(item.getConst()));
        return item1;
    }
    
    private Item mult(Element element) {
        return mult(new Factor(element));
    }
    
    private Item mult(Factor factor) {
        switch (factor.getType()) {
            case CONST:
                con = con.mult(factor);
                break;
            case VAR:
                var = var.mult(factor);
                break;
            case SIN:
            case COS:
                ListIterator<Factor> li = tri.listIterator();
                boolean flag = true;
                while (li.hasNext()) {
                    Factor tri = li.next();
                    if (factor.equivalent(tri)) {
                        li.set(factor.mult(tri));
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    tri.add(factor);
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
    
    private Item mult(Poly poly) {
        if (poly.isItem()) {
            return mult(poly.expression.get(0));
        }
        polies.add(poly.setSub());
        return this;
    }
    
    public Derivable mult(Derivable obj) {
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
    
    public void devide(Factor factor) {
        assert containsFactor(factor);
        if (con.equals(factor)) {
            con = new Factor(new Const(1));
        } else if (var.equals(factor)) {
            var = new Factor(new Const(1));
        } else {
            tri.remove(factor);
        }
    }
    
    @Override
    public Iterator<Factor> iterator() {
        ArrayList<Factor> arrayList = new ArrayList<>(tri);
        arrayList.add(0,var);
        arrayList.add(0,con);
        return arrayList.iterator();
    }
    
    @Override
    public Poly differentiate() {
        Poly poly = new Poly();
        // differentiate power fun
        Item item = clone();
        Factor factor = item.var;
        item.var = new Factor(new Const(1));
        poly = poly.add(item.mult(factor.differentiate()));
        // differentiate trigonometry fun
        for (int i = 0; i < tri.size(); i++) {
            item = clone();
            factor = item.tri.remove(i);
            poly = poly.add(item.mult(factor.differentiate()));
        }
        return poly;
    }
    
    @Override
    public int compareTo(Item item) {
        return this.getConst().compareTo(item.getConst());
    }
    
    @Override
    public Item clone() {
        Item item = new Item(tri.toArray(new Factor[0]));
        item.con = con.clone();
        item.var = var.clone();
        item.polies = new ArrayList<>();
        for (Poly poly : polies) {
            item.polies.add(poly.clone());
        }
        return item;
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
            temp.append(poly.print()).append("*");
        }
        return temp.substring(0, temp.length() - 1);
    }
}