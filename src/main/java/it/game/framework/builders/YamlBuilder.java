package it.game.framework.builders;

import it.game.framework.contexts.GameContext;
import it.game.framework.exceptions.GameException;
import it.game.framework.statemachines.StateMachine;
import it.game.framework.states.GameState;
import it.game.framework.states.library.GameStateConnection;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class YamlBuilder<C extends GameContext<C>> extends Builder<C> {

    private final String yamlPath;

    public YamlBuilder(String yamlPath) {
        this(null, yamlPath);
    }

    public YamlBuilder(StateMachine<C> machine, String yamlPath) {
        super(machine);
        this.yamlPath = yamlPath;
    }

    public void build() {
        InputStream inputStream = null;
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
        C context = (C) Class.forName(machine.context).getDeclaredConstructor().newInstance();
        Map<String, GameState<C>> refs = new HashMap<>();
        for (State s : machine.states) {
            String name = s.name;
            GameState<C> state = instantiate(s.classname);
            refs.put(name, state);
            this.machine.getStates().add(state);
        }
        for (State s : machine.states) {
            for (Connection c : s.connections) {
                GameStateConnection<C> connection = new GameStateConnection<>(
                        c.expression,
                        refs.get(s.name),
                        null,
                        refs.get(c.target)
                );
            }
        }

    }

    private GameState<C> instantiate(String classname) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object o = Class.forName(classname).getDeclaredConstructor().newInstance();
        if (!(o instanceof GameState))
            throw new ClassCastException(classname + " cannot be casted into " + GameState.class.getSimpleName());
        return (GameState) o;
    }


}

@ToString
class Machine {
    public String context;
    public List<State> states;
}

@ToString
class State {
    public String name;
    public String classname;
    public List<Connection> connections;
}

@ToString
class Connection {
    public String expression;
    public String target;
}


