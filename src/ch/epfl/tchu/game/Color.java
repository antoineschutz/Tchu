package ch.epfl.tchu.game;
import java.util.List;
/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * Les Couleurs du jeu
 */
public enum Color {
    BLACK,VIOLET,BLUE,GREEN,YELLOW,ORANGE,RED,WHITE;
    /**
     * La liste de toute les couleur du jeu
     */
    public final static List<Color> ALL = List.of(Color.values());
    /**
     * LE nombre de couleur disponible dans le jeu
     */
    public static int COUNT= ALL.size();



}
