package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Mandi Zhao
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine M = readConfig();
        String firstLine = _input.nextLine();
        if (firstLine.matches("[*] .+")) {
            setUp(M, firstLine);
            while (_input.hasNextLine()) {
                String next = _input.nextLine();
                if (next.matches("[*] .+")) {
                    setUp(M, next);
                } else if (next.isEmpty()) {
                    printMessageLine(next);
                } else {
                    next = next.toUpperCase();
                    next = next.replace(" ", "");
                    next = M.convert(next);
                    printMessageLine(next);
                }
            }
        } else {
            throw new EnigmaException("Wrong input beginning.");
        }
    }


    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            HashSet<Rotor> allRotors = new HashSet<>();
            if (_config.hasNext("[A-Z][-][A-Z]")
                    || _config.hasNext("\\S+")) {
                String alpha = _config.next();
                if (alpha.matches("[A-Z][-][A-Z]")) {
                    _alphabet = new CharacterRange(alpha.charAt(0),
                            alpha.charAt(alpha.length() - 1));
                } else {
                    _alphabet = new SpecialAlpha(alpha);
                }
                if (_config.hasNextInt()) {
                    numRotors = _config.nextInt();
                    if (_config.hasNextInt()) {
                        pawls = _config.nextInt();
                        if (pawls < numRotors && pawls >= 0) {
                            while (_config.hasNext(".+")) {
                                Rotor r = readRotor();
                                allRotors.add(r);
                            }
                        } else {
                            throw new EnigmaException(
                                    "Pawl and numRotors should be positive"
                            );
                        }
                    } else {
                        throw new EnigmaException("What about pawls?");
                    }
                } else {
                    throw new EnigmaException("How many rotors?");
                }
                return new Machine(_alphabet, numRotors, pawls, allRotors);
            } else {
                throw new EnigmaException("bad conf beginning");
            }
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next().toUpperCase();
            String second = _config.next();
            String tocycle = "";
            String notches;
            while (_config.hasNext("([(]\\S+[)])+")) {
                tocycle += _config.next().replaceAll("[)][(]", ") (") + " ";
            }
            if (second.charAt(0) == 'M') {
                if (second.length() > 1) {
                    notches = second.substring(1);
                    return new MovingRotor(name,
                            new Permutation(tocycle, _alphabet), notches);
                }
                throw new
                        EnigmaException("Moving rotors need to have notches.");
            } else if (second.charAt(0) == 'N') {
                if (second.length() == 1) {
                    return new FixedRotor(name,
                            new Permutation(tocycle, _alphabet));
                }
                throw new EnigmaException(
                        "Non-Moving rotors don't have notches."
                );
            } else if (second.charAt(0) == 'R') {
                if (second.length() == 1) {
                    return new Reflector(name,
                            new Permutation(tocycle, _alphabet));
                }
                throw new EnigmaException("Reflectors don't have notches.");
            } else {
                throw new EnigmaException("Unsupported rotor type.");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner reader = new Scanner(settings);
        int len = M.numRotors();
        String[] rotorList = new String[len];
        int movingCount = 0;
        String plugB = "";
        if (settings.matches("[*].+")) {
            reader.next();
            for (int i = 0; i < len; i++) {
                String toadd = reader.next();
                if (!M.getRotors().containsKey(toadd)) {
                    throw new EnigmaException("I don't recognize this rotor");
                } else {
                    Rotor check = M.getRotors().get(toadd);
                    if (i == 0 && (!check.reflecting())) {
                        throw new EnigmaException("Wrong reflector position");
                    }
                    rotorList[i] = toadd;
                    if (check.ratchet()) {
                        movingCount += 1;
                    }
                }
            }
            if (movingCount > M.numPawls()) {
                throw new EnigmaException("More moving rotors than should be");
            }
            M.insertRotors(rotorList);
            String initialS = reader.next();
            if (initialS.length() == len - 1) {
                M.setRotors(initialS);
                while (reader.hasNext("[(][A-Z]+[)]")) {
                    String toPlug = reader.next();
                    plugB += toPlug + " ";
                }
                if (reader.hasNext()) {
                    throw new EnigmaException("Bad plugboard settings.");
                } else if (plugB.length() > 0) {
                    Permutation plug = new Permutation(plugB, _alphabet);
                    M.setPlugboard(plug);
                }
            } else {
                throw new EnigmaException("Wrong number of settings.");
            }
        } else {
            throw new EnigmaException("Wrong settings beginning.");
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        while (msg.length() >= 5) {
            String sub = msg.substring(0, 5);
            _output.print(sub + " ");
            msg = msg.substring(5);
        }
        _output.println(msg);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Number of pawls read and stored. */
    private int pawls;

    /** Number of rotor slots read and stored. */
    private int numRotors;
}
