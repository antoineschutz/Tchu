package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *
 * contient la totalité des serdes utiles au projet
 */
public final class Serdes {
    /**
     * Un serde Pouvant (de)sérialisé des Integer
     */
    public static final Serde<Integer> INTEGER_SERDE = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    /**
     * Un serde Pouvant (de)sérialisé des String
     */
    public static final Serde<String> STRING_SERDE = Serde.of(
            i -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)),
            i -> new String(Base64.getDecoder().decode(i), StandardCharsets.UTF_8));

    /**
     * Un serde Pouvant (de)sérialisé des PlayerId
     */
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    /**
     * Un serde Pouvant (de)sérialisé des TurnKind
     */
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Un serde Pouvant (de)sérialisé des Card
     */
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    /**
     * Un serde Pouvant (de)sérialisé des Route
     */
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    /**
     * Un serde Pouvant (de)sérialisé des Ticket
     */
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /**
     * Un serde Pouvant (de)sérialisé des List de String
     */
    public static final Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE,',');

    /**
     * Un serde Pouvant (de)sérialisé des List de Card
     */
    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE,',');

    /**
     * Un serde Pouvant (de)sérialisé des List de route
     */
    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE,',');

    /**
     * Un serde Pouvant (de)sérialisé des SortedBag de Carte
     */
    public static final Serde<SortedBag<Card>> SORTED_BAG_CARD_SERDE = Serde.bagOf(CARD_SERDE,',');

    /**
     * Un serde Pouvant (de)sérialisé des SortedBag de Ticket
     */
    public static final Serde<SortedBag<Ticket>> SORTED_BAG_TICKET_SERDE = Serde.bagOf(TICKET_SERDE,',');

    /**
     * Un serde Pouvant (de)sérialisé des List de SortedBag de Carte
     */
    public static final Serde<List<SortedBag<Card>>> LIST_SORTED_BAG_CARD_SERDE =  Serde.listOf(SORTED_BAG_CARD_SERDE,';');


    /**
     * Un serde Pouvant (de)sérialisé des PublicCardState
     */
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = new Serde<PublicCardState>() {
        @Override
        public String serialize(PublicCardState toSerialise) {
            String[] serialized = new String[3];
            serialized[0] =LIST_CARD_SERDE.serialize(toSerialise.faceUpCards());
            serialized[1] = INTEGER_SERDE.serialize(toSerialise.deckSize());
            serialized[2] = INTEGER_SERDE.serialize(toSerialise.discardsSize());
            return String.join(";", serialized);
        }

        @Override
        public PublicCardState deserialize(String toDeserialise) {
            String[] deserialized = toDeserialise.split(Pattern.quote(";"), -1);
            List<Card> faceUpCards = LIST_CARD_SERDE.deserialize(deserialized[0]);
            int decksize = INTEGER_SERDE.deserialize(deserialized[1]);
            int discardSize = INTEGER_SERDE.deserialize(deserialized[2]);
            return new PublicCardState(faceUpCards,decksize,discardSize);
        }
    };

    /**
     * Un serde Pouvant (de)sérialisé des Public Player State
     */
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = new Serde<PublicPlayerState>() {
        @Override
        public String serialize(PublicPlayerState toSerialise) {
            String[] serialized = new String[4];
            serialized[0] = INTEGER_SERDE.serialize(toSerialise.ticketCount());
            serialized[1] =INTEGER_SERDE.serialize(toSerialise.cardCount());
            serialized[2] =LIST_ROUTE_SERDE.serialize(toSerialise.routes());
            serialized[3] = INTEGER_SERDE.serialize(toSerialise.initialCarCount);
            return String.join(";", serialized);
        }

        @Override
        public PublicPlayerState deserialize(String toDeserialise) {
            String[] deserialized = toDeserialise.split(Pattern.quote(";"), -1);
            int ticketCount = INTEGER_SERDE.deserialize(deserialized[0]);
            int cardCOunt = INTEGER_SERDE.deserialize(deserialized[1]);
            List<Route> routes = LIST_ROUTE_SERDE.deserialize(deserialized[2]);
            int initialCardCount = INTEGER_SERDE.deserialize(deserialized[3]);
            return new PublicPlayerState(ticketCount,cardCOunt,routes,initialCardCount);
        }
    };


    /**
     * Un serde Pouvant (de)sérialisé des Player State
     */
    public static final Serde<PlayerState> PLAYER_STATE_SERDE = new Serde<PlayerState>() {
        @Override
        public String serialize(PlayerState toSerialise) {
            String[] serialized = new String[4];
            serialized[0] = SORTED_BAG_TICKET_SERDE.serialize(toSerialise.tickets());
            serialized[1] = SORTED_BAG_CARD_SERDE.serialize(toSerialise.cards());
            serialized[2] = LIST_ROUTE_SERDE.serialize(toSerialise.routes());
            serialized[3] = INTEGER_SERDE.serialize(toSerialise.initialCardCount);
            return String.join(";", serialized);
        }

        @Override
        public PlayerState deserialize(String toDeserialise) {
            String[] deserialized = toDeserialise.split(Pattern.quote(";"), -1);
            SortedBag<Ticket> tickets = SORTED_BAG_TICKET_SERDE.deserialize(deserialized[0]);
            SortedBag<Card> cards = SORTED_BAG_CARD_SERDE.deserialize(deserialized[1]);
            List<Route> routes = LIST_ROUTE_SERDE.deserialize(deserialized[2]);
            int initialCardCount = INTEGER_SERDE.deserialize(deserialized[3]);
            return new PlayerState(tickets,cards,routes,initialCardCount);
        }
    };

    /**
     * Un serde Pouvant (de)sérialisé des Public game state
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = new Serde<PublicGameState>() {
        @Override
        public String serialize(PublicGameState toSerialise) {
            String[] serialized = new String[9];
            serialized[0] = INTEGER_SERDE.serialize(toSerialise.ticketsCount());
            serialized[1] = PUBLIC_CARD_STATE_SERDE.serialize(toSerialise.cardState());
            serialized[2] =PLAYER_ID_SERDE.serialize(toSerialise.currentPlayerId());
            serialized[3] = PUBLIC_PLAYER_STATE_SERDE.serialize(toSerialise.playerState(PlayerId.PLAYER_1));
            serialized[4] =PUBLIC_PLAYER_STATE_SERDE.serialize(toSerialise.playerState(PlayerId.PLAYER_2));
            serialized[5] = toSerialise.lastPlayer()==null ?"" : PLAYER_ID_SERDE.serialize(toSerialise.lastPlayer());
            serialized[6] = LIST_ROUTE_SERDE.serialize(toSerialise.longest1);
            serialized[7] = LIST_ROUTE_SERDE.serialize(toSerialise.longest2);
            serialized[8] = INTEGER_SERDE.serialize(toSerialise.longestALlSize);
            return String.join(":", serialized);
        }

        @Override
        public PublicGameState deserialize(String toDeserialise) {
            String[] deserialized = toDeserialise.split(Pattern.quote(":"), -1);
            int ticketsCount = INTEGER_SERDE.deserialize(deserialized[0]);
            PublicCardState cardState = PUBLIC_CARD_STATE_SERDE.deserialize(deserialized[1]);
            PlayerId currentPlayerId = PLAYER_ID_SERDE.deserialize(deserialized[2]);
            PublicPlayerState playerState_PLAYER_1 = PUBLIC_PLAYER_STATE_SERDE.deserialize(deserialized[3]);
            PublicPlayerState playerState_PLAYER_2 = PUBLIC_PLAYER_STATE_SERDE.deserialize(deserialized[4]);
            PlayerId lastPlayer;
            if(deserialized[5].equals("")){
                lastPlayer =null;
            } else {
                lastPlayer = PLAYER_ID_SERDE.deserialize(deserialized[5]);
            }
            List<Route> longest1 = LIST_ROUTE_SERDE.deserialize(deserialized[6]);
            List<Route> longest2 = LIST_ROUTE_SERDE.deserialize(deserialized[7]);
            int longestALlsize = INTEGER_SERDE.deserialize(deserialized[8]);
            Map<PlayerId, PublicPlayerState> ps = Map.of(
                    PlayerId.PLAYER_1, playerState_PLAYER_1,
                    PlayerId.PLAYER_2, playerState_PLAYER_2);
            PublicGameState g = new PublicGameState(ticketsCount,cardState,currentPlayerId,ps,lastPlayer);
            g.setLongests(longest1,longest2);
            g.setLongestALlSize(longestALlsize);
            return  g;
        }
    };

    private Serdes() {

    }


}