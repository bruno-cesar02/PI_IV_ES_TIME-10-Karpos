package cliente;

import comum.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteTeste {
    public static void main(String[] args) throws Exception {
        try (Socket s = new Socket("127.0.0.1", 5050)) {
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream  in  = new ObjectInputStream(s.getInputStream());

            // 1) Cadastro
            out.writeObject(new PedidoDeCadastro(
                    "Maria Silva", "mariateste@karpos.com", "Secreta#123",
                    "(19) 99999-0000", "123.456.789-09",
                    "Sítio Alecrim", "Estrada Rural, 100", 12.5, "Café"
            ));
            out.flush();
            System.out.println(in.readObject()); // RespostaOk ou RespostaErro

            // 2) Login
            out.writeObject(new PedidoDeLogin("mariateste@karpos.com", "Secreta#123"));
            out.flush();
            Object resp = in.readObject();
            System.out.println(resp); // ClienteLogado ou RespostaErro
        }
    }
}
