
import java.io.*;
import java.net.Socket;

/**
 * The HttpResponse class is to handle the Http Requests.
 */
public class HttpResponse {

    private static final String HTML_TEXT = "text/html";
    private static final int BYTE_LIMIT = 4096;

    private Socket socketConnection;
    private String documentPath;
    private LoggingFile mLoggingFile;
    private PrintWriter mPrintWriter;

    /**
     * HttpResponse constructor to initialize the values.
     *
     * @param socketConnection the socket connection with a client.
     * @param documentPath the path to where the server serves a particular file requested to the client
     * @param mLoggingFile to store the log requests and other details in a log file
     */
    public HttpResponse(Socket socketConnection, String documentPath, LoggingFile mLoggingFile) {
        this.socketConnection = socketConnection;
        this.documentPath = documentPath;
        this.mLoggingFile = mLoggingFile;
    }

    /**
     * Method to return the header containing information about the resource/file in the request.
     * @param responseCode response code
     * @param contentType this refers to the type of content resource requested
     * @param resourceLength this refers to the size of the content resource requested
     * @return
     */
    private String getHttpResponseHeader(String responseCode, String contentType, long resourceLength) {
        return "HTTP/1.1 " + responseCode + "\r\n" + "Content-Type: " + contentType + "\r\n" + "Content-Length: " + resourceLength + "\r\n";
    }

    /**
     *ENHANCEMENT: Return binary images (GIF, JPEG, PNG).
     *
     * @param resourcePath the path of the resource file
     */
    private void sendBinaryImgRequest(String resourcePath) {
        try {
            InputStream inputStream = new FileInputStream(resourcePath);
            OutputStream outputStream = socketConnection.getOutputStream();

            byte[] mByte = new byte[BYTE_LIMIT];
            int lengthOfChars;

            while ((lengthOfChars = inputStream.read(mByte)) != -1) {
                outputStream.write(mByte, 0, lengthOfChars);
            }

            outputStream.close();
            inputStream.close();
        } catch (FileNotFoundException fne) {
            mLoggingFile.loggingWarning(resourcePath + " is not found!!");
        } catch (IOException ioe) {
            mLoggingFile.loggingWarning(" IOException: " + ioe.getMessage());
        }
    }

    /**
     * Supporting and responding to HEAD requests.
     *
     * @param resourceName name of resource file
     */
    private void mimeHEAD(String resourceName) {
        File file = new File(documentPath + resourceName);

        if (file.exists()) {
            mPrintWriter.println(getHttpResponseHeader("200 OK", getHttpFileExtension(resourceName), file.length()));
            mLoggingFile.loggingRespond("200 OK", socketConnection.getInetAddress());
        } else {
            httpResponseNotFound(resourceName);
        }
    }

    /**
     * Supporting and responding to GET requests.
     *
     * @param fileName name of the file
     */
    private void mimeGET(String fileName) {
        File file = new File(documentPath + fileName);

        if (file.exists()) {
            mPrintWriter.println(getHttpResponseHeader("200 OK", getHttpFileExtension(fileName), file.length()));
            sendBinaryImgRequest(documentPath + fileName);

            mLoggingFile.loggingRespond("200 OK", socketConnection.getInetAddress());
        } else {
            httpResponseNotFound(fileName);
        }
    }

    /**
     * ENHANCEMENT: Supporting and responding to DELETE requests.
     *
     * @param fileName name of the file
     */
    private void mimeDELETE(String fileName) {
        File file = new File(documentPath + fileName);

        if (file.exists()) {
            mPrintWriter.println(getHttpResponseHeader("200 OK", getHttpFileExtension(fileName), 0));

            if (file.delete()) {
                mLoggingFile.loggingWarning(fileName + " has been deleted");
            } else {
                mLoggingFile.loggingWarning(fileName + " has not been deleted");
            }
        } else {
            httpResponseNotFound(fileName);
        }
    }

    /**
     * If the requested resource is not found or is invalid, 404 File Not Found header is given as a response following the Html page.
     *
     * @param resourceName name of the resource
     */
    private void httpResponseNotFound(String resourceName) {
        String htmlPage = getHttpResponseHtmlPage("404 Not Found", resourceName);

        mPrintWriter.println(getHttpResponseHeader("404 Not Found", getHttpFileExtension(resourceName), htmlPage.length()));
        mPrintWriter.print(htmlPage);

        mLoggingFile.loggingRespond("404 Not Found", socketConnection.getInetAddress());
    }

    /**
     * If the requested resource is not not recognised by the server, 404 File Not Implemented is given as a response following the Html page.
     *
     * @param fileName name of the resource
     */
    private void httpResponseNotImplemented(String fileName) {
        mPrintWriter.println(getHttpResponseHeader("501 Not Implemented", HTML_TEXT, 0));

        mLoggingFile.loggingMessage("File Name: " + fileName + " is not recognised by the server!");
        mLoggingFile.loggingRespond(" 501 Not Implemented", socketConnection.getInetAddress());
    }

    /**
     * Method to return the content of the html page with response message and the response name.
     *
     * @param responseMessage message to be returned as a response
     * @param responseName name of the response message.
     * @return
     */
    private String getHttpResponseHtmlPage(String responseMessage, String responseName) {
        if (responseMessage.equals("404 Not Found")) {
            return "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html>"
                    + "<head><title>" + responseMessage + "</title></head>"
                    + "<body><h1>" + responseMessage + "</h1><p> The requested URL " + responseName + " was not found on this server. </p></body></html>";
        }

        return "";
    }

    /**
     * Method to get the file extension.
     *
     * @param fileName name of the file.
     * @return
     */
    private String getHttpFileExtension(String fileName) {
        String fileExtention = "";
        int number = fileName.lastIndexOf('.');
        if (number > 0) {
            fileExtention = fileName.substring(number + 1);
        }

        switch (fileExtention) {
            case "html":
                return HTML_TEXT;
            case "gif":
                return "gif";
            case "jpeg":
                return "jpeg";
            case "png":
                return "png";
            default:
        }
        return "";
    }

    /**
     * Method to process the Http Requests depending on the respective request types. In this case: HEAD, GET and DELETE.
     *
     * @param httpRequest the http request
     * @throws IOException IOException
     */
    public void processHttpRequest(String httpRequest) throws IOException {
        mPrintWriter = new PrintWriter(this.socketConnection.getOutputStream(), true);

        String[] httpRequestHeader = httpRequest.split("\\s+");
        String httpRequestCode = httpRequestHeader[0];

        mLoggingFile.loggingRequest(httpRequestCode, socketConnection.getInetAddress());

        if (httpRequestCode.equals("HEAD")) {
            mimeHEAD(httpRequestHeader[1]);
        } else if (httpRequestCode.equals("GET")) {
            mimeGET(httpRequestHeader[1]);
        } else if (httpRequestCode.equals("DELETE")) {
            mimeDELETE(httpRequestHeader[1]);
        } else {
                httpResponseNotImplemented(httpRequestCode);
        }

        mPrintWriter.close();
    }
}
