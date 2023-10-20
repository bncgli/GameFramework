package it.game.framework.builders;

import it.game.framework.exceptions.ExceptionLibrary;
import it.game.framework.exceptions.GameException;
import it.game.framework.stateconnections.DirectStateConnection;
import it.game.framework.stateconnections.ExceptionStateConnection;
import it.game.framework.stateconnections.GameStateConnection;
import it.game.framework.stateconnections.interfaces.Expression;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import pl.joegreen.lambdaFromString.LambdaCreationException;
import pl.joegreen.lambdaFromString.LambdaFactory;
import pl.joegreen.lambdaFromString.TypeReference;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This structure builds the statemachine from a given yaml file
 */
@Slf4j
@Getter
public class YamlBuilder {

    /**
     * This class maps the first
     * layer of the machine containing the
     * global connections and the states of the
     * machine
     */
    @ToString
    private static class Machine {
        public List<Connection> globals;
        public List<State> states;
    }

    /**
     * This class maps each state
     * defining: name, classname and connections
     */
    @ToString
    private static class State {
        public String name;
        public String classname;
        public List<Connection> connections;
    }


    /**
     * This class maps each connection
     * defining: expression and target of the connection
     */
    @ToString
    private static class Connection {
        public String expression;
        public String target;
    }


    private final StateMachine machine;
    private final String yamlPath;
    private final List<String> keywordList;

    /**
     * Inspired from the Builder design pattern
     * the builder function returns the Yamlbuilder class
     * @param machine The state machine that has to be populated
     * @param yamlPath The path to the yaml file that contain the blueprint of the machine
     * @return The instance of YamlBuilder
     */
    public static YamlBuilder builder(StateMachine machine, String yamlPath) {
        return new YamlBuilder(machine, yamlPath);
    }

    private YamlBuilder(StateMachine machine, String yamlPath) {
        this.machine = machine;
        this.yamlPath = yamlPath;
        this.keywordList = List.of("GOTO", "goto", "CATCH", "catch", "EXIT", "exit");
    }

    /**
     * This method starts the states machine's building process
     * the YamlBuilder reads the yaml file, instantiate the classes
     * and generate the connection's expressions from file's text.
     * After the execution of this class the state machine passed as
     * argument is built.
     */
    public void build() {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(yamlPath);
            Yaml yaml = new Yaml(new Constructor(Machine.class));
            Machine data = yaml.load(inputStream);
            generateMachine(data);

        } catch (Exception e) {
            log.error(GameException.format(e));
        }
    }

    /**
     * This method contains the main process to generate the the state machine.
     * The methods firstly instantiates all the states of the machine and index them into
     * a map for future reference, then create all the connections and add them to the
     * relative state, at the end creates the global connections, each step check for
     * incongruent data and throws exceptions in case of building errors.
     * @param machine The machine data extracted from the yaml file
     * @throws Exception The building exception emerged from the building process
     */
    private void generateMachine(Machine machine) throws Exception {
        Map<String, GameState> refs = new HashMap<>();
        refs.put("EXIT", null);
        refs.put("exit", null);
        for (State s : machine.states) {
            String name = s.name;
            if(keywordList.contains(name)) throw new GameException(ExceptionLibrary.get("CLASS_NAME_IS_A_KEYWORD"));
            GameState state = instantiate(s.classname);
            refs.put(name, state);
            this.machine.getStates().add(state);
        }

        LambdaFactory factory = LambdaFactory.get();
        for (State s : machine.states) {
            if (s.connections == null) continue;
            for (Connection c : s.connections) {
                GameStateConnection connection = getConnection(s, c, refs, factory);
                this.machine.getConnections().add(connection);
            }
        }

        if (machine.globals != null) {
            for (Connection c : machine.globals) {
                GameStateConnection connection = getConnection(null, c, refs, factory);
                if(connection instanceof DirectStateConnection) throw new GameException(ExceptionLibrary.get("DIRECT_CONNECTION_IN_GLOBALS"));
                this.machine.getGlobalConnections().add(connection);
            }
        }
    }

    /**
     * This method build the adeguate GameStateConnection from the text
     * of the yaml file.
     * @param s The state extracted from the yaml file
     * @param c The connection data extracted from the yaml file for creating the GameStateConnection
     * @param refs The stored GameStates indexed by name
     * @param factory The factory to convert string to lambdas
     * @return The GameStateConnection generated from the connection data
     * @throws LambdaCreationException If the lambda is malformed or has unknown classes can throws this exception.
     */
    private GameStateConnection getConnection(State s, Connection c, Map<String, GameState> refs, LambdaFactory factory) throws LambdaCreationException {
        if (c.expression.contains("GOTO") || c.expression.contains("goto")) {
            return new DirectStateConnection(
                    s == null ? null : refs.get(s.name),
                    refs.get(c.target)
            );
        }
        if (c.expression.contains("CATCH") || c.expression.contains("catch")) {
            return new ExceptionStateConnection(
                    c.expression,
                    s == null ? null : refs.get(s.name),
                    Integer.parseInt(c.expression.split(":")[1]),
                    refs.get(c.target)
            );
        }
        return new GameStateConnection(
                prettifyExpression(c.expression),
                s == null ? null : refs.get(s.name),
                factory.createLambda(
                        rectifyExpression(c.expression),
                        new TypeReference<Expression>() {
                        } //DON'T REMOVE EXPRESSION OR JVM ERROR OCCURS
                ),
                refs.get(c.target)
        );
    }

    /**
     * This method instantiate a class from it's name,
     * it is a process inclined to fail if the class name is
     * not exact or formatted adequately.
     * Each class HAS to extends GameState otherwise fails.
     * @param classname The FULL path of the class (e.g. it.game.framework.testclasses.GameStateA)
     * @return The gamestate instanciated by name
     * @throws Exception Thrown during the instantiation process if the class is not at the classname coordinates
     */
    private GameState instantiate(String classname) throws Exception {
        Class<?> o = Class.forName(classname);
        var c = o.getDeclaredConstructor();
        var i = c.newInstance();
        if (!(i instanceof GameState))
            throw new ClassCastException(classname + " cannot be casted into " + GameState.class.getSimpleName());
        return (GameState) i;
    }


    /**
     * Convert the readable format of the class in lambda code
     * <br>e.g. "{{val1:Integer}} > 5" to "(c)->g.get("val1", Integer) > 5"
     * @param expression The expression of the lambda as a string
     * @return Returns a reformatted lambda that can be converted by the lambdafactory class
     */
    private String rectifyExpression(String expression) {
        String ret = expression;
        List<String> toReplace = Pattern.compile("\\{\\{.+?\\}\\}").matcher(expression).results().map(MatchResult::group).collect(Collectors.toList());
        List<String> replaceWith = Pattern.compile("(?<=\\{\\{).+?(?=\\}\\})").matcher(expression).results().map(MatchResult::group).collect(Collectors.toList());
        for (int i = 0; i < toReplace.size(); i++) {
            String[] split = replaceWith.get(i).split(":");
            String formula = String.format("c.get(\"%s\",%s.class)", split[0].trim(), split[1].trim());
            ret = expression.replace(toReplace.get(i), formula);
        }
        return "(c)->" + ret;
    }

    /**
     * Prettify the expression to be more compact and readable
     * inside the graphs and renders of the machine.
     * <br>e.g. "{{val1:Integer}} > 5" to "val1 > 5"
     * @param expression The expression as string
     * @return The simplified expression
     */
    private String prettifyExpression(String expression) {
        String ret = expression;
        List<String> toReplace = Pattern.compile("\\{\\{.+?\\}\\}").matcher(expression).results().map(MatchResult::group).collect(Collectors.toList());
        List<String> replaceWith = Pattern.compile("(?<=\\{\\{).+?(?=:)").matcher(expression).results().map(MatchResult::group).collect(Collectors.toList());
        for (int i = 0; i < toReplace.size(); i++) {
            ret = expression.replace(toReplace.get(i), replaceWith.get(i));
        }
        return ret;
    }


}



