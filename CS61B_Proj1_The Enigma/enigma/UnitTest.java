package enigma;

import org.junit.Test;
import ucb.junit.textui;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the enigma package.
 *  @author Mandi Zhao
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(PermutationTest.class,
                MovingRotorTest.class, FixedRotorTest.class
        );
    }
    @Test
    public void testDoubleStep() {
        Alphabet ac = new CharacterRange('A', 'D');
        Rotor one = new Reflector("R1", new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2", new Permutation("(ABCD)", ac), "C");
        Rotor three = new MovingRotor("R3", new Permutation("(ABCD)", ac), "C");
        Rotor four = new MovingRotor("R4", new Permutation("(ABCD)", ac), "C");
        String setting = "AAA";
        Rotor[] list = {one, two, three, four};
        ArrayList<Rotor> machineRotors = new ArrayList<>();
        machineRotors.add(one);
        machineRotors.add(two);
        machineRotors.add(three);
        machineRotors.add(four);
        String[] rotors = {"R1", "R2", "R3", "R4"};
        Machine mach = new Machine(ac, 4, 3, machineRotors);
        mach.insertRotors(rotors);
        mach.setRotors(setting);

        assertEquals("AAAA", getSetting(ac, list));
        mach.convert('a');
        assertEquals("AAAB", getSetting(ac, list));
        mach.convert('a');
    }

    private String getSetting(Alphabet alph, Rotor[] machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }

}


