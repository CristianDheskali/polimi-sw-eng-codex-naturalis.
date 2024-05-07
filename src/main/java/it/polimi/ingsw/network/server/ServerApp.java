package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.server.handler.SocketClientHandler;
import it.polimi.ingsw.view.TextColor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Server App Class
 */
public class ServerApp extends Thread {

    private ServerSocket socketServer;

    private ArrayList<SocketClientHandler> socHandlers;

    private GamesManager manager;

    /**
     * Start the socket server and the thread to keep it running
     * @param port
     * @throws IOException
     */
    public void startSocket(int port) throws IOException {
        try {
            socketServer = new ServerSocket(port);
            socHandlers = new ArrayList<>();
            manager = GamesManager.getInstance();
            this.start();
            System.out.println(TextColor.BRIGHT_BLUE + "[SOCKET SERVER]" + TextColor.RESET + " Server Socket" + TextColor.GREEN + " Ready");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(TextColor.RED + "[SOCKET SERVER]" + TextColor.RESET + " ERROR WHILE STARTING SOCKET SERVER");
        }
    }

    public void run() {
        try {
            while (!this.isInterrupted()) {
                socHandlers.add(new SocketClientHandler(socketServer.accept()));
                socHandlers.get(socHandlers.size() - 1).start();
                manager.addConnection(socHandlers.get(socHandlers.size() - 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
