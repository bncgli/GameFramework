package it.vegas.gameframework.testgame.states;

import it.vegas.gameframework.states.GameState;
import it.vegas.gameframework.testgame.TestGameContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Spin<C extends TestGameContext> extends GameState<C> {
    private final List<List<Character>> reels;

    public Spin() {
        super("Spin", "spin and generate the matrix of symbols", null, null, null);
        List<Character> l1 = new ArrayList<>(List.of('A', 'K', 'K', 'Q', 'Q', 'Q', 'J', 'J', 'J', 'J'));
        List<Character> l2 = new ArrayList<>(l1);
        List<Character> l3 = new ArrayList<>(l1);

        Collections.shuffle(l1);
        Collections.shuffle(l2);
        Collections.shuffle(l3);

        reels = List.of(l1, l2, l3);
    }

    private List<List<Character>> getMatrix(List<Integer> pos) {
        List<List<Character>> ret = new ArrayList<>();
        for (Integer i : pos) {
            List<Character> vis = new ArrayList<>();
            vis.add((i == 0) ? reels.get(0).get(9) : reels.get(0).get(i - 1));
            vis.add(reels.get(0).get(i));
            vis.add((i == 9) ? reels.get(0).get(0) : reels.get(0).get(i + 1));
            ret.add(vis);
        }
        return ret;
    }


    @Override
    public void execute() {
        if (context.freeSpins > 0) {
            context.freeSpins--;
            context.isFreeSpin = true;
        } else {
            context.isFreeSpin = false;
        }
        List<Integer> ints = new Random().ints(3, 0, 10).boxed().toList();
        context.matrix = getMatrix(ints);
    }
}
