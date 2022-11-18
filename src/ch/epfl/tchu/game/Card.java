package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * Les différents types de cartes du jeu
 */
public enum Card {
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);
    /**
     * LA liste de toute les cartes du jeu
     */
    public final static List<Card> ALL = List.of(Card.values());
    /**
     * le nombre de carte dans le jeu
     */
    public static int COUNT= ALL.size();
    /**
     * La liste de toute la carte Wagon
     */
    public final static List<Card> CARS =List.of(BLACK,VIOLET,BLUE,GREEN,YELLOW,ORANGE,RED,WHITE);

    private final Color couleur;

    Card(Color color){
        this.couleur = color;
    }


    /**
     * Retourne la couleur du type de carte auquel on l'applique si la carte est un wagon , ou null sinon
     *
     * @return La couleur de la carte
     */
    public Color color(){ return couleur;}

    /**
     * Retourne le type de carte wagon correspondant à la couleur donnée
     *
     * @param coll Couleur du Wagon souhaitez
     *
     * @return La carte Wagon
     */
    public static Card of(Color coll){
        for (Card car : CARS) {
            if (coll == car.couleur) {
                return car;
            }
        }
        return LOCOMOTIVE;
    }


}
