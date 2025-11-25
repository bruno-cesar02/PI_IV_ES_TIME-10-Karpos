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
                    12.5
            ));
            out.flush();
            System.out.println(in.readObject()); // RespostaOk ou RespostaErro

            // 2) Login
            out.writeObject(new PedidoDeLogin("mariateste@karpos.com", "Secreta#123", "123.456.789-09"));
            out.flush();
            Object resp = in.readObject();
            System.out.println(resp); // ClienteLogado ou RespostaErro

            // 3) Pedido Cadastro atividade Caderno de Campo
            out.writeObject(new PedidoCadastroCadernoCampo(
                    "12/12/2025", "Plantio",
                    "Usando maquinário pesado e mão de obra para fazer o plntio de soja na fazenda",
                    "mariateste@karpos.com"
            ));
            out.flush();
            Object resp2 = in.readObject();

        }
    }
}
