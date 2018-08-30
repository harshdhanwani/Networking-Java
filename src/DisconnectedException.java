/**
 * The DisconnectedException class handles the case where the server would fail to read incoming requests from the client over a
 * socket connection.
 *
 * Reference Source: https://studres.cs.st-andrews.ac.uk/CS5001/Examples/L07-10_IO_and_Networking/CS5001_ClientServerExample/src/DisconnectedException.java
 */
class DisconnectedException extends Exception {

    /**
     * Constructor method to send out the exception message over.
     * @param exceptionMessage exception message.s
     */
    DisconnectedException(String exceptionMessage) {
        super(exceptionMessage);
    }

}
