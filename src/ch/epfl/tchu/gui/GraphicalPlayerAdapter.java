package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;


/**
 *@author Antoine Schutz (314412)
 *@author Holzer Romain (330560)
 *le programme principal du client tCHu
 * Adapte une instance de GraphicalPlayer en une valeur de type Player
 */
public class GraphicalPlayerAdapter implements Player {
    private GraphicalPlayer graphicalPlayer;
    BlockingQueue<SortedBag<Ticket>> ticketBlockingQueue;
    BlockingQueue<TurnKind> turnKindBlockingQueue;
    BlockingQueue<Integer> onDrawCardsArgumentBlockingQueue;
    BlockingQueue<Route> routeBlockingQueue;
    BlockingQueue<SortedBag<Card>> sortedBagCardBlockingQueue;

    private static final int QUEUE_CAPACITY =1;
    /**
     *  Constructeur qui instancie les cinq files bloquantes utiles dans cette classe qui ont chacune une capacité de 1
     *  il y a une file bloquante pour chaque type de retour différent des méthodes de GraphicalPlayerAdapter
     */
    public GraphicalPlayerAdapter() {
        ticketBlockingQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        turnKindBlockingQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        onDrawCardsArgumentBlockingQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        routeBlockingQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        sortedBagCardBlockingQueue=new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    }

    /**
     * construit, sur le fil JavaFX, l'instance du joueur graphique GraphicalPlayer qu'elle adapte
     * @param ownId l'identité du joueur
     * @param playerNames noms des différents joueurs
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        runLater(()->{
            graphicalPlayer=new GraphicalPlayer(ownId,playerNames);


        });
    }

    /**
     *  appelle, sur le fil JavaFX, la méthode du même nom du joueur graphique
     * @param info information devant être communiquée au joueur au cours de la partie
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * appelle, sur le fil JavaFX, la méthode setState du joueur graphique
     * @param newState le nouveau état de la partie
     * @param ownState L'état du joueur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(()->graphicalPlayer.setState(newState,ownState));
    }

    /**
     * appelle, sur le fil JavaFX, la méthode chooseTickets du joueur graphique, pour lui demander de choisir ses billets initiaux, en lui passant un gestionnaire de choix qui stocke le choix du joueur dans une file bloquante
     * @param tickets Les cinq tickets de départ
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(()->graphicalPlayer.chooseTickets(tickets,(c)-> ticketBlockingQueue.add(c)));
    }

    /**
     * bloque en attendant que la file utilisée également par setInitialTicketChoice contienne une valeur, puis la retourne
     * @return le choix du joueur
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return ticketBlockingQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     *appelle, sur le fil JavaFX, la méthode startTurn du joueur graphique, en lui passant des gestionnaires d'action qui placent le type de tour choisi, de même que les éventuels «arguments» de l'action dans des files bloquantes, puis bloque en attendant qu'une valeur soit placée dans la file contenant le type de tour, qu'elle retire et retourne
     * @return Le type de tour que le joueur a choisit
     */
    @Override
    public TurnKind nextTurn() {
        runLater(()->graphicalPlayer.startTurn(
                ()-> turnKindBlockingQueue.add(TurnKind.DRAW_TICKETS)
                ,(i)->{
                    turnKindBlockingQueue.add(TurnKind.DRAW_CARDS);
                    onDrawCardsArgumentBlockingQueue.add(i);
                }
                ,(r,c)->{
                    turnKindBlockingQueue.add(TurnKind.CLAIM_ROUTE);
                    routeBlockingQueue.add(r);
                    sortedBagCardBlockingQueue.add(c);
                }

        ));

        try {
            return turnKindBlockingQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     *  appelle, sur le fil JavaFX, la méthode chooseTickets du joueur graphique, pour lui demander de choisir ses billets initiaux, en lui passant un gestionnaire de choix qui stocke le choix du joueur dans une file bloquante puis bloque en attendant que la file utilisée également par setInitialTicketChoice contienne une valeur, puis la retourne
     * @param options Les tickets proposés au joueur
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        runLater(()->graphicalPlayer.chooseTickets(options,(c)->
                ticketBlockingQueue.add(c)));

        try {
            return ticketBlockingQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * teste si la file contenant les emplacements des cartes contient une valeur; qu'il retourne. sinon appelle la méthode drawCard du joueur graphique, avant de bloquer en attendant que le gestionnaire qu'on lui passe place l'emplacement de la carte tirée dans la file, qui est alors extrait et retourné
     * @return L'emplacement de la carte a pioché choosit par le joueur
     */
    @Override
    public int drawSlot() {
        if(onDrawCardsArgumentBlockingQueue.size()!=0){
            try {
                return onDrawCardsArgumentBlockingQueue.take();
            } catch (InterruptedException e) {
                throw new Error();
            }
        } else {
            runLater(()->graphicalPlayer.drawCards((i)-> {
                graphicalPlayer.handlerToNull();
                onDrawCardsArgumentBlockingQueue.add(i);

            }));
        }


        try {
            return onDrawCardsArgumentBlockingQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * extrait et retourne le premier élément de la file contenant les routes, qui y aura été placé par le gestionnaire passé à startTurn par nextTurn
     * @return la route a claim
     */
    @Override
    public Route claimedRoute() {
        try {
            return routeBlockingQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * extrait et retourne le premier élément de la file contenant les cartes, qui y aura été placé par le gestionnaire passé à startTurn par nextTurn
     * @return les cartes initial
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return sortedBagCardBlockingQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * appelle, sur le fil JavaFX, la méthode du même nom du joueur graphique puis bloque en attendant qu'un élément soit placé dans la file contenant les multi-ensembles de cartes, qu'elle retourne
     * @param options Les possibilités de cartes présentées au joueur pour s'emparer d'un tunnel
     * @return le choix du joueur
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(()->graphicalPlayer.chooseAdditionalsCards(options,(c)->sortedBagCardBlockingQueue.add(c)));

        try {
            return sortedBagCardBlockingQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }

    }

    @Override
    public void createFinalWindow(int myPoint,int otherPoint,int routeClaimed,SortedBag<Ticket> claimedTicket,int longestTrailLenght,int myTrailLength){
       runLater(()-> graphicalPlayer.createFinalWindow(myPoint,otherPoint,routeClaimed,claimedTicket,longestTrailLenght,myTrailLength));
    }
}
