package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Constants.DISCARDABLE_TICKETS_COUNT;
import static ch.epfl.tchu.gui.guiConstants.*;
import static ch.epfl.tchu.gui.StringsFr.*;
import static javafx.application.Platform.isFxApplicationThread;


/**
 *@author Antoine Schutz (314412)
 *@author Holzer Romain (330560)
 *
 * l'interface graphique d'un joueur de tCHu
 */
public class GraphicalPlayer {

    private final ObservableGameState gameState;
    private final ObservableList<Text> list = FXCollections.observableArrayList();
    private final Stage primaryStage;
    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketHandlerProperty;
    private final ObjectProperty<ActionHandlers.DrawCardHandler> cardHandlerProperty;
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> routeHandlerProperty;


    /**
     *  construit l'interface graphique
     * @param id l'identité du joueur
     * @param joueurs la table associative des noms des joueurs
     */
    public GraphicalPlayer(PlayerId id, Map<PlayerId, String> joueurs) {
        assert isFxApplicationThread();
        ObservableGameState gameState = new ObservableGameState(id);
        this.gameState = gameState;
        ticketHandlerProperty = new SimpleObjectProperty<>();
        cardHandlerProperty = new SimpleObjectProperty<>();
        routeHandlerProperty = new SimpleObjectProperty<>();



        Node mapView = MapViewCreator
                .createMapView(gameState, routeHandlerProperty, this::chooseCards);
        Node cardsView = DecksViewCreator
                .createCardsView(gameState, ticketHandlerProperty, cardHandlerProperty);
        Node handView = DecksViewCreator
                .createHandView(gameState);
        Node infoView = InfoViewCreator
                .createInfoView(id, joueurs, gameState, list);

        primaryStage = new Stage();
        primaryStage.setTitle(GAME_NAME+" \u2014 "+joueurs.get(id));

        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);
        Scene s = new Scene(mainPane);
        primaryStage.setScene(s);
        primaryStage.show();

    }


    /**
     * appelle setState sur l'état observable du joueur
     * @param newGameState le nouvel état de jeu
     * @param ps le nouvel état du joueur
     */
    public void setState(PublicGameState newGameState , PlayerState ps) {
        assert isFxApplicationThread();
        gameState.setState(newGameState, ps);
    }

    /**
     * ajoute le message passé en paramètre au bas des informations sur le déroulement de la partie
     * @param message le message à ajouter dans la vue des informations
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        list.add(new Text(message));
        if(list.size()>MESSAGE_MAX){
            list.remove(0);
        }
    }

    /**
     * fait en sorte que, lorsque le joueur décide d'effectuer une action, le gestionnaire correspondant soit appelé et met a null les autres gestionnaires
     * @param ticketsHandler gestionnaire d'action gérant la prise de billets
     * @param cardHandler gestionnaire gérant la prise de cartes
     * @param routeHandler gestionnaire gérant la prise de routes
     */
    public void startTurn(ActionHandlers.DrawTicketsHandler ticketsHandler, ActionHandlers.DrawCardHandler cardHandler, ActionHandlers.ClaimRouteHandler routeHandler) {
        assert isFxApplicationThread();
        if (gameState.canDrawTickets()) {
            ticketHandlerProperty.setValue(()-> {
                handlerToNull();
                ticketsHandler.onDrawTickets();
            });
        }
        if (gameState.canDrawCards()) {
            cardHandlerProperty.setValue((c)-> {
                handlerToNull();
                cardHandler.onDrawCards(c);
            });
        }

        routeHandlerProperty.setValue((r, s)->{
            handlerToNull();
            routeHandler.onClaimRoute(r,s);
        });
    }

    /**
     * Met a null les valeur des property ,pour enlever la possibilité de cliquer sur les boutons correspondant
     */
    public void handlerToNull(){
        ticketHandlerProperty.setValue(null);
        cardHandlerProperty.setValue(null);
        routeHandlerProperty.setValue(null);
    }

    /**
     * ouvre une fenêtre permettant au joueur de faire son choix de billets et appelle ensuite le gestionnaire de choix de billets avec le choix du joueur en paramètre
     * @param billets multi-ensemble contenant cinq ou trois billets
     * @param choixBillets gestionnaire de choix de billets
     */
    public void chooseTickets(SortedBag<Ticket> billets, ActionHandlers.ChooseTicketsHandler choixBillets) {
        assert isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        ObservableList<Ticket> tickets = FXCollections.observableArrayList();
        tickets.addAll(billets.toList());
        ListView<Ticket> listView = new ListView<>(tickets);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button button = new Button(CHOOSE);

        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan(billets.size()-DISCARDABLE_TICKETS_COUNT));
        button.setOnAction(
                (e)-> {

                    stage.hide();
                    choixBillets.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));

                });
        createChooseWindow(stage,listView,button,TICKETS_CHOICE,String.format(StringsFr.CHOOSE_TICKETS,billets.size()-DISCARDABLE_TICKETS_COUNT,StringsFr.plural(billets.size()-DISCARDABLE_TICKETS_COUNT)));
    }

    private <K> void createChooseWindow(Stage stage,ListView<K> listView,Button button,String stageTitle,String chooseMessage){



        stage.setTitle(stageTitle);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        VBox v = new VBox();
        TextFlow t = new TextFlow();
        Text t2 = new Text(chooseMessage);
        t.getChildren().add(t2);
        v.getChildren().addAll(t, listView, button);
        Scene sc = new Scene(v);
        sc.getStylesheets().add(CHOOSER_CSS);
        stage.setScene(sc);
        stage.show();
        stage.setOnCloseRequest(Event::consume);

    }

    /**
     * autorise le joueur a pioché une carte et appelle le gestionnaire de choix de cartes avec le choix du joueur en paramètre
     * @param choix gestionnaire de choix de cartes
     */
    public void drawCards(ActionHandlers.DrawCardHandler choix) {
        assert isFxApplicationThread();
        cardHandlerProperty.setValue(choix);
    }



    /**
     * ouvre une fenêtre permettant au joueur de faire son choix de carte et appelle ensuite le gestionnaire de choix de carte avec le choix du joueur en paramètre
     * @param list liste de multi-ensembles des cartes initiales que le joueur peut utiliser pour s'emparer d'une route
     * @param choixCartes gestionnaire de choix de cartes
     */
    public void chooseClaimCards(List<SortedBag<Card>> list, ActionHandlers.ChooseCardsHandler choixCartes) {
        assert isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        ObservableList<SortedBag<Card>> vue = FXCollections.observableArrayList();
        vue.addAll(list);
        ListView<SortedBag<Card>> listView = new ListView<>(vue);
        listView.setCellFactory(z ->
                new TextFieldListCell<>(new CardBagStringConverter()));

        Button button = new Button(CHOOSE);
        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan(1));
        button.setOnMouseClicked(
                (e)-> {
                    stage.hide();
                    choixCartes.onChooseCards(listView.getSelectionModel().getSelectedItem());
                });

        createChooseWindow(stage,listView,button,CARDS_CHOICE,StringsFr.CHOOSE_CARDS);
    }

    /**
     * ouvre une fenêtre permettant au joueur de faire son choix de carte et appelle ensuite le gestionnaire de choix de carte avec le choix du joueur en paramètre
     * @param list liste de multiensembles des cartes additionnelles que le joueur peut utiliser pour s'emparer d'un tunnel
     * @param choixCartesSupp gestionnaire de choix de cartes
     */
    public void chooseAdditionalsCards(List<SortedBag<Card>> list, ActionHandlers.ChooseCardsHandler choixCartesSupp) {
        assert isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        ObservableList<SortedBag<Card>> vue = FXCollections.observableArrayList();
        vue.addAll(list);
        ListView<SortedBag<Card>> listView = new ListView<>(vue);
        listView.setCellFactory(z ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        Button button = new Button(CHOOSE);
        button.setOnMouseClicked(
                (e)-> {
                    stage.hide();
                    if(listView.getSelectionModel().isEmpty()){

                        choixCartesSupp.onChooseCards(SortedBag.of());
                    }else{
                        choixCartesSupp.onChooseCards(listView.getSelectionModel().getSelectedItem());
                    }

                });


        createChooseWindow(stage,listView,button,CARDS_CHOICE,StringsFr.CHOOSE_ADDITIONAL_CARDS);
    }









    /**
     * ouvre une fenêtre permettant au joueur de faire son choix de carte et appelle ensuite le gestionnaire de choix de carte avec le choix du joueur en paramètre
     * @param options liste de multi-ensembles des cartes  que le joueur peut utiliser pour s'emparer d'une route
     * @param chooser gestionnaire de choix de cartes
     */
    public  void chooseCards(List<SortedBag<Card>> options,
                             ActionHandlers.ChooseCardsHandler chooser) {
        assert isFxApplicationThread();


        if(options.size()==1){
            chooser.onChooseCards(options.get(0));
        } else {

            Stage stage = new Stage(StageStyle.UTILITY);
            ObservableList<SortedBag<Card>> vue = FXCollections.observableArrayList();
            vue.addAll(options);
            ListView<SortedBag<Card>> listView = new ListView<>(vue);
            listView.setCellFactory(z ->
                    new TextFieldListCell<>(new CardBagStringConverter()));
            Button button = new Button(CHOOSE);

            button.setOnMouseClicked(
                    (e) -> {
                        stage.hide();
                        chooser.onChooseCards(listView.getSelectionModel().getSelectedItem());

                    });

            button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan(1));
            createChooseWindow(stage, listView, button, CARDS_CHOICE, StringsFr.CHOOSE_CARDS);

        }



    }

    public void createFinalWindow(int myPoint,int otherPoint,int routeClaimed,SortedBag<Ticket> claimedTicket,int LongestTrailLenght,int myTrailLenght){
        assert isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);

        stage.setWidth(600);
        stage.setHeight(300);
        Button button = new Button("Fermer");

        button.setOnMouseClicked(
                (e)-> {
                    stage.hide();
                        System.exit(0);
                });
        stage.setTitle(myPoint>otherPoint ? "Victoire":"Défaite");
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        Text pointMessage = new Text("Vous avez obtenu "+myPoint+" point(s) contre "+otherPoint);
        Text routeNumb = new Text("Vous vous êtes emparé de "+routeClaimed+" routes");
        Text tickets = new Text("Liste de Tickets complété :");
        ObservableList<Ticket> claimed = FXCollections.observableArrayList();
        claimed.addAll(claimedTicket.toList());
        ListView<Ticket> listView = new ListView<>(claimed);

        listView.setPrefHeight(200);
        listView.setPrefWidth(400);

        Text longestLength = new Text("Votre plus long chemin a une longueur de : "+myTrailLenght);
        Text longestWIn = new Text();
        if(myTrailLenght>=LongestTrailLenght){
            longestWIn.setText("Vous avez obtenu le plus long Chemin de la Partie");
        }

        grid.addRow(0, pointMessage);
        grid.addRow(1 ,routeNumb);
        grid.addRow(2,longestLength);
        if(!longestWIn.getText().equals("")){
            grid.addRow(3,longestWIn);
        }
        grid.addRow(4,tickets);
        grid.addRow(5,listView);


        grid.add(button, 0, 6);

        Scene sc = new Scene(grid);
        stage.setScene(sc);
        stage.show();
        stage.setOnCloseRequest(Event::consume);
    }


    /**
     * Classe permettant le formatage des sortedBag de carte pour leur affichage
     */
    static class CardBagStringConverter extends StringConverter<SortedBag<Card>>{

        @Override
        public String toString(SortedBag<Card> object) {
            return Info.cardSetName(object);
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }


}
