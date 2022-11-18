package ch.epfl.tchu.game;
import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;



/**
 *  @author Antoine Schutz (314412)
 *  @author Holzer Romain (330560)
 *
 *  repésente l'état du joueur
 */

public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;
    public final int initialCardCount;


    /**
     *   Construit un état public du joueur

     * @param tickets un multi-ensemble trié composé des tickets du joueur
     * @param cards un multi-ensemble trié composé des cartes du joueur
     * @param routes la liste des routes que possède le joueur
     *
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes,int initialCardCount) {
        super(  tickets.size(),cards.size(),routes,initialCardCount);

        this.tickets = tickets;
        this.cards = cards;
        this.routes = List.copyOf(routes);
        this.initialCardCount =initialCardCount;

    }



    /**
     *   retourne un état initial du joueur avec les cartes initiales données en paramètre et sans tickets ni routes
     * @param initialCards un multi-ensemble trié composé des cartes initiales données au joueur
     * @return l'état du joueur ainsi créé
     *
     * @throws IllegalArgumentException si la taille du multi-ensemble (le nombre de cartes initiales) n'est pas égal à 4
     **/
      /*
    public static PlayerState initial(SortedBag<Card> initialCards) {
        SortedBag<Ticket> t = SortedBag.of();
        List<Route> r =new ArrayList<>();
        return new PlayerState(SortedBag.of(t), initialCards,r);

    }
    */
    /**
     * Retourne le multi-ensemble composé de tous les tickets du joueur
     * @return le multi-ensemble composé de tous les tickets du joueur
     */
    public SortedBag<Ticket> tickets() {return tickets;}

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {

        return new PlayerState(tickets.union(newTickets), cards, routes,initialCardCount);

    }

    /**
     * Retourne le multi-ensemble composé de toutes les cartes du joueur
     * @return le multi-ensemble composé de toutes les cartes du joueur
     */
    public SortedBag<Card> cards() {return cards;}

    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets, cards.union(SortedBag.of(card)), routes,initialCardCount);
    }


    /**
     * Retourne vrai ssi le joueur peut s'emparer de la route donnée, c-à-d s'il lui reste assez de wagons et s'il possède les cartes nécessaires
     * @return vrai ssi le joueur peut s'emparer de la route donnée, c-à-d s'il lui reste assez de wagons et s'il possède les cartes nécessaires
     */
    public boolean canClaimRoute(Route route) {
        if (carCount() >= route.length()){
            return possibleClaimCards(route).size()>0;
        } else {
            return false;
        }

    }
    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour prendre possession de la route donnée
     * @param route la route qui veut être prise en possession
     * @return la liste ainsi crée
     *
     * @throws IllegalArgumentException si le joueur n'a pas assez de wagons pour s'emparer de la route,*
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount() >= route.length());
        List<SortedBag<Card>> liste = new ArrayList<>();
        for (SortedBag<Card> w : route.possibleClaimCards()) {
            if (cards.contains(w)) {
                liste.add(w);
            }

        }
        return liste;
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour s'emparer d'un tunnel (triée par ordre croissant du nombre de cartes locomotives)
     * @param additionalCardsCount le nombre de cartes additionnelles que le joueur doit jouer pour s'emparer du tunnel
     * @param initialCards l'ensemble des cartes posées initialement pour s'emparer du tunnel

     * @return la liste ainsi crée
     *
     * @throws IllegalArgumentException si le nombre de cartes additionnelles n'est pas compris entre 1 et 3 (inclus), si l'ensemble des cartes initiales est vide ou contient plus de 2 types de cartes différents, ou si l'ensemble des cartes tirées ne contient pas exactement 3 cartes
     *
     */

    public  List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards) {
        int nbre = 0;
        for (Card w : Card.ALL) {
            if (initialCards.contains(w)) {
                nbre++;
            }
        }

        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= ADDITIONAL_TUNNEL_CARDS && !(initialCards.isEmpty()) && nbre <= 2);

        SortedBag<Card> potentielCards = SortedBag.of();
        for (Card w : cards.difference(initialCards)) {
            if (initialCards.contains(w)|| w.equals(Card.LOCOMOTIVE)) {
                potentielCards = potentielCards.union(SortedBag.of(w));
            }
        }


        List<SortedBag<Card>> options = new ArrayList<>();
        if(potentielCards.size()<additionalCardsCount){
            return options;
        }
        Set<SortedBag<Card>> ensemble = potentielCards.subsetsOfSize(additionalCardsCount);
        options.addAll(ensemble);
        options.sort(
                Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return options;

    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur s'est de plus emparé de la route donnée au moyen des cartes données,
     * @param route la route que le joueur vient de s'emparer et qui doit être rajoutée à la liste des routes du joueur
     * @param claimCards l'ensemble des cartes que le joueur a utilisé pour s'emparer de la nouvelle route et qui doit donc être retiré des cartes en main du joueur

     * @return le nouvel état du joueur avec la nouvelle route et les cartes en moins
     *
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> routes = new ArrayList<>(routes());
        routes.add(route);
        SortedBag<Card> cardsFinale = cards.difference(claimCards);
        return new PlayerState(tickets, cardsFinale, routes,initialCardCount);

    }

    /**
     * retourne le nombre de points obtenue par le joueur grâce a ses billets
     * @return le nombre de points obtenue par le joueur grâce a ses billets
     */
    public int ticketPoints() {
        int maxID =0;
        for (Route r : routes ) {
            if (r.station1().id() > maxID) {
                maxID = r.station1().id();
            }
            if (r.station2().id() > maxID) {
                maxID = r.station2().id();
            }
        }
        StationPartition.Builder  b = new StationPartition.Builder(maxID+1);

        for(Route r : routes){
            b.connect(r.station1(),r.station2());
        }
        StationPartition s = b.build();
        int totalPoints =0;
        for(Ticket  t : tickets){
            totalPoints+=t.points(s);
        }
        return  totalPoints;
    }


    /**
     * Retourne le nombre de points obtenus par le joueur jusqu'à présent (la somme des points obtenus grâce aux tickets et de ceux obtenus grâce aux constructions de routes )
     * @return le nombre de points obtenus par le joueur (ticketPoints() + claimPoints())
     *
     */
    public int finalPoints() {return ticketPoints() + claimPoints();}



}
