import client.BookInfo;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private static final int port = 1234;
    //  Variable per controlar si ja hi ha un client connectat
    private static BooksDB booksdb;
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(port);
            booksdb = new BooksDB("booksDB.dat");
            while (true) {
                Socket s = ss.accept(); // Acceptar la connexió del client
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
        private final DataOutputStream dos;
        private final ObjectInputStream ois;

        public threadServerR(Socket s) throws IOException {
            this.s = s;
            this.dos = new DataOutputStream(s.getOutputStream());
            this.ois = new ObjectInputStream(s.getInputStream());
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
                            listTitles(dos);
                            break;
                        case 2:
                            // Obté la informació d'un llibre
                            listInfoFromOneBook(dis, dos);
                            break;
                        case 3:
                            addBook(ois);
                            break;
                        case 4:
                            deleteBook(dis, dos);
                            break;
                        case 5:
                            String desconnectar = dis.readUTF();
                            System.out.println(desconnectar);
                            dis.close();
                            dos.close();
                            s.close();
                            return;
                        // Sortir
                        default:
                            // Opción no válida
                            break;
                    }
                }

            } catch (IOException e) {
                System.out.println("Connexió tancada.");
                /*                System.exit(0); // Sortir del programa*/
            }
        }

        private void deleteBook(DataInputStream dis, DataOutputStream dos) {
            synchronized (booksdb) {
                try {
                    String title = dis.readUTF();
                    boolean borrat = booksdb.deleteByTitle(title);
                    dos.writeBoolean(borrat);
                } catch (IOException ex) {
                    System.err.println("Operació cancel·lada.");
                }
            }
        }


        private void addBook(ObjectInputStream ois) {
            synchronized (booksdb) {
                try {
                    BookInfo book = (BookInfo) ois.readObject();
                    if (booksdb.insertNewBook(book)) {
                        dos.writeUTF("Llibre afegit correctament.");
                        dos.flush();
                    } else {
                        dos.writeUTF("Aquest llibre ja estava a la base de dades.");
                        dos.flush();
                    }

                } catch (IOException | ClassNotFoundException ex) {
                    System.err.println("Operació cancel·lada.");
                }
            }
        }


        private void listInfoFromOneBook(DataInputStream dis, DataOutputStream dos) {
            synchronized (booksdb) {
                try {
                    String title = dis.readUTF();
                    int bookIndex = booksdb.searchBookByTitle(title);
                    if (bookIndex != -1) {
                        dos.writeBoolean(true);
                        BookInfo book = booksdb.readBookInfo(bookIndex);
                        byte[] bookBytes = book.toBytes();
                        dos.writeInt(bookBytes.length); // Send the length of the byte array
                        dos.write(bookBytes); // Send the byte array
                    } else {
                        dos.writeBoolean(false);
                    }
                    dos.flush();
                } catch (IOException ex) {
                    System.err.println("Operació cancel·lada.");
                }
            }
        }
    }

    // Thread per enviar dades al client
    private static void listTitles(DataOutputStream dos) {
        synchronized (booksdb) {

            try {
                int numBooks = booksdb.getNumBooks();
                dos.writeInt(numBooks);
                for (int i = 0; i < numBooks; i++) {
                    BookInfo book = booksdb.readBookInfo (i);
                    dos.writeUTF(book.getTitle());
                }
                dos.flush();
            } catch (IOException ex) {
                System.err.println ("Error al llegir la base de dades.");
            }
        }
    }

}