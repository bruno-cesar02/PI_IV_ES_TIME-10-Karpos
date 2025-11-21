package servidor;

import comum.*;
import servidor.dbConection.DBConection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerSemBanco {
    public static void main(String[] args) throws Exception {
        int porta = 5050;
        RepositorioClientes repo = new RepositorioClientesEmMemoria(); // apenas memória

        // 1) Tenta conectar no Mongo, mas NÃO derruba o servidor se der erro
        try {
            System.out.println("Tentando conectar ao MongoDB...");
            DBConection.conectarMongoDB("Karpos-PI");
            System.out.println("Conexão MongoDB estabelecida com sucesso!");
        } catch (Exception e) {
            System.err.println("Falha ao conectar no MongoDB (servidor vai continuar rodando mesmo assim): "
                    + e.getMessage());
            e.printStackTrace();
        }

        // 2) Sobe o servidor de socket
        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor ouvindo na porta " + porta);

            while (true) {
                Socket s = servidor.accept();
                System.out.println("Nova conexão recebida de " + s.getRemoteSocketAddress());

                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream  in  = new ObjectInputStream(s.getInputStream());

                new Thread(new TratadoraDePedidos(in, out, repo)).start();
            }
        }
    }
}
