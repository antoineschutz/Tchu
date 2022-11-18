package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 *@author Antoine Schutz (314412)
 *@author Holzer Romain (330560)
 *
 *Le programme principal du Serveur de  tCHu
 *
 */
public class ServerMain extends Application {
    private static final String DEFAULT_NAME_1 ="Ada";
    private static final String DEFAULT_NAME_2 ="Charles";




    public static void main(String[] args) { launch(args); }



    private void setTextFieldNumberOnly(TextField textField){
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

    }

    /**
     * Démarre le Serveur en analysant les arguments passés au programme
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {


        Platform.setImplicitExit(false);

        BlockingQueue<Integer> carCount = new ArrayBlockingQueue<>(1);
        BlockingQueue<Integer> cardCount= new ArrayBlockingQueue<>(1);
        BlockingQueue<String> namePlayer1 = new ArrayBlockingQueue<>(1);
        BlockingQueue<String> namePlayer2 = new ArrayBlockingQueue<>(1);
        BlockingQueue<Boolean> soloQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<BotDifficulty> soloQueueGameMode = new ArrayBlockingQueue<>(1);
        BlockingQueue<Boolean> debugMode = new ArrayBlockingQueue<>(1);

        Stage choose = new Stage();
        choose.setResizable(false);
        Label nameL = new Label("Nom joueur 1:");
        TextField nameF = new TextField();

        Label pwL = new Label("Nom joueur 2:");
        TextField pwF = new TextField();

        Label initCard = new Label("Carte en début de partie");
        TextField initCardF = new TextField();
        Label initCar = new Label("Wagon en début de partie");
        TextField initCarF = new TextField();

        ChoiceBox<BotDifficulty> soloDifficulty = new ChoiceBox<>();
        soloDifficulty.getItems().addAll(BotDifficulty.ALL);
        soloDifficulty.setDisable(true);
        BooleanProperty buttonBind = new SimpleBooleanProperty(false);
        soloDifficulty.setOnAction((e)->{
            if(!soloDifficulty.getSelectionModel().isEmpty()){
                buttonBind.setValue(false);
            }
            if(soloDifficulty.getValue()!=null){
                pwF.setText("Bot_"+soloDifficulty.getValue().name());
            }
        });
        CheckBox enSolo =new CheckBox("Jouer en solo ");
        CheckBox debugModeButton  = new CheckBox("Debug mode ");
        debugModeButton.setDisable(true);


        enSolo.setOnMouseClicked((e)->{
            if(enSolo.isSelected()){
                soloDifficulty.setDisable(false);
                soloDifficulty.show();
                pwF.setDisable(true);
                if(soloDifficulty.getSelectionModel().isEmpty()){
                    buttonBind.setValue(true);
                } else {
                    buttonBind.setValue(false);
                }
                debugModeButton.setDisable(false);
            } else{
                soloDifficulty.setDisable(true);
                soloDifficulty .getSelectionModel().clearSelection();
                soloDifficulty.hide();
                pwF.setDisable(false);
                pwF.clear();
                debugModeButton.setDisable(true);
            }

        });

        setTextFieldNumberOnly(initCarF);
        setTextFieldNumberOnly(initCardF);
        choose.setOnCloseRequest((e)-> {
            System.out.println(choose.getHeight());
        });







        Button button = new Button("Confirmation");

        button.disableProperty().bind(buttonBind);
        GridPane grid = new GridPane();
        grid.addRow(0, nameL, nameF);
        grid.addRow(1, pwL, pwF);
        grid.addRow(3 ,initCard,initCardF);
        grid.addRow(4,initCar,initCarF);
        grid.addRow(5,enSolo);
        grid.addRow(6,debugModeButton);
        grid.add(soloDifficulty,2,5);



        grid.add(button, 0, 6, 2, 1);

        GridPane.setHalignment(button, HPos.CENTER);
        BorderPane mainPane =
                new BorderPane(grid, null, null, null, null);
        Scene scene = new Scene(mainPane);
        choose.setScene(scene);
        choose.setWidth(400);
        choose.setHeight(200);




        button.setOnAction(
                (e)-> {
                    choose.hide();
                    choose.close();

                    if((!initCardF.getText().equals("")) &&Integer.parseInt(initCardF.getText()) >0 && Integer.parseInt(initCardF.getText())<50 ){

                            cardCount.add(Integer.parseInt(initCardF.getText()));
                    } else {
                        cardCount.add(5);
                    }
                    if((!initCarF.getText().equals("")) && Integer.parseInt(initCarF.getText()) >0 && Integer.parseInt(initCarF.getText())<50){

                            carCount.add(Integer.parseInt(initCarF.getText()));
                    } else {
                        carCount.add(40);
                    }
                    soloQueue.add(enSolo.isSelected());
                    debugMode.add(debugModeButton.isSelected());
                    if(enSolo.isSelected()){
                        soloQueueGameMode.add(soloDifficulty.getSelectionModel().getSelectedItem());
                    }


                   namePlayer1.add(!nameF.getText().equals("")? nameF.getText() : DEFAULT_NAME_1);
                    namePlayer2.add(!pwF.getText().equals("") ?pwF.getText():DEFAULT_NAME_2);



                });

        choose.show();


        new Thread(()->{
            boolean solo = false;
            try{
                solo = soloQueue.take();
            } catch (Exception e){

            }

            if(!solo) {
                try (ServerSocket serverSocket = new ServerSocket(ClientMain.DEFAULT_PORT)) {

                    Socket socket = serverSocket.accept();
                    Map<PlayerId, String> playerNames = Map.of( PLAYER_1, namePlayer1.remove(),
                                                                PLAYER_2, namePlayer2.remove());

                    GraphicalPlayerAdapter player1 = new GraphicalPlayerAdapter();
                    Player player2 = new RemotePlayerProxy(socket);


                    Map<PlayerId, Player> players =
                            Map.of(PLAYER_1, player1,
                                    PLAYER_2, player2);
                    new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random(), cardCount.remove(), carCount.remove())).start();

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {

                Map<PlayerId, String> playerNames = Map.of(PLAYER_1, namePlayer1.remove(),
                        PLAYER_2, namePlayer2.remove());

                GraphicalPlayerAdapter player1 = new GraphicalPlayerAdapter();
                Player player2;
                Random rng = new Random();
                player2 = new BotPlayer(rng.nextLong(), soloQueueGameMode.remove(),debugMode.remove());

                Map<PlayerId, Player> players =
                        Map.of(PLAYER_1, player1,
                                PLAYER_2, player2);
                new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random(), cardCount.remove(), carCount.remove())).start();
            }



        }).start();
        }

        public void botBenchTest(int iterMax){


        // Section de test pour evaluer la force relative des bot :
        int victoireBot1 = 0;
        int victoireBOt2 = 0;
        for(int i=0;i<iterMax;i++){

            Map<PlayerId, String> playerNames = Map.of(PLAYER_1, "BOT1",
                    PLAYER_2, "BOT2");



            Random rng = new Random();
            Player player1 = new BotPlayer(rng.nextLong(), BotDifficulty.NORMAL,false);
            Player player2 = new BotPlayer(rng.nextLong(), BotDifficulty.DIFFICILE,false);

            Map<PlayerId, Player> players =
                    Map.of(PLAYER_1, player1,
                            PLAYER_2, player2);
            int winner =  Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random(),5,40);
            if(winner ==0){
                victoireBot1++;
            } else {
                victoireBOt2++;
            }

        }
        System.out.println("VICTOIRE BOT 1 : "+victoireBot1);
        System.out.println("VICTOIRE BOT 2 : "+victoireBOt2);

        }

}
