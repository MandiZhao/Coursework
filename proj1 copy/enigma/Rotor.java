package enigma;

import java.util.HashSet;


/** Superclass that represents a rotor in the enigma machine.
 *  @author Mandi ZHAO
 */
class Rotor {
    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return (ratchet && atNotch());
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        setting = alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int tocheck = permutation().permute(p + setting()) - setting();
        return permutation().wrap(tocheck);
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int tocheck = permutation().invert(e + setting()) - setting();
        return permutation().wrap(tocheck);
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Set my list of notches to NOTCHES. */
    void setNotches(HashSet<Integer> notches) {
        notchList =  notches;
    }

    /** Return my notchlist. */
    HashSet<Integer> getNotches() {
        return notchList;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() { }

    /** Make sure the setting is at 0. */
    void clear() { }

    /**
     * Get the rotor to its left
     * and return it.
     */
    Rotor getLeft() {
        return leftRotor;
    }

    /**
     * Get the rotor to its right
     * and return it.
     */
    Rotor getRight() {
        return rightRotor;
    }

    /** Set the left rotor
     * to R. */
    void setLeft(Rotor r) {
        leftRotor = r;
    }

    /** Set the right rotor
     * to R. */
    void setRight(Rotor r) {
        rightRotor = r;
    }

    /** Set rachet boolean to BOOL. */
    void setRatchet(boolean bool) {
        ratchet = bool;
    }

    /** Get rachet value and return it. */
    boolean ratchet() {
        return ratchet;
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** Whether this rotor has a ratchet. */
    private boolean ratchet;

    /** The rotor to my left. */
    private Rotor leftRotor;

    /** The rotor to my right. */
    private Rotor rightRotor;

    /** My setting integer of index in alphabet. */
    private int setting;

    /** My notch char(s)' (int) value. */
    private HashSet<Integer> notchList;

}
