package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.gui.guiConstants.*;

/**
 *@author Antoine Schutz (314412)
 *@author Holzer Romain (330560)
 *
 * créer la vue des informations
 */
class InfoViewCreator {

    /**
     * crée et retourne la vue des informations
     * @param playerId identité du joueur auquel l'interface correspond
     * @param player la table associative des noms des joueurs
     * @param  partie l'état de jeu observable
     * @param infos une liste (observable) contenant les informations sur le déroulement de la partie, sous la forme d'instances de Text
     * @return une instance de Pane, qui est la vue des informations créés
     */
    public static Pane createInfoView(PlayerId playerId, Map<PlayerId, String> player, ObservableGameState partie, ObservableList<Text> infos) {

        VBox base = new VBox();
        base.getStylesheets().addAll(INFO_CSS, COLORS_CSS);

        VBox base2 = new VBox();
        base2.setId(PLAYER_STATS);


        for (int i = 1; i <= PlayerId.ALL.size(); ++i) {
            PlayerId id = (i==1)? playerId : playerId.next();
            TextFlow tf = new TextFlow();
            tf.getStyleClass().add("PLAYER_" +((id==PlayerId.PLAYER_1)? "1":"2"));
            Circle c = new Circle(INFO_CIRCLE_RADIUS);
            c.getStyleClass().add(FILLED);
            Text t = new Text();
            t.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, player.get(id), partie.inHandTicket(id), partie.inHandCard(id), partie.wagons(id), partie.points(id)));
            tf.getChildren().addAll(c, t);
            base2.getChildren().add(tf);
        }
        Separator sep = new Separator(Orientation.HORIZONTAL);
        TextFlow textBase = new TextFlow();
        textBase.setId(GAME_INFO);
        Text text1 = new Text();
        textBase.getChildren().add(text1);
        List<Node> o = textBase.getChildren();
        Bindings.bindContent(o, infos);
        base.getChildren().addAll(base2, sep, textBase);
        return base;
    }
}