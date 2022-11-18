package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Station;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.List;

import static ch.epfl.tchu.gui.guiConstants.*;


/**
 *@author Antoine Schutz (314412)
 *@author Holzer Romain (330560)
 *
 * créer la vue de la carte
 */
class MapViewCreator {
    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandlers.ChooseCardsHandler handler);
    }

    /**
     * crée et retourne la vue de la carte
     * @param obs l'état du jeu observable
     * @param handler une propriété qui contient le gestionnaire d'action à utiliser lorsque le joueur désire s'emparer d'une route
     * @param chooser un sélectionneur de cartes crée avec l'interface CardChooser qui se trouve dans la même classe
     * @return une instance de Pane, qui est la vue de la carte créé
     */
    public static Pane createMapView(ObservableGameState obs,
                                     ObjectProperty<ActionHandlers.ClaimRouteHandler> handler,
                                     CardChooser chooser) {

        Pane carte = new Pane();

        carte.getStylesheets().add(MAP_CSS);
        carte.getStylesheets().add(COLORS_CSS);

        ImageView imageView = new ImageView();
        Pane backGround = new Pane(imageView);

        carte.getChildren().add(backGround);
        for(Station s :ChMap.stations()){
            Circle stationCIrcle = new Circle(20);
            stationCIrcle.setId(String.valueOf(s.id()));
            stationCIrcle.visibleProperty().bind(obs.stationsToShow.get(s));

            obs.stationColorMap.addListener((MapChangeListener<? super Station, ? super String>) (e)->{
                if(obs.stationColorMap.get(s).equals("RED")){
                    stationCIrcle.setStyle(" -fx-fill: RED");
                } else if (obs.stationColorMap.get(s).equals("GREEN")) {
                    stationCIrcle.setStyle(" -fx-fill: Green");
                }

            });
            carte.getChildren().add(stationCIrcle);


        }



        for (Route route : ChMap.routes()) {

            Group routeGroup = new Group();
            routeGroup.setId(route.id());
            obs.routePossession(route).addListener((p,o,n)-> {
                if(n!=null){
                    routeGroup.getStyleClass().add(n.name());
                }
            });

            routeGroup.disableProperty().bind(
                    handler.isNull().or(obs.routeClaimability(route).not()));

            if (route.color() == null) {
                routeGroup.getStyleClass().addAll(ROUTE, route.level().toString(), NEUTRAL);

            } else {
                routeGroup.getStyleClass().addAll(ROUTE, route.level().toString(), route.color().name());

            }
            carte.getChildren().add(routeGroup);

            for (int i = 1; i <= route.length(); ++i) {
                Group caseGroup = new Group();
                caseGroup.setId(route.id() + "_" + i);

                caseGroup.getStyleClass().add(route.level().toString());
                if (route.color() == null) {
                    caseGroup.getStyleClass().add(NEUTRAL);
                } else {
                    caseGroup.getStyleClass().add(route.color().name());
                }

                routeGroup.getChildren().add(caseGroup);

                Rectangle rectangleVoie = new Rectangle(ROUTE_WIDTH, ROUTE_HEIGHT);
                rectangleVoie.getStyleClass().addAll(TRACK, FILLED);
                Group wagonGroup = new Group();
                wagonGroup.getStyleClass().add(CAR);
                Rectangle rectangle1 = new Rectangle(ROUTE_WIDTH, ROUTE_HEIGHT);
                rectangle1.getStyleClass().add(FILLED);
                Circle circle1 = new Circle(CIRCLE1_CENTER_X, CIRCLE_CENTER_Y, CIRCLE_RADIUS);
                Circle circle2 = new Circle(CIRCLE2_CENTER_X, CIRCLE_CENTER_Y, CIRCLE_RADIUS);
                wagonGroup.getChildren().addAll(rectangle1, circle1, circle2);
                caseGroup.getChildren().addAll(rectangleVoie, wagonGroup);
                obs.longestRoute1.addListener((ListChangeListener<? super Route>) (e)->{

                    for (int j = 0; j <obs.longestRoute1.size() ; j++) {
                        if(obs.longestRoute1.contains(route)){
                            rectangleVoie.setStroke(Paint.valueOf("RED"));
                            rectangleVoie.setStrokeWidth(3);
                            rectangleVoie.getStrokeDashArray().clear();


                        }
                    }


                });

                obs.longestRoute2.addListener((ListChangeListener<? super Route>) (e)->{

                    for (int j = 0; j <obs.longestRoute2.size() ; j++) {
                        if(obs.longestRoute2.contains(route)){

                            rectangleVoie.setStroke(Paint.valueOf("RED"));
                            rectangleVoie.setStrokeWidth(3);
                            rectangleVoie.getStrokeDashArray().clear();


                        }
                    }


                });
            }


            routeGroup.setOnMouseClicked((e) -> {
                List<SortedBag<Card>> possibleClaimCards = obs.possibleClaimCards(route);
                ActionHandlers.ClaimRouteHandler claimRouteH = handler.get();
                if (possibleClaimCards.size() > 1) {

                    ActionHandlers.ChooseCardsHandler chooseCardsH =
                            chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                    chooser.chooseCards(possibleClaimCards, chooseCardsH);
                } else {
                    claimRouteH.onClaimRoute(route, possibleClaimCards.get(0));
                }

            });
        }
        return carte;
    }

}










