import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BooksDB booksDB;

    public ClientHandler(Socket clientSocket, BooksDB booksDB) {
        this.clientSocket = clientSocket;
        this.booksDB = booksDB;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String request;
            while ((request = in.readLine()) != null) {
                String response;
                switch (request) {
                    case "1":
                        response = listTitles();
                        break;
                    case "2":
                        response = infoFromOneBook(in.readLine());
                        break;
                    case "3":
                        response = addBook(in.readLine(), Integer.parseInt(in.readLine()), in.readLine(), in.readLine());
                        break;
                    case "4":
                        response = deleteBook(in.readLine());
                        break;
                    default:
                        response = "Invalid option";
                        break;
                }
                out.println(response);
            }
        } catch (IOException ex) {
            System.err.println("Error handling client!");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                System.err.println("Error closing client socket!");
            }
        }
    }

    private String listTitles() {
        int numBooks = booksDB.getNumBooks();
        StringBuilder titles = new StringBuilder();
        try {
            for (int i = 0; i < numBooks; i++) {
                BookInfo book = booksDB.readBookInfo(i);
                titles.append(book.getTitle()).append("\n");
            }
        } catch (IOException ex) {
            return "Database error!";
        }
        return titles.toString();
    }

    private String infoFromOneBook(String title) {
        try {
            int n = booksDB.searchBookByTitle(title);
            if (n != -1) {
                BookInfo book = booksDB.readBookInfo(n);
                return book.toString();
            } else {
                return "Book not found.";
            }
        } catch (IOException ex) {
            return "Database error!";
        }
    }

    private String addBook(String title, int pages, String author, String series) {
        BookInfo book = new BookInfo(title, pages, author, series);
        try {
            boolean success = booksDB.insertNewBook(book);
            if (!success) {
                return "This book already exists in the database.";
            }
            return "Book added successfully.";
        } catch (IOException ex) {
            return "Database error!";
        }
    }

    private String deleteBook(String title) {
        try {
            boolean success = booksDB.deleteByTitle(title);
            if (!success) {
                return "Book not found.";
            }
            return "Book deleted successfully.";
        } catch (IOException ex) {
            return "Database error!";
        }
    }
}