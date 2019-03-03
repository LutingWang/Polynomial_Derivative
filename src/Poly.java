import java.math.BigInteger;
import java.util.ArrayList;
import java.util.TreeSet;

public class Poly {
    public static final String VAR = "x";
    private TreeSet<Item> expression = new TreeSet<>();
    
    private class Item implements Comparable<Item> {
        private BigInteger coef;
        private BigInteger exp;
        
        Item(BigInteger coef, BigInteger exp) {
            this.coef = coef;
            this.exp = exp;
        }
        
        public void differenciate() {
            coef = coef.multiply(exp);
            exp = exp.subtract(BigInteger.ONE);
        }
        
        /**
         * Firstly compare the exponents. If the first result was zero, compare
         * their coefficients. The return value only specifies the relationship.
         * Its value has no meaning.
         */
        @Override
        public int compareTo(Item it) {
            if (this.hashCode() == it.hashCode()) {
                return 0;
            }
            int cond = this.exp.compareTo(it.exp);
            if (cond == 0) {
                it.coef = it.coef.add(this.coef);
            }
            return cond;
        }
        
        /*@Override
        public int hashCode() {
            return exp;
        }*/
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Item)) {
                throw new ClassCastException(
                        "Attempting to compare non-item object with an item.");
            }
            Item it = (Item)obj;
            return this.exp.equals(it.exp) && this.coef.equals(it.coef);
        }
        
        @Override
        public String toString() {
            if (coef.equals(BigInteger.ZERO)) {
                return "";
            }
            StringBuilder s = new StringBuilder();
            if (coef.compareTo(BigInteger.ZERO) > 0) { s.append('+'); }
            else { s.append('-'); }
            BigInteger abs = coef.abs();
            if (exp.equals(BigInteger.ZERO)) { s.append(abs); }
            else {
                if (!abs.equals(BigInteger.ONE)) { s.append(abs + "*"); }
                s.append(Poly.VAR);
                if (!exp.equals(BigInteger.ONE)) { s.append("^" + exp); }
            }
            return s.toString();
        }
    }
    
    public void add(BigInteger coef, BigInteger exp) {
        expression.add(new Item(coef, exp));
    }
    
    public Poly differenciate() {
        for (Item it : expression) {
            it.differenciate();
        }
        return this;
    }
    
    /**
     * Transforms the polynomial to a string representation. The sequence of
     * items is decided by their coefficients, specifically decreasing. The
     * purpose is to try to set the leading term with a positive coefficient.
     *
     * @return a string representation of the polynomial.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        ArrayList<Item> al = new ArrayList<>(expression);
        al.sort((Item it2, Item it1) -> { // Shuffling to decrease
            int coefDiff = it1.coef.compareTo(it2.coef);
            if (coefDiff == 0) { return it1.exp.compareTo(it2.exp); }
            return coefDiff;
        });
        for (Item it : al) {
            s.append(it.toString());
        }
        if (s.length() != 0 && '+' == s.charAt(0)) {
            s.deleteCharAt(0);
        }
        if (s.length() == 0) {
            return "0";
        }
        return s.toString();
    }
}