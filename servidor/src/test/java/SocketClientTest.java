<<<<<<< HEAD
/*import org.junit.jupiter.api.Assertions;
=======
package servidor.src.test.java;

import org.junit.jupiter.api.Assertions;
>>>>>>> 82ac2b2 (organizado caderno campo e custos)
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientTest {

    @Test
    @DisplayName("Deve conectar no servidor e receber resposta de Login ou Teste")
    public void testarConexaoSocket() {
        // ajusta aqui o ip e porta do servidor
        String ip = "127.0.0.1"; // ou localhost
        int porta = 12345;     

        try (Socket socket = new Socket(ip, porta)) {
            // Prepara para enviar dados
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Envia um JSON de teste
            String jsonPedido = "{\"operacao\": \"LOGIN\", \"email\": \"teste.socket@karpos.com\", \"senha\": \"123456\"}";

            System.out.println("Enviando para o servidor: " + jsonPedido);
            out.println(jsonPedido);

            // Lê a resposta do servidor
            String resposta = in.readLine();
            System.out.println("Resposta do servidor: " + resposta);

            // Valida se a resposta não é nula e contem sucesso/erro esperado
            Assertions.assertNotNull(resposta, "O servidor não retornou nada");

        } catch (Exception e) {
            Assertions.fail("Não foi possível conectar ao servidor. Erro: " + e.getMessage());
        }
    }
}*/