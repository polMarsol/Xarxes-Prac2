import java.io.*;
import java.net.Socket;

public class Client {
    private static final int port = 1234;
    /*private static String host = "127.0.0.1";*/ //  Adreça IP del servidor
    private static String host = "192.168.56.1";
    public static void main(String[] args) {
        if (args.length > 0) {
            host = args[0]; // Si s'especifica una adreça IP com a argument
        }
        try {
            Socket socket = new Socket(host, port); // Connectar-se al servidor
            Thread tR = new Thread(new threadClientR(socket)); // Thread per rebre dades del servidor
            Thread tW = new Thread(new threadClientW(socket)); // Thread per enviar dades al servidor

            tR.start(); // Iniciar-los
            tW.start();
            tR.join();
            tW.join(); // Esperar que acabin

            socket.close(); // Tancar la connexió amb el servidor

        } catch (IOException | InterruptedException e) {
            System.err.println("Servidor no disponible."); // Manejar errors d'entrada/sortida o interrupcions
            // quan el servidor no està obert.
        }
    }

    // Thread per rebre dades del servidor
    private static class threadClientR implements Runnable {
        Socket s;

        private threadClientR(Socket s) {
            this.s = s;
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream()); //Obrir canal de lectura
                String str = "";
                while (!str.equals("FI")) {
                    str = dis.readUTF(); // Llegir el missatge del servidor
                    System.out.println("Server: <<" + str + ">>");
                }
                dis.close();
                s.close();
                System.exit(0); // Sortir del programa
            } catch (IOException e) {
                System.out.println("Connexió tancada.");
                System.exit(0);
            }
        }
    }

    // Fil per enviar dades al servidor
    // Fil per enviar dades al servidor
    private static class threadClientW implements Runnable {
        Socket s;

        private threadClientW(Socket s) {
            this.s = s;
        }

        public void run() {
            try {
                InputStream consola = System.in;
                BufferedReader d = new BufferedReader(new InputStreamReader(consola));
                String entrada = "";
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                DataInputStream dis = new DataInputStream(s.getInputStream());

                while (!entrada.equals("FI")) {
                    entrada = d.readLine().trim(); // Llegir l'entrada de l'usuari borrant els
                    //espais inicials i finals per evitar els que són innecessaris. Serveix d'ajuda per la lectura

                    if (!entrada.trim().isEmpty()) { //Evitar missatges buits
                        dos.writeUTF(entrada); // Enviar el missatge al servidor
                    }
                    dos.flush(); //Per esborrar el buffer i que no s'acumulin els missatges i es puguin enviar
                    // immediatament.
                }
                dos.close();
                dis.close();
                s.close();
            } catch (Exception e) {
                System.exit(0); // Sortir del programa
            }
        }
    }
}