package it.vegas.gameframework.testgame.states;

import it.vegas.gameframework.exceptions.GameException;
import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.testgame.TestGameContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckWinning<C extends TestGameContext> extends GameState<C> {

    private final List<List<Integer>> winlineRules;
    private final Map<Character, Long> paytable;

    public CheckWinning() {
        super("Check Winning", "Checks the winning by given matrix");
        winlineRules = List.of(
                List.of(1, 1, 1),
                List.of(0, 0, 0),
                List.of(2, 2, 2),
                List.of(0, 1, 2),
                List.of(2, 1, 0)
        );
        paytable = new HashMap<>();
        paytable.put('A', 1000L);
        paytable.put('K', 500L);
        paytable.put('Q', 100L);
        paytable.put('J', 50L);
    }

    private List<List<Character>> getWinline(List<List<Character>> matrix) {
        List<List<Character>> res = new ArrayList<>();
        for (List<Integer> rule : winlineRules) {
            res.add(
                    List.of(
                            matrix.get(0).get(rule.get(0)),
                            matrix.get(1).get(rule.get(1)),
                            matrix.get(2).get(rule.get(2))
                    )
            );
        }
        return res;
    }

    private void check(List<List<Character>> winlines) {
        List<String> results = new ArrayList<>();
        long total = 0L;
        int freeSpins = 0;
        for (List<Character> i : winlines) {
            char first = i.get(0);
            int count = 0;
            for (Character c : i) {
                if (c != first) break;
                count++;
            }
            if (count == 3) {
                if (context.isFreeSpin) {
                    results.add(first + "x" + count + " " + (paytable.get(first) / 2) + "$");
                    total += paytable.get(first) / 2;
                } else {
                    results.add(first + "x" + count + " " + paytable.get(first) + "$");
                    total += paytable.get(first);
                    if (first == 'Q') freeSpins += 3;
                }
            }
        }
        context.results += String.join(", ", results) + " ";
        context.winning += total;
        context.freeSpins += freeSpins;
    }

    @Override
    public void execute() throws GameException {
        if (context.matrix != null) throw new GameException(100, "Missing matrix on check winning");
        List<List<Character>> winlines = getWinline(context.matrix);
        check(winlines);
    }


}
