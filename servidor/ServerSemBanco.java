package servidor;

import comum.*;
import servidor.dbConection.DBConection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerSemBanco {

    // Flag para controlar o loop principal
    private static volatile boolean ativo = true;

    public static void main(String[] args) {
        int porta = 5050;
        RepositorioClientes repo = new RepositorioClientesEmMemoria();

        // Lista de todos os clientes conectados (Parceiros)
        ArrayList<Parceiro> usuarios = new ArrayList<>();

        // 1) Tenta conectar ao MongoDB
        try {
            System.out.println("Tentando conectar ao MongoDB...");
            DBConection.conectarMongoDB("Karpos-PI");
            System.out.println("Conexão MongoDB estabelecida com sucesso!");
        } catch (Exception e) {
            System.err.println("Falha ao conectar no MongoDB: " + e.getMessage());
            e.printStackTrace();
        }

        ServerSocket servidor = null;

        try {
            // 2) Sobe o servidor de socket
            servidor = new ServerSocket(porta);
            System.out.println("Servidor ouvindo na porta " + porta);

            // 2.1) Thread para ler comando "desativar" do console
            ServerSocket finalServidor = servidor;
            Thread consoleAdmin = new Thread(() -> {
                Scanner teclado = new Scanner(System.in);
                System.out.println("Digite 'desativar' para encerrar o servidor.");

                while (true) {
                    String linha = teclado.nextLine();
                    if (linha == null) continue;

                    linha = linha.trim().toLowerCase();

                    if ("desativar".equals(linha)) {
                        System.out.println("Comando 'desativar' recebido.");
                        System.out.println("Avisando todos os clientes e desligando o servidor...");

                        // Envia ComunicadoDeDesligamento para todos os clientes
                        synchronized (usuarios) {
                            for (Parceiro p : usuarios) {
                                try {
                                    p.receba(new ComunicarDesligamento());
                                } catch (Exception ex) {
                                    // Se o cliente já estiver desconectado, ignoramos
                                }
                            }
                        }

                        // Para o loop principal
                        ativo = false;

                        // Fecha o ServerSocket para fazer o accept() sair
                        try {
                            finalServidor.close();
                        } catch (Exception ignore) {}

                        break;
                    } else {
                        System.out.println("Comando desconhecido: " + linha +
                                " (use 'desativar')");
                    }
                }
            });
            consoleAdmin.setDaemon(true);
            consoleAdmin.start();

            // 3) Loop principal de aceitação de conexões
            while (ativo) {
                try {
                    Socket s = servidor.accept();
                    System.out.println("Nova conexão recebida de " + s.getRemoteSocketAddress());

                    ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                    ObjectInputStream  in  = new ObjectInputStream(s.getInputStream());

                    // Cria Parceiro e registra na lista
                    Parceiro usuario = new Parceiro(s, in, out);
                    synchronized (usuarios) {
                        usuarios.add(usuario);
                    }

                    // Sobe a thread que trata os pedidos desse cliente
                    new Thread(new TratadoraDePedidos(in, out, repo)).start();

                } catch (SocketException se) {
                    // Vai cair aqui quando o ServerSocket for fechado ao digitar "desativar"
                    System.out.println("ServerSocket fechado. Encerrando loop de accept.");
                    break;
                }
            }

            System.out.println("Loop principal do servidor encerrado.");

        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 4) FECHA A CONEXÃO COM O BANCO AQUI
            System.out.println("Servidor encerrado — fechando conexão com o MongoDB...");
            try {
                if (servidor != null && !servidor.isClosed())
                    servidor.close();
            } catch (Exception ignore) {}

            try {
                DBConection.fecharConexao();
            } catch (Exception ignore) {}
        }

        // Garante que tudo morra
        System.exit(0);
    }
}
