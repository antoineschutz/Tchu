package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 *  @author Antoine Schutz (314412)
 *  @author Holzer Romain (330560)
 *
 * Repésente un tas de cartes
 * @param <C> Le type de carte utilisée
 */
public final class Deck<C extends Comparable<C>> {
    private final List<C> deck;



    /**
     * Méthode de construction statique qui retourne un tas de cartes ayant les memes cartes que le multi-ensemble "cards" mélanger au moyen du générateur de nombre aléatoire rng
     * @param cards Le multi-ensemble de carte
     * @param rng   Le générateur de nombre aléatoire
     * @param <C>   Le type de carte du deck
     * @return  Un deck
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        List<C> temporary = cards.toList();
        Collections.shuffle(temporary,rng);

        return new Deck<>(temporary);

    }

    private Deck(List<C> deck) {
        this.deck = List.copyOf(deck);
    }

    /**
     * retourne la taille du deck
     * @return la taille d deck
     */
    public int size(){
        return deck.size();
    }

    /**
     * return vrai ssi la teas est vide
     * @return vrai ssi la tas est vide
     */
    public boolean isEmpty(){
        return deck.isEmpty();
    }

    /**
     * retourne la carte au sommet du tas
     * @return la carte au sommet du tas
     * @throws IllegalArgumentException si le tas est vide
     */
    public C topCard(){
        Preconditions.checkArgument(!isEmpty());
        return deck.get(size()-1);
    }

    /**
     * retourne un tas identique a this sans la carte au sommet
     * @return un tas identique a this sans la carte au sommet
     *
     * @throws IllegalArgumentException si le tas est vide
     */
    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(!isEmpty());
        List<C> a= new ArrayList<>();
        for(int i=0;i<size()-1;i++){
            a.add(deck.get(i));
        }

        return new Deck<>(a);
    }

    /**
     * Retourne un multi-ensemble contenant les "count" cartes au sommet
     * @param count le nombres de cartes du multi-ensemble souhaiter
     *                 (doit être compris entre 0 et size()
     * @return Le multi-ensemble
     *
     * @throws IllegalArgumentException si count n'est pas compris entre 0 et la taille du tas
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(count>=0 && count <=size() );
        SortedBag.Builder<C> builder = new SortedBag.Builder<>();
        for(int i=size()-1;i>=size()-count;i--){
            builder.add(deck.get(i));
        }
        return builder.build();
    }

    /**
     * retourne un tas identique au récepteur sans les "count" carte au sommet
     * @param count Le nombre de carte a retirer du récepteur
     *      *                 (doit être compris entre 0 et size()
     * @return Le tas sans les count carte au sommet
     * @throws IllegalArgumentException si count n'est pas compris entre 0 et la taille du tass
     */
    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument(count>=0 && count <=size() );
        List<C> a = deck.subList(0,size()-count);
        return new Deck<>(a);
    }


}