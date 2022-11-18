package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.util.*;



/**
 *@author Antoine Schutz (314412)
 *@author Holzer Romain (330560)
 *
 * représente l'état observable d'une partie de tCHu
 */
public class ObservableGameState {

    private PublicGameState gameState;
    private PlayerState playerState;
    public final PlayerId myID ; //todo private

    private final List<ObjectProperty<Card>> faceUpCards =
            createFaceUpCards();
    private final IntegerProperty ticketPourcentage = new  SimpleIntegerProperty(0);
    private final IntegerProperty deckCardPourcentage = new SimpleIntegerProperty(0);
    private final Map<Route,ObjectProperty<PlayerId>> routePossession = createroutePossession();


    private final Map<PlayerId,IntegerProperty> inHandTicket = createPlayIdtoIngerMap();
    private final Map<PlayerId,IntegerProperty> inHandCard = createPlayIdtoIngerMap();
    private final Map<PlayerId,IntegerProperty> wagons = createPlayIdtoIngerMap();
    private final Map<PlayerId,IntegerProperty> points = createPlayIdtoIngerMap();



    private final ObservableList<Ticket> playerTicket = FXCollections.observableArrayList();
    private final Map<Card,IntegerProperty> mapCardProperty = createMapCardProperty();
    private final Map<Route , BooleanProperty> routeClaimability = createRouteClaimability();

    private final Map<Ticket,BooleanProperty> ticketBooleanPropertyMap =createTicketMap();

    public final Map<Station,BooleanProperty> stationsToShow =createStations();

    public final ObservableMap<Station , String> stationColorMap=createColorMap();

    public final ObservableList<Route> longestRoute1=FXCollections.observableArrayList();
    public final ObservableList<Route> longestRoute2=FXCollections.observableArrayList();

    private static ObservableMap<Station , String> createColorMap(){
        ObservableMap<Station , String> ColorMap = FXCollections.observableMap(new HashMap<>());
        for (Station s : ChMap.stations()){
            ColorMap.put(s,"");
        }
        return ColorMap;
    }

    private static Map<Station,BooleanProperty> createStations(){
        Map<Station,BooleanProperty> stations = new HashMap<>();
        for(Station s:ChMap.stations()){
            stations.put(s,new SimpleBooleanProperty(false));
        }
        return stations;
    }

    public void setColor(Station s , String color){
        stationColorMap.put(s,color);
    }
    public void clearColor(){
        for(Station s:ChMap.stations()){
            stationColorMap.put(s,"");
        }
    }
    public void addStations(Station s){

        stationsToShow.get(s).set(true);
    }
    public void clearStations(){
        for(Station s:ChMap.stations()){
            stationsToShow.get(s).set(false);
        }
    }

    private static Map<Ticket,BooleanProperty> createTicketMap(){
        Map<Ticket,BooleanProperty> map = new HashMap<>();
        for(Ticket ticket : ChMap.tickets()){
            map.put(ticket,new SimpleBooleanProperty(false));
        }
        return map;
    }


    private static Map<Route , BooleanProperty> createRouteClaimability(){
        Map<Route , BooleanProperty> map = new HashMap<>();
        for(int i=0;i<ChMap.routes().size();i++){
            map.put(ChMap.routes().get(i),new SimpleBooleanProperty(false));
        }
        return map;
    }

    private static Map<Card , IntegerProperty>  createMapCardProperty(){
        Map<Card,IntegerProperty> map = new HashMap<>();
        for(Card c : Card.ALL){
            map.put(c,new SimpleIntegerProperty());
        }
        return map;
    }





    private static Map<Route,ObjectProperty<PlayerId>> createroutePossession(){
        Map<Route,ObjectProperty<PlayerId>> map = new HashMap<>();
        for(int i=0;i<ChMap.routes().size();i++){
            map.put(ChMap.routes().get(i), new SimpleObjectProperty<>(null));

        }
        return map;
    }


    private static List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> list = new ArrayList<>();
        for(int i=0;i<Constants.FACE_UP_CARDS_COUNT;i++){
            list.add(new SimpleObjectProperty<>(null));
        }
        return list;
    }

    private static Map<PlayerId,IntegerProperty> createPlayIdtoIngerMap(){
        Map<PlayerId,IntegerProperty> map = new HashMap<>();
        map.put(PlayerId.PLAYER_1,new SimpleIntegerProperty(0));
        map.put(PlayerId.PLAYER_2,new SimpleIntegerProperty(0));
        return map;
    }




    /**
     *  construit un état observable d'une partie de tCHu avec l'identité du joueur donnée en initialisant à zéro le gameState et le playerState
     * @param id l'identité du joueur ; de type PlayerId
     */
    public ObservableGameState(PlayerId id){
        gameState = null;
        playerState = null;
        myID = id;
    }

    /**
     * met à jour l'état observable avec un nouvel état de jeu et un nouvel état de joueur placés en argument
     * @param newGameState l'identité de la route ; un String
     * @param ps La premiere gare
     */
    public void setState(PublicGameState newGameState , PlayerState ps){
        gameState =newGameState;
        playerState =ps;
            longestRoute1.setAll(newGameState.longest1);
            longestRoute2.setAll(newGameState.longest2);

        for(int i=0;i<Constants.FACE_UP_CARDS_COUNT;i++){
            Card newCard = newGameState.cardState().faceUpCard(i);
            faceUpCards.get(i).set(newCard);
        }
        List<Ticket> tickets = playerState.tickets().toList();
        playerTicket.setAll(tickets);
        for(Card c : Card.ALL){
            mapCardProperty.get(c).set(playerState.cards().countOf(c));
        }
        boolean canClaimRoute;
        if(myID==newGameState.currentPlayerId()) {
            Set<List<Station>> pairList = new HashSet<>();
            for (Route r : newGameState.claimedRoutes()) {
                pairList.add(r.stations());
            }
            for (Route r : ChMap.routes()) {
                if (!pairList.contains(r.stations())) {
                    canClaimRoute = playerState.canClaimRoute(r);
                } else {
                    canClaimRoute=false;
                }
                routeClaimability.get(r).set(canClaimRoute);
            }
        }




        for(PlayerId pid : PlayerId.ALL){
            PublicPlayerState state =  newGameState.playerState(pid);
            inHandTicket.get(pid).set(state.ticketCount());
            inHandCard.get(pid).set(state.cardCount());
            wagons.get(pid).set(state.carCount());
            points.get(pid).set(state.claimPoints());
        }


        ticketPourcentage.set((int)((newGameState.ticketsCount() /(double) ChMap.tickets().size())*100));
        deckCardPourcentage.set((int)((newGameState.cardState().deckSize() /(double) Constants.ALL_CARDS.size())*100));


        for(Route r : ChMap.routes()){
            if(newGameState.claimedRoutes().contains(r)){
                if(playerState.routes().contains(r)){
                    routePossession.get(r).set(myID);
                } else{
                    routePossession.get(r).set(myID.next());
                }
            } else{
                routePossession.get(r).set(null);
            }
        }

        int maxID =0;
        for (Route r : newGameState.playerState(myID).routes() ) {
            if (r.station1().id() > maxID) {
                maxID = r.station1().id();
            }
            if (r.station2().id() > maxID) {
                maxID = r.station2().id();
            }
        }
        StationPartition.Builder  b = new StationPartition.Builder(maxID+1);

        for(Route r :  newGameState.playerState(myID).routes()){
            b.connect(r.station1(),r.station2());
        }
        StationPartition s = b.build();


        for(Ticket  t : ps.tickets()){
            ticketBooleanPropertyMap.get(t).set(t.points(s)>0 ? true :false);
        }


    }
    /**
     * retourne la propriété correspondant à l'emplacement donné en paramètre
     * @param slot l'emplacement de la propriété voulue
     * @return la propriété correspondant à l'emplacement donné en paramètre
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }


    /**
     * retourne la propriété correspondant à la route donnée en paramètre
     * @param r la route de la propriété voulue
     * @return la propriété correspondant à la route donnée en paramètre
     */
    public ReadOnlyObjectProperty<PlayerId> routePossession(Route r){
        return routePossession.get(r);
    }


    /**
     * retourne la propriété liée au nombre de billets du joueur
     * @return la propriété liée au nombre de billets du joueur
     */
    public ReadOnlyIntegerProperty ticketPourcentage(){
        return ticketPourcentage;
    }


    /**
     * retourne la propriété liée au nombre de cartes du joueur
     * @return la propriété liée au nombre de cartes du joueur
     */
    public ReadOnlyIntegerProperty deckCardPourcentage(){
        return deckCardPourcentage;
    }


    /**
     * retourne la propriété liée au nombre de billets correspondant au joueur passé en paramètre
     * @param playerId l'identité du joueur courant
     * @return la propriété liée au nombre de billets correspondant au joueur passé en paramètre
     */
    public ReadOnlyIntegerProperty inHandTicket(PlayerId playerId){
        return inHandTicket.get(playerId);
    }


    /**
     * retourne la propriété liée au nombre de cartes correspondant au joueur passé en paramètre
     * @param playerId l'identité du joueur courant
     * @return la propriété liée au nombre de cartes correspondant au joueur passé en paramètre
     */
    public ReadOnlyIntegerProperty inHandCard(PlayerId playerId){
        return inHandCard.get(playerId);
    }


    /**
     * retourne la propriété liée au nombre de wagons correspondant au joueur passé en paramètre
     * @param playerId l'identité du joueur courant
     * @return la propriété liée au nombre de wagons correspondant au joueur passé paramètre
     */
    public ReadOnlyIntegerProperty wagons(PlayerId playerId){
        return wagons.get(playerId);
    }

    /**
     * retourne la propriété liée au nombre de points correspondant au joueur passé en paramètre
     * @param playerId l'identité du joueur courant
     * @return la propriété liée au nombre de points correspondant au joueur passé en paramètre
     */
    public ReadOnlyIntegerProperty points(PlayerId playerId){
        return points.get(playerId);
    }

    /**
     * retourne la propriété contenant la liste des billets du joueur
     * @return la propriété contenant la liste des billets du joueur
     */
    public ObservableList<Ticket> playerTicket(){
        return FXCollections.unmodifiableObservableList(playerTicket);
    }

    /**
     * retourne la propriété contenant le nombre de cartes du type passé en paramètre que le joueur a en main
     * @param c le type de carte auquel on veut savoir son nombre dans la main du joueur
     * @return la propriété contenant le nombre de cartes du type passé en paramètre que le joueur a en main
     */
    public ReadOnlyIntegerProperty CardProperty(Card c){
        return mapCardProperty.get(c);
    }

    /**
     * retourne vrai ssi le joueur peut s'emparer de la route passée en paramètre
     * @param r la route à laquelle on veut savoir si le joueur peut s'en emparer ou pas
     * @return vrai ssi le joueur peut s'emparer de la route passée en paramètre
     */
    public ReadOnlyBooleanProperty routeClaimability(Route r){
        return routeClaimability.get(r);
    }


    /**
     * retourne vrai ssi il est possible de tirer des billets
     * @return vrai ssi il est possible de tirer des billets
     */
    public boolean canDrawTickets(){
        return gameState.canDrawTickets();
    }

    /**
     * retourne vrai ssi il est possible de tirer des cartes
     * @return vrai ssi il est possible de tirer des cartes
     */
    public boolean canDrawCards(){
        return gameState.canDrawCards();
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour prendre possession de la route donnée
     * @param r la route qui veut être prise en possession
     * @return la liste ainsi crée
     */
    public List<SortedBag<Card>> possibleClaimCards(Route r){
        return playerState.possibleClaimCards(r);
    }
    public ReadOnlyBooleanProperty ticketBooleanProperty(Ticket t){
        return ticketBooleanPropertyMap.get(t);
    }


}
