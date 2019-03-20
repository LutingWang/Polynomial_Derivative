package poly.element;

import poly.Derivable;
import poly.Factor;

import java.util.EnumMap;
import java.util.Map;

/**
 * The most basic elements that would appear in a legal expression. Instance of
 * this class is immutable.
 */
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
    
    @Override
    public boolean isZero() {
        return false;
    }
    
    @Override
    public boolean isOne() {
        return false;
    }
    
    @Override
    public Derivable mult(Derivable derivable) {
        return new Factor(this).mult(derivable);
    }
    
    @Override
    public abstract Element clone();
    
    @Override
    public boolean equals(Object obj) {
        return Derivable.isInstance(obj, this.getClass())
                && this.type == ((Element) obj).type;
    }
    
    @Override
    public int hashCode() {
        return ENUM_INTEGER_MAP.get(type);
    }
}
