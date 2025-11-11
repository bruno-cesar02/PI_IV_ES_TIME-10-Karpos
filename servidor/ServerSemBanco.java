import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSemBanco {
    public static void main(String[] args) throws Exception {
        int porta = 5050;
        RepositorioClientes repo = new RepositorioClientesEmMemoria(); // apenas memória
        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor ouvindo na porta " + porta);
            while (true) {
                Socket s = servidor.accept();
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream  in  = new ObjectInputStream(s.getInputStream());
                new Thread(new TratadoraDePedidos(in, out, repo)).start();
            }
        }
    }
}
