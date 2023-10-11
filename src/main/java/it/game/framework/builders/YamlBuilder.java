package it.game.framework.builders;

import it.game.framework.exceptions.GameException;
import it.game.framework.exceptions.GameExceptionsLibrary;
import it.game.framework.executors.GameExecutor;
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

@Slf4j
@Getter
public class YamlBuilder {

    @ToString
    private static class Machine {
        public List<Connection> globals;
        public List<State> states;
    }

    @ToString
    private static class State {
        public String name;
        public String classname;
        public List<Connection> connections;
    }

    @ToString
    private static class Connection {
        public String expression;
        public String target;
    }


    private final StateMachine machine;
    private final String yamlPath;

    public static YamlBuilder builder(StateMachine machine, String yamlPath) {
        return new YamlBuilder(machine, yamlPath);
    }

    private YamlBuilder(StateMachine machine, String yamlPath) {
        this.machine = machine;
        this.yamlPath = yamlPath;
    }

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

    private void generateMachine(Machine machine) throws Exception {
        Map<String, GameState> refs = new HashMap<>();
        refs.put("EXIT", null);
        for (State s : machine.states) {
            String name = s.name;
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
                if(connection instanceof DirectStateConnection) throw new GameException(GameExceptionsLibrary.DIRECT_CONNECTION_IN_GLOBALS);
                this.machine.getGlobalConnections().add(connection);
            }
        }
    }

    private GameStateConnection getConnection(State s, Connection c, Map<String, GameState> refs, LambdaFactory factory) throws LambdaCreationException {
        if (c.expression.contains("GOTO")) {
            return new DirectStateConnection(
                    s == null ? null : refs.get(s.name),
                    refs.get(c.target)
            );
        }
        if (c.expression.contains("CATCH")) {
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

    private GameState instantiate(String classname) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> o = Class.forName(classname);
        var c = o.getDeclaredConstructor();
        var i = c.newInstance();
        if (!(i instanceof GameState))
            throw new ClassCastException(classname + " cannot be casted into " + GameState.class.getSimpleName());
        return (GameState) i;
    }


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



