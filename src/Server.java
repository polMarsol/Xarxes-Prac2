
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private static final int port = 1234;
    private static boolean clientConnectat = false; //  Variable per controlar si ja hi ha un client connectat
    private static BooksDB booksdb;
    private static DataOutputStream dos; // Añade esta línea

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(port);
            booksdb = new BooksDB("booksDB.dat");
            while (true) {
                Socket s = ss.accept(); // Acceptar la connexió del client
                dos = new DataOutputStream(s.getOutputStream());
                    // Iniciar fils per gestionar la comunicació amb el client
                Thread tR = new Thread(new threadServerR(s));
                System.err.println("Connexió acceptada.");
                tR.start(); // Iniciar-los
            }
        } catch (IOException e) {
            System.err.println("Servidor no disponible. Ja està en ús.");
        }
    }

    // Thread per escoltar al client
    // Thread per escoltar al client
// Thread per escoltar al client
    private static class threadServerR implements Runnable {
        private final Socket s;

        public threadServerR(Socket s) {
            this.s = s;
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                int str;

                while (true) {
                    str = dis.readInt(); // Llegir el missatge del client
                    switch (str) {
                        case 1:
                            // Llista tots els títols
                            listTitles();
                            break;
                        case 2:
                            // Obté la informació d'un llibre
                            listInfoFromOneBook(dis);
                            break;
                        case 3:
                            addBook(dis, dos);
                            break;
                        case 4:
                            deleteBook(dis, dos);
                            break;
                        case 5:
                            String desconnectar = dis.readUTF();
                            System.out.println(desconnectar);
                            dis.close();
                            s.close();
                            clientConnectat = false; // Sortir del programa
                            return;
                        // Sortir
                        default:
                            // Opción no válida
                            break;
                    }
                }

            } catch (IOException e) {
                System.out.println("Connexió tancada.");
                System.exit(0); // Sortir del programa
            }
        }

        private void deleteBook(DataInputStream dis, DataOutputStream dos) {
                try {
                    String title = dis.readUTF();
                    boolean borrat = booksdb.deleteByTitle(title);
                    dos.writeBoolean(borrat);
                } catch (IOException ex) {
                    System.err.println("Database error!");
                }
            }


        private void addBook(DataInputStream dis, DataOutputStream dos) {
                try {
                    dos.writeUTF("Introdueix el títol del llibre: ");
                    String title = dis.readUTF();;
                    int numPages = dis.readInt();
                    dos.writeUTF("Introdueix l'autor del llibre (deixe'l buit si és anònim): ");
                    String author = dis.readUTF();
                    dos.writeUTF("Especifica la sèrie (buida si és un llibre solt): ");
                    String series = dis.readUTF();
                    BookInfo book = new BookInfo(title, numPages, author, series);
                    if(booksdb.insertNewBook(book)) {
                        dos.writeUTF("Llibre afegit correctament.");
                        dos.flush();
                    } else {
                        dos.writeUTF("Aquest llibre ja estava a la base de dades.");
                        dos.flush();
                    }

                } catch (IOException ex) {
                    System.err.println("Database error!");
                }
        }


        private void listInfoFromOneBook(DataInputStream dis) {
            try {
                String title = dis.readUTF();
                int numTitles = booksdb.searchBookByTitle(title);
                boolean exists;
                if(numTitles != -1) {
                    exists = true;
                    dos.writeBoolean(exists);
                    dos.flush();
                    BookInfo book = booksdb.readBookInfo(numTitles);
                    dos.writeUTF(book.toString());
                    dos.flush();
                } else {
                    exists = false;
                    dos.writeBoolean(exists);
                    dos.flush();
                }
            } catch (IOException ex) {
                System.err.println("Database error!");
            }
        }
    }

    // Thread per enviar dades al client
    private static void listTitles() {
            try {
                int numBooks = booksdb.getNumBooks();
                dos.writeInt(numBooks);
                for (int i = 0; i < numBooks; i++) {
                    BookInfo book = booksdb.readBookInfo (i);
                    dos.writeUTF(book.getTitle());
                }
                dos.flush();
            } catch (IOException ex) {
                System.err.println ("Database error!");
            }
    }

}
