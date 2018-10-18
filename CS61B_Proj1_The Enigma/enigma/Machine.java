package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;


/** Class that represents a complete enigma machine.
 *  @author Mandi Zhao
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = new HashMap<>();
        for (Rotor r: allRotors) {
            _allRotors.put(r.name(), r);
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        list = new ArrayList<>();
        if (rotors.length != _numRotors) {
            throw new EnigmaException("Wrong number of rotors than I permit.");
        }
        for (int i = 0; i < rotors.length; i++) {
            Rotor toadd = _allRotors.get(rotors[i]);
            if (list.contains(toadd)) {
                throw new EnigmaException("Rotors are one of a kind.");
            } else if (i < numPawls() - 1 && toadd.ratchet()) {
                throw new EnigmaException("This should be non-moving.");
            } else if (i > numPawls() - 1 && (!toadd.ratchet())) {
                throw new EnigmaException("This should be moving.");
            } else {
                toadd.clear();
                list.add(i, toadd);
            }
        }
        Rotor rightMost = list.get(rotors.length - 1);
        rightMost.setRight(null);
        Rotor reflector = _allRotors.get(rotors[0]);
        reflector.setLeft(null);
        for (int j = 1; j < rotors.length - 1; j++) {
            list.get(j).setRight(list.get(j + 1));
            list.get(j).setLeft(list.get(j - 1));
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("Wrong number of rotors.");
        }
        for (int i = 0; i < setting.length(); i++) {
            list.get(i + 1).set(setting.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard =  plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        advanceTogether();
        c = c % _alphabet.size();
        if (c < 0) {
            c += _alphabet.size();
        }
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            c = list.get(i).convertForward(c);
        }
        for (int j = 1; j < list.size(); j++) {
            c = list.get(j).convertBackward(c);
        }
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        return c;
    }

    /** Advance the first rotor, check which other
     * rotors can move, and advance them. */
    void advanceTogether() {
        HashSet<Rotor> whosMoving = new HashSet<>();
        Rotor first = list.get(numRotors() - 1);
        whosMoving.add(first);
        if (first.atNotch()) {
            whosMoving.add(list.get(numRotors() - 2));
        }
        for (int i = numRotors() - 2; i > numPawls() - 1; i -= 1) {
            Rotor current = list.get(i);
            if (current.getRight().atNotch()
                    || whosMoving.contains(current.getLeft())) {
                whosMoving.add(current);
            }
            if (current.atNotch()) {
                whosMoving.add(current);
                whosMoving.add(current.getLeft());
            }
        }
        for (Rotor r: whosMoving) {
            r.advance();
        }
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        if (msg.isEmpty()) {
            return msg;
        }
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            result +=
                    _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
        }
        return result;
    }

    /** Return the total rotors instock. */
    HashMap<String, Rotor> getRotors() {
        return _allRotors;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotor slots. */
    private int _numRotors;

    /** Number of pawl slots. */
    private int _pawls;

    /** Collection of rotors. */
    private HashMap<String, Rotor> _allRotors;

    /** list of rotors in use. */
    private ArrayList<Rotor> list;

    /** My plugboard. */
    private Permutation _plugboard;
}
