

package client;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;


public class Client {
    private static final int port = 1234;
    private static String host = "127.0.0.1"; //  Adreça IP del servidor
    /*private static String host = "192.168.56.1";*/
    public static void main(String[] args) {
        if (args.length > 0) {
            host = args[0]; // Si s'especifica una adreça IP com a argument
        }
        try {
            Socket socket = new Socket(host, port); // Connectar-se al servidor
            Thread tW = new Thread(new threadClientW(socket)); // Thread per enviar dades al servidor

            tW.start();
            tW.join(); // Esperar que acabin

            socket.close(); // Tancar la connexió amb el servidor

        } catch (IOException | InterruptedException e) {
            System.err.println("Servidor no disponible."); // Manejar errors d'entrada/sortida o interrupcions
            // quan el servidor no està obert.
            System.exit(0);
        }
    }
    private static class threadClientW implements Runnable {
        Socket s;
        private final DataOutputStream dos;
        private final DataInputStream dis;
        private final ObjectOutputStream oos;

        private threadClientW(Socket s) throws IOException {
            this.s = s;
            this.dos = new DataOutputStream(s.getOutputStream());
            this.oos = new ObjectOutputStream(s.getOutputStream());
            this.dis = new DataInputStream(s.getInputStream());
        }

        public void run() {
            try {
                InputStream consola = System.in;
                BufferedReader d = new BufferedReader(new InputStreamReader(consola));
                String entrada;
                while (true) {
                    printMenu();
                    entrada = d.readLine().trim();
                    if (!entrada.trim().isEmpty()) {
                        int opcio = getOption(entrada, dos);
                        switch(opcio) {
                            case 1:
                                listTitles(dis);
                                break;
                            case 2:
                                listInfoFromOneBook(dis, dos);
                                break;
                            case 3:
                                addBook(oos);
                                break;
                            case 4:
                                deleteBook(dis, dos);
                                break;
                            case 5:
                                dos.writeUTF("Client desconnectat.");
                                dos.close();
                                dis.close();
                                s.close();
                                System.exit(0);
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (SocketException e) {
                System.err.println("Servidor no disponible.");
                System.exit(0);
            } catch (Exception e) {
                System.exit(0);
            }
        }

        private void deleteBook(DataInputStream dis, DataOutputStream dos) {

            try {
                BufferedReader d = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Introdueix el títol del llibre a eliminar: ");
                String title = d.readLine();
                dos.writeUTF(title);
                dos.flush();
                boolean deleted = dis.readBoolean();
                if(deleted){
                    System.out.println("Llibre eliminat.");
                } else {
                    System.out.println("Llibre no trobat.");
                }
            } catch (IOException ex) {
                System.err.println("Database error.");
            }
        }

        private void addBook(ObjectOutput oos) {
            try {
                BufferedReader d = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Escriu el títol del llibre a afegir: ");
                String titol = d.readLine();
                while (titol == null || titol.isEmpty()) {
                    System.out.println ("El títol del llibre no pot ser buit.");
                    System.out.println ("Escriu el títol del llibre a afegir: ");
                    titol = d.readLine();
                }
                int pages = -1;
                while (pages < 0) {
                    System.out.println("Introdueix el número de pàgines del llibre: ");
                    String nPagesStr = d.readLine();
                    if (nPagesStr != null) {
                        try {
                            pages = Integer.parseInt(nPagesStr);
                        } catch (NumberFormatException ex) {
                            // Ignore
                        }
                    }
                }

                System.out.println("Escriu l'autor del llibre: (deixe-ho en blanc si és anònim): ");
                String author = d.readLine();
                if (author == null) {
                    author = "";
                }
                System.out.println("Especifica la sèrie (buida si un llibre solt): ");
                String series = d.readLine();
                if (series == null) {
                    series = "";
                }
                BookInfo book = new BookInfo(titol, pages, author, series);
                oos.writeObject(book);
                String trobar = dis.readUTF();
                System.out.println(trobar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void listInfoFromOneBook(DataInputStream dis, DataOutputStream dos) throws IOException {
            System.out.println("Introdueix el títol del llibre: ");
            BufferedReader d = new BufferedReader(new InputStreamReader(System.in));
            String title = d.readLine();
            dos.writeUTF(title);
            dos.flush();
            boolean exists = dis.readBoolean();
            if (!exists) {
                System.out.println("Llibre no trobat.");
            } else {
                System.out.println("Llibre trobat.");
                String bookInfo = dis.readUTF();
                System.out.println(bookInfo);
            }
        }

        private void listTitles(DataInputStream dis) throws IOException {
            int numLlibres = dis.readInt();
            for (int i = 0; i < numLlibres; i++) {
                String titol = dis.readUTF();
                System.out.println(titol);
            }
        }

        private int getOption(String entrada, DataOutputStream dos) throws IOException {
            try {
                int opcio = Integer.parseInt(entrada);
                dos.writeInt(opcio);
                dos.flush();
                return opcio;
            } catch (NumberFormatException e) {
                System.out.println("Opció no vàlida.");
                return -1;
            }
        }
    }
    private static void printMenu() {
        System.out.println("");
        System.out.println ("Menú d'opcions:");
        System.out.println ("1 - Llista tots els títols.");
        System.out.println ("2 - Obté la informació d'un llibre.");
        System.out.println ("3 - Afegeix un llibre.");
        System.out.println ("4 - Elimina un llibre.");
        System.out.println ("5 - Sortir.");
    }
}