package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * représente un joueur de tCHu.
 */
public interface Player {


    /**
     * représente les trois types d'actions qu'un joueur de tCHu
     */
    enum TurnKind{
        DRAW_TICKETS,DRAW_CARDS,CLAIM_ROUTE;
        /**
         * La liste de toutes les actions d'un joueur
         */
        public final static List<TurnKind> ALL = List.of(TurnKind.values());
    }


    /**
     * communique au joueur sa propre identité et les noms des différents joueurs
     * @param ownId l'identité du joueur
     * @param playerNames noms des différents joueurs
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * communique au joueur l'information donnée en paramètre
     * @param info information devant être communiquée au joueur au cours de la partie
     */
    void receiveInfo(String info);

    /**
     * appelée lorsque l'état du jeu a changé, informe au joueur la composante publique du nouvel état de la partie et son propre état
     * @param newState le nouveau état de la partie
     * @param ownState L'état du joueur
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * communique au joueur ses cinq billets de départ
     * @param tickets Les cinq tickets de départ
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * retourne les billets retenus par le joueur parmi les cinq présentés au début de la partie
     * @return les billets retenus par le joueur parmi les cinq présentés au début de la partie
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * retourne l'action que veut faire le joueur au cours du tour actuel
     * retourne l'action que veut faire le joueur au cours du tour actuel
     */
    TurnKind nextTurn();

    /**
     * retourne les billets gardés par le joueur en cours de partie parmi ceux proposés en paramètre
     * @param options Les tickets proposés au joueur
     * @return les billets gardés par le joueur en cours de partie parmi ceux proposés en paramètre
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * retourne un nombre entre 0 et 4 si le joueur décide de tirer une des cinq cartes visibles
     * retourne -1 si le joueur tire une carte de la pioche
     * @return un nombre entre 0 et 4 ou -1
     */
    int drawSlot();

    /**
     * retourne la route que le joueur veut tenter de s'emparer
     * @return la route que le joueur veut tenter de s'emparer
     */
    Route claimedRoute();

    /**
     * retourne les cartes que le joueur décide d'utiliser pour s'emparer d'une route
     * @return les cartes que le joueur décide d'utiliser pour s'emparer d'une route
     */
    SortedBag<Card> initialClaimCards();

    /**
     * retourne les cartes que le joueur décide d'utiliser pour s'emparer d'un tunnel parmi les propositions de carte passées en paramètre
     * @param options Les possibilités de cartes présentées au joueur pour s'emparer d'un tunnel
     * @return les cartes que le joueur décide d'utiliser pour s'emparer d'un tunnel parmi les propositions de carte passées en paramètre
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    void createFinalWindow(int myPoint,int otherPoint,int routeClaimed,SortedBag<Ticket> claimedTicket,int longestTrailLenght,int myTrailLength);
}
