package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;

/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * représente la partie publique de l'état d'une partie de tCHu
 */
public class PublicGameState {
    private final static int CAN_DRAW_CARD_COND =5;
    private final int ticketsCount;
    private final PublicCardState publicCardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> publicPlayerState;
    private final PlayerId lastPlayer;


    public List<Route> longest1 = new ArrayList<>();
    public List<Route> longest2= new ArrayList<>();
    public int longestALlSize ;

    public void setLongestALlSize(int longestALlSize) {
        this.longestALlSize = longestALlSize;
    }

    public void  setLongests(List<Route> longest1, List<Route> longest2){
        if(longest1==null){
            return;
        }
        if(!longest1.isEmpty()){
            this.longest1=longest1;
        }
        if(!longest2.isEmpty()){
            this.longest2=longest2;
        }

    }
    /**
     *
     * Construit la partie publique de l'état d'une partie de tCHu .
     *
     * @param ticketsCount La taille de la pioche de billets
     *                      (Doit être >=0)
     * @param cardState État public des cartes wagon(locomotive)
     *                      (doit être non null)
     * @param currentPlayerId Le joueur courant
     *                        (doit être non null)
     * @param playerState  État public des joueur
     *                         (doit être non null)
     *
     * @param lastPlayer Identité du dernier joueur
     *
     * @throws IllegalArgumentException Si la taille de la pioche est strictement négative ou si playerState ne continent pas exactement deux paires clef/valeur
     * @throws NullPointerException si l'un des autres argument est nul.
     */
    public  PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer){

        Preconditions.checkArgument(ticketsCount>=0 && Objects.requireNonNull(playerState).size()==2);

        this.ticketsCount = ticketsCount;
        this.publicCardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.publicPlayerState = Objects.requireNonNull(playerState);
        this.lastPlayer = lastPlayer;
    }
    //
    /**
     *
     * @return la taille de la pioche de billets
     */
    public int ticketsCount(){
        return ticketsCount;
    }

    /**
     * retourne vrai ssi il est possible de tirer des billets
     * @return vrai ssi il est possible de tirer des billets
     */
    public boolean canDrawTickets(){
        return (ticketsCount>0);
    }

    /**
     * retourne la partie publique de l'état des cartes wagon/locomotive
     * @return la partie publique de l'état des cartes wagon/locomotive
     */
    public PublicCardState cardState(){
        return publicCardState;
    }

    /**
     * retourne vrai ssi il est possible de tirer des cartes .
     * @return vrai ssi il est possible de tirer des cartes .
     */
    public boolean canDrawCards(){
        return (publicCardState.deckSize()+ publicCardState.discardsSize())>=CAN_DRAW_CARD_COND;
    }

    /**
     * retourne l'identité du joueur actuel
     * @return l'identité du joueur actuel
     */
    public PlayerId currentPlayerId(){
        return currentPlayerId;
    }

    /**
     * retourne la partie public de l'état du joueur d'identité donnée
     *
     * @param playerId l'identité du joueur dont on souhaite obtenir la partie publique
     * @return la partie public de l'état du joueur d'identité donnée
     */
    public PublicPlayerState playerState(PlayerId playerId){
        return publicPlayerState.get(playerId);
    }

    /**
     * retourne la partie public de l'état du joueur courant
     * @return la partie public de l'état du joueur courant
     */
    public PublicPlayerState currentPlayerState(){
        return publicPlayerState.get(currentPlayerId);
    }

    /**
     * retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     * @return la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     */
    public List<Route> claimedRoutes(){ //
        List<Route> l = new ArrayList<>();
        publicPlayerState.forEach((player,state)->l.addAll(state.routes()));
        return l;
    }

    /**
     * retourne l'identité du dernier joueur ou null si elle n'est pas encore connue
     *
     * @return l'identité du dernier joueur ou null si elle n'est pas encore connue
     */
    public PlayerId lastPlayer(){
        return lastPlayer;
    }
}
