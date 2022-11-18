package ch.epfl.tchu.game;

import java.util.List;
import ch.epfl.tchu.Preconditions;

/**
 *  @author Antoine Schutz (314412)
 *  * @author Holzer Romain (330560)
 *
 *  repésente une partie de l'état du joueur
 */

public class PublicPlayerState {

    private final List<Route> routes;
    private final int ticketCount;
    private final int cardCount;
    private final int carCount;
    private int claimPoints;
    public int initialCarCount;

    /**
     *   Construit un état public des cartes

     * @param routes la liste des routes que possède le joueur
     * @param ticketCount le nombre de tickets que possède le joueur
     *                    (doit être positif)
     * @param cardCount le nombre de cartes que possède le joueur
     * 	 *                (doit être positif)
     * @throws IllegalArgumentException si ticketCount et/ou cardCount sont négatif
     *
     */

    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes,int initialCarCount) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        this.routes = List.copyOf(routes);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.initialCarCount = initialCarCount;

        int carUsed = 0;
        for (Route r : routes) {
            carUsed += r.length();
            claimPoints += r.claimPoints();
        }
        carCount = this.initialCarCount - carUsed;
    }

    /**
     * Retourne le nombre de tickets du joueur
     * @return Le nombre de tickets du joueur
     */
    public int ticketCount() {return ticketCount;}

    /**
     * Retourne le nombre de cartes du joueur
     * @return Le nombre de cartes du joueur
     */
    public int cardCount() {
        return cardCount;}

    /**
     * Retourne la liste des routes du joueur
     * @return la liste des routes du joueur
     */
    public List<Route> routes() {return List.copyOf(routes);}

    /**
     * Retourne le nombre de wagons restant au joueur
     * @return le nombre de wagons restant au joueur
     */
    public int carCount() {return carCount;}

    /**
     * Retourne le nombre de points de construction obtenus par le joueur
     * @return le nombre de points de construction obtenus par le joueur
     */
    public int claimPoints() {return claimPoints;}
}