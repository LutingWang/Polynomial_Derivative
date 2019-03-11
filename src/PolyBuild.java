import poly.*;
import poly.element.*;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Scanner;

import static poly.StatusEnum.FACTOR_EXP;

// TODO: Checkstyle
public class PolyBuild {
    
    private static final String LEGEL = " \t0123456789cinosx+-*^()";
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
         *
         * @return If the next character is white character.
         */
        public boolean isWhite() {
            return nextIn(WHITE);
        }
        
        public void jumpWhite() {
            while (hasNext() && isWhite()) {
                next();
            }
        }
        
        /**
         * If the next character is num character (0-9)
         *
         * @return If the next character is num character.
         */
        public boolean isNum() {
            return nextIn(NUM);
        }
        
        public boolean isVar() {
            return nextIn(Derivable.VAR);
        }
        
        @Override
        public boolean hasNext() {
            return ind < sb.length();
        }
        
        public boolean end() {
            return !hasNext();
        }
        
        @Override
        public Character next() {
            return sb.charAt(ind++);
        }
        
        public void add(String c) {
            sb.insert(ind, c);
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
            System.out.println(pb.parsePoly().differenciate().merge());
        } catch (IllegalArgumentException e) {
            System.out.println(ERROR);
        } catch (Exception e) {
            throw e;
//            System.out.println(ERROR);
        }
    }
    
    public Poly parsePoly() {
        assert status == StatusEnum.START;
        while (status != StatusEnum.END) {
            status = StatusEnum.ITEM_START;
            poly.add(parseItem(si.next() == '-'));
        }
        return poly;
    }
    
    private Item parseItem(boolean neg) {
        Item item = new Item();
        if (neg) {
            item.mult(new Factor(new Const(-1)));
        }
        if (si.end()) {
            status = StatusEnum.END;
            return item;
        }
        boolean firstFactor = true;
        while (status != StatusEnum.START && status != StatusEnum.END) {
            switch (status) {
                case ITEM_START:
                    si.jumpWhite();
                    if (si.isNum() || si.isVar() || si.nextIn("cs+-")) {
                        status = StatusEnum.FACTOR_START;
                        item.mult(parseFactor(firstFactor));
                        firstFactor = false;
                    } else {
                        si.raise();
                    }
                    break;
                case ITEM_END:
                    if (si.end()) {
                        status = StatusEnum.END;
                    } else {
                        status = StatusEnum.START;
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
        while (status != StatusEnum.ITEM_START && status != StatusEnum.ITEM_END) {
            switch (status) {
                case FACTOR_START:
                    status = StatusEnum.ELEMENT_START;
                    element = parseElement(first);
                    break;
                case FACTOR_EXP:
                    si.jumpWhite();
                    if (si.nextIn("+-")) {
                        status = StatusEnum.FACTOR_SIGN;
                    } else if (si.isNum()) {
                        status = StatusEnum.FACTOR_NUM;
                    } else {
                        si.raise();
                    }
                    break;
                case FACTOR_SIGN:
                    neg = si.next() == '-';
                    if (si.isNum()) {
                        status = StatusEnum.FACTOR_NUM;
                    } else {
                        si.raise();
                    }
                    break;
                case FACTOR_NUM:
                    StringBuilder num = new StringBuilder();
                    while (si.isNum()) {
                        num.append(si.next());
                    }
                    exp = exp.multiply(new BigInteger(num.toString()));
                    if (neg) {
                        exp = exp.negate();
                    }
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*")) {
                        status = StatusEnum.FACTOR_END;
                    } else {
                        si.raise();
                    }
                    break;
                case FACTOR_END:
                    if (si.end() || si.nextIn("+-")) {
                        status = StatusEnum.ITEM_END;
                    } else if (si.nextIn("*")) {
                        si.next();
                        status = StatusEnum.ITEM_START;
                    } else {
                        si.raise();
                    }
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
        while (status != FACTOR_EXP && status != StatusEnum.FACTOR_END) {
            switch (status) {
                case ELEMENT_START:
                    if (si.nextIn("+-")) {
                        status = StatusEnum.ELEMENT_SIGN;
                    } else if (si.isNum()) {
                        status = StatusEnum.ELEMENT_NUM;
                    } else if (si.isVar()) {
                        si.next();
                        status = StatusEnum.ELEMENT_VAR;
                    } else if (si.nextIn("cs")) {
                        status = StatusEnum.ELEMENT_TRI;
                    } else {
                        si.raise();
                    }
                    break;
                case ELEMENT_SIGN:
                    neg = si.next() == '-';
                    if (si.isNum()) {
                        status = StatusEnum.ELEMENT_NUM;
                    } else if (!first) {
                        si.raise();
                    } else if (si.isWhite() || si.isVar() || si.nextIn("cs+-")) {
                        si.add("1*");
                        status = StatusEnum.ELEMENT_NUM;
                    } else {
                        si.raise();
                    }
                    break;
                case ELEMENT_NUM:
                    StringBuilder num = new StringBuilder();
                    while (si.isNum()) {
                        num.append(si.next());
                    }
                    element = new Const(new BigInteger(num.toString()));
                    if (neg) {
                        ((Const) element).negate();
                    }
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*")) {
                        status = StatusEnum.ELEMENT_END;
                    } else {
                        si.raise();
                    }
                    break;
                case ELEMENT_VAR:
                    element = new Var();
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*^")) {
                        status = StatusEnum.ELEMENT_END;
                    } else {
                        si.raise();
                    }
                    break;
                case ELEMENT_TRI:
                    String temp = "";
                    for (int i = 0; i < 3; i++) {
                        temp += si.next();
                    }
                    if (si.next() != '(') {
                        throw new IllegalArgumentException();
                    }
                    if (Derivable.VAR.indexOf(si.next()) == -1) {
                        throw new IllegalArgumentException();
                    }
                    if (si.next() != ')') {
                        throw new IllegalArgumentException();
                    }
                    if ("sin".equals(temp)) {
                        element = new Triangular(TypeEnum.SIN);
                    } else if ("cos".equals(temp)) {
                        element = new Triangular(TypeEnum.COS);
                    } else {
                        si.raise();
                    }
                    si.jumpWhite();
                    if (si.end() || si.nextIn("+-*^")) {
                        status = StatusEnum.ELEMENT_END;
                    } else {
                        si.raise();
                    }
                    break;
                case ELEMENT_END:
                    if (si.nextIn("^")) {
                        si.next();
                        status = FACTOR_EXP;
                    } else if (si.end() || si.nextIn("+-*")) {
                        status = StatusEnum.FACTOR_END;
                    } else {
                        si.raise();
                    }
                    break;
                default:
                    si.raise();
            }
        }
        return element;
    }
}

