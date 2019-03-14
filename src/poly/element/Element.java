package poly.element;

import poly.Derivable;

import java.util.EnumMap;
import java.util.Map;

public abstract class Element implements Derivable {
    private static final Map<TypeEnum, Integer>
            ENUM_INTEGER_MAP = new EnumMap<>(TypeEnum.class);
    private final TypeEnum typeEnum;
    
    static {
        ENUM_INTEGER_MAP.put(TypeEnum.CONST, 2);
        ENUM_INTEGER_MAP.put(TypeEnum.VAR, 3);
        ENUM_INTEGER_MAP.put(TypeEnum.SIN, 5);
        ENUM_INTEGER_MAP.put(TypeEnum.COS, 7);
    }
    
    Element(TypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }
    
    TypeEnum type() {
        return typeEnum;
    }
    
    @Override
    public int hashCode() {
        return ENUM_INTEGER_MAP.get(typeEnum);
    }
    
    @Override
    public abstract Element clone();
}
