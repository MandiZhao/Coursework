package enigma;


/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Mandi Zhao
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        pairs = new int[_alphabet.size()];
        for (int i = 0; i < pairs.length; i++) {
            if (cycles.length() == 0) {
                pairs[i] = i;
            } else {
                pairs[i] = -1;
            }
        }
        if (cycles.length() == 0) {
            return;
        } else {
            int rememberIndex = 0;
            for (int i = 0; i < cycles.length(); i++) {
                if (cycles.charAt(i) == '(') {
                    rememberIndex = i + 1;
                } else if (cycles.charAt(i) == ')') {
                    String toadd = cycles.substring(rememberIndex, i);
                    addCycle(toadd);
                }
            }
            for (int i = 0; i < pairs.length; i += 1) {
                if (pairs[i] == -1) {
                    pairs[i] = i;
                }
            }
        }
    }


    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        int end = cycle.length() - 1;
        for (int i = 0; i < end; i++) {
            int original = _alphabet.toInt(cycle.charAt(i));
            int replace = _alphabet.toInt(cycle.charAt(i + 1));
            pairs[original] = replace;
        }
        pairs[_alphabet.toInt(cycle.charAt(end))]
                = _alphabet.toInt(cycle.charAt(0));
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        int result = 0;
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] != -1) {
                result += 1;
            }
        }
        return result;
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return pairs[wrap(p)];
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] == wrap(c)) {
                return i;
            }
        }
        throw new EnigmaException("There is no correspondence");
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(pairs[_alphabet.toInt(p)]);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        int cIndex = _alphabet.toInt(c);
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] == cIndex) {
                return (int) _alphabet.toChar(i);
            }
        }
        throw new EnigmaException("There is no inverse");
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] == i) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Explicit intger pairs of this permutation,
     * where index represents a char, and its value
     * represents the char it gets transformed to. */
    private int[] pairs;

}
