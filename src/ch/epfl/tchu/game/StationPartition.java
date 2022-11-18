package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.HashMap;
import java.util.Map;
/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 *Représente un partition applatie de garre
 */
public final class StationPartition implements StationConnectivity {
    private final HashMap<Integer,Integer> partitions;



    private StationPartition(HashMap<Integer,Integer> partitions){
        this.partitions=partitions;
    }

    /**
     * retourne vrai Ssi les gares sont connectés ( Si elle sont dans la meme partitions )
     * @param s1 La premières gare
     * @param s2 La seconde Gare
     * @return vrai Ssi les gares sont connectés ( Si elle sont dans la meme partitions )
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if(s1.id()>=partitions.size()||s2.id()>=partitions.size()){
            return s1.id()==s2.id();
        }
        return (partitions.get(s1.id())==partitions.get(s2.id()));
    }


    /**
     * Bâtisseur de partition de gare
     */
    public static final class Builder{
        private final HashMap<Integer,Integer> partitions;



        /**
         * construit un bâtisseur de partition d'un ensemble de gares
         * @param stationCount Le nombre de station dans la partition
         *                      (doit être >=0-
         *
         * @throws IllegalArgumentException si stationCount <0
         */
        public Builder(int stationCount){
            Preconditions.checkArgument(stationCount>=0);
            partitions = new HashMap<>();
            for(int i=0;i<stationCount;i++){
                partitions.put(i,i);
            }

        }

        /**
         * joint les sous ensembles contenant les deux gares passées en argument
         * @param s1 la premières gare
         * @param s2 la seconde gare
         * @return Un bâtisseur avec les gares connectées
         */
        public Builder connect(Station s1, Station s2){
            int rep1 = representative(s1.id());
            int rep2 = representative(s2.id());
            partitions.put(rep1,rep2);
            return this;
        }


        /**
         * retourne une partition aplatie des gares
         * @return une partition aplatie des gares
         */
        public StationPartition build(){
            for(Map.Entry<Integer,Integer> map: partitions.entrySet()){
                if(!(partitions.get(map.getKey())==representative(map.getKey()))){
                    partitions.put(map.getKey(),representative(map.getKey()));
                }
            }
            return new StationPartition(partitions);
        }


        private Integer representative(int i){

            int reprensentative = partitions.get(i);
            while (!(reprensentative==partitions.get(reprensentative))){
                reprensentative=partitions.get(reprensentative);
            }
            return reprensentative;
        }


    }
}
