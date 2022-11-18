package ch.epfl.tchu.game;


import java.util.ArrayList;
import java.util.List;
/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * Les Chemin dans le réseau d'un joueur  du jeu
 */
public final class Trail {
    private final int length;
    private final Station station1;
    private final Station station2;
    private final List<Route> listeDesRoutes;


    private Trail(Station depart, Station arrive, List<Route> listeDesRoutes){
        station1=depart;
        station2=arrive;
        this.listeDesRoutes=listeDesRoutes;
        int taille =0;
        if(listeDesRoutes!=null) {
            for (Route listeDesRoute : listeDesRoutes) {
                taille += listeDesRoute.length();
            }
        }
        this.length=taille;
    }


    private Trail prolonge(Route r){
        List<Route> routes = new ArrayList<>(listeDesRoutes);
        routes.add(r);
        Station arrival = r.stationOpposite(station2);
        return new Trail(station1,arrival,routes);
    }

    /**
     * Retourne le plus long chemin du réseau de routes passée en paramètre
     * @param routes La listes de routes
     * @return La routes ayant le plus long chemin
     */
    public static Trail longest(List<Route> routes){
        if (routes ==null){
            return new Trail(null,null,null);
        }
        if (routes.size()==0){
            return new Trail(null,null,null);
        }
        int longueur =0;
        ArrayList<Trail> cs = new ArrayList<>();
        for(Route route :routes){
            cs.add(new Trail(route.station1(), route.station2(),List.of(route)));
        }
        for(Route route :routes){
            cs.add(new Trail(route.station2(), route.station1(),List.of(route)));
        }

        Trail win = new Trail(null,null,null);

        while(cs.size()>0){

            for(Trail trail : cs){
                if(trail.length>=longueur){
                   win = trail;
                   longueur = trail.length;
                }
            }
            ArrayList<Trail> css = new ArrayList<>();
            for(Trail c:cs){
                ArrayList<Route> rs = new ArrayList<>();
                for(Route route :routes){
                    Station trailArrival = c.station2();
                    Station routeStation1 = route.station1();
                    Station routeStation2 = route.station2();
                    boolean peutProlonger = (routeStation1.id() == trailArrival.id()) ||(routeStation2.id() == trailArrival.id()) ;


                   if(!c.contains(route) && peutProlonger ){
                       rs.add(route);
                   }
                }
                for(Route r : rs){
                    css.add(c.prolonge(r));
                }
            }
            cs=css;

        }
        return win;
    }

    /**
     * Retourne la longueur du chemin
     * @return la longueur du chemin
     */
    public int length(){return length;}

    /**
     * retourne la premiere gare du chemin ou null si le chemin est de longueur 0
     * @return La premiere gare
     *          ( ou null si longueur ==0)
     */
    public Station station1(){
        if (length==0){ return null;}
        return station1;
    }

    /**
     * Retourne la seconde station du chemin ou null si le chemin est de longueur 0
     * @return La seconde gare
     *          (ou null si longueur ==0)
     */
    public Station station2(){
        if (length==0){ return null;}
        return station2;
    }

    private boolean sameRoad(Route r1,Route r2){
        String name1 = r1.station1().name();
        String name2 = r1.station2().name();
        String name3 =r2.station1().name();
        String name4 = r2.station2().name();

        return (name1.equals(name3) && name2.equals(name4)) || name1.equals(name4) && name2.equals(name3);

    }
    private boolean contains(Route r){
        boolean in = false;
        for(Route route :  listeDesRoutes){
            if(sameRoad(route,r)){
                in=true;
            }
        }

        return in;
    }

    /**
     * représentation textuelle du chemin
     *
     */
    public String toString(){
        if (station1==null||station2==null){
            return "Chemin null";
        }
        ArrayList<Station> stat=new ArrayList<>();
        stat.add(station1);
        for(int i=0;i<listeDesRoutes.size();i++){
            stat.add(listeDesRoutes.get(i).stationOpposite(stat.get(i)));
        }
        List<String> names = new ArrayList<>();
        for(Station station :stat){
            names.add(station.name());
        }
        String s = String.join(" - ",names);
        s+= " ("+length+")";
        return s;
    }

    public List<Route> getListeDesRoutes() {
        return listeDesRoutes;
    }
}
