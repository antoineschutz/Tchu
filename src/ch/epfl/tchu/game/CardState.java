package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;

/**
 *  @author Antoine Schutz (314412)
 *  * @author Holzer Romain (330560)
 *
 *  repésente l'états des cartes wagon/locomotive qui ne sont pas en main des joueurs
 */
public final class CardState extends PublicCardState {
            private final Deck<Card> deck;
            private final SortedBag<Card> discard;




    /**
     * méthode de construction qui retourne un état dans lequel les 5 cartes disposées face visibles sont less 5 premieres du tas , la pioche est constituée des cartes du tas restantes et la défausse est vide
     * @param deck Le tas de cartes utilisée pour crée l'état
     *             (doit contenir au moins 5 carte)
     * @return Un état de carte
     *
     * @throws IllegalArgumentException si le deck ne contient pas au moins 5 cartes
     */
    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size()>4);
        ArrayList<Card> faceUpCards =new ArrayList<>();
        while (faceUpCards.size()<FACE_UP_CARDS_COUNT){
            faceUpCards.add(deck.topCard());
            deck = deck.withoutTopCard();
        }
        return new CardState(faceUpCards,deck);
    }

    // crée un cardState avec discard vide
    private CardState(List<Card> faceUpCards,Deck<Card> deck) {
        super(faceUpCards, deck.size(), 0);
        this.deck =deck;
        this.discard = SortedBag.of();
    }
    //crée un cardState avec le discard donnée
    private CardState(List<Card> faceUpCards,Deck<Card> deck,SortedBag<Card> discard) {
        super(faceUpCards, deck.size(), discard.size());
        this.deck =deck;
        this.discard =discard;
    }



    /**
     * retourne un ensemble de carte identique au récepteur en ayant remplacé la face visible d'index slot par celle au sommet de la pioche
     * @param slot L'index de la face a remplacé
     * @return L'ensemble de carte ainsi crée
     *
     * @throws IllegalArgumentException si la pioche (deck) est vide
     *
     * @throws  IndexOutOfBoundsException si l'index donnée n'est pas entre 0 et 5 exclus
     */
    public CardState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(!deck.isEmpty());
        ArrayList<Card> faceUp= new ArrayList<>(faceUpCards());
        Card top = topDeckCard();
        faceUp.set(slot,top);
        return new CardState(faceUp,deck.withoutTopCard(),discard);
    }

    /**
     * retourne la carte au sommet de la pioche
     * @return la carte au sommet de la pioche
     *
     * @throws IllegalArgumentException si la pioche est vide
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }

    /**
     * retourne un ensemble de carte identique au récepteur mais sans la carte au sommet de la pioche
     * @return L'ensemble de carte sans la carte au sommet de la pioche
     *
     * @throws IllegalArgumentException si la pioche est vide
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!isDeckEmpty());
        return new CardState(faceUpCards(),deck.withoutTopCard(),discard);
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur dont les cartes de la défausse on été mélangées au moyen du générateur aléatoire rng
     *
     * @param rng Le générateur de nombre aléatoire
     * @return un ensemble de cartes avec le deck recrée a partir d'un mélange de la défausse
     *
     * @throws IllegalArgumentException si le deck n'est pas vide
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(isDeckEmpty());
        return new CardState(faceUpCards(),Deck.of(discard,rng));
    }

    /**
     * retourne un ensemble de carte identique au récepteur mais avec les carte ajoutées a la défausse
     * @param additionalDiscards Les cartes a ajouté a la défausse
     * @return l'ensemble de carte avec les cartes de plus dans la défausse
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        return new CardState(faceUpCards(),deck,discard.union(additionalDiscards));
    }


}
