package it.vegas.gameframework.testgame;

import it.vegas.gameframework.contexts.GameContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class TestGameContext extends GameContext {
    public List<List<Character>> matrix = new ArrayList<>();
    public String results = "";
    public long winning = 0L;
    public int freeSpins = 0;
    public boolean isFreeSpin = false;
}
