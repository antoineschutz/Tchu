package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * Les Gares du jeu
 */
public final class Station {
    private final int id;
    private final String name;

    /**
     * Construit une gare avec un numéro d'identification et un nom
     * @param id Le numéro d'identification ( doit être >=0)
     * @param name le nom de la gare
     *
     * @throws IllegalArgumentException si le numéro est négatif
     */
    public Station(int id , String name){
        Preconditions.checkArgument(id>=0);
        this.id=id;
        this.name=name;
    }

    /**
     * retourne le numéro d'identification de la gare
     * @return le numéro d'identification de la gare
     */
    public int id(){return id;}

    /**
     * retourne le nom de la gare
     * @return le nom de la gare
     */
    public String name(){return name;}

    @Override
    public String toString(){ return name;}
}
