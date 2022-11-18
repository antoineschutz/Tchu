package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import java.util.*;

/**
 * @author Antoine Schutz (314412)
 *  * @author Holzer Romain (330560)
 *
 * représente une partie de tCHu
 */
public final class Game {

    /**
     * fait jouer une partie de tCHu aux joueurs donnés
     * @param playerNames Noms des joueurs
     * @param tickets les billets disponibles pour cette partie
     * @param rng le générateur aléatoire
     *
     * @throws IllegalArgumentException si l'une des deux tables associatives a une taille différente de 2
     */
    public static int play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng,int innitialCardCount,int InitialCarCount){
        Preconditions.checkArgument(players.size()==2 && playerNames.size()==2);
        players.forEach((playerId,player)->player.initPlayers(playerId,playerNames));
        GameState gameState = GameState.initial(tickets,rng,innitialCardCount,InitialCarCount);
        Map<PlayerId,Info> infoMap = new HashMap<>();
        players.forEach(((playerId, player) -> infoMap.put(playerId,new Info(playerNames.get(playerId)))));
        recieveInfoShared(players,infoMap.get(gameState.currentPlayerId()).willPlayFirst());


        for(Map.Entry<PlayerId,Player> e : players.entrySet()){
            SortedBag<Ticket> t = gameState.topTickets(Constants.INITIAL_TICKETS_COUT);

            e.getValue().setInitialTicketChoice(t);

            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUT);
        }

        for(Map.Entry<PlayerId,Player> e : players.entrySet()){
            for(Map.Entry<PlayerId,Player> a : players.entrySet()){

                a.getValue().updateState(gameState,gameState.playerState(a.getKey()));
            }
            gameState = gameState.withInitiallyChosenTickets(e.getKey(),e.getValue().chooseInitialTickets());

        }

        for(Map.Entry<PlayerId,Player> e : players.entrySet()) {
            recieveInfoShared(players, infoMap.get(e.getKey()).keptTickets(gameState.playerState(e.getKey()).tickets().size()));
        }

        boolean lastTurnAnnounce =false;
        int afterLastPlayer =0;

        while( afterLastPlayer!=2) {
            if(gameState.lastPlayer()!=null){
                afterLastPlayer++;
            }
            if(gameState.lastPlayer()!=null && !lastTurnAnnounce){
                recieveInfoShared(players,infoMap.get(gameState.lastPlayer()).lastTurnBegins(gameState.playerState(gameState.lastPlayer()).carCount()));
                lastTurnAnnounce=true;
            }


            recieveInfoShared(players,infoMap.get(gameState.currentPlayerId()).canPlay());
            for(Map.Entry<PlayerId,Player> a : players.entrySet()){
                a.getValue().updateState(gameState,gameState.playerState(a.getKey()));
            }
            //
            switch (players.get(gameState.currentPlayerId()).nextTurn()) {
                case DRAW_TICKETS:

                    SortedBag<Ticket> options = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    recieveInfoShared(players, infoMap.get(gameState.currentPlayerId()).drewTickets(options.size()));
                    SortedBag<Ticket> chosit = players.get(gameState.currentPlayerId()).chooseTickets(options);
                    gameState = gameState.withChosenAdditionalTickets(options,chosit);
                    recieveInfoShared(players,infoMap.get(gameState.currentPlayerId()).keptTickets(chosit.size()));
                    break;
                case DRAW_CARDS:

                    for(int i=0;i<2;i++) {
                        if(i==1){
                            for(Map.Entry<PlayerId,Player> a : players.entrySet()){
                                a.getValue().updateState(gameState,gameState.playerState(a.getKey()));
                            }
                        }
                        int slot = players.get(gameState.currentPlayerId()).drawSlot();
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        if (slot == Constants.DECK_SLOT) {
                            gameState = gameState.withBlindlyDrawnCard();
                            recieveInfoShared(players, infoMap.get(gameState.currentPlayerId()).drewBlindCard());
                        } else {
                            Card c = gameState.cardState().faceUpCard(slot);
                            gameState = gameState.withDrawnFaceUpCard(slot);
                            recieveInfoShared(players, infoMap.get(gameState.currentPlayerId()).drewVisibleCard(c));
                        }
                    }
                    break;
                case CLAIM_ROUTE:
                    Route r = players.get(gameState.currentPlayerId()).claimedRoute();
                    SortedBag<Card> initialClaimCards = players.get(gameState.currentPlayerId()).initialClaimCards();
                    if (r.level() == Route.Level.OVERGROUND) {
                        gameState = gameState.withClaimedRoute(r, initialClaimCards);
                        recieveInfoShared(players,infoMap.get(gameState.currentPlayerId()).claimedRoute(r,initialClaimCards));
                    } else {
                        recieveInfoShared(players,infoMap.get(gameState.currentPlayerId()).attemptsTunnelClaim(r,initialClaimCards));
                        SortedBag.Builder<Card> drawnCard = new SortedBag.Builder<>();
                        for(int i=0;i<Constants.ADDITIONAL_TUNNEL_CARDS;i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCard.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }


                        SortedBag<Card> drawnCards = drawnCard.build();
                        gameState = gameState.withMoreDiscardedCards(drawnCards);
                        int additionalclaimCards = r.additionalClaimCardsCount(initialClaimCards,drawnCards);
                        recieveInfoShared(players, infoMap.get(gameState.currentPlayerId()).drewAdditionalCards(drawnCards, additionalclaimCards));
                        if(additionalclaimCards>=1) {

                            List<SortedBag<Card>> possibleAdditionalCards =  gameState.currentPlayerState().possibleAdditionalCards(additionalclaimCards, initialClaimCards);
                            if (!possibleAdditionalCards.isEmpty()) {
                                SortedBag<Card> chosenCard = players.get(gameState.currentPlayerId()).chooseAdditionalCards(possibleAdditionalCards);
                                if(chosenCard.isEmpty()){
                                    recieveInfoShared(players, infoMap.get(gameState.currentPlayerId()).didNotClaimRoute(r));
                                    break;
                                }
                                gameState = gameState.withClaimedRoute(r, initialClaimCards.union(chosenCard));
                                recieveInfoShared(players, infoMap.get(gameState.currentPlayerId()).claimedRoute(r, initialClaimCards.union(chosenCard)));

                            } else {
                                recieveInfoShared(players, infoMap.get(gameState.currentPlayerId()).didNotClaimRoute(r));
                            }
                        } else {
                            gameState = gameState.withClaimedRoute(r,initialClaimCards);

                            recieveInfoShared(players,infoMap.get(gameState.currentPlayerId()).claimedRoute(r, initialClaimCards));
                        }
                    }
                    for(Map.Entry<PlayerId,Player> a : players.entrySet()){
                        a.getValue().updateState(gameState,gameState.playerState(a.getKey()));
                    }
            }
            gameState =  gameState.forNextTurn();

        }


        Map<PlayerId,Integer> pointMap = new HashMap<>();
        players.forEach(((playerId, player) -> pointMap.put(playerId,0)));
        Map<PlayerId,Integer> longestLength = new HashMap<>();
        players.forEach(((playerId, player) -> longestLength.put(playerId,0)));

        //calcul des longest trail
        for(PlayerId pId : players.keySet()){
            longestLength.put(pId,Trail.longest(gameState.playerState(pId).routes()).length());
        }
        //recherche de la longueur du trail le plus long
        int longestLengthMAx = 0;
        for(PlayerId pId : players.keySet()){
            if (longestLength.get(pId)>longestLengthMAx){
                longestLengthMAx = longestLength.get(pId);
            }
        }
        gameState.setLongestALlSize(longestLengthMAx);
        gameState.setLongests(Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes()).getListeDesRoutes(),Trail.longest(gameState.playerState(PlayerId.PLAYER_2).routes()).getListeDesRoutes());


        for(PlayerId pId : players.keySet()){
            int totalpoints =0;
            totalpoints+= gameState.playerState(pId).finalPoints();

            if(longestLength.get(pId)==longestLengthMAx){
                Trail longestTrail = Trail.longest(gameState.playerState(pId).routes());
                recieveInfoShared(players,infoMap.get(pId).getsLongestTrailBonus(longestTrail));
                totalpoints+=Constants.LONGEST_TRAIL_BONUS_POINTS;
            }
            pointMap.put(pId,totalpoints);
        }
        int maxPoints =Integer.MIN_VALUE;
        PlayerId winner =null ;
        for(PlayerId pId : players.keySet()){
            if(pointMap.get(pId)>maxPoints){
                maxPoints = pointMap.get(pId);
                winner = pId;
            }
        }
        boolean exaeqo = false;
        int countMax =Integer.MIN_VALUE;
        int looserpoint=Integer.MIN_VALUE;

        List<String> playerExaeqo = new ArrayList<>();
        for(PlayerId pId : players.keySet()){
            if(pointMap.get(pId)==maxPoints){
                countMax++;
                winner = pId;
                playerExaeqo.add(playerNames.get(pId));
            }
            if(pointMap.get(pId)<maxPoints && pointMap.get(pId)>looserpoint){
                looserpoint = pointMap.get(pId);
            }
        }
        if (countMax >1){
            exaeqo = true ;
        }
        for(Map.Entry<PlayerId,Player> a : players.entrySet()){
            a.getValue().updateState(gameState,gameState.playerState(a.getKey()));
        }
        if (exaeqo){
            recieveInfoShared(players,Info.draw(playerExaeqo,maxPoints));
        } else {
            System.out.println("LE GAGNANT EST : "+playerNames.get(winner)); //todo
            recieveInfoShared(players,infoMap.get(winner).won(maxPoints,looserpoint ));
        }
        for(Player p :players.values()){
           PlayerId myId =  players.get(PlayerId.PLAYER_1)==p ? PlayerId.PLAYER_1 : PlayerId.PLAYER_2 ;


                int myPoint = pointMap.get(myId);
                int otherPoint =  pointMap.get(myId.next());
                int routeClaimed = gameState.playerState(myId).routes().size();


            int maxID =0;
            for (Route r : gameState.playerState(myId).routes() ) {
                if (r.station1().id() > maxID) {
                    maxID = r.station1().id();
                }
                if (r.station2().id() > maxID) {
                    maxID = r.station2().id();
                }
            }
            StationPartition.Builder  b = new StationPartition.Builder(maxID+1);

            for(Route r :  gameState.playerState(myId).routes()){
                b.connect(r.station1(),r.station2());
            }
            StationPartition s = b.build();

            List<Ticket> claimedTickets =new ArrayList<>();
            for(Ticket  t : gameState.playerState(myId).tickets()){

                if(t.points(s)>0){ claimedTickets.add(t);}
            }
                p.createFinalWindow(myPoint,otherPoint,routeClaimed,SortedBag.of(claimedTickets),longestLengthMAx,longestLength.get(myId));


        }

        return PlayerId.ALL.indexOf(winner);

        }
    private static void recieveInfoShared(Map<PlayerId, Player> players , String info){
        players.forEach((playerId,player)->player.receiveInfo(info));
    }
    }




