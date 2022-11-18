package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;


/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 *Un Serde
 */
public interface Serde<T> {
    /**
     * Prend en argument la chaine a sérialisé et retournant la chaine correspondante
     * @param toSerialise l'argument a serialisé
     * @return la chaine sérialisé
     */
    String serialize(T toSerialise);

    /**
     * Prend en argument la chaine a desérialisé et retournant la chaine correspondante
     * @param toDeserialise la chaine a desérialisé
     * @return la chaine desérialisé
     */
    T deserialize(String toDeserialise);

    /**
     *Utilise les fonctions passé en paramètre pour créer un Serde
     *
     *
     * @param serializationFunction La fonction de sérialisation
     * @param DeserializationFunction La fonction de desérialisation
     * @param <T> Le Paramètre de type de la méthode
     * @return Un serde utilisant les fonctions passer en paramètre
     */
    static <T> Serde<T> of(Function<T, String> serializationFunction, Function<String, T> DeserializationFunction){
        return new Serde<>() {
            @Override
            public String serialize(T t) {
                return serializationFunction.apply(t);
            }

            @Override
            public T deserialize(String s) {
                return DeserializationFunction.apply(s);
            }
        };
    }

    /**
     * Créer un Serde pouvant agir sur les valeurs d'un type énuméré passée en paramètre
     *
     * @param list La list de toutes les valeurs d'un ensemble de valeurs énuméré
     * @param <T> Le type de ces valeurs énuméré
     * @return un Serde aggisant sur des valeurs de ce type
     */
    static <T> Serde<T> oneOf(List<T> list){
        Function<T, String> f = (T t) -> String.valueOf(list.indexOf(t));
        Function<String, T> s = (String numb) -> list.get(Integer.parseInt(numb));
        return Serde.of(f,s);
    }

    /**
     * Construit un Serde capable d'agir sur des listes de valeur avec un serde donné
     *
     * @param serde Le Serde a utiliser pour traiter la liste des valeurs
     * @param separateur un caractère de séparation
     * @param <T> le type des valeurs que le serde dois traiter
     * @return Le Serde capable d'agir sur des listes de valeur
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, char separateur){
        return new Serde<>() {
            @Override
            public String serialize(List<T> t) {
                if (t.isEmpty()) {
                    return "";
                }
                List<String> l = new ArrayList<>();
                for (T value : t) {
                    l.add(serde.serialize(value));
                }
                return String.join(String.valueOf(separateur), l);

            }

            @Override
            public List<T> deserialize(String s) {
                if (s.equals("")) {
                    return List.of();
                }
                List<T> deserialized = new ArrayList<>();

                String[] a = s.split(Pattern.quote(String.valueOf(separateur)), -1);

                for (String value : a) {
                    deserialized.add(serde.deserialize(value));
                }
                return deserialized;
            }
        };
    }


    /**
     * Construit un Serde capable d'agir sur des Sorted bag  de valeurs  avec un serde donné
     *
     * @param serde Le Serde a utiliser pour traiter le Sorted bag  de valeurs
     * @param separateur un caractère de séparation
     * @param <T> le type des valeurs que le serde dois traiter
     * @return Le Serde capable d'agir sur des Sortedbag de <T>
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, char separateur){
        Serde<List<T>>  serde1 = Serde.listOf(serde,separateur);
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> t) {
                if (t.isEmpty()) {
                    return "";
                }
                List<T> list = t.toList();
                return serde1.serialize(list);
            }

            @Override
            public SortedBag<T> deserialize(String s) {
                if (s.equals("")) {
                    return SortedBag.of();
                }
                List<T> list = serde1.deserialize(s);
                return SortedBag.of(list);
            }
        };
    }
}
