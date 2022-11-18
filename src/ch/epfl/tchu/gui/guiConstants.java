package ch.epfl.tchu.gui;

/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * Les constantes lié a l'interface graphique du jeu
 */
public class guiConstants {

    /**
     * Feuille de style des elements de la main
     */
    public static final String  DECKS_CSS ="decks.css";

    /**
     * Feuille de style des couleurs du jeu
     */
    public static final String COLORS_CSS ="colors.css";

    /**
     * feuille de style des elements JavaFx permettant des choix
     */
    public static final String CHOOSER_CSS ="chooser.css";

    /**
     * Feuille de style des infos des deux joueur
     */
    public static final String INFO_CSS="info.css";

    /**
     * Feuille de style de la carte du jeu
     */
    public static final String MAP_CSS="map.css";



    /**
     * Sélecteur utilisé dans la feuille de style deck.css
     */
    public static final String HAND_PANE ="hand-pane";

    /**
     * Sélecteur utilisé dans la feuille de style deck.css
     */
    public static final String CARD_PANE ="card-pane";



    /**
     * Sélecteur utilisé dans la feuille de style decks.css
     */
    public static final String CARD ="card";

    /**
     * Sélecteur utilisé dans la feuille de style decks.css
     */
    public static final String OUTSIDE ="outside";

    /**
     * Sélecteur utilisé dans la feuille de style decks.css et colors.css
     */
    public static final String FILLED ="filled";

    /**
     * Sélecteur utilisé dans la feuille de style decks.css
     */
    public static final String INSIDE ="inside";

    /**
     * Sélecteur utilisé dans la feuille de style decks.css
     */
    public static final String TRAIN_IMAGE ="train-image";

    /**
     * Sélecteur utilisé dans la feuille de style decks.css
     */
    public static final String GAUGED ="gauged";


    /**
     * Sélecteur utilisé dans la feuille de style decks.css
     */
    public static final String COUNT ="count";

    /**
     * Sélecteur utilisé dans la feuille de style decks.css
     */
    public static final String BACKGROUND ="background";

    /**
     * Sélecteur utilisé dans la feuille de style decks.css
     */
    public static final String FOREGROUND ="foreground";


    /**
     * Sélecteur utilisé dans la feuille de style decks.css
     */
    public static final String ROUTE ="route";

    /**
     * Sélecteur utilisé dans la feuille de style decks.css
     */
    public static final String TICKETS ="tickets";



    /**
     * Sélecteur utilisé dans la feuille de style info.css
     */
    public static final String PLAYER_STATS ="player-stats";


    /**
     *Sélecteur utilisé dans la feuille de style info.css
     */
    public static final String GAME_INFO ="game-info";

    /**
     * Sélecteur utilisé dans la feuille de style colors.css
     */
    public static final String NEUTRAL ="NEUTRAL";



    /**
     * Sélecteur utilisé dans la feuille de style map.css
     */
    public static final String TRACK ="track";

    /**
     * Sélecteur utilisé dans la feuille de style map.css
     */
    public static final String CAR ="car";





    /**
     * Nom du jeu
     */
    public static final String GAME_NAME ="tCHu";

    /**
     * Le nombre de message maximum affiché a gauche de la fenêtre de jeu
     */
    public static final int MESSAGE_MAX =5;

    /**
     * largeur du rectangle extérieur dans la représentation des cartes
     */
    public static final int CARDS_OUTSIDE_RECTANGLE_WIDTH =60;
    /**
     * longueur du rectangle extérieur dans la représentation des cartes
     */
    public static final int CARDS_OUTSIDE_RECTANGLE_HEIGHT =90;
    /**
     * largeur du rectangle intérieur dans la représentation des cartes
     */
    public static final int CARDS_INSIDE_RECTANGLE_WIDTH =40;
    /**
     * longueur du rectangle intérieur  dans la représentation des cartes
     */
    public static final int CARDS_INSIDE_RECTANGLE_HEIGHT =70;

    /**
     * largeur de la jauge de la pioche de cartes et de billets
     */
    public static final int GAUGE_WIDTH =50;
    /**
     * hauteur de la jauge de la pioche de cartes et de billets
     */
    public static final int GAUGE_HEIGHT =5;

    /**
     * hauteur de des routes
     */
    public static final int ROUTE_HEIGHT=12;
    /**
     * largeur des routes
     */
    public static final int ROUTE_WIDTH=36;

    /**
     * Position X du premier cercle apparaissant sur les routes claim
     */
    public static final int CIRCLE1_CENTER_X =12;
    /**
     * Position X du second cercle apparaissant sur les routes claim
     */
    public static final int CIRCLE2_CENTER_X =24;

    /**
     * Position Y des deux cercles apparaissant sur les routes claim
     */
    public static final int CIRCLE_CENTER_Y =6;
    /**
     * Rayon des cercles apparaissant sur les routes claim
     */
    public static final int CIRCLE_RADIUS=3;

    /**
     * Rayon des cercle des infos
     */
    public static final int INFO_CIRCLE_RADIUS=5;
}
