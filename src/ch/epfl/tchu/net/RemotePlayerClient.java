package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;


/**
 *@author Antoine Schutz (314412)
 *@author Holzer Romain (330560)
 *
 * représente un client de joueur distant
 */
public class RemotePlayerClient {
    private final Player player;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    /**
     *  Se connecte a socket avec les paramètres données et créer les outils pour envoyer et recevoir des messages du mandataire
     *
     * @param player le joueur auquel elle doit fournir un accès distant
     * @param host le nom à utiliser pour se connecter au mandataire
     * @param port le port à utiliser pour se connecter au mandataire
     */
    public RemotePlayerClient(Player player, String host, int port) {
        this.player = player;
        try {
            Socket socket = new Socket(host, port);


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

    private void sendMessage(String message){
        try {
            writer.write(message+"\n");
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    /**
     * attend un message en provenance du mandataire en fonction de ce type de message, désérialise les arguments, appelle la méthode correspondante du joueur; si cette méthode retourne un résultat, le sérialise pour le renvoyer au mandataire en réponse.
     */
    public void run(){
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                String[] Message = message.split(Pattern.quote(" "), -1);
                switch (MessageId.valueOf(Message[0])){
                    case INIT_PLAYERS:
                        PlayerId ownId =PlayerId.ALL.get(Integer.parseInt(Message[1]));
                        String[] names = Message[2].split(Pattern.quote(","), -1);
                        Map<PlayerId, String> playerNames = new HashMap<>();
                        playerNames.put(PlayerId.PLAYER_1,STRING_SERDE.deserialize(names[0]));
                        playerNames.put(PlayerId.PLAYER_2,STRING_SERDE.deserialize(names[1]));
                        player.initPlayers(ownId,playerNames);
                        break;
                    case RECEIVE_INFO:
                        player.receiveInfo(STRING_SERDE.deserialize(Message[1]));
                        break;
                    case UPDATE_STATE:
                        player.updateState(PUBLIC_GAME_STATE_SERDE.deserialize(Message[1]),PLAYER_STATE_SERDE.deserialize(Message[2]));
                        break;
                    case SET_INITIAL_TICKETS:
                        if(Message.length>1){
                            player.setInitialTicketChoice(SORTED_BAG_TICKET_SERDE.deserialize(Message[1]));
                        } else {
                            player.setInitialTicketChoice(SORTED_BAG_TICKET_SERDE.deserialize(""));
                        }

                        break;
                    case CHOOSE_INITIAL_TICKETS:
                        SortedBag<Ticket> Tickets = player.chooseInitialTickets();
                        sendMessage(SORTED_BAG_TICKET_SERDE.serialize(Tickets));
                        break;
                    case NEXT_TURN:
                        Player.TurnKind nextTurn = player.nextTurn();
                        sendMessage(TURN_KIND_SERDE.serialize(nextTurn));
                        break;
                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> chosenTickets =player.chooseTickets(SORTED_BAG_TICKET_SERDE.deserialize(Message[1]));
                        sendMessage(SORTED_BAG_TICKET_SERDE.serialize(chosenTickets));
                        break;
                    case DRAW_SLOT:
                        int slot = player.drawSlot();
                        sendMessage(INTEGER_SERDE.serialize(slot));
                        break;
                    case ROUTE:
                        Route route = player.claimedRoute();
                        sendMessage(ROUTE_SERDE.serialize(route));
                        break;
                    case CARDS:
                        SortedBag<Card> cards = player.initialClaimCards();
                        sendMessage(SORTED_BAG_CARD_SERDE.serialize(cards));
                        break;
                    case CHOOSE_ADDITIONAL_CARDS:
                        SortedBag<Card> additionalCards = player.chooseAdditionalCards(LIST_SORTED_BAG_CARD_SERDE.deserialize(Message[1]));
                        sendMessage(SORTED_BAG_CARD_SERDE.serialize(additionalCards));
                    case FINAL_WINDOW:
                        player.createFinalWindow(INTEGER_SERDE.deserialize(Message[1]),INTEGER_SERDE.deserialize(Message[2]),INTEGER_SERDE.deserialize(Message[3]),SORTED_BAG_TICKET_SERDE.deserialize(Message[4]),INTEGER_SERDE.deserialize((Message[5])),INTEGER_SERDE.deserialize(Message[6]));
                        break;


                }
            }
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
