package test.servidor;

import comum.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TESTE INTERCLASSES - Testa o fluxo completo do servidor Karpos
 * Usa as classes de protocolo corretas (PedidoDeCadastro, RespostaOk, RespostaErro)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CadastroInterclassesTest {

    private static final int PORTA = 5050;
    private static final String HOST = "localhost";

    /**
     * CENÁRIO NORMAL: Cadastro com dados válidos
     */
    @Test
    @Order(1)
    @DisplayName("NORMAL: Cadastro com dados válidos deve retornar RespostaOk")
    void testCenarioNormal_CadastroCompleto() throws Exception {
        String nome = "João Silva Normal";
        String email = "joao.normal" + System.currentTimeMillis() + "@teste.com";
        String senha = "senha12345";
        String telefone = "19999998889";
        String cpfCnpj = "12345632801";
        String data = "2025-11-27";
        double hectares = 40.0;

        PedidoDeCadastro pedido = new PedidoDeCadastro(nome, email, senha, telefone, cpfCnpj, data, hectares);

        System.out.println("\n========================================");
        System.out.println(">>> TESTE 1: CENÁRIO NORMAL");
        System.out.println("========================================");
        System.out.println("Nome: " + nome);
        System.out.println("Email: " + email);
        System.out.println("Hectares: " + hectares);

        try (Socket socket = new Socket(HOST, PORTA);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("✓ Conectado! Enviando PedidoDeCadastro...");

            out.writeObject(pedido);
            out.flush();
            System.out.println("✓ Pedido enviado! Aguardando resposta...");

            Object respostaObj = in.readObject();
            System.out.println("✓ Resposta recebida: " + respostaObj.getClass().getSimpleName());

            // Verifica se é RespostaOk
            assertTrue(respostaObj instanceof RespostaOk,
                    "Deveria retornar RespostaOk. Recebido: " + respostaObj.getClass().getSimpleName());

            RespostaOk resposta = (RespostaOk) respostaObj;
            System.out.println("Mensagem: " + resposta.mensagem);  // ✅ CORRETO: RespostaOk.mensagem

            assertNotNull(resposta.mensagem, "Mensagem não deveria ser nula");
            assertTrue(resposta.mensagem.toLowerCase().contains("ok") ||
                            resposta.mensagem.toLowerCase().contains("sucesso") ||
                            resposta.mensagem.toLowerCase().contains("cadastro"),
                    "Mensagem deveria indicar sucesso. Recebido: " + resposta.mensagem);

            System.out.println("✓ TESTE PASSOU!\n");
        }
    }

    /**
     * VARIAÇÃO 1: Email inválido (sem @)
     */
    @Test
    @Order(2)
    @DisplayName("VARIAÇÃO 1: Email inválido deve retornar RespostaErro")
    void testVariacao1_EmailInvalido() throws Exception {
        String nome = "Maria Variacao1";
        String emailInvalido = "maria.sem.arroba.com"; // SEM @
        String senha = "senha123";
        String telefone = "19999999999";
        String cpfCnpj = "12345678901";
        String data = "2025-11-27";
        double hectares = 20.0;

        PedidoDeCadastro pedido = new PedidoDeCadastro(nome, emailInvalido, senha, telefone, cpfCnpj, data, hectares);

        System.out.println("\n========================================");
        System.out.println(">>> TESTE 2: VARIAÇÃO 1 (Email Inválido)");
        System.out.println("========================================");
        System.out.println("Email (inválido): " + emailInvalido);

        try (Socket socket = new Socket(HOST, PORTA);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(pedido);
            out.flush();
            System.out.println("✓ Pedido enviado! Aguardando resposta...");

            Object respostaObj = in.readObject();
            System.out.println("✓ Resposta recebida: " + respostaObj.getClass().getSimpleName());

            // Deve retornar RespostaErro
            assertTrue(respostaObj instanceof RespostaErro,
                    "Deveria retornar RespostaErro. Recebido: " + respostaObj.getClass().getSimpleName());

            RespostaErro resposta = (RespostaErro) respostaObj;
            System.out.println("Mensagem de erro: " + resposta.erro);  // ✅ CORRETO: RespostaErro.erro

            String erroLower = resposta.erro.toLowerCase();  // ✅ CORRETO: usa 'erro' não 'mensagem'
            assertTrue(erroLower.contains("email") || erroLower.contains("inválido") ||
                            erroLower.contains("invalido"),
                    "Erro deveria mencionar email inválido. Recebido: " + resposta.erro);

            System.out.println("✓ TESTE PASSOU! Erro esperado retornado.\n");
        }
    }

    /**
     * VARIAÇÃO 2: Email duplicado
     */
    @Test
    @Order(3)
    @DisplayName("VARIAÇÃO 2: Email duplicado deve retornar RespostaErro")
    void testVariacao2_EmailDuplicado() throws Exception {
        String nome = "Carlos Variacao2";
        String emailDuplicado = "carlos.dup" + System.currentTimeMillis() + "@teste.com";
        String senha = "senha123456";
        String telefone = "19988887777";
        String cpfCnpj = "98765432100";
        String data = "2025-11-27";
        double hectares = 15.0;

        PedidoDeCadastro pedido = new PedidoDeCadastro(nome, emailDuplicado, senha, telefone, cpfCnpj, data, hectares);

        System.out.println("\n========================================");
        System.out.println(">>> TESTE 3: VARIAÇÃO 2 (Email Duplicado)");
        System.out.println("========================================");
        System.out.println("Email: " + emailDuplicado);
        System.out.println("\nETAPA 1: Primeiro cadastro");

        // PRIMEIRO CADASTRO
        try (Socket socket1 = new Socket(HOST, PORTA);
             ObjectOutputStream out1 = new ObjectOutputStream(socket1.getOutputStream());
             ObjectInputStream in1 = new ObjectInputStream(socket1.getInputStream())) {

            out1.writeObject(pedido);
            out1.flush();

            Object resp1Obj = in1.readObject();
            System.out.println("Resposta 1: " + resp1Obj.getClass().getSimpleName());

            assertTrue(resp1Obj instanceof RespostaOk,
                    "Primeiro cadastro deveria retornar RespostaOk. Recebido: " + resp1Obj.getClass().getSimpleName());

            RespostaOk resposta1 = (RespostaOk) resp1Obj;
            System.out.println("Mensagem: " + resposta1.mensagem);  // ✅ CORRETO
        }

        System.out.println("✓ Primeiro cadastro OK! Aguardando...\n");
        Thread.sleep(1000);

        System.out.println("ETAPA 2: Segundo cadastro (mesmo email)");

        // SEGUNDO CADASTRO
        try (Socket socket2 = new Socket(HOST, PORTA);
             ObjectOutputStream out2 = new ObjectOutputStream(socket2.getOutputStream());
             ObjectInputStream in2 = new ObjectInputStream(socket2.getInputStream())) {

            out2.writeObject(pedido);
            out2.flush();

            Object resp2Obj = in2.readObject();
            System.out.println("Resposta 2: " + resp2Obj.getClass().getSimpleName());

            // Deve retornar RespostaErro
            assertTrue(resp2Obj instanceof RespostaErro,
                    "Segunda tentativa deveria retornar RespostaErro. Recebido: " + resp2Obj.getClass().getSimpleName());

            RespostaErro resposta2 = (RespostaErro) resp2Obj;
            System.out.println("Mensagem de erro: " + resposta2.erro);  // ✅ CORRETO: RespostaErro.erro

            String erroLower = resposta2.erro.toLowerCase();  // ✅ CORRETO
            assertTrue(erroLower.contains("cadastrado") || erroLower.contains("duplicado") ||
                            erroLower.contains("existe") || erroLower.contains("já") || erroLower.contains("ja"),
                    "Erro deveria indicar duplicidade. Recebido: " + resposta2.erro);

            System.out.println("✓ TESTE PASSOU! Duplicidade detectada.\n");
        }
    }

    /**
     * TESTE ADICIONAL: Conexões simultâneas
     */
    @Test
    @Order(4)
    @DisplayName("ADICIONAL: Múltiplas conexões simultâneas")
    void testConexoesSimultaneas() throws Exception {
        System.out.println("\n========================================");
        System.out.println(">>> TESTE 4: CONEXÕES SIMULTÂNEAS");
        System.out.println("========================================\n");

        CountDownLatch latch = new CountDownLatch(3);
        boolean[] sucesso = new boolean[3];
        String[] mensagens = new String[3];

        for (int i = 0; i < 3; i++) {
            final int indice = i;
            new Thread(() -> {
                try {
                    Thread.sleep(indice * 100);

                    String nome = "Usuario" + indice;
                    String email = "user" + indice + "_" + System.currentTimeMillis() + "@teste.com";
                    PedidoDeCadastro pedido = new PedidoDeCadastro(nome, email, "senha123",
                            "19999999999", "12345678901", "2025-11-27", 10.0);

                    System.out.println("Thread " + indice + " conectando...");

                    try (Socket socket = new Socket(HOST, PORTA);
                         ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                         ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                        out.writeObject(pedido);
                        out.flush();

                        Object respObj = in.readObject();
                        sucesso[indice] = (respObj instanceof RespostaOk);

                        if (respObj instanceof RespostaOk) {
                            RespostaOk resp = (RespostaOk) respObj;
                            mensagens[indice] = resp.mensagem;  // ✅ CORRETO
                            System.out.println("Thread " + indice + " ✓ SUCESSO: " + resp.mensagem);
                        } else if (respObj instanceof RespostaErro) {
                            RespostaErro resp = (RespostaErro) respObj;
                            mensagens[indice] = "ERRO: " + resp.erro;  // ✅ CORRETO
                            System.out.println("Thread " + indice + " ❌ ERRO: " + resp.erro);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Thread " + indice + " ❌ EXCEÇÃO: " + e.getMessage());
                    sucesso[indice] = false;
                    mensagens[indice] = "EXCEÇÃO: " + e.getMessage();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        boolean terminouATempo = latch.await(20, TimeUnit.SECONDS);

        System.out.println("\n--- Resultados ---");
        for (int i = 0; i < 3; i++) {
            System.out.println("Thread " + i + ": " + (sucesso[i] ? "✓" : "❌") + " - " + mensagens[i]);
        }

        assertTrue(terminouATempo, "Todas as conexões deveriam terminar");

        for (int i = 0; i < 3; i++) {
            assertTrue(sucesso[i], "Thread " + i + " deveria ter sucesso. Msg: " + mensagens[i]);
        }

        System.out.println("\n✓ TESTE PASSOU!\n");
    }
}
