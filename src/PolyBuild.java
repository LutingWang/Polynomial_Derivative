import java.math.BigInteger;
import java.util.Iterator;
import java.util.Scanner;

// TODO: Checkstyle
public class PolyBuild {
    
    private static final String ERROR = "WRONG FORMAT!";
    private static final String WHITE = " \t";
    private static final String NUM = "1234567890";
    private StatusEnum status = StatusEnum.START;
    
    private final StringIterator si;
    
    private static class StringIterator implements Iterator<Character> {
        private StringBuilder sb;
        private int ind = 0;
        
        public StringIterator(String s) {
            this.sb = new StringBuilder(s);
        }
        
        public boolean nextIn(String set) {
            return hasNext() && set.indexOf(sb.charAt(ind)) != -1;
        }
    
        /**
         * If the next character is white character ("space", "tab")
         * @return If the next character is white character.
         */
        public boolean isWhite() {
            return nextIn(WHITE);
        }
        
        public void jumpWhite() {
            while (isWhite()) { next(); }
        }
    
        /**
         * If the next character is white character ("space", "tab")
         * @return If the next character is white character.
         */
        public boolean isNum() {
            return nextIn(NUM);
        }
        
        @Override
        public boolean hasNext() {
            return ind < sb.length();
        }
        
        @Override
        public Character next() {
            return sb.charAt(ind++);
        }
        
        public void raise() {
            if (hasNext()) {
                throw new IllegalArgumentException(
                        "String format is not parsable!");
            }
        }
    }
    
    private Poly poly = new Poly();
    
    public PolyBuild(String s) {
        si = new StringIterator(s);
    }
    
    private  void parseCoef(StringBuilder coef) {
        while (si.hasNext() && status != StatusEnum.START
                && status != StatusEnum.EXP_START) {
            switch (status) {
                case ITEM_START:
                    si.jumpWhite();
                    if (si.nextIn("+-")) { status = StatusEnum.ITEM_SIGN; }
                    else if (si.isNum()) { status = StatusEnum.ITEM_NUM; }
                    else if (si.nextIn(Poly.VAR)) {
                        coef.append(1);
                        status = StatusEnum.EXP_START;
                    } else { si.raise(); }
                    break;
                case ITEM_SIGN:
                    coef.append(si.next());
                    if (si.isNum()) { status = StatusEnum.ITEM_NUM; }
                    else if (si.nextIn(Poly.VAR)) {
                        coef.append(1);
                        status = StatusEnum.EXP_START;
                    } else if (si.isWhite()) {
                        coef.append(1);
                        status = StatusEnum.ITEM_NUM_OVER;
                    } else { si.raise(); }
                    break;
                case ITEM_NUM:
                    while (si.isNum()) { coef.append(si.next()); }
                    if (si.nextIn("*")) { status = StatusEnum.ITEM_MULT; }
                    else if (si.isWhite()) {
                        status = StatusEnum.ITEM_NUM_WHITE;
                    }
                    else if (si.nextIn("+-")) { status = StatusEnum.START; }
                    else { si.raise(); }
                    break;
                case ITEM_NUM_WHITE:
                    si.jumpWhite();
                    if (si.nextIn("*")) { status = StatusEnum.ITEM_MULT; }
                    else if (si.nextIn("+-")) { status = StatusEnum.START; }
                    else { si.raise(); }
                    break;
                case ITEM_MULT:
                    si.next(); // fallthrough
                case ITEM_NUM_OVER:
                    si.jumpWhite();
                    if (si.nextIn(Poly.VAR)) { status = StatusEnum.EXP_START; }
                    else { si.raise(); }
                    break;
                default:
                    throw new RuntimeException("Unexpected coef enum class");
            }
        }
    }
    
    private void parseItem(boolean neg) {
        StringBuilder coef = new StringBuilder();
        StringBuilder exp = new StringBuilder();
        if (neg) { coef.append('-'); }
        
        parseCoef(coef);
        while (si.hasNext() && status != StatusEnum.START) {
            switch (status) {
                case EXP_START:
                    si.next();
                    si.jumpWhite();
                    if (si.nextIn("^")) { status = StatusEnum.EXP_HAT; }
                    else if (si.nextIn("+-")) {
                        exp.append(1);
                        status = StatusEnum.START;
                    } else {
                        si.raise();
                        exp.append(1);
                    }
                    break;
                case EXP_HAT:
                    si.next();
                    si.jumpWhite();
                    if (si.nextIn("+-")) { status = StatusEnum.EXP_SIGN; }
                    else if (si.isNum()) { status = StatusEnum.EXP_NUM; }
                    else { si.raise(); }
                    break;
                case EXP_SIGN:
                    exp.append(si.next());
                    if (si.isNum()) { status = StatusEnum.EXP_NUM; }
                    else { si.raise(); }
                    break;
                case EXP_NUM:
                    while (si.isNum()) { exp.append(si.next()); }
                    if (si.isWhite()) { status = StatusEnum.EXP_WHITE; }
                    else if (si.nextIn("+-")) { status = StatusEnum.START; }
                    else { si.raise(); }
                    break;
                case EXP_WHITE:
                    si.jumpWhite();
                    if (si.nextIn("+-")) { status = StatusEnum.START; }
                    else { si.raise(); }
                    break;
                default:
                    throw new RuntimeException("Unexpected exp enum class");
            }
        }
        
        if (coef.length() >= 2) {
            char c = coef.charAt(1);
            if (c == '+' || c == '-') {
                coef.deleteCharAt(1);
                if (coef.charAt(0) == c) { coef.deleteCharAt(0); }
                else { coef.setCharAt(0, '-'); }
            }
        }
        if (exp.length() == 0) { exp.append(0); }
        
        poly.add(new BigInteger(coef.toString()),
                new BigInteger(exp.toString()));
    }
    
    public Poly parsePoly() {
        while (si.hasNext()) {
            status = StatusEnum.ITEM_START;
            parseItem(si.next() == '-');
        }
        switch (status) {
            case ITEM_NUM:
            case ITEM_NUM_WHITE:
            case EXP_START:
            case EXP_NUM:
            case EXP_WHITE:
                return poly;
            default:
                throw new IllegalArgumentException("String ended unexpectedly");
        }
    }
    
    public static void main(String[] args) {
        // TODO: if multiple spaces are passed in, is that legal?
        try {
            String s = new Scanner(System.in).nextLine();
            int i = 0;
            for (; " \t".contains(s.substring(i,i + 1)); i++) {}
            s = s.substring(i);
            if (s.isEmpty()) { throw new IllegalArgumentException("Empty"); }
            if (!"x*".contains(s.substring(0,1))) { s = "0" + s; }
            s = "+" + s;
            PolyBuild pb = new PolyBuild(s);
            System.out.println(pb.parsePoly().differenciate());
        } catch (IllegalArgumentException e) {
            System.out.println(ERROR);
        } catch (Exception e) {
            // throw e;
            System.out.println(ERROR);
        }
    }
}
