package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.Data.SerializedGame;
import it.polimi.ingsw.model.Enumerations.AnglePos;
import it.polimi.ingsw.model.Enumerations.CardType;
import it.polimi.ingsw.model.Enumerations.Color;
import it.polimi.ingsw.model.Enumerations.Resource;
import it.polimi.ingsw.model.GameComponents.*;
import it.polimi.ingsw.model.Player.Player;
import it.polimi.ingsw.model.Player.PlayerHand;
import it.polimi.ingsw.network.client.ClientController;
import it.polimi.ingsw.network.client.ClientSR;
import it.polimi.ingsw.network.client.commands.*;
import it.polimi.ingsw.network.server.updates.InitialCardSideUpdate;
import it.polimi.ingsw.view.ViewInterface;

import java.io.IOException;
import java.security.MessageDigestSpi;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that represents the TUI View
 */
public class View extends Thread implements ViewInterface {

    private static View instance;

    private Scanner s = new Scanner(System.in);

    private boolean chatMode = false;

    private View() {}

    public static View getInstance() {
        synchronized (View.class) {
            if (instance == null) {
                instance = new View();
            }
        }
        return instance;
    }

    /**
     * Initialize the TUI
     */
    @Override
    public void run() {
        try {
            selectInitialCardSide();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        menu();
    }

    /**
     * Clear the screen
     */
    public void clear() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    /**
     * Show the title screen
     */
    public void showTitleScreen() {
        System.out.println(TextColor.BRIGHT_GREEN +
                        " ▄▄·       ·▄▄▄▄  ▄▄▄ .▐▄• ▄      ▐ ▄  ▄▄▄· ▄▄▄▄▄▄• ▄▌▄▄▄   ▄▄▄· ▄▄▌  ▪  .▄▄ · \n" +
                        "▐█ ▌▪▪     ██▪ ██ ▀▄.▀· █▌█▌▪    •█▌▐█▐█ ▀█ •██  █▪██▌▀▄ █·▐█ ▀█ ██•  ██ ▐█ ▀. \n" +
                        "██ ▄▄ ▄█▀▄ ▐█· ▐█▌▐▀▀▪▄ ·██·     ▐█▐▐▌▄█▀▀█  ▐█.▪█▌▐█▌▐▀▀▄ ▄█▀▀█ ██▪  ▐█·▄▀▀▀█▄\n" +
                        "▐███▌▐█▌.▐▌██. ██ ▐█▄▄▌▪▐█·█▌    ██▐█▌▐█ ▪▐▌ ▐█▌·▐█▄█▌▐█•█▌▐█ ▪▐▌▐█▌▐▌▐█▌▐█▄▪▐█\n" +
                        "·▀▀▀  ▀█▄▀▪▀▀▀▀▀•  ▀▀▀ •▀▀ ▀▀    ▀▀ █▪ ▀  ▀  ▀▀▀  ▀▀▀ .▀  ▀ ▀  ▀ .▀▀▀ ▀▀▀ ▀▀▀▀ \n"
        );
    }

    /**
     * Load the screen to pick username and color
     */
    public void pickUsernameAndColor() {

        Messages.getInstance().input("Insert your username: ");

        String username = s.nextLine();

        showColors();

        if (ClientController.getInstance().getAvailableColors().size() > 1) {
            Messages.getInstance().input("Insert the number representing the color you would like to choose: ");
            String input = s.nextLine();
            int colorIndex = -1;

            boolean goodToGo = false;
            while (!goodToGo) {
                if (!isNumeric(input)) {
                    Messages.getInstance().error("Wrong format, expecting a number");
                    Messages.getInstance().input("Insert the number representing the color you would like to choose: ");
                    input = s.nextLine();
                    continue;
                }
                colorIndex = Integer.parseInt(input);
                if (colorIndex < 0 || colorIndex >= ClientController.getInstance().getAvailableColors().size()) {
                    Messages.getInstance().error("Wrong format, the number must be between 0 and " + (ClientController.getInstance().getAvailableColors().size() - 1));
                    Messages.getInstance().input("Insert the number representing the color you would like to choose: ");
                    input = s.nextLine();
                    continue;
                }
                goodToGo = true;
            }

            if (ClientController.getInstance().getAvailableColors().get(colorIndex) != null) {
                Color c = ClientController.getInstance().getAvailableColors().get(colorIndex);
                ClientController.getInstance().sendUsernameAndColor(username, c);
            }
        } else {
            Color c = ClientController.getInstance().getAvailableColors().get(0);
            ClientController.getInstance().sendUsernameAndColor(username, c);
        }
    }

    /**
     * Show available colors
     */
    public void showColors() {
        Messages.getInstance().info("Here's a list of the available colors: ");
        for(int i = 0; i < ClientController.getInstance().getAvailableColors().size(); i++) {
            System.out.println(i + ": " + ClientController.getInstance().getAvailableColors().get(i).toString());
        }
    }

    /**
     * Load the screen for selecting a personal {@link it.polimi.ingsw.model.Goals.Goal}
     */
    public void selectPersonalGoal() {
        System.out.print("1)");
        ClientController.getInstance().getGoalsToPick().get(0).draw();
        System.out.println("   Goal Score: " + TextColor.BRIGHT_YELLOW + ClientController.getInstance().getGoalsToPick().get(0).getScore() + TextColor.RESET);
        System.out.println("\n");
        System.out.print("2)");
        ClientController.getInstance().getGoalsToPick().get(1).draw();
        System.out.println("   Goal Score: " + TextColor.BRIGHT_YELLOW + ClientController.getInstance().getGoalsToPick().get(1).getScore() + TextColor.RESET);
        System.out.println("\n");
        s.reset();
        Messages.getInstance().info("Digit the number of the goal you want to choose");
        int optionChoosen = getOptionsInput(2);
        if(optionChoosen == 1) {
            ClientController.getInstance().selectPersonalGoal(ClientController.getInstance().getGoalsToPick().get(0));
        } else if(optionChoosen == 2) {
            ClientController.getInstance().selectPersonalGoal(ClientController.getInstance().getGoalsToPick().get(1));
        }

    }

    public void joinOrCreateMatch() {

        Messages.getInstance().input("Do you want to join or create a match? \nJoin: 1\nCreate: 2\n");
        int input = getOptionsInput(2);

        if (input == 1) {
            RefreshAvailableGamesCommand cmd = new RefreshAvailableGamesCommand();
            try {
                ClientSR.getInstance().sendCommand(cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            createMatch();
        }

    }

    /**
     * Create a match screen
     */
    public void createMatch() {
        int maxPlayers = 0;

        while (maxPlayers < 2 || maxPlayers > 4) {
            Messages.getInstance().input("Select the maximum number of players that can join the game (from " + TextColor.BRIGHT_YELLOW + "2" + TextColor.RESET + " to " + TextColor.BRIGHT_YELLOW + "4" + TextColor.RESET + "): ");

            String input = s.nextLine();
            if (!isNumeric(input)) {
                Messages.getInstance().error("Wrong format, expecting a number");
                continue;
            }

            maxPlayers = Integer.parseInt(input);

            if (maxPlayers < 2 || maxPlayers > 4) {
                Messages.getInstance().error("The inserted number must be between " + TextColor.BRIGHT_RED + "2" + TextColor.RESET + " and " + TextColor.BRIGHT_RED + "4" + TextColor.RESET);
            }

        }

        //s.nextLine();

        CreateMatchCommand cmd = new CreateMatchCommand(maxPlayers);
        try {
            ClientSR.getInstance().sendCommand(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Load the select available match screen
     * @param availableMatches the ArrayList of {@link SerializedGame} representing all the available matches
     * @param error
     * @throws IOException
     */
    public void selectAvailableMatch(ArrayList<SerializedGame> availableMatches, String error) {
        if (error != null) System.out.println(error);
        int possibleChoice = 1;
        if(!availableMatches.isEmpty()) {
            Messages.getInstance().info("Here's a list of the available matches: ");
            for (int i = 0; i < availableMatches.size(); i++) {
                SerializedGame g = availableMatches.get(i);
                System.out.println(possibleChoice + ": " + TextColor.BRIGHT_BLUE + g.getGameID() + TextColor.RESET + " (" + TextColor.BRIGHT_YELLOW + g.getCurrentPlayers() + TextColor.RESET + "/" + TextColor.BRIGHT_YELLOW + g.getMaxPlayers() + TextColor.RESET + ")");
                possibleChoice++;
            }
        } else {
            System.out.println(TextColor.BRIGHT_RED + "[INFO] " + TextColor.RESET + "No matches available" + TextColor.RESET);
        }
        System.out.println(possibleChoice + ": " + TextColor.BRIGHT_BLUE + "Refresh" + TextColor.RESET);
        possibleChoice++;
        System.out.println(possibleChoice + ": " + TextColor.BRIGHT_BLUE + "Back to main menu" + TextColor.RESET);
        int optionChoosen = getOptionsInput(availableMatches.size() + 2);
        if(optionChoosen == availableMatches.size() + 1) {
            try {
                RefreshAvailableGamesCommand cmd = new RefreshAvailableGamesCommand();
                ClientSR.getInstance().sendCommand(cmd);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(optionChoosen == availableMatches.size() + 2) {
            joinOrCreateMatch();
            return;
        }
        ClientController.getInstance().JoinGame(availableMatches.get(optionChoosen - 1).getGameID());
    }

    /**
     * Load the screen for the waiting room
     */
    public void waitingRoom() {
        Messages.getInstance().info("Personal goal choosen, waiting for other players!");
    }

    /**
     * Load the screen for selecting an {@link it.polimi.ingsw.model.GameComponents.InitialCard} side
     * @throws IOException
     */
    public void selectInitialCardSide() throws IOException {
        clear();
        Messages.getInstance().info("Initial Card back side:");
        InitialCard initialCard = (InitialCard) ClientController.getInstance().getCodexMap().get(ClientController.getInstance().getPlayerByUsername(ClientController.getInstance().getUsername())).getCard(new Coordinate(80, 80));
        Angle UL = initialCard.getBackAngle(AnglePos.UL);
        Angle UR = initialCard.getBackAngle(AnglePos.UR);
        Angle DL = initialCard.getBackAngle(AnglePos.DL);
        Angle DR = initialCard.getBackAngle(AnglePos.DR);

        if(UL.getResource() == null) {
            System.out.println("Top Left Corner : "+TextColor.BRIGHT_BLACK+"Blocked"+TextColor.RESET);
        } else {
            System.out.println("Top Left Corner : " + printResource(UL.getResource()) + TextColor.RESET);
        }
        if(UR.getResource() == null) {
            System.out.println("Top Right Corner : "+TextColor.BRIGHT_BLACK+"Blocked"+TextColor.RESET);
        } else {
            System.out.println("Top Right Corner : " + printResource(UR.getResource()) + TextColor.RESET);
        }
        if(DL.getResource() == null) {
            System.out.println("Bottom Left Corner : "+TextColor.BRIGHT_BLACK+"Blocked"+TextColor.RESET);
        } else {
            System.out.println("Bottom Left Corner : " + printResource(DL.getResource()) + TextColor.RESET);
        }
        if(DR.getResource() == null) {
            System.out.println("Bottom Right Corner : "+TextColor.BRIGHT_BLACK+"Blocked"+TextColor.RESET);
        } else {
            System.out.println("Bottom Right Corner : " + printResource(DR.getResource()) + TextColor.RESET);
        }
        System.out.print("Back resources:");
        for(Resource resource : initialCard.getBackResources()) {
            System.out.print(" " + printResource(resource));
        }

        System.out.println("\n");
        Messages.getInstance().input("Do you want to flip the initial card on the back? (1: Yes, 2:No)\n");
        int input = getOptionsInput(2);
        if(input == 1) {
            ClientController.getInstance().selectInitialCardSide(1);
        } else {
            ClientController.getInstance().selectInitialCardSide(0);
        }
    }

    /**
     * Show the winning screen
     */
    public void winScreen() {
        clear();
        System.out.println(
                        "\n" +
                        "                                                                          \n" +
                        "                                                                          \n" +
                        "____     ___                      ____              ___                8  \n" +
                        "`MM(     )M'                      `Mb(      db      )d' 68b           (M) \n" +
                        " `MM.    d'                        YM.     ,PM.     ,P  Y89           (M) \n" +
                        "  `MM.  d' _____  ___   ___        `Mb     d'Mb     d'  ___ ___  __   (M) \n" +
                        "   `MM d' 6MMMMMb `MM    MM         YM.   ,P YM.   ,P   `MM `MM 6MMb   M  \n" +
                        "    `MM' 6M'   `Mb MM    MM         `Mb   d' `Mb   d'    MM  MMM9 `Mb  M  \n" +
                        "     MM  MM     MM MM    MM          YM. ,P   YM. ,P     MM  MM'   MM  M  \n" +
                        "     MM  MM     MM MM    MM          `Mb d'   `Mb d'     MM  MM    MM  8  \n" +
                        "     MM  MM     MM MM    MM           YM,P     YM,P      MM  MM    MM     \n" +
                        "     MM  YM.   ,M9 YM.   MM           `MM'     `MM'      MM  MM    MM 68b \n" +
                        "    _MM_  YMMMMM9   YMMM9MM_           YP       YP      _MM__MM_  _MM_Y89 \n" +
                        "                                                                          \n" +
                        "                                                                          \n" +
                        "                                                                          \n"
        );
        System.exit(0);
    }

    /**
     * Show the loosing screen
     */
    public void looseScreen() {
        clear();
        System.out.println(
                        "\n" +
                        "                                                                                     \n" +
                        "                                                                                     \n" +
                        "____     ___                      ____                                            8  \n" +
                        "`MM(     )M'                      `MM'                                           (M) \n" +
                        " `MM.    d'                        MM                                            (M) \n" +
                        "  `MM.  d' _____  ___   ___        MM        ____     ____   (M) \n" +
                        "   `MM d' 6MMMMMb `MM    MM        MM        6MMMMb\\  6MMMMb   M  \n" +
                        "    `MM' 6M'   `Mb MM    MM        MM      6M'   `Mb MM'    ` 6M'  `Mb  M  \n" +
                        "     MM  MM     MM MM    MM        MM      MM     MM YM.      MM    MM  M  \n" +
                        "     MM  MM     MM MM    MM        MM      MM     MM  YMMMMb  MMMMMMMM  8  \n" +
                        "     MM  MM     MM MM    MM        MM      MM     MM      `Mb MM           \n" +
                        "     MM  YM.   ,M9 YM.   MM        MM    / YM.   ,M9 L    ,MM YM    d9 68b \n" +
                        "    _MM_  YMMMMM9   YMMM9MM_      _MMMMMMM   YMMMMM9  MYMMMM9   YMMMM9  Y89 \n" +
                        "                                                                                     \n" +
                        "                                                                                     \n" +
                        "                                                                                     \n"
        );
        System.exit(0);
    }

    /**
     * Show the menu
     */
    public void menu() {
        showCommands();
        while(true) {
            updateInfo(null, false);
            String input = s.nextLine().trim();
            String[] args = input.split(" ");
            String command = args[0].toLowerCase();
            switch (command) {
                case "playcard":
                    if (args.length == 5) {
                        if (!isNumeric(args[1]) || !isNumeric(args[2]) || !isNumeric(args[3])) {
                            Messages.getInstance().error("Format error. Use: playcard [#x] [#y] [#CardToPlayFromHand] [#CardToPickFromGround / ResourceDeck / GoldDeck] [optional: Turn]");
                            continue;
                        }
                        int x = Integer.parseInt(args[1]);
                        int y = Integer.parseInt(args[2]);
                        int cardToPlay = Integer.parseInt(args[3]);
                        String cardToPick = args[4];
                        playCard(x, y, cardToPlay, cardToPick, false);
                    } else if(args.length == 6 && args[5].equals("Turn")) {
                        if (!isNumeric(args[1]) || !isNumeric(args[2]) || !isNumeric(args[3])) {
                            Messages.getInstance().error("Format error. Use: playcard [#x] [#y] [#CardToPlayFromHand] [#CardToPickFromGround / ResourceDeck / GoldDeck] [optional: Turn]");
                            continue;
                        }
                        int x = Integer.parseInt(args[1]);
                        int y = Integer.parseInt(args[2]);
                        int cardToPlay = Integer.parseInt(args[3]);
                        String cardToPick = args[4];
                        playCard(x, y, cardToPlay, cardToPick, true);
                    } else {
                        Messages.getInstance().error("Format error. Use: playcard [#x] [#y] [#CardToPlayFromHand] [#CardToPickFromGround / ResourceDeck / GoldDeck] [optional: Turn]");
                    }
                    continue;

                case "inspectcodex":
                    String nameOfPlayer = args.length > 1 ? args[1] : ClientController.getInstance().getUsername();
                    if (!existsPlayer(nameOfPlayer)) {
                        Messages.getInstance().error("The specified player doesn't exist");
                        continue;
                    }
                    inspectCodex(nameOfPlayer);
                    continue;

                case "inspectcard":
                    if (args.length == 4) {
                        if (!isNumeric(args[2]) || !isNumeric(args[3])) {
                            Messages.getInstance().error("Format error. Use: inspectCard [optional: {NameOfPlayer}, default:{You}] [#x] [#y]");
                            continue;
                        }
                        nameOfPlayer = args[1];
                        int x = Integer.parseInt(args[2]);
                        int y = Integer.parseInt(args[3]);
                        if (!existsPlayer(nameOfPlayer)) {
                            Messages.getInstance().error("The specified player doesn't exist");
                            continue;
                        }
                        inspectCard(nameOfPlayer, x, y);
                    } else if (args.length == 3) {
                        if (!isNumeric(args[1]) || !isNumeric(args[2])) {
                            Messages.getInstance().error("Format error. Use: inspectCard [optional: {NameOfPlayer}, default:{You}] [#x] [#y]");
                            continue;
                        }
                        int x = Integer.parseInt(args[1]);
                        int y = Integer.parseInt(args[2]);
                        inspectCard(ClientController.getInstance().getUsername(), x, y);
                    } else {
                        Messages.getInstance().error("Format error. Use: inspectCard [optional: {NameOfPlayer}, default:{You}] [#x] [#y]");
                    }
                    continue;

                case "inspecthand":
                    if (args.length == 2) {
                        if (!isNumeric(args[1])) {
                            Messages.getInstance().error("Wrong input, expecting a number");
                            continue;
                        }
                        int cardToInspectFromHand = Integer.parseInt(args[1]);
                        inspectHand(cardToInspectFromHand);
                    } else {
                        inspectHand(-1);
                    }
                    continue;

                case "inspectground":
                    if (args.length == 2) {
                        if (!isNumeric(args[1])) {
                            Messages.getInstance().error("Wrong input, expecting a number");
                            continue;
                        }
                        int cardToInspectFromGround = Integer.parseInt(args[1]);
                        inspectGround(cardToInspectFromGround);
                    } else {
                        inspectGround(-1);
                    }
                    continue;

                case "viewgoals":
                    viewGoals();
                    continue;

                case "viewscores":
                    viewScores();
                    continue;

                case "chat":
                    chat();
                    continue;

                case "commands":
                    showCommands();
                    continue;

                default:
                    Messages.getInstance().error("Command not valid");
            }
        }
    }

    /**
     * Show the command list
     */
    public void showCommands() {
        clear();
        System.out.println(
                        "╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
                        "║                                                   Commands                                                    ║\n" +
                        "╠═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╣\n" +
                        "║ - playcard [#x] [#y] [#CardToPlayFromHand] [#CardToPickFromGround / ResourceDeck / GoldDeck] [optional: Turn] ║\n" +
                        "║ - inspectcodex [optional: {NameOfPlayer}, default:{You}]                                                      ║\n" +
                        "║ - inspectcard [optional: {NameOfPlayer}, default:{You}] [#x] [#y]                                             ║\n" +
                        "║ - inspecthand [optional: #CardToInspectFromHand]                                                              ║\n" +
                        "║ - inspectground [optional: #CardToInspectFromGround]                                                          ║\n" +
                        "║ - viewgoals                                                                                                   ║\n" +
                        "║ - viewscores                                                                                                  ║\n" +
                        "║ - chat                                                                                                        ║\n" +
                        "║ - commands                                                                                                    ║\n" +
                        "╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝"
        );

    }

    /**
     * Update the screen after a new turn
     * @param message a message in case of error
     * @param clear true if is needed to clear the screen (Only for TUI)
     */
    public void updateInfo(String message, boolean clear) {
        if(clear)
            clear();
        Messages.getInstance().info("Current Player: " + printPlayer(ClientController.getInstance().getCurrentPlayer()));
        if(message != null) {
            Messages.getInstance().error(message);
        }
        Messages.getInstance().input("Command: ");
    }

    public void playCard(int x, int y, int cardToPlay, String cardToPick, boolean turn) {
        if(ClientController.getInstance().isMyTurn()) {
            Coordinate coordinate = new Coordinate(x, y);
            Card cardPlaced = ClientController.getInstance().getPlayerHand().getCards().get(cardToPlay - 1);
            if(turn) {
                cardPlaced.turn();
            }
            if(cardToPick.equals("ResourceDeck")) {
                ClientController.getInstance().playWithPickFromDeck(coordinate, cardPlaced, 0);
            } else if(cardToPick.equals("GoldDeck")) {
                ClientController.getInstance().playWithPickFromDeck(coordinate, cardPlaced, 1);
            } else {
                ArrayList<Card> cardsToPick = new ArrayList<Card>();
                cardsToPick.addAll(ClientController.getInstance().getCardToPick());
                cardsToPick.addAll(ClientController.getInstance().getGoldCardToPick());
                Card cardPicked = cardsToPick.get(Integer.parseInt(cardToPick) - 1);
                ClientController.getInstance().playWithPickFromGround(coordinate, cardPlaced, cardPicked);
            }
        } else {
            Messages.getInstance().error("Wait your turn before playing!");
        }
    }

    /**
     * Show the inspect view for the codex
     * @param username the username of the player to show the codex to
     */
    public void inspectCodex(String username) {
        clear();
        System.out.println(getPlayerColor() +
                        " ▄▄·       ·▄▄▄▄  ▄▄▄ .▐▄• ▄ \n" +
                        "▐█ ▌▪▪     ██▪ ██ ▀▄.▀· █▌█▌▪\n" +
                        "██ ▄▄ ▄█▀▄ ▐█· ▐█▌▐▀▀▪▄ ·██· \n" +
                        "▐███▌▐█▌.▐▌██. ██ ▐█▄▄▌▪▐█·█▌\n" +
                        "·▀▀▀  ▀█▄▀▪▀▀▀▀▀•  ▀▀▀ •▀▀ ▀▀\n"
                + TextColor.RESET
        );
        Player player = ClientController.getInstance().getPlayerByUsername(username);
        Codex codex = ClientController.getInstance().getCodexMap().get(player);
        List<Integer> xCoordinates = codex.getCards().keySet().stream().map(Coordinate::getX).distinct().sorted().collect(Collectors.toList());
        List<Integer> yCoordinates = codex.getCards().keySet().stream().map(Coordinate::getY).distinct().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        System.out.printf("    ");
        for(Integer i : xCoordinates) {
            System.out.printf("     " + i);
        }
        System.out.printf("\n");
        for(Integer j : yCoordinates) {
            System.out.printf("    " + j + "   ");
            for(Integer i : xCoordinates) {
                Coordinate c = new Coordinate(i, j);
                if(codex.getCard(c) != null) {
                    System.out.printf(getCardColor(codex.getCard(c)) + "██     " + TextColor.RESET);
                } else {
                    System.out.printf("       ");
                }
            }
            System.out.printf("\n\n");
        }

        for(Resource r : codex.getNumOfResources().keySet()) {
            switch (r) {
                case FUNGI:
                    System.out.println(TextColor.RED + "Fungi: " + TextColor.RESET + codex.getNumOfResources(r));
                    break;
                case PLANT:
                    System.out.println(TextColor.GREEN + "Plant: " + TextColor.RESET + codex.getNumOfResources(r));
                    break;
                case INSECT:
                    System.out.println(TextColor.PURPLE + "Insect: " + TextColor.RESET + codex.getNumOfResources(r));
                    break;
                case ANIMAL:
                    System.out.println(TextColor.BRIGHT_BLUE + "Animal: " + TextColor.RESET + codex.getNumOfResources(r));
                    break;
                case FEATHER:
                    System.out.println(TextColor.BRIGHT_YELLOW + "Feather: " + TextColor.RESET + codex.getNumOfResources(r));
                    break;
                case JAR:
                    System.out.println(TextColor.BRIGHT_YELLOW + "Jar: " + TextColor.RESET + codex.getNumOfResources(r));
                    break;
                case SCROLL:
                    System.out.println(TextColor.BRIGHT_YELLOW + "Scroll: " + TextColor.RESET + codex.getNumOfResources(r));
                    break;
            }
        }
    }

    /**
     * Show the screen of a {@link Card}
     * @param username the username of the player
     * @param x the x coordinate of the card
     * @param y the y coordinate of the card
     */
    public void inspectCard(String username, int x, int y) {
        clear();
        Player player = ClientController.getInstance().getPlayerByUsername(username);
        Codex codex = ClientController.getInstance().getCodexMap().get(player);
        Coordinate coordinate = new Coordinate(x, y);
        Card card = codex.getCard(coordinate);
        printCardCoverage(card);
        printCardStatistics(card);
    }

    /**
     * Show the screen for inspectiing the hand of the player
     * @param cardToInspect the {@link } card to inspect from the hand
     */
    public void inspectHand(int cardToInspect) {
        clear();
        System.out.println(getPlayerColor() +
                        " ▄ .▄ ▄▄▄·  ▐ ▄ ·▄▄▄▄  \n" +
                        "██▪▐█▐█ ▀█ •█▌▐███▪ ██ \n" +
                        "██▀▐█▄█▀▀█ ▐█▐▐▌▐█· ▐█▌\n" +
                        "██▌▐▀▐█ ▪▐▌██▐█▌██. ██ \n" +
                        "▀▀▀ · ▀  ▀ ▀▀ █▪▀▀▀▀▀•\n"
                + TextColor.RESET
        );
        PlayerHand playerHand = ClientController.getInstance().getPlayerHand();
        if(cardToInspect == -1) {
            int cardNumber = 1;
            for(Card c : playerHand.getCards()) {
                String cardString = "";
                if(c.getClass() == GoldCard.class || c.getClass() == AngleGoldCard.class || c.getClass() == ResourceGoldCard.class) {
                    cardString = TextColor.BRIGHT_YELLOW + "Gold Card" + TextColor.RESET;
                } else {
                    cardString = getCardColor(c) + "Card" + TextColor.RESET;
                }
                System.out.println(cardNumber + ") " + cardString);
                cardNumber++;
            }
        } else {
            Card card = playerHand.getCards().get(cardToInspect - 1);
            printCardCoverage(card);
            printCardStatistics(card);
        }
    }

    /**
     * Show the screen for inspecting the ground
     * @param cardToInspectFromGround the {@link Card} to inspect from the ground
     */
    public void inspectGround(int cardToInspectFromGround) {
        clear();
        System.out.println(getPlayerColor() +
                        " ▄▄ • ▄▄▄        ▄• ▄▌ ▐ ▄ ·▄▄▄▄  \n" +
                        "▐█ ▀ ▪▀▄ █·▪     █▪██▌•█▌▐███▪ ██ \n" +
                        "▄█ ▀█▄▐▀▀▄  ▄█▀▄ █▌▐█▌▐█▐▐▌▐█· ▐█▌\n" +
                        "▐█▄▪▐█▐█•█▌▐█▌.▐▌▐█▄█▌██▐█▌██. ██ \n" +
                        "·▀▀▀▀ .▀  ▀ ▀█▄▀▪ ▀▀▀ ▀▀ █▪▀▀▀▀▀•\n"
                + TextColor.RESET
        );
        ArrayList<Card> cardsToPick = new ArrayList<Card>();
        cardsToPick.addAll(ClientController.getInstance().getCardToPick());
        cardsToPick.addAll(ClientController.getInstance().getGoldCardToPick());
        if(cardToInspectFromGround == -1) {
            int cardNumber = 1;
            for(Card c : cardsToPick) {
                String cardString = "";
                if(c.getClass() == GoldCard.class || c.getClass() == AngleGoldCard.class || c.getClass() == ResourceGoldCard.class) {
                    cardString = TextColor.BRIGHT_YELLOW + "Gold Card" + TextColor.RESET;
                } else {
                    cardString = getCardColor(c) + "Card" + TextColor.RESET;
                }
                System.out.println(cardNumber + ") " + cardString);
                cardNumber++;
            }
        } else {
            Card cardToInspect = cardsToPick.get(cardToInspectFromGround - 1);
            printCardCoverage(cardToInspect);
            printCardStatistics(cardToInspect);
        }
    }

    /**
     * Show the screen for all the goals
     */
    public void viewGoals() {
        clear();
        System.out.println(getPlayerColor() +
                        " ▄▄ •        ▄▄▄· ▄▄▌  .▄▄ · \n" +
                        "▐█ ▀ ▪▪     ▐█ ▀█ ██•  ▐█ ▀. \n" +
                        "▄█ ▀█▄ ▄█▀▄ ▄█▀▀█ ██▪  ▄▀▀▀█▄\n" +
                        "▐█▄▪▐█▐█▌.▐▌▐█ ▪▐▌▐█▌▐▌▐█▄▪▐█\n" +
                        "·▀▀▀▀  ▀█▄▀▪ ▀  ▀ .▀▀▀  ▀▀▀▀\n"
                + TextColor.RESET
        );
        System.out.println("Common Goals: ");
        System.out.print("1)");
        ClientController.getInstance().getCommonGoals().get(0).draw();
        System.out.print("2)");
        ClientController.getInstance().getCommonGoals().get(1).draw();
        System.out.println("Personal Goal: ");
        System.out.print("  ");
        ClientController.getInstance().getPersonalGoal().draw();
    }

    /**
     * Show the screen for the scores
     */
    public void viewScores() {
        clear();
        System.out.println(getPlayerColor() +
                    ".▄▄ ·  ▄▄·       ▄▄▄  ▄▄▄ ..▄▄ · \n" +
                    "▐█ ▀. ▐█ ▌▪▪     ▀▄ █·▀▄.▀·▐█ ▀. \n" +
                    "▄▀▀▀█▄██ ▄▄ ▄█▀▄ ▐▀▀▄ ▐▀▀▪▄▄▀▀▀█▄\n" +
                    "▐█▄▪▐█▐███▌▐█▌.▐▌▐█•█▌▐█▄▄▌▐█▄▪▐█\n" +
                    " ▀▀▀▀ ·▀▀▀  ▀█▄▀▪.▀  ▀ ▀▀▀  ▀▀▀▀\n"
                + TextColor.RESET
        );
        for(Player player : ClientController.getInstance().getPlayers()) {
            System.out.println(printPlayer(player) + " Score: " + TextColor.BRIGHT_YELLOW + ClientController.getInstance().getCodexMap().get(player).getScore() + TextColor.RESET);
        }
    }

    /**
     * Show the screen for the chat
     */
    public void chat() {
        chatMode = true;
        updateChatView("");

        while (chatMode) {
            String text = s.nextLine();
            String[] args = text.split(" ");

            if (args.length == 1) {

                if (args[0].equals("0")) {
                    chatMode = false;
                    menu();
                    return;
                }

                updateChatView("The message is too short");
                continue;
            }

            if (args[0].equals("public")) {

                ClientController.getInstance().sendPublicMessage(args);

                updateChatView("");

            } else {
                boolean found = false;

                for (Player p : ClientController.getInstance().getPlayers()) {
                    if (p.getNickname().equals(args[0])) found = true;
                }

                if (!found) {
                    updateChatView("The specified player does not exists");
                    continue;
                }

                ClientController.getInstance().sendPrivateMessage(args);

                updateChatView("");

            }

        }

    }

    /**
     * Load the screen for the chat
     * @param error
     */
    public synchronized void updateChatView(String error) {
        if (chatMode) {
            clear();
            Messages.getInstance().info("You're in the chat section");

            if (ClientController.getInstance().getMessages() != null) {
                for (String s : ClientController.getInstance().getMessages()) {
                    System.out.println(s);
                }
            }

            if (!error.equals("")) {
                Messages.getInstance().error(error);
            }

            Messages.getInstance().input("Message: ");
        }
    }

    /**
     * Show an error message
     * @param error the string of the error message
     */
    public void showError(String error) {
        Messages.getInstance().error(error);
    }

    private void printCardCoverage(Card card) {
        String cardColor = getCardColor(card);
        String black = TextColor.BRIGHT_BLACK.toString();
        boolean isULHidden = card.getAngle(AnglePos.UL).isHidden();
        boolean isURHidden = card.getAngle(AnglePos.UR).isHidden();
        boolean isDLHidden = card.getAngle(AnglePos.DL).isHidden();
        boolean isDRHidden = card.getAngle(AnglePos.DR).isHidden();

        if(isULHidden == false && isURHidden == false) {
            System.out.println(cardColor+
                    "    ┌─────────────────────────────────────────────┐\n" +
                    "    │                                             │\n" +
                    "    │                                             │\n" +
                    "    │                                             │\n" +
                    "    │                                             │"
            );
        } else if(isULHidden == true && isURHidden == false) {
            System.out.println(black+
                    "                │                                  \n" +
                    "                ├"+cardColor+"─────────────────────────────────┐\n" +black+
                    "                │"+cardColor+"                                 │\n" +black+
                    "                │"+cardColor+"                                 │\n" +black+
                    "                │"+cardColor+"                                 │\n" +black+
                    "────┬───────────┘"+cardColor+"                                 │" +TextColor.RESET
            );
        } else if(isULHidden == false && isURHidden == true) {
            System.out.println(black+
                    "                                      │                \n" +cardColor+
                    "    ┌─────────────────────────────────"+black+"┤                \n" +cardColor+
                    "    │                                 "+black+"│                \n" +cardColor+
                    "    │                                 "+black+"│                \n" +cardColor+
                    "    │                                 "+black+"│                \n" +cardColor+
                    "    │                                 "+black+"└───────────┬────" +TextColor.RESET
            );
        } else if(isULHidden == true && isURHidden == true) {
            System.out.println(black+
                    "                │                     │                \n" +
                    "                ├"+cardColor+"─────────────────────"+black+"┤                \n" +
                    "                │"+cardColor+"                     "+black+"│                \n" +
                    "                │"+cardColor+"                     "+black+"│                \n" +
                    "                │"+cardColor+"                     "+black+"│                \n" +
                    "────┬───────────┘"+cardColor+"                     "+black+"└───────────┬────" +TextColor.RESET
            );
        }

        System.out.println(cardColor+
                "    │                                             │\n" +
                "    │                                             │\n" +
                "    │                                             │" +TextColor.RESET
        );
        if(isDLHidden == false && isDRHidden ==false) {
            System.out.println(cardColor+
                    "    │                                             │\n" +
                    "    │                                             │\n" +
                    "    │                                             │\n" +
                    "    │                                             │\n" +
                    "    └─────────────────────────────────────────────┘\n" +TextColor.RESET
            );
        } else if(isDLHidden == true && isDRHidden == false) {
            System.out.println(black+
                    "────┴───────────┐"+cardColor+"                                 │    \n" +black+
                    "                │"+cardColor+"                                 │    \n" +black+
                    "                │"+cardColor+"                                 │    \n" +black+
                    "                │"+cardColor+"                                 │    \n" +black+
                    "                ├"+cardColor+"─────────────────────────────────┘    \n" +black+
                    "                │                                      \n"+TextColor.RESET
            );
        } else if(isDLHidden == false && isDRHidden == true) {
            System.out.println(cardColor+
                    "    │                                 "+black+"┌───────────┴────\n" +cardColor+
                    "    │                                 "+black+"│                \n" +cardColor+
                    "    │                                 "+black+"│                \n" +cardColor+
                    "    │                                 "+black+"│                \n" +cardColor+
                    "    └─────────────────────────────────"+black+"┤                \n" +
                    "                                      │                \n"+TextColor.RESET
            );
        } else if(isDLHidden == true && isDRHidden ==true) {
            System.out.println(black +
                    "────┴───────────┐                     ┌───────────┴────\n" +
                    "                │                     │                \n" +
                    "                │                     │                \n" +
                    "                │                     │                \n" +
                    "                ├" + cardColor + "─────────────────────" + black + "┤                \n" +
                    "                │                     │                \n" + TextColor.RESET
            );
        }
    }

    /**
     * Print the card statistics of the specified card
     * @param card the {@link Card}
     */
    private void printCardStatistics(Card card) {
        Angle UL = card.getAngle(AnglePos.UL);
        Angle UR = card.getAngle(AnglePos.UR);
        Angle DL = card.getAngle(AnglePos.DL);
        Angle DR = card.getAngle(AnglePos.DR);
        boolean isULHidden = card.getAngle(AnglePos.UL).isHidden();
        boolean isURHidden = card.getAngle(AnglePos.UR).isHidden();
        boolean isDLHidden = card.getAngle(AnglePos.DL).isHidden();
        boolean isDRHidden = card.getAngle(AnglePos.DR).isHidden();

        if (card.getClass() == Card.class) {
            System.out.println(TextColor.WHITE + "Resource Card" + TextColor.RESET);
        } else if(card.getClass() == GoldCard.class) {
            System.out.println(TextColor.BRIGHT_YELLOW + "Gold " + TextColor.WHITE + "Card" + TextColor.RESET);
        } else if(card.getClass() == AngleGoldCard.class) {
            System.out.println(TextColor.BRIGHT_YELLOW + "Angle Gold " + TextColor.WHITE + "Card" + TextColor.RESET);
        } else if(card.getClass() == ResourceGoldCard.class) {
            System.out.println(TextColor.BRIGHT_YELLOW + "Resource Gold " + TextColor.WHITE + "Card" + TextColor.RESET);
            System.out.println("Resource Type: " + printResource(((ResourceGoldCard) card).getResourceType()) + TextColor.RESET);
        }


        System.out.println(TextColor.BRIGHT_WHITE + "Card Turned" + TextColor.RESET);
        if(card.getClass() == InitialCard.class) {
            System.out.print("Back Resources:");
            for (Resource resource : ((InitialCard) card).getBackResources()) {
                System.out.print(" " + printResource(resource));
            }
            System.out.print("\n");
        }


        if(isULHidden || UL.getResource() == null) {
            System.out.println("Top Left Corner : "+TextColor.BRIGHT_BLACK+"Blocked"+TextColor.RESET);
        } else {
            System.out.println("Top Left Corner : " + printResource(UL.getResource()) + TextColor.RESET);
        }
        if(isURHidden || UR.getResource() == null) {
            System.out.println("Top Right Corner : "+TextColor.BRIGHT_BLACK+"Blocked"+TextColor.RESET);
        } else {
            System.out.println("Top Right Corner : " + printResource(UR.getResource()) + TextColor.RESET);
        }
        if(isDLHidden || DL.getResource() == null) {
            System.out.println("Bottom Left Corner : "+TextColor.BRIGHT_BLACK+"Blocked"+TextColor.RESET);
        } else {
            System.out.println("Bottom Left Corner : " + printResource(DL.getResource()) + TextColor.RESET);
        }
        if(isDRHidden || DR.getResource() == null) {
            System.out.println("Bottom Right Corner : "+TextColor.BRIGHT_BLACK+"Blocked"+TextColor.RESET);
        } else {
            System.out.println("Bottom Right Corner : " + printResource(DR.getResource()) + TextColor.RESET);
        }

        if(!card.isTurned()) {
            if(card.getCardScore() != 0) {
                System.out.println("Card Score : " + TextColor.BRIGHT_YELLOW+card.getCardScore()+TextColor.RESET);
            }
            if(card.getClass() == GoldCard.class || card.getClass() == AngleGoldCard.class || card.getClass() == ResourceGoldCard.class) {
                System.out.printf("Play Condition: ");
                for(Resource r : ((GoldCard) card).getPlayCondition()) {
                    System.out.printf(printResource(r) + " ");
                }
                System.out.printf("\n");
            }
        }

    }

    /**
     * Print the player with the correct color
     * @param player the {@link Player} to print
     * @return
     */
    public String printPlayer(Player player) {
        switch(player.getColor()) {
            case Red: return TextColor.RED + player.getNickname() + TextColor.RESET;
            case Blue: return TextColor.BLUE + player.getNickname() + TextColor.RESET;
            case Green: return TextColor.GREEN + player.getNickname() + TextColor.RESET;
            case Yellow: return TextColor.BRIGHT_YELLOW + player.getNickname() + TextColor.RESET;
        }
        return null;
    }

    /**
     * @return the {@link Color} for the player
     */
    public String getPlayerColor() {
        switch(ClientController.getInstance().getColor()) {
            case Red: return TextColor.RED + "";
            case Blue: return TextColor.BLUE + "";
            case Green: return TextColor.GREEN + "";
            case Yellow: return TextColor.BRIGHT_YELLOW + "";
        }
        return "";
    }

    /**
     * Print correctly the resource
     * @param resource the {@link Resource} to print
     * @return
     */
    public String printResource(Resource resource) {
        if (resource == null) {
            return " ";
        } else {
            switch (resource) {
                case FUNGI:
                    return TextColor.RED + "Fungi" + TextColor.RESET;
                case PLANT:
                    return TextColor.GREEN + "Plant" + TextColor.RESET;
                case INSECT:
                    return TextColor.PURPLE + "Insect" + TextColor.RESET;
                case ANIMAL:
                    return TextColor.BRIGHT_BLUE + "Animal" + TextColor.RESET;
                case FEATHER:
                    return TextColor.BRIGHT_YELLOW + "Feather" + TextColor.RESET;
                case JAR:
                    return TextColor.BRIGHT_YELLOW + "Jar" + TextColor.RESET;
                case SCROLL:
                    return TextColor.BRIGHT_YELLOW + "Scroll" + TextColor.RESET;
                case BLANK:
                    return TextColor.BRIGHT_WHITE + "Empty" + TextColor.RESET;
                default:
                    return "";
            }
        }
    }

    /**
     * Get the card color
     * @param card the {@link Card} to get the color from
     * @return a string representing the color
     */
    public String getCardColor(Card card) {
        if(card.getCardType() == null) {
            return TextColor.WHITE.toString();
        }
        switch (card.getCardType()) {
            case FUNGI:
                return TextColor.RED.toString();
            case PLANT:
                return TextColor.GREEN.toString();
            case INSECT:
                return TextColor.PURPLE.toString();
            case ANIMAL:
                return TextColor.BRIGHT_BLUE.toString();
            default:
                return "";
        }
    }

    /**
     * Get the card type color
     * @param cardType the {@link CardType} to get the color from
     * @return a string representing the color
     */
    public String getCardTypeColor(CardType cardType) {
        switch (cardType) {
            case FUNGI:
                return TextColor.RED.toString();
            case PLANT:
                return TextColor.GREEN.toString();
            case INSECT:
                return TextColor.PURPLE.toString();
            case ANIMAL:
                return TextColor.BRIGHT_BLUE.toString();
            default:
                return "";
        }
    }

    /**
     * Get the reversed card type color
     * @param cardType the {@link CardType} to get the reversed color from
     * @return a string representing the color
     */
    public String getReverseCardTypeColor(CardType cardType) {
        switch (cardType) {
            case FUNGI:
                return TextColor.GREEN.toString();
            case PLANT:
                return TextColor.PURPLE.toString();
            case INSECT:
                return TextColor.BRIGHT_BLUE.toString();
            case ANIMAL:
                return TextColor.RED.toString();
            default:
                return "";
        }
    }

    /**
     * Get a generic input from the plauer
     * @param numOfOptions number of total options
     * @return the option chosen by the player
     */
    public int getOptionsInput(int numOfOptions) {
        Messages.getInstance().input("Choose an option (1 to "+numOfOptions+"): ");
        String input = s.nextLine();
        int option = -1;

        boolean goodToGo = false;

        while(!goodToGo) {
            if (!isNumeric(input)) {
                Messages.getInstance().error("Wrong format, expecting a number");
                Messages.getInstance().input("Choose an option (1 to "+numOfOptions+"): ");
                input = s.nextLine();
                continue;
            }
            option = Integer.parseInt(input);
            if (option < 1 || option > numOfOptions) {
                Messages.getInstance().error("Option not valid, try again");
                Messages.getInstance().input("Choose an option (1 to "+numOfOptions+"): ");
                input = s.nextLine();
                continue;
            }
            goodToGo = true;
        }

        /*while (option < 1 || option > numOfOptions) {
            Messages.getInstance().error("Option not valid, try again");
            Messages.getInstance().input("Choose an option (1 to "+numOfOptions+"): ");
            option = s.nextInt();
        }
        s.nextLine();*/
        return option;
    }

    /**
     * @param string
     * @return true if the string is a number, false otherwise.
     */
    private boolean isNumeric(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param username
     * @return true if the player exists in game, false otherwise.
     */
    private boolean existsPlayer(String username) {
        for (Player p : ClientController.getInstance().getPlayers()) {
            if (p.getNickname().equals(username)) return true;
        }
        return false;
    }

}