package ch.epfl.tchu.game;

import java.util.List;

public enum BotDifficulty {
    FACILE,NORMAL,DIFFICILE;

    public final static List<BotDifficulty> ALL = List.of(BotDifficulty.values());
}
