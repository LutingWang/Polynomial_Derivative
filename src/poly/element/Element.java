package poly.element;

import poly.Derivable;

import java.util.EnumMap;
import java.util.Map;

public abstract class Element implements Derivable {
    private static final Map<TypeEnum, Integer> ENUM_INTEGER_MAP = new EnumMap<>(TypeEnum.class);
    
    static {
        ENUM_INTEGER_MAP.put(TypeEnum.CONST, 0);
        ENUM_INTEGER_MAP.put(TypeEnum.VAR, 1);
        ENUM_INTEGER_MAP.put(TypeEnum.SIN, 2);
        ENUM_INTEGER_MAP.put(TypeEnum.COS, 3);
    }
    
    public int getCode(TypeEnum typeEnum) {
        return ENUM_INTEGER_MAP.get(typeEnum);
    }
    
    public static int typeNum() {
        return ENUM_INTEGER_MAP.size();
    }
    
    public abstract Element clone();
}
