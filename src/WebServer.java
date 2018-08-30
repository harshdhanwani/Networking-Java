
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * The Web Server class is used to handle the requests from the clients.
 *
 * ENHANCEMENT:  Support for multiple concurrent client connection requests up to a specified limit implemented.
 *
 * Reference source: https://studres.cs.st-andrews.ac.uk/CS5001/Examples/L07-10_IO_and_Networking/CS5001_ClientServerExample/src/Server.java
 */

class WebServer {

    private static int currentNumberOfClients = 0;
    private ServerSocket mServerSocket; // listen for client connection requests on this server socket.
    private LoggingFile mLoggingFile;

    /**
     * Getter method to get the number of current clients connected to the server.
     * @return current number of clients.
     */
    static int getCurrentNumberOfClients() {
        return currentNumberOfClients;
    }

    /**
     * Setter method to set the number of current clients connected to the server.
     * @param currentNumberOfClients current number of clients.
     */
    static void setCurrentNumberOfClients(int currentNumberOfClients) {
        WebServer.currentNumberOfClients = currentNumberOfClients;
    }

    /**
     * Here, the server listens for the client connection requests on this particular server socket.
     * It then waits until client requests a connection, then returns connection(socket).
     * A new handler for the connection is created then to support multiple concurrent client connection requests up to a specified limit.
     * The handler is started.
     *
     * @param documentPath the path to where the server serves a particular file requested to the client
     * @param port the port that the socket would listen to.
     * @param logPath the path to where the log file with all the details will be logged.
     * @param clientConnectionLimit a certain limit for the number of client connections.
     */
    WebServer(String documentPath, int port, String logPath, int clientConnectionLimit) {
        try {
            mLoggingFile = new LoggingFile(logPath);
            mServerSocket = new ServerSocket(port);

            mLoggingFile.loggingMessage(" Server has started ... listening on port " + port + " ....");

            while (true) {
                Socket socketConnection = mServerSocket.accept();
                mLoggingFile.loggingMessage(" Server got new connection request from " + socketConnection.getInetAddress());

                if (currentNumberOfClients < clientConnectionLimit) {
                    setCurrentNumberOfClients(getCurrentNumberOfClients() + 1);
                   ConnectionHandler connectionHandler = new ConnectionHandler(socketConnection, documentPath, mLoggingFile);
                   connectionHandler.start();
                } else {
                    mLoggingFile.loggingWarning("CLIENT CONNECTION LIMIT EXCEEDED!!");
                }
            }
        } catch (IOException ioe) {
            Logger.getLogger(WebServer.class.getName()).severe("Error" + ioe.getMessage());
        }
    }

}
