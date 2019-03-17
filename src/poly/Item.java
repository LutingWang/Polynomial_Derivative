package poly;

import poly.element.Element;
import poly.element.Const;

import java.util.ArrayList;
import java.util.ListIterator;

public class Item implements Comparable<Item>, Derivable {
    private Factor con = new Factor(new Const(1)); // Const
    private Factor var = new Factor(new Const(1)); // Var
    private ArrayList<Factor> tri = new ArrayList<>(); // Tri
    
    public Item(Factor... factors) {
        for (Factor factor : factors) {
            mult(factor.clone());
        }
    }
    
    private boolean isConst() {
        return equals(new Item());
    }
    
    private Const getConst() {
        return con.getConst();
    }
    
    private Const setConst(Const newValue) {
        Const oldValue = getConst();
        con = new Factor(newValue);
        return oldValue;
    }
    
    @Override
    public boolean isZero() {
        return con.isZero();
    }
    
    public boolean equivalent(Item item) {
        if (!var.equals(item.var) || this.tri.size() != item.tri.size()) {
            return false;
        }
        ListIterator<Factor> thisLi = this.tri.listIterator();
        ListIterator<Factor> itemLi = item.tri.listIterator();
        while (thisLi.hasNext()) {
            boolean flag = true;
            Factor factor = thisLi.next();
            while (itemLi.hasNext()) {
                if (factor.equals(itemLi.next())) {
                    flag = false;
                    thisLi.remove();
                    itemLi.remove();
                    break;
                }
            }
            if (flag) {
                return false;
            }
        }
        return true;
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
            return ((Poly) obj).mult(this);
        }
        throw new ClassCastException();
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
        return temp.substring(0, temp.length() - 1);
    }
}