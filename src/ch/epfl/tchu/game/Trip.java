package ch.epfl.tchu.game;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ch.epfl.tchu.Preconditions;
/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * Le Concepts de trajet dans le jeu
 */
public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Construit un nouveau trajet entre les deux gares donnée valant le nombre de points données
     * @param from La gare de départ
     *             (non nulle)
     * @param to La gare d'arrivée
     *             (non nulle)
     * @param points  Le nombre de points du trajet
     *                (strictement positif)
     *
     * @throws NullPointerException si une des deux gares et nulles
     * @throws IllegalArgumentException Si le nombre de points n'est pas strictement positif
     */
    public Trip(Station from,Station to,int points){
        Preconditions.checkArgument(points>0);
        this.from=Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Retourne la liste de tous les trajets possibles allant d'une des gare de la premiere liste a l'une  des gares de la seconde
     *
     *
     * @param from Liste des gares de depart
     *             (non vide)
     * @param to Liste des gares d'arrivées
     *              (non vide)
     * @param points Le nombre de points des trajets créer
     *               (strictement positif)
     * @return La liste de tous les trajets possibles
     * @throws IllegalArgumentException si une des listes est vide ou si le nombre de points n'est pas strictement positif
     */
    public static List<Trip> all(List<Station> from,List<Station> to , int points){
        Preconditions.checkArgument(points>0);
        if (from==null || to == null){ throw new NullPointerException();}
        List<Trip> all= new ArrayList<>();
            for (Station start : from){
                for(Station arrival :to){
                    all.add(new Trip(start,arrival,points));
                }
        }
            return all;
    }

    /**
     * Retourne la station de départ du trajet
     * @return la station de départ du trajet
     *
     */
    public Station from(){return from;}

    /**
     * Retourne la station d'arrivé du trajet
     * @return la station d'arrivé du trajet
     */
    public Station to(){return to;}

    /**
     * Retourne le nombre de points du trajet
     * @return le nombre de points du trajet
     */
    public int points(){ return points;}
    /**
     * Retourne le nombre de points du trajet pour la connectivité donnée
     * @param connectivity La connectivité
     *
     * @return le nombre de points du trajet
     */
    public int points(StationConnectivity connectivity){
        return connectivity.connected(from,to)?points:-points;
    }
}
