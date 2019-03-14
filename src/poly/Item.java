package poly;

import poly.element.Const;
import poly.element.Element;
import poly.element.TypeEnum;

import java.util.EnumMap;

public class Item implements Comparable<Item>, Derivable {
    private final EnumMap<TypeEnum, Factor> factorEnumMap
            = new EnumMap<>(TypeEnum.class);
    
    {
        for (TypeEnum type : TypeEnum.values()) {
            put(type, new Factor(new Const(1)));
        }
    }
    
    private Factor put(TypeEnum type, Factor factor) {
        Factor oldValue = factorEnumMap.get(type);
        factorEnumMap.put(type, factor);
        return oldValue;
    }
    
    private Factor get(TypeEnum type) {
        return factorEnumMap.get(type);
    }
    
    public Item(Factor... factors) {
        for (Factor factor : factors) {
            mult(factor);
        }
    }
    
    boolean isConst() {
        return equals(new Item());
    }
    
    Const getConst() {
        return get(TypeEnum.CONST).getConst();
    }
    
    boolean isZero() {
        return get(TypeEnum.CONST).isZero();
    }
    
    Item add(Item item) {
        assert equals(item);
        TypeEnum type = TypeEnum.CONST;
        put(type, new Factor(this.getConst().add(item.getConst())));
        return this;
    }
    
    private Item mult(Element element) {
        return mult(new Factor(element));
    }
    
    private Item mult(Factor factor) {
        TypeEnum type = factor.getType();
        put(type, get(type).mult(factor));
        return this;
    }
    
    private Item mult(Item item) {
        for (Factor factor : item.factorEnumMap.values()) {
            mult(factor);
        }
        return this;
    }
    
    public Item mult(Object obj) {
        if (obj instanceof Element) {
            return mult((Element) obj);
        }
        if (obj instanceof Factor) {
            return mult((Factor) obj);
        }
        if (obj instanceof Item) {
            return mult((Item) obj);
        }
        throw new ClassCastException();
    }
    
    @Override
    public Poly differenciate() {
        Poly poly = new Poly();
        for (TypeEnum type : TypeEnum.values()) {
            Item item = clone();
            Factor factor = item.put(type, new Factor(new Const(1)));
            poly.add(item.mult(factor.differenciate()));
        }
        return poly;
    }
    
    @Override
    public int compareTo(Item item) {
        return this.getConst().compareTo(item.getConst());
    }
    
    @Override
    public Item clone() {
        Item item = new Item();
        for (Factor factor : factorEnumMap.values()) {
            item.mult(factor);
        }
        return item;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Derivable)) {
            throw new ClassCastException();
        }
        if (!(obj instanceof Item)) {
            return false;
        }
        Item item = (Item) obj;
        for (TypeEnum type : TypeEnum.values()) {
            if (type == TypeEnum.CONST) {
                continue;
            }
            if (!this.get(type).equals(item.get(type))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        if (isConst()) {
            temp.append(get(TypeEnum.CONST).toString());
            if (getConst().abs().isOne()) {
                temp.append(1);
            }
            return temp.toString();
        }
        for (TypeEnum type : TypeEnum.values()) {
            Factor factor = get(type);
            if (factor.isOne() && type != TypeEnum.CONST) {
                continue;
            }
            temp.append(factor);
            if (!factor.isOne()) {
                temp.append("*");
            }
        }
        return temp.substring(0, temp.length() - 1);
    }
}