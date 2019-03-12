import poly.Derivable;
import poly.SE;
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
    private SE status = SE.START;
    
    private final StringIterator si;
    
    private static class StringIterator implements Iterator<Character> {
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
            return !hasNext();
        }
        
        @Override
        public Character next() {
            return sb.charAt(ind++);
        }
        
        public String next(int length) {
            String temp = sb.substring(ind, ind + length);
            ind += length;
            return temp;
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
            s = s.trim();
            if (s.isEmpty()) {
                throw new IllegalArgumentException("Empty");
            }
            if ("+-".indexOf(s.charAt(0)) == -1) {
                s = "+" + s;
            }
            PolyBuild pb = new PolyBuild(s);
            System.out.println(pb.parsePoly()
                    .differenciate()
                    .merge()
            );
        } catch (IllegalArgumentException e) {
            System.out.println(ERROR);
        } catch (Exception e) {
            //throw e;
            System.out.println(ERROR);
        }
    }
    
    private Poly parsePoly() {
        while (status != SE.END) {
            status = SE.ITEM_START;
            poly.add(parseItem(si.next() == '-'));
        }
        return poly;
    }
    
    private Item parseItem(boolean neg) {
        Item item = new Item();
        if (neg) {
            item.mult(new Factor(new Const(-1)));
        }
        boolean firstFactor = true;
        while (status != SE.START && status != SE.END) {
            switch (status) {
                case ITEM_START:
                    si.jumpWhite();
                    if (si.isNum() || si.isVar() || si.nextIn("cs+-")) {
                        status = SE.FACTOR_START;
                        item.mult(parseFactor(firstFactor));
                        firstFactor = false;
                    } else {
                        si.raise();
                    }
                    break;
                case ITEM_END:
                    if (si.end()) {
                        status = SE.END;
                    } else {
                        status = SE.START;
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
        while (status != SE.ITEM_START && status != SE.ITEM_END) {
            switch (status) {
                case FACTOR_START:
                    status = SE.ESTART;
                    element = parseElement(first);
                    break;
                case FACTOR_EXP:
                    si.jumpWhite();
                    if (si.nextIn("+-")) {
                        status = SE.FACTOR_SIGN;
                    } else if (si.isNum()) {
                        status = SE.FACTOR_NUM;
                    } else { si.raise(); }
                    break;
                case FACTOR_SIGN:
                    neg = si.next() == '-';
                    if (si.isNum()) { status = SE.FACTOR_NUM; }
                    else { si.raise(); }
                    break;
                case FACTOR_NUM:
                    StringBuilder num = new StringBuilder();
                    while (si.isNum()) { num.append(si.next()); }
                    exp = exp.multiply(new BigInteger(num.toString()));
                    if (neg) { exp = exp.negate(); }
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*")) {
                        status = SE.FACTOR_END;
                    } else { si.raise(); }
                    break;
                case FACTOR_END:
                    if (si.end() || si.nextIn("+-")) {
                        status = SE.ITEM_END;
                    } else if (si.nextIn("*")) {
                        si.next();
                        status = SE.ITEM_START;
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
        while (status != SE.FACTOR_EXP && status != SE.FACTOR_END) {
            switch (status) {
                case ESTART:
                    if (si.nextIn("+-")) { status = SE.ES; }
                    else if (si.isNum()) { status = SE.EN; }
                    else if (si.isVar()) {
                        si.next();
                        status = SE.EV;
                    } else if (si.nextIn("cs")) { status = SE.ET; }
                    else { si.raise(); }
                    break;
                case ES:
                    neg = si.next() == '-';
                    if (si.isNum()) { status = SE.EN; }
                    else if (!first) { si.raise(); }
                    else if (si.isWhite() || si.isVar() || si.nextIn("cs+-")) {
                        si.add("1*");
                        status = SE.EN;
                    } else { si.raise(); }
                    break;
                case EN:
                    StringBuilder num = new StringBuilder();
                    while (si.isNum()) { num.append(si.next()); }
                    element = new Const(new BigInteger(num.toString()));
                    if (neg) { ((Const) element).negate(); }
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*")) { status = SE.EE; }
                    else { si.raise(); }
                    break;
                case EV:
                    element = new Var();
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*^")) { status = SE.EE; }
                    else { si.raise(); }
                    break;
                case ET:
                    element = parseTri();
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*^")) { status = SE.EE; }
                    else { si.raise(); }
                    break;
                case EE:
                    if (si.nextIn("^")) {
                        si.next();
                        status = SE.FACTOR_EXP;
                    } else if (si.end() || si.nextIn("+-*")) {
                        status = SE.FACTOR_END;
                    } else { si.raise(); }
                    break;
                default:
                    si.raise();
            }
        }
        return element;
    }
    
    public Tri parseTri() {
        final String temp = si.next(3);
        si.jumpWhite();
        if (si.next() != '(') { throw new TriParseException(); }
        si.jumpWhite();
        if (Derivable.VAR.indexOf(si.next()) == -1) {
            throw new IllegalArgumentException();
        }
        si.jumpWhite();
        if (si.next() != ')') { throw new TriParseException(); }
        if ("sin".equals(temp)) { return new Tri(TypeEnum.SIN); }
        else if ("cos".equals(temp)) { return new Tri(TypeEnum.COS); }
        else { si.raise(); }
        return null;
    }
}

