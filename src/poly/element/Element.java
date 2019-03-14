package poly.element;

import poly.Derivable;
import poly.Factor;

import java.util.EnumMap;
import java.util.Map;

public abstract class Element implements Derivable {
    private static final Map<TypeEnum, Integer>
            ENUM_INTEGER_MAP = new EnumMap<>(TypeEnum.class);
    private final TypeEnum type;
    
    static {
        ENUM_INTEGER_MAP.put(TypeEnum.CONST, 2);
        ENUM_INTEGER_MAP.put(TypeEnum.VAR, 3);
        ENUM_INTEGER_MAP.put(TypeEnum.SIN, 5);
        ENUM_INTEGER_MAP.put(TypeEnum.COS, 7);
    }
    
    Element(TypeEnum type) {
        this.type = type;
    }
    
    public TypeEnum getType() {
        return type;
    }
    
    public boolean isSin() {
        assert this instanceof Tri;
        return type == TypeEnum.SIN;
    }
    
    public boolean isCos() {
        assert this instanceof Tri;
        return type == TypeEnum.COS;
    }
    
    Factor toFactor() {
        return new Factor(this);
    }
    
    @Override
    public abstract Element clone();
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Derivable)) {
            throw new ClassCastException();
        }
        return obj instanceof Element
                && this.type == ((Element) obj).type;
    }
    
    @Override
    public int hashCode() {
        return ENUM_INTEGER_MAP.get(type);
    }
}
