package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * Route reliant deux vile voisine
 */
public final class Route {
    /**
     * représente les deux niveaux possible d'une route
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     *   construit une route avec l'identité, les gares, la longueur, le niveau et la couleur donné;
     *   lève IllegalArgumentException si les deux gares sont égales
     *   ou si la longueur n'est pas comprise dans les limites acceptables,
     *   ou NullPointerException si l'identité, l'une des deux gares ou le niveau sont nuls.
     * @param id l'identité de la route ; un String
     * @param station1 La premiere gare
     * @param station2 La seconde gare
     * @param length la longueur de la route
     * @param level le niveau de la route
     * @param color sa couleur ( ou null si sans couleur)
     *
     * @throws NullPointerException si l'identité,les gare ou le niveau sont null.
     * @throws IllegalArgumentException si les gares sont égales.
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {

        Preconditions.checkArgument(((!station1.equals(station2)) && (length <= Constants.MAX_ROUTE_LENGTH) && (length >= Constants.MIN_ROUTE_LENGTH) ));
        if (id == null || station1 == null || station2 == null || level == null) {
            throw new NullPointerException();
        }


        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
        this.level = level;
        this.color = color;

    }

    /**
     * retourne l'identité de la gare
     * @return l'identité
     */
    public String id() {return id;}

    /**
     * retourne la premiere station
     * @return la station
     */
    public Station station1() {return station1;}

    /**
     * retourne la seconde station
     * @return la seconde station
     */
    public Station station2() {return station2;}

    /**
     * retourne la longueur de la route
     * @return la longueur de la route
     */
    public int length() {return length;}

    /**
     * retourne le niveau de la route
     * @return le niveau de la route
     */
    public Level level() {return level;}

    /**
     * retourne la couleur de la route ( ou null si la route est sans couleur )
     * @return la couleur de la route
     */
    public Color color() {return color;}

    /**
     * retourne le nombre de points de construction obtenue par le joueur qui s'empare de la route
     * @return Le nombre de points obtenue
     */
    public int claimPoints(){
            return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

    /**
     * Retourne la liste des gare dans l'ordre dans lequel elles on été passée au constructeur
     *
     * @return la liste des gare
     */
    public List<Station> stations() {
        List<Station> all= new ArrayList<>();
        all.add(station1);
        all.add(station2);
        return all;
    }

    /**
     * Retourne La gare de la route qui n'est pas donné
     * @param station La station dont on souhaite savoir l'opposée
     * @return La gare opposée a la station donnée
     *
     * @throws IllegalArgumentException Si la gare passée en paramètre n'est ni la premiere ni la seconde gare de la route
     */
    public Station stationOpposite(Station station) {
        if (station == station1) {return station2;}
        else if (station==station2){return station1;}
        throw new IllegalArgumentException();
    }

    /**
     * retourne la liste de tous les ensembles de cartes qui pourraient être joués pour (tenter de) s'emparer de la route,
     * trié par ordre croissant de nombre de cartes locomotive,
     * puis par couleur,
     *
     * @return La liste de tout les ensemble de cartes joué pour prendre la route
     *
     */
    public List<SortedBag<Card>> possibleClaimCards() {

        List<SortedBag<Card>> liste = new ArrayList<>();

        for (Color w: Color.ALL) {
            if  (Card.of(w) == Card.of(color) || Card.of(color) == Card.of(null)) {
                liste.add(SortedBag.of(length, Card.of(w)));
            }
        }

        if (level == Level.UNDERGROUND && color ==null) {
            for (int i = length-1; i > 0; --i) {
                for(Color w:Color.ALL) {


                    liste.add(SortedBag.of(i, Card.of(w), (length - i), Card.LOCOMOTIVE));

                }
            }
            liste.add(SortedBag.of(length,Card.LOCOMOTIVE));
        }
        if(level==Level.UNDERGROUND && color !=null){
            for (int i = length-1; i > 0; --i) {



                    liste.add(SortedBag.of(i, Card.of(color), (length - i), Card.LOCOMOTIVE));


            }
            liste.add(SortedBag.of(length,Card.LOCOMOTIVE));
        }

        return liste;
    }

    /**
     * Retourne le nombre de carte additionnelles a jouer pour s'emparer de la route en tunnel
     * @param claimCards Carte posée par le joueur
     * @param drawnCards Carte tirée du sommet de la pioche ( doit être de taille 3)
     *
     * @return nombre de carte additionnelles a jouer par le joueur
     * @throws IllegalArgumentException si la route n'est pas un tunnel ou si DrawnCard ne contient pas 3 cartes
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument((drawnCards.size()==Constants.ADDITIONAL_TUNNEL_CARDS) && (level==Level.UNDERGROUND) );

        int nombre = 0;

        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
            if (claimCards.contains(drawnCards.get(i)) || drawnCards.get(i)==Card.of(null)) {
                ++nombre;
            }
        }

        return nombre;
    }





}
