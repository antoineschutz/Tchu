package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.*;

public class BotPlayer implements Player{


        private final Random rng;
    private final BotDifficulty difficulty;
        private PlayerId myId;

        private PlayerState ownState;
        private PublicGameState gameState;
        private SortedBag<Ticket> tickets;
        private final List<Ticket> ticketsValide = new ArrayList<>();

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;

        private final Set<Card> wishedCard = new HashSet<>();
        private final List<Route> wishedRoutes = wishedRoutes();

        private final boolean debugMode;




        private int turnCounter=0;
    private static final int TURN_LIMIT = 1000;

    private static List<Route> wishedRoutes(){
        List<Route> whished = new ArrayList<>();
        List<Integer> wishedIndex = List.of(1,3,4,16,17,19,34,35,42,43,44,45,46,47,53,54,55,56,60,65,67,68,76,81,83);
        for(Integer i : wishedIndex){
            whished.add(ChMap.routes().get(i));
        }
        return whished;
    }

        public BotPlayer(long randomSeed, BotDifficulty difficulty, boolean debugMode) {
            this.rng = new Random(randomSeed);
            // Toutes les routes de la carte
            this.difficulty = difficulty;
            this.debugMode = debugMode;
        }
        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames){
            myId=ownId;

        }

        @Override
        public void receiveInfo(String info) {
            if (debugMode) System.out.println("Nouvelle info reçu " + info);
        }
        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;

            int maxID =0;
            for (Route r : newState.playerState(myId).routes() ) {
                if (r.station1().id() > maxID) {
                    maxID = r.station1().id();
                }
                if (r.station2().id() > maxID) {
                    maxID = r.station2().id();
                }
            }
            StationPartition.Builder  b = new StationPartition.Builder(maxID+1);

            for(Route r :   newState.playerState(myId).routes()){
                b.connect(r.station1(),r.station2());
            }
            StationPartition s = b.build();


            for(Ticket  t : tickets){

                if(t.points(s)>0){
                   if(!ticketsValide.contains(t)){
                       ticketsValide.add(t);
                   }
                }
            }


        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        if(debugMode){
            System.out.println("appel de setInitialTicketChoice " +tickets);
        }

            this.tickets=tickets;
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            if(debugMode){
                System.out.println("je suis dans chooseInitialTicket");
            }
            return tickets;
        }

        @Override
        public TurnKind nextTurn() {
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");
            return switch (difficulty) {
                case FACILE -> nextTurnFacile();
                case NORMAL -> nextTurnNormal();
                case DIFFICILE -> nextTurnDifficile();
            };


        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        if(debugMode) {
            System.out.println("appel de chooseTickets , je prend tout");
        }
            return options;
        }

        @Override
        public int drawSlot() {

            return switch (difficulty) {
                case FACILE -> drawSlotFacile();
                case NORMAL -> drawSlotNormal();
                case DIFFICILE -> drawSlotDifficile(wishedCard);
            };

        }

        @Override
        public Route claimedRoute() {
            return routeToClaim;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return initialClaimCards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return options.get(0);
        }

    @Override
    public void createFinalWindow(int myPoint,int otherPoint,int routeClaimed,SortedBag<Ticket> claimedTicket,int longestTrailLenght,int myTrailLength) {
        //ne fait rien
    }


    private TurnKind nextTurnFacile(){
            //choisit une action au hasard
            int rand = rng.nextInt(3);
            TurnKind turnKind = TurnKind.ALL.get(rand);
            switch (turnKind){
                // si je tombe sur claim route , je fait la list des routes prenables , si elles est non vide j'en prend une , sinon pioche de carte
                case CLAIM_ROUTE:
                    List<Route> claimableRoutes =ClaimableRoutes(gameState,ownState);
                    if (claimableRoutes.isEmpty()) {
                        return TurnKind.DRAW_CARDS;
                    } else {
                        int routeIndex = rng.nextInt(claimableRoutes.size());
                        Route route = claimableRoutes.get(routeIndex);
                        List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                        routeToClaim = route;
                        initialClaimCards = cards.get(0);
                        return TurnKind.CLAIM_ROUTE;
                    }

                case DRAW_TICKETS:
                    if(gameState.ticketsCount()>0) {
                        return TurnKind.DRAW_TICKETS;
                    }else {
                        return TurnKind.DRAW_CARDS;
                    }
                case DRAW_CARDS:
                    return TurnKind.DRAW_CARDS;
                default:
                    throw new Error();
            }




        }
        private TurnKind nextTurnNormal(){

            // je prend le maximum de route possible sans m'occuper des Tickets
                    List<Route> claimableRoutes =ClaimableRoutes(gameState,ownState);
                    if (claimableRoutes.isEmpty()) {
                        return TurnKind.DRAW_CARDS;
                    } else {
                        int routeIndex = rng.nextInt(claimableRoutes.size());
                        Route route = claimableRoutes.get(routeIndex);
                        List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                        routeToClaim = route;
                        initialClaimCards = cards.get(0);
                        return TurnKind.CLAIM_ROUTE;
                    }



        }



        private TurnKind nextTurnDifficile(){
            if(new HashSet<>(ownState.routes()).containsAll(wishedRoutes)){
                if(gameState.ticketsCount()>0) {
                    return TurnKind.DRAW_TICKETS;
                }else {
                    return TurnKind.DRAW_CARDS;
                }
            } else {
                List<Route> claimableRoutes = ClaimableRoutes(gameState, ownState);
                claimableRoutes.retainAll(wishedRoutes);
                if (claimableRoutes.isEmpty()) {
                    if(gameState.canDrawCards()) {
                        for (Route r : wishedRoutes) {
                            wishedCard.add(Card.of(r.color()));
                        }
                        return TurnKind.DRAW_CARDS;
                    } else if(gameState.canDrawTickets()){
                        return TurnKind.DRAW_TICKETS;
                    } else{
                        throw new Error("ERREUR , PASSER LE TOUR");
                    }
                } else {

                    int routeIndex = rng.nextInt(claimableRoutes.size());
                    Route route = claimableRoutes.get(routeIndex);
                    List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                    routeToClaim = route;
                    initialClaimCards = cards.get(0);
                    return TurnKind.CLAIM_ROUTE;
                }
            }

        }

    private int drawSlotFacile(){
        int slot = rng.nextInt(6)-1;
        if(debugMode){
            System.out.println("appel de DrawSlot , je choisit un nombre au hasard : " + slot);
        }
        return slot;
    }
    private int drawSlotNormal(){
        int slot = rng.nextInt(6)-1;
        if(debugMode){
            System.out.println("appel de DrawSlot , je choisit un nombre au hasard : " + slot);
        }

        return slot;
    }
     private int drawSlotDifficile(Set<Card> carteSouhaite){
            for(Card c : gameState.cardState().faceUpCards()){
                if(carteSouhaite.contains(c)){
                    wishedCard.remove(c);
                    return gameState.cardState().faceUpCards().indexOf(c);

                }
            }
         return -1;
        }

        private List<Route> ClaimableRoutes(PublicGameState newState, PlayerState ownState){
            boolean canClaimRoute;
            ArrayList<Route> Claimable = new ArrayList<>();
                Set<List<Station>> pairList = new HashSet<>();
                for (Route r : newState.claimedRoutes()) {
                    pairList.add(r.stations());
                }
                for (Route r : ChMap.routes()) {
                    if (!pairList.contains(r.stations())) {
                        canClaimRoute = ownState.canClaimRoute(r);
                    } else {
                        canClaimRoute=false;
                    }
                    if(canClaimRoute) {
                        Claimable.add(r);
                    }
                }
                return Claimable;

        }

}

