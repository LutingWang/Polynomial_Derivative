import poly.Derivable;
import poly.Se;
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

// TODO: Checkstyle
public class PolyBuild {
    
    private static final String LEGEL = " \t0123456789cinosx+-*^()";
    private static final String ERROR = "WRONG FORMAT!";
    private static final String WHITE = " \t";
    private static final String NUM = "1234567890";
    private Se status = Se.START;
    private int layer = 0;
    
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
        
        boolean end() {
            return !hasNext() || (layer != 0 && nextIn(")"));
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
            final int startInd = ind;
            
            int layer = 0;
            while (hasNext()) {
                if (nextIn("(")) {
                    layer++;
                } else if (nextIn(")")) {
                    layer--;
                }
                if (layer == -1) {
                    return sb.substring(startInd, ind);
                } else {
                    next();
                }
            }
            raise();
            return null;
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
    
    // TODO: check if empty () is allowed
    PolyBuild(String s) {
        s = s.trim();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Empty");
        }
        if ("+-".indexOf(s.charAt(0)) == -1) {
            s = "+" + s;
        }
        si = new StringIterator(s);
    }
    
    public static void main(String[] args) {
        try {
            String s = new Scanner(System.in).nextLine();
            for (char c : s.toCharArray()) {
                if (LEGEL.indexOf(c) == -1) {
                    throw new IllegalArgumentException("Illegal character.");
                }
            }
            PolyBuild pb = new PolyBuild(s);
            System.out.println(pb.parsePoly()
                    .differenciate()
                    .sort()
            );
        } catch (IllegalArgumentException e) {
            System.out.println(ERROR);
        } catch (Exception e) {
            throw e;
            //System.out.println(ERROR);
        }
    }
    
    private Poly parsePoly() {
        while (status != Se.END) {
            status = Se.ITEM_START;
            poly = poly.add(parseItem(si.next() == '-'));
        }
        return poly;
    }
    
    private Item parseItem(boolean neg) {
        Item item = new Item();
        if (neg) {
            item.mult(new Factor(new Const(-1)));
        }
        boolean firstFactor = true;
        while (status != Se.START && status != Se.END) {
            switch (status) {
                case ITEM_START:
                    si.jumpWhite();
                    if (si.isNum() || si.isVar() || si.nextIn("cs+-")) {
                        status = Se.FACTOR_START;
                        item = (Item) item.mult(parseFactor(firstFactor));
                        firstFactor = false;
                    } else {
                        si.raise();
                    }
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
        return item;
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
                    else if (si.isWhite() || si.isVar() || si.nextIn("cs+-")) {
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
    
    private Tri parseTri() {
        final String triFuncName = si.next(3);
        si.jumpWhite();
        if (si.next() != '(') { throw new TriParseException(); }
        si.jumpWhite();
        Poly poly = new PolyBuild(si.nextLayer()).parsePoly();
        if (si.next() != ')') { throw new TriParseException(); }
        si.jumpWhite();
        if ("sin".equals(triFuncName)) {
            return new Tri(TypeEnum.SIN, poly);
        } else if ("cos".equals(triFuncName)) {
            return new Tri(TypeEnum.COS, poly);
        } else { si.raise(); }
        return null;
    }
}

