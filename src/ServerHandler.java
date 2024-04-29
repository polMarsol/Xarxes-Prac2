import java.io.*;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private Socket serverSocket;

    public ServerHandler(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

            String menu = getMenu();
            out.println(menu);

            String option;
            while ((option = in.readLine()) != null) {
                out.println(option);
            }
        } catch (IOException ex) {
            System.err.println("Error handling server!");
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                System.err.println("Error closing server socket!");
            }
        }
    }

    private String getMenu() {
        return "Menu options:\n" +
                "1 - List all titles.\n" +
                "2 - Get information about a book.\n" +
                "3 - Add a book.\n" +
                "4 - Delete a book.\n" +
                "5 - Quit.";
    }
}