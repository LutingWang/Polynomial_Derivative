package poly;

import poly.element.Const;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

public class Poly implements Derivable {
    private LinkedList<Item> expression = new LinkedList<>();
    
    public LinkedList<Item> getExpression() {
        return expression;
    }
    
    public Poly add(Derivable derivable) {
        if (derivable instanceof Item) {
            expression.add((Item) derivable);
        } else if (derivable instanceof Poly) {
            expression.addAll(((Poly) derivable).getExpression());
        } else {
            throw new ClassCastException();
        }
        return this;
    }

    public Poly differenciate() {
        Poly poly = new Poly();
        for (Item it : expression) {
            poly.add(it.differenciate());
        }
        return poly;
    }

    public Poly merge() {
        LinkedList<Item> linkedList = new LinkedList<>();
        for (Item item : expression) {
            boolean flag = false; // item added with one of linkedList.
            for (Item item1 : linkedList) {
                if (item1.equals(item)) {
                    item1.add(item);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                linkedList.add(item.clone());
            }
        }
        ListIterator<Item> listIterator = linkedList.listIterator();
        while (listIterator.hasNext()) {
            Item item = listIterator.next();
            if (!item.getConst().equals(new Const(0))) {
                item.merge();
            } else {
                listIterator.remove();
            }
        }
        expression = linkedList;
        return this;
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
        ArrayList<Item> al = new ArrayList<>(expression);
        al.sort(Item::compareTo);
        Collections.reverse(al);
        for (Item it : al) {
            s.append(it.toString());
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
            poly.add(item.clone());
        }
        return poly;
    }
}