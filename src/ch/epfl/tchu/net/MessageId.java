package ch.epfl.tchu.net;

import java.util.List;

/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 *Les types de messages que le serveur peut envoyer au clients
 */
public enum MessageId {
    INIT_PLAYERS, RECEIVE_INFO, UPDATE_STATE, SET_INITIAL_TICKETS, CHOOSE_INITIAL_TICKETS,
    NEXT_TURN, CHOOSE_TICKETS, DRAW_SLOT, ROUTE, CARDS, CHOOSE_ADDITIONAL_CARDS,FINAL_WINDOW;
    /**
     * LA liste de toute les Messages
     */
    public final static List<MessageId> ALL = List.of(MessageId.values());
    /**
     * le nombre de messages dans le jeu
     */
    public static int COUNT = ALL.size();

}
