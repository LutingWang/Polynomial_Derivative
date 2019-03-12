package poly;

import poly.element.Const;
import poly.element.Element;

import java.util.Arrays;
import java.util.LinkedList;

public class Item implements Comparable<Item>, Derivable {
    private LinkedList<Factor> linkedList = new LinkedList<>();
    
    {
        linkedList.add(new Factor(new Const(1)));
    }
    
    public Item() {}
    
    public Item(boolean flag) {
        linkedList.remove(0);
    }
    
    public Item(LinkedList<Factor> linkedList) {
        this.linkedList.addAll(linkedList);
    }
    
    public Item(Factor... factors) {
        this(new LinkedList<>(Arrays.asList(factors)));
    }
    
    public LinkedList<Factor> getLinkedList() {
        return linkedList;
    }
    
    public Const getConst() {
        return (Const) linkedList.stream()
                .filter(Factor::isConst)
                .findAny()
                .get()
                .getElement();
    }
    
    @Override
    public Poly differenciate() {
        Poly poly = new Poly();
        Factor[] factors = clone().getLinkedList().toArray(new Factor[0]);
        for (int i = 0; i < linkedList.size(); i++) {
            Item item = new Item();
            for (int j = 0; j < linkedList.size(); j++) {
                if (j == i) {
                    item.mult(factors[j].differenciate());
                } else {
                    item.mult(factors[j].clone());
                }
            }
            poly.add(item);
        }
        return poly;
    }
    
    public Item add(Item item) {
        assert merge().equals(item.merge());
        Const c = getConst();
        c = c.add(item.getConst());
        return this;
    }
    
    public Item mult(Derivable d) {
        if (d instanceof Element) {
            linkedList.add(new Factor((Element) d));
        } else if (d instanceof Factor) {
            linkedList.add((Factor) d);
        } else if (d instanceof Item) {
            linkedList.addAll(((Item) d).getLinkedList());
        } else {
            throw new RuntimeException(
                    "Item multiplied with incompatible class.");
        }
        return this;
    }
    
    public boolean isConst() {
        return linkedList.size() == 1;
    }
    
    @Override
    public int compareTo(Item item) {
        return getConst().compareTo(item.getConst());
    }
    
    public Item merge() {
        LinkedList<Factor> linkedList = new LinkedList<>();
        linkedList.add(new Factor(new Const(1)));
        for (Factor factor : this.linkedList) {
            factor.merge();
            boolean flag = false; // factor multiplied with one of linkedList
            for (Factor factor1 : linkedList) {
                if (factor1.hashCode() == factor.hashCode()) {
                    factor1.mult(factor);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                linkedList.add(factor);
            }
        }
        linkedList.sort(Factor::compareTo);
        this.linkedList = linkedList;
        return this;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Derivable)) {
            throw new ClassCastException();
        }
        if (obj instanceof Item) {
            LinkedList<Factor> linkedList = merge().getLinkedList();
            LinkedList<Factor> linkedList1 =
                    ((Item) obj).merge().getLinkedList();
            if (linkedList.size() != linkedList1.size()) {
                return false;
            }
            for (int i = 1; i < linkedList.size(); i++) {
                // do not include constant
                if (!linkedList.get(i).equals(linkedList1.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        for (Factor factor : linkedList) {
            temp.append(factor).append("*");
            if (factor.isConst() && temp.length() == 2) {
                temp.deleteCharAt(1);
            }
        }
        if (isConst() && temp.length() == 1) {
            temp.append(1);
            return temp.toString();
        } else {
            return temp.substring(0, temp.length() - 1);
        }
    }
    
    @Override
    public Item clone() {
        Item item = new Item(true);
        for (Factor factor : linkedList) {
            item.mult(factor.clone());
        }
        return item;
    }
}