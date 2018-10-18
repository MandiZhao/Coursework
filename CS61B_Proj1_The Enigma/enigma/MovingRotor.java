package enigma;

import java.util.HashSet;


/** Class that represents a rotating rotor in the enigma machine.
 *  @author Mandi ZHAO
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        HashSet<Integer> notchList = new HashSet<>();
        for (int i = 0; i < notches.length(); i++) {
            notchList.add(perm.alphabet().toInt(notches.charAt(i)));
        }
        setNotches(notchList);
        setRatchet(true);
        clear();
    }

    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }
    @Override
    boolean atNotch() {
        return getNotches().contains(setting());
    }

    /** Return its setting to 0 for a new translation work. */
    void clear() {
        set(0);
    }
}
