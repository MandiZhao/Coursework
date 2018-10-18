package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.error;

/**Initiate a new special Alphabet, in which each character is specified an
 * index and translation according to a string value
 * read from configuration file(s).
 * @author  Mandi Zhao
 */


public class SpecialAlpha extends Alphabet {

    /** A new special, more general Alphabet based on
     * input CHARACTERS. */
    SpecialAlpha(String characters) {
        _characters = characters.toUpperCase();
        charList = new HashMap<>();
        for (int i = 0; i < size(); i++) {
            charList.put(characters.charAt(i), i);
        }
    }

    @Override
    int size() {
        return _characters.length();
    }

    @Override
    boolean contains(char ch) {
        return charList.containsKey(ch);
    }

    @Override
    char toChar(int index) {
        for (Character key : charList.keySet()) {
            if (charList.get(key) == index) {
                return key;
            }
        }
        throw error("character index out of range");
    }

    @Override
    int toInt(char ch) {
        if (!charList.containsKey(ch)) {
            throw error("character out of range");
        } else {
            return charList.get(ch);
        }
    }

    /** A HashMap that stores each character in order,
     * according to the input. */
    private HashMap<Character, Integer> charList;


    /** String that stores the original input list of characters. */
    private String _characters;

}

