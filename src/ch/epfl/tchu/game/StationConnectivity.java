package ch.epfl.tchu.game;

public interface StationConnectivity {
     /**
      * retourne vrai ssi les gares données sont reliés par le réseaux du joueur
      * @param s1 La premières gare
      * @param s2 La seconde Gare
      * @return vrai ssi les gares données sont reliés par le réseaux du joueur
      */
     boolean connected(Station s1, Station s2);
}
