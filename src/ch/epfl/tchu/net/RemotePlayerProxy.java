package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;


/**
 *@author Antoine Schutz (314412)
 *@author Holzer Romain (330560)
 *
 * représente un mandataire de joueur distant
 */
public class RemotePlayerProxy implements Player {
    private final BufferedReader reader;
    private final BufferedWriter writer;



    /**
     *  Stock le  socket passé en paramètre et créee les outils pour envoyer et recevoir des messages du mandataire
     *
     * @param socket  La «prise»  que le mandataire utilise pour communiquer à travers le réseau
     *
     */
    public RemotePlayerProxy(Socket socket){
        try {
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(),
                            US_ASCII));
            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(),
                            US_ASCII));
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
    private void sendMessage(MessageId id , String argument){
        String toSend;
        if(argument.equals("")){

            toSend =id.name()+"\n";

        } else {
            toSend = id.name() + " " + argument+"\n";
        }
        try {
            writer.write(toSend);
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
    private <T>T receiveMessage(Serde<T> serde){
        String message;
        try {
            message = reader.readLine();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return serde.deserialize(message);
    }

    /**
     * Envoie un message sur le réseaux correspondant a la forme serialisé des argument de initPlayer
     * @param ownId l'identité du joueur
     * @param playerNames noms des différents joueurs
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        ArrayList<String> names = new ArrayList<>();
        names.add(playerNames.get(PlayerId.PLAYER_1));
        names.add(playerNames.get(PlayerId.PLAYER_2));

        String message = PLAYER_ID_SERDE.serialize(ownId)+" "+LIST_STRING_SERDE.serialize(names);
        sendMessage(MessageId.INIT_PLAYERS,message);
    }

    /**
     * Envoie un message sur le réseaux correspondant a la forme serialisé de l'information reçu
     * @param info information devant être communiquée au joueur au cours de la partie
     */
    @Override
    public void receiveInfo(String info) {
        sendMessage(MessageId.RECEIVE_INFO,STRING_SERDE.serialize(info));
    }

    /**
     * Envoie un message sur le réseaux correspondant a la forme serialisé des arguements de updateState
     * @param newState le nouveau état de la partie
     * @param ownState L'état du joueur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String message = PUBLIC_GAME_STATE_SERDE.serialize(newState)+" "+PLAYER_STATE_SERDE.serialize(ownState);
        sendMessage(MessageId.UPDATE_STATE,message);

    }

    /**
     * Envoie un message sur le réseaux correspondant a la forme serialisé des arguments de setInitialTicketChoice
     * @param tickets le nouveau état de la partie
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendMessage(MessageId.SET_INITIAL_TICKETS,SORTED_BAG_TICKET_SERDE.serialize(tickets));
    }


    /**
     * Envoie un message sur le réseaux indiquant que le joueur doit choisir des tickets initiaux et retourne la réponse du client en la déserialisant
     * @return Les Ticket choisit
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS,"");
        return receiveMessage(SORTED_BAG_TICKET_SERDE);
    }

    /**
     * Envoie un message sur le réseaux indiquant le prochain tour et retourne la réponse du client en la déserialisant
     * @return Le type de tour choisit par le joueur
     */
    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN,"");
        return receiveMessage(TURN_KIND_SERDE);
    }

    /**
     * Envoie un message sur le réseaux correspondant a la forme sérialise des argument de chooseTickets et retourne le choix du client en la déserialisant
     * @return Les Ticket choisit
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(MessageId.CHOOSE_TICKETS,SORTED_BAG_TICKET_SERDE.serialize(options));
        return receiveMessage(SORTED_BAG_TICKET_SERDE);
    }

    /**
     * Envoie un message sur le réseaux correspondant a la forme sérialise de drawslot et retourne le choix du client en la déserialisant
     * @return L'emplacement choisit
     */
    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT,"");
        return receiveMessage(INTEGER_SERDE);
    }

    /**
     * Envoie un message sur le réseaux correspondant a la forme sérialise de claimedRoute et retourne le choix du client en la déserialisant
     * @return La route choisit
     */
    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE,"");
        return receiveMessage(ROUTE_SERDE);
    }


    /**
     * Envoie un message sur le réseaux correspondant a la forme sérialise de initialClaimCards et retourne le choix du client en la déserialisant
     * @return Les cartes choisit
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS,"");
        return receiveMessage(SORTED_BAG_CARD_SERDE);
    }


    /**
     * Envoie un message sur le réseaux correspondant a la forme sérialise de chooseAdditionalCards et retourne le choix du client en la déserialisant
     * @return Les cartes additionnels choisit
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS,LIST_SORTED_BAG_CARD_SERDE.serialize(options));
        return receiveMessage(SORTED_BAG_CARD_SERDE);
    }

    @Override
    public void createFinalWindow(int myPoint,int otherPoint,int routeClaimed,SortedBag<Ticket> claimedTicket,int longestTrailLenght,int myTrailLength) {
        String message = INTEGER_SERDE.serialize(myPoint)+" "+INTEGER_SERDE.serialize(otherPoint)+" "+INTEGER_SERDE.serialize(routeClaimed)+" "+SORTED_BAG_TICKET_SERDE.serialize(claimedTicket)+" "+INTEGER_SERDE.serialize(longestTrailLenght)+" "+INTEGER_SERDE.serialize(myTrailLength);
        sendMessage(MessageId.FINAL_WINDOW,message);
    }
}
