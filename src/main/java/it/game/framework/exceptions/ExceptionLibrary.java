package it.game.framework.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The ExceptionLibrary is a dynamic library saved as singleton
 * that store all the exception information(name, hash and Message)
 * about GameExceptions, this class was developed like this to
 * create a centralized and extendable structure for store Exceptions
 */
public class ExceptionLibrary implements Serializable {

    /**
     * This inner class store confront and return the data
     * of the exception Name, Hash and Message
     */
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

    /**
     * This method populates the ExceptionLibrary when
     * is called with known exceptions
     *
     * @return The list of Exceptions Entries
     */
    private static List<Entry> init() {
        List<Entry> e = new LinkedList<>();

        //Building Exceptions
        e.add(new Entry("MACHINE_BUILDING_EXCEPTION", 9100, "Machine building exception"));
        e.add(new Entry("STARTING_STATE_IS_NOT_IN_MACHINE_STATES", 9101, "Starting state is not in the machine's states list"));
        e.add(new Entry("CONNECTION_STARTINGSTATE_IS_NULL", 9102, "Connection's starting state missing"));
        e.add(new Entry("CONNECTION_STATE_IS_NOT_IN_MACHINE_STATES", 9103, "States in connection are not contained in the machine's states list"));
        e.add(new Entry("DIRECT_CONNECTION_IS_NOT_LAST", 9104, "A connection that returns always TRUE is not last, connections following this one are not reachable"));
        e.add(new Entry("DIRECT_CONNECTION_IN_GLOBALS", 9105, "A connection that returns always TRUE is in the global connections, connections following this one are not reachable"));
        e.add(new Entry("CLASS_NAME_IS_A_KEYWORD", 9106, "The class name is a keyword between GOTO, CATCH and EXIT, this is prohibited please change it"));

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

    /**
     * Returns the list of the entries
     *
     * @return The list of exception entries
     */
    private static List<Entry> exceptions() {
        if (exceptions == null) exceptions = init();
        return exceptions;
    }

    /**
     * Returns the Size of the exception library
     *
     * @return The size of the library
     */
    public static int size() {
        return exceptions().size();
    }

    /**
     * Check if the exception library is empty
     *
     * @return True if the exception library is empty
     */
    public static boolean isEmpty() {
        return exceptions().isEmpty();
    }

    /**
     * Checks if there is an exception with the given name
     *
     * @param exceptionName The name of the exception to search
     * @return returns true if the exception is present inside the library
     */
    public static boolean containsException(String exceptionName) {
        return exceptions().stream().anyMatch(s -> s.getName().equals(exceptionName));
    }

    /**
     * Checks if there is an exception with the given hash
     *
     * @param hash The hash of the exception to search
     * @return returns true if the exception is present inside the library
     */
    public static boolean containsException(Integer hash) {
        return exceptions().stream().anyMatch(s -> s.getHash().equals(hash));
    }

    /**
     * Returns the entry with the given name
     *
     * @param exceptionName The name of the entry to return
     * @return The entry of the library with the given name
     */
    public static Entry get(String exceptionName) {
        return exceptions().stream().filter(v -> v.getName().equals(exceptionName)).findFirst().orElse(null);
    }

    /**
     * Returns the entry with the given hash
     *
     * @param exceptionHash The hash of the entry to return
     * @return The entry of the library with the given hash
     */
    public static Entry get(Integer exceptionHash) {
        return exceptions().stream().filter(v -> v.getHash().equals(exceptionHash)).findFirst().orElse(null);
    }


    /**
     * Adds a new Exception Entry to the library
     *
     * @param name    The name of the new exception to index it with
     * @param hash    The hash of the new exception
     * @param message The message of the new exception
     * @return True if the new exception is added with success, false otherwise
     */
    public static boolean add(String name, Integer hash, String message) {
        Entry o = new Entry(name, hash, message);
        if (exceptions().contains(o)) return false;
        exceptions.add(o);
        return true;
    }

    /**
     * Returns the Set of names of all the entries of the library
     *
     * @return The Set containing all the names of the entry in the library
     */
    public static Set<String> namesSet() {
        return exceptions().stream().map(Entry::getName).collect(Collectors.toSet());
    }

    /**
     * Returns the Set of Hashes of all the entries of the library
     *
     * @return The Set containing all the hashes of the entry in the library
     */
    public static Set<Integer> hashSet() {
        return exceptions().stream().map(Entry::getHash).collect(Collectors.toSet());
    }

    /**
     * Returns the collection of Messages of all the entries of the library
     *
     * @return The collection containing all the messages of the entry in the library
     */
    public static Collection<String> messageList() {
        return exceptions().stream().map(Entry::getMessage).collect(Collectors.toSet());
    }

    /**
     * Returns the set of entries of the library
     *
     * @return The set of entries of the library
     */
    public static Set<Entry> entrySet() {
        return new HashSet<>(exceptions());
    }

    /**
     * Renders a formatted string with a table of all the exception entry currently into the library
     * @return A string formatted as a table with The hash, name and message columns
     */
    public static String printExceptions() {
        StringBuilder ret = new StringBuilder();
        ret.append(String.format("| %4s | %-50s | %s%n", "HASH", "NAME", "MESSAGE"));
        exceptions().forEach(e -> ret.append(String.format("| %4s | %-50s | %s%n", e.getHash(), e.getName(), e.getMessage())));
        return ret.toString();
    }
}
