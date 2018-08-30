/**
 * The WebServerMain is a class to connect and run the server.
 *
 *
 * Reference Source: https://studres.cs.st-andrews.ac.uk/CS5001/Examples/L07-10_IO_and_Networking/CS5001_ClientServerExample/src/ServerMain.java
 */
public class WebServerMain {

    private static final String LOG_FILE_PATH = System.getProperty("user.dir") + "/logs";
    private static final int CLIENT_CONNECTION_LIMIT = 125;
    /**
     * Main method to connect to the server and run it.
     *
     * @param args the arguments passed in. Here, it is the document path, port, the log file path and the client connection limit.
     */
    public static void main(String[] args) {
        try {
            String documentPath = args[0];
            int port = Integer.parseInt(args[1]);
            new WebServer(documentPath, port, LOG_FILE_PATH, CLIENT_CONNECTION_LIMIT);

        } catch (Exception e) {
            System.out.println("Usage: java WebServerMain <document_root> <port>");
        }
    }
}
