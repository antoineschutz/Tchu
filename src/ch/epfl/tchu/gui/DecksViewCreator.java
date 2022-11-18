package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.game.Constants.DECK_SLOT;
import static ch.epfl.tchu.game.Constants.FACE_UP_CARD_SLOTS;
import static ch.epfl.tchu.gui.guiConstants.*;



/**
 *@author Antoine Schutz (314412)
 *@author Holzer Romain (330560)
 *
 * Créer la vue de la main
 */
public class DecksViewCreator  {

    /**
     * crée et retourne la vue de la main
     * @param obs l'état du jeu observable
     * @return une instance de HBox, qui est la vue de la main créée
     */
    public static HBox createHandView(ObservableGameState obs) {

        HBox base = new HBox();
        base.getStylesheets().add(DECKS_CSS);
        base.getStylesheets().add(COLORS_CSS);

        ListView<Ticket> listView = new ListView<>(obs.playerTicket());
        listView.setId(TICKETS);
        listView.setCellFactory(new Callback<ListView<Ticket>, ListCell<Ticket>>() {
            @Override
            public ListCell<Ticket> call(ListView<Ticket> param) {
                return new ListCell<Ticket>() {
                    @Override
                    protected void updateItem(Ticket item, boolean empty) {
                        super.updateItem(item, empty);
                        if(obs.ticketBooleanProperty(item)!=null) {
                            if (obs.ticketBooleanProperty(item).get()) {
                                setStyle("-fx-control-inner-background:" + "lightgreen");
                            } else{
                                setStyle("-fx-control-inner-background:" + "White");
                            }
                        }
                        if(item !=null) {
                            setText(item.toString());
                        }
                    }

                };


            }


        });

        listView.setOnMouseClicked((e)->{
            if(!listView.getSelectionModel().isEmpty()) {
                obs.clearStations();
                Station first = null;
                List<Station> green = new ArrayList<>();
                for (int i = 0; i < listView.getSelectionModel().getSelectedItem().getStations().size(); i++) {
                    Station s = listView.getSelectionModel().getSelectedItem().getStations().get(i);
                    obs.addStations(s);
                    if (i != 0) {
                        obs.setColor(s, "RED");
                    } else {
                        first = s;
                    }
                    if (first != null && first.name().equals(s.name())) {
                        green.add(s);
                    }
                }
                ;
                if (first != null) {
                    obs.setColor(first, "GREEN");
                }
                for (Station s : green) {
                    obs.setColor(s, "GREEN ");
                }
            }
        });
        base.setOnMouseClicked((e)->obs.clearStations());


        base.getChildren().add(listView);

        HBox base2 = new HBox();
        base2.setId(HAND_PANE);

        for (Card c : Card.ALL) {
            StackPane sp = new StackPane();

            ReadOnlyIntegerProperty count = obs.CardProperty(c);
            sp.visibleProperty().bind(Bindings.greaterThan(count, 0));
            if (c.color() == null) {
                sp.getStyleClass().addAll(NEUTRAL, CARD);
            } else {
                sp.getStyleClass().addAll(Card.of(c.color()).toString(), CARD);
            }
            Rectangle r1 = new Rectangle(CARDS_OUTSIDE_RECTANGLE_WIDTH, CARDS_OUTSIDE_RECTANGLE_HEIGHT);
            r1.getStyleClass().add(OUTSIDE);
            Rectangle r2 = new Rectangle(CARDS_INSIDE_RECTANGLE_WIDTH, CARDS_INSIDE_RECTANGLE_HEIGHT);
            r2.getStyleClass().addAll(FILLED, INSIDE);
            Rectangle r3 = new Rectangle(CARDS_INSIDE_RECTANGLE_WIDTH, CARDS_INSIDE_RECTANGLE_HEIGHT);
            r3.getStyleClass().add(TRAIN_IMAGE);
            Text t = new Text();
            t.textProperty().bind(Bindings.convert(obs.CardProperty(c)));
            t.visibleProperty().bind(Bindings.greaterThan(obs.CardProperty(c),1));
            t.getStyleClass().add(COUNT);
            sp.getChildren().addAll(r1, r2, r3, t);
            base2.getChildren().add(sp);

        }

        base.getChildren().addAll(base2);

        return base;


    }

    /**
     * crée et retourne la vue des pioches
     * @param obs l'état du jeu observable
     * @param  ticketsHandler propriété qui contient le gestionnaire d'action gérant le tirage de billets
     * @param cardHandler propriété qui contient le gestionnaire d'action gérant le tirage des cartes
     * @return une instance de VBox, qui est la vue des pioches créé
     */
    public static VBox createCardsView(ObservableGameState obs, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandler  , ObjectProperty<ActionHandlers.DrawCardHandler> cardHandler  ) {
        VBox base = new VBox();
        base.setId(CARD_PANE);
        base.getStylesheets().add(DECKS_CSS);
        base.getStylesheets().add(COLORS_CSS);

        Button ticketButoon = new Button(StringsFr.TICKETS);
        ticketButoon.getStyleClass().add(GAUGED);
        ticketButoon.setId(StringsFr.TICKETS);
        ticketButoon.setOnMouseClicked((e)-> ticketsHandler.get().onDrawTickets());

        ticketButoon.disableProperty().bind(ticketsHandler.isNull());

        Group gr1 = new Group();
        Rectangle ticketGaugeBack = new Rectangle(GAUGE_WIDTH, GAUGE_HEIGHT);
        ticketGaugeBack.getStyleClass().add(BACKGROUND);

        Rectangle ticketGaugeFront = new Rectangle(GAUGE_WIDTH, GAUGE_HEIGHT);
        ticketGaugeFront.getStyleClass().add(FOREGROUND);


        ReadOnlyIntegerProperty pctProperty1 = obs.ticketPourcentage();
        ticketGaugeFront.widthProperty().bind(
                pctProperty1.multiply(GAUGE_WIDTH).divide(100));


        gr1.getChildren().addAll(ticketGaugeBack, ticketGaugeFront);

        ticketButoon.setGraphic(gr1);

        Button cardsButton = new Button(StringsFr.CARDS);
        cardsButton.getStyleClass().add(GAUGED);
        cardsButton.disableProperty().bind(cardHandler.isNull());

        cardsButton.setOnMouseClicked((e)-> cardHandler.get().onDrawCards(DECK_SLOT));
        Group gr2 = new Group();
        Rectangle cardsGaugeFront = new Rectangle(GAUGE_WIDTH, GAUGE_HEIGHT);
        cardsGaugeFront.getStyleClass().add(BACKGROUND);

        Rectangle cardsGaugeBack = new Rectangle(GAUGE_WIDTH, GAUGE_HEIGHT);
        cardsGaugeBack.getStyleClass().add(FOREGROUND);


        ReadOnlyIntegerProperty pctProperty =obs.deckCardPourcentage();
        cardsGaugeBack.widthProperty().bind(
                pctProperty.multiply(GAUGE_WIDTH).divide(100));
        gr2.getChildren().addAll(cardsGaugeFront, cardsGaugeBack);

        cardsButton.setGraphic(gr2);

        base.getChildren().add(ticketButoon);


        for (Integer i :FACE_UP_CARD_SLOTS) {
            StackPane sp = new StackPane();
            sp.getStyleClass().addAll(obs.faceUpCard(i).toString(), CARD);

            sp.disableProperty().bind(cardHandler.isNull());

            obs.faceUpCard(i).addListener((p, o, n) ->{
                String s = (n==Card.LOCOMOTIVE)? NEUTRAL  :n.name();
                sp.getStyleClass().setAll(s, CARD);
            });

            sp.setOnMouseClicked((e)-> cardHandler.get().onDrawCards(i));

            Rectangle outsideRectangle = new Rectangle(CARDS_OUTSIDE_RECTANGLE_WIDTH, CARDS_OUTSIDE_RECTANGLE_HEIGHT);
            outsideRectangle.getStyleClass().add(OUTSIDE);

            Rectangle insideRectangle = new Rectangle(CARDS_INSIDE_RECTANGLE_WIDTH, CARDS_INSIDE_RECTANGLE_HEIGHT);
            insideRectangle.getStyleClass().addAll(FILLED, INSIDE);

            Rectangle trainImage = new Rectangle(CARDS_INSIDE_RECTANGLE_WIDTH, CARDS_INSIDE_RECTANGLE_HEIGHT);
            trainImage.getStyleClass().add(TRAIN_IMAGE);
            sp.getChildren().addAll(outsideRectangle, insideRectangle, trainImage);
            base.getChildren().add(sp);
        }


        base.getChildren().add(cardsButton);

        return base;


    }
}
