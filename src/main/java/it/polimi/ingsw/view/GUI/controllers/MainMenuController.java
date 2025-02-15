package it.polimi.ingsw.view.GUI.controllers;

import it.polimi.ingsw.network.client.ClientController;
import it.polimi.ingsw.network.client.ClientSR;
import it.polimi.ingsw.network.client.commands.RefreshAvailableGamesCommand;
import it.polimi.ingsw.view.GUI.GUIApplication;
import it.polimi.ingsw.view.GUI.SceneEnum;
import it.polimi.ingsw.view.TUI.View;
import javafx.fxml.FXML;

import java.io.IOException;

/**
 * The MainMenuController class handles the interactions for the main menu of the GUI.
 */
public class MainMenuController extends ViewController {
    /**
     * Handles the action of navigating to the game list menu
     * @throws IOException
     */
    @FXML
    public void game_list() throws IOException {
        try {
            RefreshAvailableGamesCommand cmd = new RefreshAvailableGamesCommand();
            ClientSR.getInstance().sendCommand(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action of navigating to the create game menu
     * @throws IOException
     */
    @FXML
    public void create_game() throws IOException {
        ((GUIApplication) ClientController.getInstance().getViewInterface()).setMainScene(SceneEnum.CREATE_GAME_MENU);
    }

    /**
     * Handles the action of exiting the application.
     */
    @FXML
    public void exit(){
        System.exit(0);
    }
}