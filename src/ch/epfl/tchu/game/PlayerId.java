package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 *Les joueurs du jeu
 */
public enum PlayerId {
    PLAYER_1,PLAYER_2;

    /**
     * LA liste de toute les Joueur du jeu
     */
    public final static List<PlayerId> ALL = List.of(PlayerId.values());
    /**
     * le nombre de Joueur dans le jeu
     */
    public static int COUNT= ALL.size();


    /**
     * Retourne l'identité du joueur qui suit celui auquel on l'applique
     * @return l'identité du joueur qui suit celui auquel on l'applique
     */
    public PlayerId next(){

        if (this==PLAYER_1){
            return PLAYER_2;
        }
        return PLAYER_1;
    }
}
