package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
/**
 *@author Antoine Schutz (314412)
 *@author Holzer Romain (330560)
 *
 *Le programme principal du client tCHu
 *
 */
public class ClientMain  extends Application {
    private static final String DEFAULT_HOST ="localhost";
    public static final int DEFAULT_PORT =5108;



    public static void main(String[] args) { launch(args); }


    /**
     * Démarre le client en analysant les arguments passé au programme
     * @param primaryStage (non utilisé)
     */
    @Override
    public void start(Stage primaryStage) {
        Platform.setImplicitExit(false);
        getParameters().getRaw();
        String host = getParameters().getRaw().isEmpty() ? DEFAULT_HOST : getParameters().getRaw().get(0);
        int port = getParameters().getRaw().isEmpty() ? DEFAULT_PORT : Integer.parseInt(getParameters().getRaw().get(1));
        RemotePlayerClient client = new RemotePlayerClient(new GraphicalPlayerAdapter(),host,port);
        new Thread(client::run).start();
    }
}
