package enigma;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import java.util.HashMap;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the FixedRotor class.
 *  @author Mandi Zhao
 */

public class FixedRotorTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);
    /* ***** TESTING UTILITIES ***** */

    private Rotor fixedrotor;
    private String alpha = UPPER_STRING;

    /** Set the rotor to the one with given NAME and permutation as
     *  specified by the NAME entry in ROTORS. */
    private void setFixedrotor(String name, HashMap<String, String> rotors) {
        fixedrotor = new FixedRotor(name,
                new Permutation(rotors.get(name), UPPER)
        );
    }

    /** Do small translations by one single rotor
     * FIXEDROTOR on a STRING
     */
    private String translate(String string, Rotor rotor) {
        String result = "";
        for (int i = 0; i < string.length(); i++) {
            char toTrans = string.toUpperCase().charAt(i);
            int p = rotor.alphabet().toInt(toTrans);
            p =  rotor.convertForward(p);
            result += rotor.alphabet().toChar(p);
        }
        return result;
    }

    /* ***** TESTS ***** */
    @Test
    public void checkMovingStatus() {
        setFixedrotor("Gamma", NAVALA);
        assertFalse(fixedrotor.atNotch());
        assertEquals(fixedrotor.getNotches(), null);
        assertFalse(fixedrotor.ratchet());
    }

    @Test
    public void checkTranslation() {
        setFixedrotor("Beta", NAVALA);
        String result1 = translate("casalinga", fixedrotor);
        assertEquals("YLKLBXMNL", result1);
        String result2 = translate("B", fixedrotor);
        assertEquals("E", result2);
    }

    @Test
    public void checkDerange() {
        setFixedrotor("Beta", NAVALA);
        Permutation perm = fixedrotor.permutation();
        assertTrue(perm.derangement());
    }
}
