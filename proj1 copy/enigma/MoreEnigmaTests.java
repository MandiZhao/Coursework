package enigma;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


/** Some extra tests for Enigma.
 *  @author Mandi Zhao
 */
public class MoreEnigmaTests {
    @Test
    public void testInvertChar() {
        Permutation p = new Permutation(
                "(PNH) (ABDFIKLZYXW) (JC)", new CharacterRange('A', 'Z')
        );
        assertEquals(p.invert('B'), 'A');
        assertEquals(p.invert('G'), 'G');
        assertEquals(p.invert('W'), 'X');
        assertEquals(p.invert('A'), 'W');
    }

    @Test
    public void testPermuteChar() {
        Permutation p = new Permutation(
                "(HGE) (RTYUI)", new CharacterRange('A', 'Z')
        );
        assertEquals(p.permute('R'), 'T');
    }

    @Test
    public void testDerangement() {
        Permutation p = new Permutation(
                "(HGE) (RTYUI)", new CharacterRange('A', 'Z')
        );
        assertEquals(p.derangement(), false);
    }

    @Test
    public void testDoubleStep() {
        Alphabet ac = new CharacterRange('A', 'D');
        Rotor one = new Reflector("R1", new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2", new Permutation("(ABCD)", ac), "C");
        Rotor three = new MovingRotor("R3", new Permutation("(ABCD)", ac), "C");
        Rotor four = new MovingRotor("R4", new Permutation("(ABCD)", ac), "C");
        Rotor[] machineRotors = {one, two, three, four};
        String[] rotors = {"R1", "R2", "R3", "R4"};

        assertEquals("AAAA", getSetting(ac, machineRotors));
        assertEquals("AAAB", getSetting(ac, machineRotors));
    }

    /** Helper method to get the String
     * representation of the current Rotor settings */
    private String getSetting(Alphabet alph, Rotor[] machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }
}
