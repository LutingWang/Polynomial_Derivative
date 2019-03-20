import poly.Derivable;
import poly.Poly;
import poly.Item;
import poly.Factor;
import poly.element.TypeEnum;
import poly.element.Element;
import poly.element.Const;
import poly.element.Var;
import poly.element.Tri;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Checkstyle
public class PolyBuild {
    
    private static final String LEGEL = " \t0123456789cinosx+-*^()";
    private static final String ERROR = "WRONG FORMAT!";
    private static final String WHITE = " \t";
    private static final String NUM = "1234567890";
    private Se status = Se.START;
    
    private final StringIterator si;
    
    private class StringIterator implements Iterator<Character> {
        private StringBuilder sb;
        private int ind = 0;
        
        StringIterator(String s) {
            this.sb = new StringBuilder(s);
        }
        
        boolean nextIn(String set) {
            return hasNext() && set.indexOf(sb.charAt(ind)) != -1;
        }
        
        /**
         * If the next character is white character ("space", "tab")
         *
         * @return If the next character is white character.
         */
        boolean isWhite() {
            return nextIn(WHITE);
        }
        
        void jumpWhite() {
            while (hasNext() && isWhite()) {
                next();
            }
        }
        
        /**
         * If the next character is num character (0-9)
         *
         * @return If the next character is num character.
         */
        boolean isNum() {
            return nextIn(NUM);
        }
        
        boolean isVar() {
            return nextIn(Derivable.VAR);
        }
        
        @Override
        public boolean hasNext() {
            return ind < sb.length();
        }
        
        boolean hasNextLayer() {
            return hasNext() && nextIn("(");
        }
        
        boolean end() {
            return !hasNext();
        }
        
        @Override
        public Character next() {
            return sb.charAt(ind++);
        }
        
        String next(int length) {
            String temp = sb.substring(ind, ind + length);
            ind += length;
            return temp;
        }
        
        String nextLayer() {
            jumpWhite();
            if (next() != '(') {
                throw new IllegalArgumentException();
            }
            final int startInd = ind;
            
            int layer = 0;
            while (hasNext() && layer != -1) {
                char c = next();
                if (c == '(') {
                    layer++;
                } else if (c == ')') {
                    layer--;
                }
            }
            if (layer != -1) {
                raise();
            }
            return sb.substring(startInd, ind - 1);
        }
        
        void add(String c) {
            sb.insert(ind, c);
        }
        
        void raise() {
            throw new IllegalArgumentException(
                    "String format is not parsable!");
        }
    }
    
    private Poly poly = new Poly();
    
    public PolyBuild(String s) {
        String string = s.trim();
        if (string.isEmpty()) {
            throw new IllegalArgumentException("Empty");
        }
        if ("+-".indexOf(string.charAt(0)) == -1) {
            string = "+" + string;
        }
        si = new StringIterator(string);
    }
    
    public static void main(String[] args) {
        try {
            String s = new Scanner(System.in).nextLine();
            for (char c : s.toCharArray()) {
                if (LEGEL.indexOf(c) == -1) {
                    throw new IllegalArgumentException("Illegal character.");
                }
            }
            Poly poly = new PolyBuild(s).parsePoly().differentiate();
            System.out.println(poly);
        } catch (Exception e) { // IllegalArgumentException is expected
            System.out.println(ERROR);
        }
    }
    
    private Poly parsePoly() {
        while (status != Se.END) {
            status = Se.ITEM_START;
            poly = poly.add(parseItem(si.next() == '-'));
        }
        return poly;
    }
    
    private Poly parseItem(boolean neg) {
        Item item = new Item();
        if (neg) {
            item = (Item) item.mult(new Factor(new Const(-1)));
        }
        Poly poly = new Poly().add(new Item(new Factor(new Const(1))));
        boolean firstFactor = true;
        while (status != Se.START && status != Se.END) {
            switch (status) {
                case ITEM_START:
                    si.jumpWhite();
                    if (si.isNum() || si.isVar() || si.nextIn("cs+-")) {
                        status = Se.FACTOR_START;
                        item = (Item) item.mult(parseFactor(firstFactor));
                    } else if (si.hasNextLayer()) {
                        status = Se.ITEM_LAYER;
                    } else {
                        si.raise();
                    }
                    firstFactor = false;
                    break;
                case ITEM_LAYER:
                    poly = poly.mult(new PolyBuild(si.nextLayer()).parsePoly());
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-")) {
                        status = Se.ITEM_END;
                    } else if (si.nextIn("*")) {
                        si.next();
                        status = Se.ITEM_START;
                    } else { si.raise(); }
                    break;
                case ITEM_END:
                    if (si.end()) {
                        status = Se.END;
                    } else {
                        status = Se.START;
                    }
                    break;
                default:
                    si.raise();
            }
        }
        return poly.mult(item);
    }
    
    private Factor parseFactor(boolean first) {
        si.jumpWhite();
        Element element = null;
        BigInteger exp = BigInteger.ONE;
        boolean neg = false;
        while (status != Se.ITEM_START && status != Se.ITEM_END) {
            switch (status) {
                case FACTOR_START:
                    status = Se.ESTART;
                    element = parseElement(first);
                    break;
                case FACTOR_EXP:
                    si.jumpWhite();
                    if (si.nextIn("+-")) {
                        status = Se.FACTOR_SIGN;
                    } else if (si.isNum()) {
                        status = Se.FACTOR_NUM;
                    } else { si.raise(); }
                    break;
                case FACTOR_SIGN:
                    neg = si.next() == '-';
                    if (si.isNum()) { status = Se.FACTOR_NUM; }
                    else { si.raise(); }
                    break;
                case FACTOR_NUM:
                    StringBuilder num = new StringBuilder();
                    while (si.isNum()) { num.append(si.next()); }
                    exp = exp.multiply(new BigInteger(num.toString()));
                    if (neg) { exp = exp.negate(); }
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*")) {
                        status = Se.FACTOR_END;
                    } else { si.raise(); }
                    break;
                case FACTOR_END:
                    if (si.end() || si.nextIn("+-")) {
                        status = Se.ITEM_END;
                    } else if (si.nextIn("*")) {
                        si.next();
                        status = Se.ITEM_START;
                    } else { si.raise(); }
                    break;
                default:
                    si.raise();
            }
        }
        return new Factor(element, exp);
    }
    
    private Element parseElement(boolean first) {
        boolean neg = false;
        Element element = null;
        while (status != Se.FACTOR_EXP && status != Se.FACTOR_END) {
            switch (status) {
                case ESTART:
                    if (si.nextIn("+-")) { status = Se.ES; }
                    else if (si.isNum()) { status = Se.EN; }
                    else if (si.isVar()) {
                        si.next();
                        status = Se.EV;
                    } else if (si.nextIn("cs")) { status = Se.ET; }
                    else { si.raise(); }
                    break;
                case ES:
                    neg = si.next() == '-';
                    if (si.isNum()) { status = Se.EN; }
                    else if (!first) { si.raise(); }
                    else if (si.isWhite() || si.isVar() || si.nextIn("cs+-(")) {
                        si.add("1*");
                        status = Se.EN;
                    } else { si.raise(); }
                    break;
                case EN:
                    StringBuilder num = new StringBuilder();
                    while (si.isNum()) { num.append(si.next()); }
                    element = new Const(new BigInteger(num.toString()));
                    if (neg) { element = ((Const) element).negate(); }
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*")) { status = Se.EE; }
                    else { si.raise(); }
                    break;
                case EV:
                    element = new Var();
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*^")) { status = Se.EE; }
                    else { si.raise(); }
                    break;
                case ET:
                    element = parseTri();
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*^")) { status = Se.EE; }
                    else { si.raise(); }
                    break;
                case EE:
                    if (si.nextIn("^")) {
                        si.next();
                        status = Se.FACTOR_EXP;
                    } else if (si.end() || si.nextIn("+-*")) {
                        status = Se.FACTOR_END;
                    } else { si.raise(); }
                    break;
                default:
                    si.raise();
            }
        }
        return element;
    }
    
    private Element parseTri() {
        final String triFuncName = si.next(3);
        String temp = si.nextLayer();
        if (!isFactor(temp)) {
            throw new IllegalArgumentException();
        }
        Poly poly = new PolyBuild(temp).parsePoly();
        if ("sin".equals(triFuncName)) {
            if (poly.isZero()) {
                return new Const(0);
            } else {
                return new Tri(TypeEnum.SIN, poly);
            }
        } else if ("cos".equals(triFuncName)) {
            if (poly.isZero()) {
                return new Const(1);
            } else {
                return new Tri(TypeEnum.COS, poly);
            }
        }
        si.raise();
        return null;
    }
    
    private boolean isFactor(String target) {
        String regex = "^([+-]?\\d+|(x|(sin|cos)\\(.+\\))(\\^[+-]?\\d+)?"
                + "|\\(.+\\))$";
        Matcher matcher = Pattern.compile(regex)
                .matcher(target.replaceAll("[ \\t]", ""));
        return matcher.matches();
    }
}

