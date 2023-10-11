package it.game.framework.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class ExceptionLibrary {

    @Getter
    @AllArgsConstructor
    public static class Entry {
        String name;
        Integer hash;
        String message;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return Objects.equals(name, entry.name) && Objects.equals(hash, entry.hash);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    private static List<Entry> exceptions;

    private static List<Entry> init() {
        List<Entry> e = new LinkedList<>();

        //Building Exceptions
        e.add(new Entry("MACHINE_BUILDING_EXCEPTION", 9100, "Machine building exception"));
        e.add(new Entry("STARTING_STATE_IS_NOT_IN_MACHINE_STATES", 9101, "Starting state is not in the machine's states list"));
        e.add(new Entry("CONNECTION_STARTINGSTATE_IS_NULL", 9102, "Connection's starting state missing"));
        e.add(new Entry("CONNECTION_STATE_IS_NOT_IN_MACHINE_STATES", 9103, "States in connection are not contained in the machine's states list"));
        e.add(new Entry("DIRECT_CONNECTION_IS_NOT_LAST", 9104, "A connection that returns always TRUE is not last, connections following this one are not reachable"));
        e.add(new Entry("DIRECT_CONNECTION_IN_GLOBALS", 9105, "A connection that returns always TRUE is in the global connections, connections following this one are not reachable"));
        e.add(new Entry("CLASS_NAME_IS_A_KEYWORD",9106, "The class name is a keyword between GOTO, CATCH and EXIT, this is prohibited please change it"));

        //Execution Exceptions
        e.add(new Entry("EXECUTION_EXCEPTION", 9200, "Machine execution exception"));
        e.add(new Entry("STATEMACHINE_IS_NULL", 9201, "The state machine reference inside the executor is null"));
        e.add(new Entry("CONTEXT_IS_NULL", 9202, "The context reference inside the executor is null"));
        e.add(new Entry("STARTING_STATE_IS_NULL", 9203, "Machine's starting state is null"));
        e.add(new Entry("GAMEEXECUTOR_IS_NULL", 9204, "The GameExecutor instance inside the ThreadedExecutor is null"));

        //GameContext Exceptions
        e.add(new Entry("CONTEXT_EXCEPTION", 9300, "GameContext exception"));
        e.add(new Entry("CONTEXT_CASTING_EXCEPTION", 9301, "Object cannot be casted in the specified class"));

        return e;
    }

    private static List<Entry> exceptions() {
        if (exceptions == null) exceptions = init();
        return exceptions;
    }

    public static int size() {
        return exceptions().size();
    }

    public static boolean isEmpty() {
        return exceptions().isEmpty();
    }

    public static boolean containsException(String exceptionName) {
        return exceptions().stream().anyMatch(s -> s.getName().equals(exceptionName));
    }

    public static boolean containsException(Integer hash) {
        return exceptions().stream().anyMatch(s -> s.getHash().equals(hash));
    }

    public static Entry get(String exceptionName) {
        return exceptions().stream().filter(v -> v.getName().equals(exceptionName)).findFirst().orElse(null);
    }

    public static Entry get(Integer exceptionHash) {
        return exceptions().stream().filter(v -> v.getHash().equals(exceptionHash)).findFirst().orElse(null);
    }

    public static boolean add(String name, Integer hash, String message) {
        Entry o = new Entry(name, hash, message);
        if (exceptions().contains(o)) return false;
        exceptions.add(o);
        return true;
    }

    public static Set<String> namesSet() {
        return exceptions().stream().map(Entry::getName).collect(Collectors.toSet());
    }

    public static Set<Integer> hashSet() {
        return exceptions().stream().map(Entry::getHash).collect(Collectors.toSet());
    }

    public static Collection<String> messageList() {
        return exceptions().stream().map(Entry::getMessage).collect(Collectors.toSet());
    }

    public static Set<Entry> entrySet() {
        return new HashSet<>(exceptions());
    }
}
