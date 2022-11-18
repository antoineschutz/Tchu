package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * représenteg l'état d'une partie de tCHu
 */
public final class GameState extends PublicGameState {
    private final static int CAR_COUNT_FOR_LAST_TURN =2;
    private final Deck<Ticket> ticketList;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;


    /**
     * Retourne l'état initial d'une partie de tCHu dans laquelle la pioche des billets contient les billets donnés et la pioche des cartes contient les cartes de Constants.ALL_CARDS, sans les 8 (2×4) du dessus, distribuées aux joueurs; ces pioches sont mélangées au moyen du générateur aléatoire donné, qui est aussi utilisé pour choisir au hasard l'identité du premier joueur.
     * @param tickets La pioche de billet
     * @param rng Le générateur aléatoire utilisée pour mélangées les deck et choisir au hasard un joueur
     * @return l'état ainsi crée
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng,int initialCardCount,int initialCarCount){
        Deck<Ticket> ticketDeck= Deck.of(tickets,rng);
        Deck<Card> cardDeck = Deck.of(Constants.ALL_CARDS,rng);
        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        for (PlayerId id: PlayerId.ALL){
            PlayerState p = new PlayerState(SortedBag.of(),cardDeck.topCards(initialCardCount), new ArrayList<>(),initialCarCount);
            cardDeck = cardDeck.withoutTopCards(initialCardCount);
            playerState.put(id,p);
        }

        PlayerId currentPlayerID = PlayerId.values()[rng.nextInt(2)];
        return new GameState(currentPlayerID,null,ticketDeck, CardState.of(cardDeck),playerState);
    }

    private GameState( PlayerId currentPlayerId, PlayerId lastPlayer, Deck<Ticket> ticketList, CardState cardState, Map<PlayerId, PlayerState> playerState) {
        super(ticketList.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.ticketList = ticketList;
        this.cardState = cardState;
        this.playerState = playerState;
    }

    /**
     * redéfinit la méthode hériter de PublicGameState en retournant l'état Complet du joueur d'identité donnée
     * @param playerId l'identité du joueur dont on souhaite obtenir la partie publique
     * @return l'état Complet du joueur d'identité donnée
     */
    @Override
    public PlayerState playerState(PlayerId playerId){
        return playerState.get(playerId);
    }

    /**
     * redéfinit la méthode hériter de PublicGameState en retournant l'état Complet du joueur courant
     * @return l'état Complet du joueur courant
     */
    @Override
    public PlayerState currentPlayerState(){
        return playerState.get(currentPlayerId());
    }

    /**
     * retourne les count billet du sommet de la pioche
     * @param count le nombre de billet a retourner
     *                  (doit être compris entre 0 et la taille de la pioche (inclus))
     * @return les billets au sommet de la pioche
     *
     * @throws IllegalArgumentException si count n'est pas dans les bornes imposée
     */
    public SortedBag<Ticket> topTickets(int count){
        return ticketList.topCards(count);
    }

    /**
     *
     * Retourne un état identique au récepteur mais sans les count billets au sommet de la pioche
     *
     * @param count Le nombre de billet a enlever au sommet de la pioche
     *              (doit être compris entre 0 et la taille de la pioche (inclus))
     *
     * @return l'état ainsi obtenue
     *
     * @throws IllegalArgumentException si count n'est pas dans les bornes imposées
     */
    public GameState withoutTopTickets(int count){
        return new GameState(currentPlayerId(),lastPlayer(),ticketList.withoutTopCards(count),cardState,playerState);
    }

    /**
     * retourne la carte au sommet de la pioche
     *
     * @return la carte au sommet de la pioche
     *
     * @throws IllegalArgumentException si la pioche est vide.
     */
    public Card topCard(){
        return cardState.topDeckCard();
    }

    /**
     *
     * retourne un état identique au récepteur mais sans la carte au sommet de la pioche
     *
     * @return un état identique au récepteur mais sans la carte au sommet de la pioche
     *
     * @throws IllegalArgumentException si la pioche est vide.
     */
    public GameState withoutTopCard(){
        return new GameState(currentPlayerId(),lastPlayer(),ticketList,cardState.withoutTopDeckCard(),playerState);
    }

    /**
     *  retourne un état identique au récepteur mais avec les cartes données ajoutées à la défausse,
     * @param discardedCards Les cartes a ajouté a la défausse
     * @return l'état ainsi obtenue
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        return new GameState(currentPlayerId(),lastPlayer(),ticketList,cardState.withMoreDiscardedCards(discardedCards),playerState);
    }

    /**
     * retourne un état identique au récepteur sauf si la pioche de cartes est vide, auquel cas elle est recréée à partir de la défausse, mélangée au moyen du générateur aléatoire donné.
     * @param rng générateur aléatoire utilisé pour recréée la pioche
     *
     * @return l'état ainsi obtenue
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        if(cardState.isDeckEmpty()){
            return new GameState(currentPlayerId(),lastPlayer(),ticketList,cardState.withDeckRecreatedFromDiscards(rng),playerState);
        } else {
            return this;
        }
    }



    /**
     * retourne un état identique au récepteur mais dans lequel les billets donnés ont été ajoutés à la main du joueur donné.
     * @param playerId l' id du joueur choisissant les tickets
     * @param chosenTickets les tickets choisit par le joueur
     * @return l'état ainsi obtenue
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(!((playerState.get(playerId).ticketCount())>=1));
        Map<PlayerId, PlayerState> playerState1 = new EnumMap<>(playerState);
        playerState1.put(playerId,playerState.get(playerId).withAddedTickets(chosenTickets));
        return new GameState(currentPlayerId(),lastPlayer(),ticketList,cardState,playerState1);
    }

    /**
     * retourne un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets drawnTickets du sommet de la pioche, et choisi de garder ceux contenus dans chosenTicket
     *
     * @param drawnTickets les tickets que le joueur a tiré
     * @param chosenTickets les tickets que le joueur a choisit de gardée
     * @return l'état ainsi obtenue //
     *
     * @throws IllegalArgumentException si l'ensemble des billet gardés n'est pas inclus dans celui des billets tirés
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Map<PlayerId, PlayerState> playerState1 = new EnumMap<>(playerState);
        playerState1.put(currentPlayerId(),playerState.get(currentPlayerId()).withAddedTickets(chosenTickets));
        Deck<Ticket> ticketList1 =ticketList;
        for(int i=0;i<drawnTickets.size();i++){
            ticketList1 = ticketList1.withoutTopCard();
        }
        return new GameState(currentPlayerId(),lastPlayer(),ticketList1,cardState,playerState1);
    }

    /**
     *
     * retourne un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche
     *
     * @param slot l'emplacement de la carte retourné a placé dans la main du joueur
     *
     * @return l'état ainsi obtenue
     *
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes.
     */
    public GameState withDrawnFaceUpCard(int slot){

        Map<PlayerId, PlayerState> playerState1 = new EnumMap<>(playerState);
        playerState1.put(currentPlayerId(),playerState.get(currentPlayerId()).withAddedCard(cardState.faceUpCard(slot)));
        return new GameState(currentPlayerId(),lastPlayer(),ticketList,cardState.withDrawnFaceUpCard(slot),playerState1);
    }

    /**
     * retourne un état identique au récepteur si ce n'est que la carte du sommet de la pioche a été placée dans la main du joueur courant
     * @return l'état ainsi obtenue
     *
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes,
     */
    public GameState withBlindlyDrawnCard(){

        Map<PlayerId, PlayerState> playerState1 = new EnumMap<>(playerState);
        playerState1.put(currentPlayerId(),playerState.get(currentPlayerId()).withAddedCard(cardState.topDeckCard()));
        return new GameState(currentPlayerId(),lastPlayer(),ticketList,cardState.withoutTopDeckCard(),playerState1);
    }

    /**
     * retourne un état identique au récepteur mais dans lequel le joueur courant s'est emparé de la route donnée au moyen des cartes données.
     *
     * @param route La route dont le joueur s'est emparée
     * @param cards Les cartes utilisées par le joueur pour s'emparer de la route
     * @return l'état ainsi obtenue
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){
        Map<PlayerId, PlayerState> playerState1 = new EnumMap<>(playerState);
        playerState1.put(currentPlayerId(),playerState.get(currentPlayerId()).withClaimedRoute(route,cards));
        CardState cardState1 = cardState;
        cardState1 =  cardState1.withMoreDiscardedCards(cards);
        return new GameState(currentPlayerId(),lastPlayer(),ticketList,cardState1,playerState1);
    }



    /**
     * retourne vrai ssi le dernier tour commence
     * @return vrai ssi le dernier tour commence
     */
    public boolean lastTurnBegins(){
        return lastPlayer()==null && currentPlayerState().carCount()<=CAR_COUNT_FOR_LAST_TURN;
    }

    /**
     * Termine le tour du joueur courant
     * @return retourne un état identique au récepteur si ce n'est que le joueur courant est celui qui suit le joueur courant actuel; de plus, si lastTurnBegins retourne vrai, le joueur courant actuel devient le dernier joueur.
     */
    public GameState forNextTurn(){
        if(lastTurnBegins()){
            return new GameState(currentPlayerId().next(),currentPlayerId(),ticketList,cardState,playerState);
        } else {
            return new GameState(currentPlayerId().next(),lastPlayer(),ticketList,cardState,playerState);
        }
    }
}
