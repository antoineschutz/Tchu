package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 *
 * @author Antoine Schutz (314412)
 * @author Holzer Romain (330560)
 *  les différents «gestionnaires d'actions».
 */
public interface ActionHandlers {

    /**
     * Gestionnaire appelé lorsque le joueur désire tirer des billets
     */
    @FunctionalInterface
    interface DrawTicketsHandler {
        void onDrawTickets();
    }

    /**
     * Gestionnaire  appelé lorsque le joueur désire tirer une carte de l'emplacement donné
     */
    @FunctionalInterface
    interface DrawCardHandler {
        void onDrawCards(int num);
    }

    /**
     * Gestionnaire appelée lorsque le joueur désire s'emparer de la route donnée au moyen des cartes (initiales) données
     */
    @FunctionalInterface
    interface ClaimRouteHandler {
        void onClaimRoute(Route route, SortedBag<Card> cartes);
    }

    /**
     * Gestionnaire appelée lorsque le joueur a choisi de garder les billets donnés suite à un tirage de billets
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {
        void onChooseTickets(SortedBag<Ticket> billets);
    }

    /**
     * Gestionnaire est appelée lorsque le joueur a choisi d'utiliser les cartes données comme cartes initiales ou additionnelles lors de la prise de possession d'une route
     */
    @FunctionalInterface
    interface ChooseCardsHandler {
        void onChooseCards(SortedBag<Card> cartes);
    }

}
