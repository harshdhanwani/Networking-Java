
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * The ConnectionHandler class acts a handler to serve a respective client request once the client-server connection is set up.
 *
 * Reference source: https://studres.cs.st-andrews.ac.uk/CS5001/Examples/L07-10_IO_and_Networking/CS5001_ClientServerExample/src/ConnectionHandler.java
 */

public class ConnectionHandler extends Thread {
    private InputStream inputStream; // get data from client on the input stream
    private BufferedReader bufferedReader;   // use buffered reader to read client data
    private LoggingFile loggingFile;
    private Socket socketConnection;  // socket representing TCP/IP connection to Client
    private String documentPath;

    /**
    * The following constructor initializes the variables.
    *
    * @param socketConnection socket representing TCP/IP connection to Client
    * @param documentPath the path to where the server serves a particular file requested to the client
    * @param loggingFile used to log every requests into a file
     */
    public ConnectionHandler(Socket socketConnection, String documentPath, LoggingFile loggingFile) {
        this.documentPath = documentPath;
        this.socketConnection = socketConnection;

        try {
            inputStream = socketConnection.getInputStream(); // get data from client on this input stream
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); // buffered reader to read client data
            this.loggingFile = loggingFile;
        } catch (IOException ioe) {
            loggingFile.loggingMessage("Connection Handler: " + ioe.getMessage());
        }
    }

/**
 * Run method is invoked when the Thread's start method (ch.start(); in WebServer class) is invoked.
 */
    public void run() {
        loggingFile.loggingMessage("new ConnectionHandler thread started ....");
        try {
            displayClientInfo();
        } catch (Exception e) {
            // exit cleanly for any Exception (including IOException, ClientDisconnectedException)
            loggingFile.loggingMessage("ConnectionHandler: run " + e.getMessage());
        }
        // clean and exit
        cleanUp();
        WebServer.setCurrentNumberOfClients(WebServer.getCurrentNumberOfClients() - 1);
    }
    /**
     * The method below is used to get data from the client over socket. If the readline method of buffered reader fails
     * we can deduce that the connection to the client is broken and shut down the connection on this side cleanly by throwing a DisconnectedException
     * which will be passed up the call stack to the nearest handler (catch block) in the run method.
     *
     * @throws DisconnectedException
     * @throws IOException
     */
    private void displayClientInfo() throws DisconnectedException, IOException {
            String line = bufferedReader.readLine();
            if (line != null) {
                new HttpResponse(socketConnection, documentPath, loggingFile).processHttpRequest(line);
            } else {
                throw new DisconnectedException("... connection terminated by client ...");
            }
    }

    /**
     * Method to close the socket connection, buffered reader and input stream.
     */
    private void cleanUp() {
        loggingFile.loggingMessage("ConnectionHandler: .. cleaning up and exiting ... ");
        try {
            bufferedReader.close();
            inputStream.close();
            socketConnection.close();
        } catch (IOException ioe) {
            loggingFile.loggingSevereMsg("ConnectionHandler: Clean up " + ioe.getMessage());
        }
    }
}
