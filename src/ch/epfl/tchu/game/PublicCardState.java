package ch.epfl.tchu.game;
import ch.epfl.tchu.Preconditions;
import java.util.List;
import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;

/**
 *  @author Antoine Schutz (314412)
 *  @author Holzer Romain (330560)
 *
 *  Représente une partie de l'état des cartes qui ne sont pas en main des joueur
 */
public class PublicCardState {
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;


    /**
     * Construit un état public des cartes
     *
     * @param faceUpCards Les cartes face visible
     *                     (doit contenir exactement 5 élément)
     * @param deckSize Le nombre de carte que contient la pioche
     *                      (doit être positive )
     *
     * @param discardsSize Le nombre de carte que contient la défausse
     *                     (doit être positive
     * @throws IllegalArgumentException si faceUpCards ne contient pas exactement 5 élément ou si deckSize et/ou discardsSize ne sont pas positive
     *
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size()==FACE_UP_CARDS_COUNT&&deckSize>=0&&discardsSize>=0);
        this.faceUpCards = List.copyOf(faceUpCards);

        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }


    /**
     * retourne les 5 cartes face visibles
     * @return Les cartes face visibles
     */
    public List<Card> faceUpCards(){ return List.copyOf(faceUpCards);}

    /**
     * retourne La carte visible a l'index donnée
     * @param slot L'index de la carte souhaité
     *             (doit être entre 0 et 5 exclus)
     * @return La carte a l'index donnée
     *
     * @throws IndexOutOfBoundsException si l'index n'est pas entre 0 et 5 exclus
     */
    public Card faceUpCard(int slot){
        if(slot<0 || slot >=FACE_UP_CARDS_COUNT){
            throw new IndexOutOfBoundsException();
        }
        return faceUpCards.get(slot);
    }

    /**
     * retourne la taille de la pioche
     * @return la taille de la pioche
     */
    public int deckSize(){return deckSize;}

    /**
     * retourne vrai ssi la pioche est vide
     * @return vrai ssi la pioche est vide
     */
    public boolean isDeckEmpty(){return deckSize==0;}

    /**
     * retourne la taille de la défausse
     * @return la taille de la défausse
     */
    public int discardsSize(){return discardsSize;}
}
