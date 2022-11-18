package ch.epfl.tchu.game;
import ch.epfl.tchu.Preconditions;


import java.util.*;

/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * Les Billets  du jeu
 */
public final class Ticket implements Comparable<Ticket> {
    private final List<Trip> trips;
    private final String text;
    private final String startName;




    /**
     * Construit un billet constitué de la liste des trajet donnée
     *
     * @param trips
     *      La liste de trajet
     * @throws IllegalArgumentException
     *          Si La liste est vide
     *          Ou si les gares de départ des trajets non pas le même nom
     */
    public Ticket(List<Trip> trips){
        Preconditions.checkArgument(trips.size()!=0);
        this.startName=trips.get(0).from().name();
        for (Trip trip : trips){
            if (!trip.from().name().equals(startName)) throw new IllegalArgumentException();
        }
        this.trips=List.copyOf(trips);
        this.text = computeText();

    }


    /**
     * Construit un billet constitué d'un unique trajet partant de la gare
     *
     * @param from La gare de départ
     * @param to La gare d'arrivée
     * @param points Le nombre de points du ticket
     */
    public Ticket(Station from,Station to,int points){
        this(List.of(new Trip(from,to,points)));
    }

    /**
     * retourne le nombre de points que vaut le billet
     * @param connectivity
     *          La connectivité du joueur possédant le billet
     * @return Le nombre de points que vaut le billet
     */
    public int points(StationConnectivity connectivity){

        int max =0;
        int min = trips.get(0).points(connectivity); // on cherche l'entier strictement négatif le plus grand possible
        boolean connected = false;
        for (Trip trip : trips){

            int current = trip.points(connectivity);
            if (current <0 && current > min){ min = current;}
            if (current>max){
                max =current;
                connected =true;
            }

        }
        if (connected){ return max;}
        return min;
    }

    /**
     * retourne la representation textuelle du billet
     * @return La representation textuelle du billet
     */
    public String text(){return text;}

    public String toString(){
        return text;
    }

    private String computeText(){
        int size = trips.size();
        TreeSet<String> nameAndPoints = new TreeSet<>();

        for (Trip trip : trips) {
            String nameAndPoint = trip.to().name() + " (" + trip.points() + ")";
            nameAndPoints.add(nameAndPoint);
        }
        if (size ==1){
            return startName+" - "+ nameAndPoints.first();
        } else {
            return startName+" - {"+String.join(", ",nameAndPoints)+"}";
        }
    }

    /**
     * Compare le billet courant avec celui passé en argument par ordre alphabétique
     *
     * @param that Le billet avec lequel comparé le billet coutant
     * @return un entier négatif , positif ou zero
     *      si le billet courant est respectivement plus petit / plus grand , égale au billet passé en paramètre
     */
    public int compareTo(Ticket that){
        return (this.text()).compareTo(that.text());
    }

    public List<Station> getStations(){
        List<Station> stations = new ArrayList<>();
        for(Trip t : trips){

            stations.add(t.from());
            stations.add(t.to());
        }
        return stations;
    }


}
