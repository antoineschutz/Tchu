package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antoine Schutz (314412)
 *  * @author Holzer Romain (330560)
 *
 *  Permet de generer les textes décrivant le déroulement de la parties
 */
public final class Info {
    private final String playerName;

    /**
     * Construit un générateur de messages liées au joueur
     * @param playerName Le joueur
     */
    public Info(String playerName){
        this.playerName=playerName;
    }

    /**
     * retourne le nom de la carte donnée avec gestion du singulier pluriel en fonction de la valeur de count ( singulier si |count|==1)
     * @param card La carte a afficher
     * @param count Le nombre de fois que la carte apparais ( pour singulier pluriel
     * @return Le nom de la carte
     */
    public static String cardName(Card card, int count){
       String plural = StringsFr.plural(count);
       switch (card){
           case WHITE:
               return StringsFr.WHITE_CARD +plural;
           case BLACK:
               return StringsFr.BLACK_CARD+plural;
           case GREEN:
               return StringsFr.GREEN_CARD+plural;
           case RED:
               return StringsFr.RED_CARD+plural;
           case BLUE:
               return StringsFr.BLUE_CARD+plural;
           case ORANGE:
               return StringsFr.ORANGE_CARD+plural;
           case VIOLET:
               return StringsFr.VIOLET_CARD+plural;
           case YELLOW:
               return StringsFr.YELLOW_CARD+plural;
           case LOCOMOTIVE:
               return StringsFr.LOCOMOTIVE_CARD+plural;

       }
       return "";
    }

    /**
     * retourne le message déclarant que les joueurs sont ex æquo en déclarant leur score
     * @param playerNames Le nom des joueurs
     * @param points Les points qu'ils ont obtenues
     * @return Le message les déclarant ex æquo
     */
    public static String draw(List<String> playerNames, int points){
        return String.format(StringsFr.DRAW,playerNames.get(0)+StringsFr.AND_SEPARATOR+playerNames.get(1),points);
    }

    /**
     * retourne le message déclarant que le joueur jouera en premier
     * @return le message déclarant que le joueur jouera en premier
     */
    public String willPlayFirst(){
        return String.format(StringsFr.WILL_PLAY_FIRST,playerName);
    }

    /**
     * retourne le message déclarant que le joueur a gardé le nombre de billets donné
     * @param count Le nombre de billet que le joueur a gardé
     * @return le message déclarant que le joueur a gardé le nombre de billets donné
     */
    public String keptTickets(int count){
        return String.format(StringsFr.KEPT_N_TICKETS,playerName,count,StringsFr.plural(count));
    }

    /**
     * Retourne  le message déclarant que le joueur peut jouer
     * @return le message déclarant que le joueur peut jouer
     */
    public String canPlay(){
        return String.format(StringsFr.CAN_PLAY,playerName);
    }

    /**
     * Retourne le message déclarant que le joueur a tiré le nombre donné de billets
     * @param count Le nombre de ticket tiré par le joueur
     * @return e message déclarant que le joueur a tiré le nombre donné de billets
     */
    public String drewTickets(int count){
        return String.format(StringsFr.DREW_TICKETS,playerName,count, StringsFr.plural(count));
    }

    /**
     * retourne le message déclarant que le joueur a tiré une carte «à l'aveugle»
     * @return le message déclarant que le joueur a tiré une carte «à l'aveugle»
     */
    public String drewBlindCard(){
        return String.format(StringsFr.DREW_BLIND_CARD,playerName);
    }

    /**
     *retourne le message déclarant que le joueur a tiré la carte disposée face visible donnée
     * @param card La carte tiré par le joueur
     * @return le message déclarant que le joueur a tiré la carte disposée face visible donnée
     */
    public String drewVisibleCard(Card card){
        return String.format(StringsFr.DREW_VISIBLE_CARD,playerName,cardName(card,1));
    }

    /**
     * retourne le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     * @param route La route
     * @param cards Les cartes utilisées
     * @return le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     */
    public String claimedRoute(Route route, SortedBag<Card> cards){

        return String.format(StringsFr.CLAIMED_ROUTE,playerName,RoadName(route),cardSetName(cards));
    }

    /**
     * retourne le message déclarant que le joueur désire s'emparer de la route en tunnel donnée en utilisant initialement les cartes données
     * @param route La route
     * @param initialCards Les cartes initialement données
     * @return le message déclarant que le joueur désire s'emparer de la route en tunnel donnée en utilisant initialement les cartes données
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM,playerName,RoadName(route),cardSetName(initialCards));
    }

    /**
     * Retourne le message déclarant que le joueur a tiré les trois cartes additionnelles données, et qu'elles impliquent un coût additionnel du nombre de cartes donné
     * @param drawnCards Les 3 cartes tirées
     * @param additionalCost Le cout additionnel
     *
     * @return le message déclarant que le joueur a tiré les trois cartes additionnelles données, et qu'elles impliquent un coût additionnel du nombre de cartes donné
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        String firstPart = String.format(StringsFr.ADDITIONAL_CARDS_ARE,cardSetName(drawnCards));
        String secondPart = (additionalCost==0)?StringsFr.NO_ADDITIONAL_COST:String.format(StringsFr.SOME_ADDITIONAL_COST,additionalCost,StringsFr.plural(additionalCost));
        return firstPart+secondPart;
    }

    /**
     * retourne le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné
     * @param route La route
     * @return  le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné
     */
    public String didNotClaimRoute(Route route){
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE,playerName,RoadName(route));
    }

    /**
     *  retourne le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égale à 2) de wagons, et que le dernier tour commence donc
     * @param carCount Le nombre de wagon restant que le joueur possède
     * @return  le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égale à 2) de wagons, et que le dernier tour commence donc
     */
    public String lastTurnBegins(int carCount){
        return String.format(StringsFr.LAST_TURN_BEGINS,playerName,carCount,StringsFr.plural(carCount));
    }

    /**
     *  Retourne le message déclarant que le joueur obtient le bonus de fin de partie grâce au chemin donné, qui est le plus long, ou l'un des plus longs
     * @param longestTrail Un des plus long chemins
     * @return le message déclarant que le joueur obtient le bonus de fin de partie grâce au chemin donné, qui est le plus long, ou l'un des plus longs
     */
    public String getsLongestTrailBonus(Trail longestTrail){
        return String.format(StringsFr.GETS_BONUS,playerName,trailName(longestTrail));
    }

    /**
     * Retourne le message déclarant que le joueur remporte la partie avec le nombre de points donnés, son adversaire n'en ayant obtenu que loserPoints.
     * @param points Le nombre de points du gagnant
     * @param loserPoints Le nombre de points du perdant
     * @return le message déclarant que le joueur remporte la partie avec le nombre de points donnés, son adversaire n'en ayant obtenu que loserPoints.
     */
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS,playerName,points,StringsFr.plural(points),loserPoints,StringsFr.plural(loserPoints));
    }

    private static String RoadName(Route route){
        return route.station1()+StringsFr.EN_DASH_SEPARATOR+route.station2();
    }

    private static String trailName(Trail t){
        return t.station1().name()+StringsFr.EN_DASH_SEPARATOR+t.station2().name();
    }

    /**
     *
     * Retourne une chaine de caractère correspondant a la representation textuelle du sortedbag de carte passé en paramètre
     *
     * @param cards l'ensemble de carte
     * @return la representation textuelle du sortedbag
     */
    public static String cardSetName(SortedBag<Card> cards ){
        StringBuilder s = new StringBuilder();
            ArrayList<Card> order= new ArrayList<>();

            for(Card c : Card.ALL){
                if (!order.contains(c)&&cards.contains(c)){
                    order.add(c);
                }
            }
            for(int i=0;i<order.size();i++){

                    int n = cards.countOf(order.get(i));
                    s.append(n).append(" ").append(cardName(order.get(i), n));
                    if(order.size()>=3 && i<order.size()-2){
                        s.append(", ");
                    }
                    if(order.size()>=2 && i==order.size()-2){
                        s.append(StringsFr.AND_SEPARATOR);
                    }

            }


        return s.toString();
    }
}
