package cliente;

import comum.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class ClienteTeste {
    public static void main(String[] args) throws Exception {
        try (Socket s = new Socket("127.0.0.1", 5050)) {
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream  in  = new ObjectInputStream(s.getInputStream());

            // 1) Cadastro
            out.writeObject(new PedidoDeCadastro(
                    "Maria Silva", "mariateste@karpos.com", "Secreta#123",
                    "(19) 99999-0000", "123.456.789-09",
                    "12/12/2025" , 12.5
            ));
            out.flush();
            System.out.println(in.readObject()); // RespostaOk ou RespostaErro

            // 2) Login
            out.writeObject(new PedidoDeLogin("mariateste@karpos.com", "Secreta#123"));
            out.flush();
            Object resp = in.readObject();
            System.out.println(resp); // ClienteLogado ou RespostaErro

            // 3) Pedido Cadastro atividade Caderno de Campo
            out.writeObject(new PedidoCadastroCadernoCampo(
                    "2025-12-12", "Plantio",
                    "Usando maquinário pesado e mão de obra para fazer o plntio de soja na fazenda",
                    "mariateste@karpos.com"
            ));
            out.flush();
            Object resp2 = in.readObject();


            // 4) Pedido de busca por data de atividade
            out.writeObject(new PedidoBuscaDataAtividade(
                    "12/12/2025", "mariateste@karpos.com"
            ));
            out.flush();
            Object resp3 = in.readObject();

            double num = 232312313;
            // 5) Add custo
            out.writeObject(new PedidoCadastroCusto(
                    "12/12/2025", "imposto",
                    "Custo essencial",
                    "mariateste@karpos.com",
                    num,
                    "N/A"
            ));
            out.flush();
            Object resp5 = in.readObject();

            // 6) Pedido de busca por data de custo
            out.writeObject(new PedidoBuscaDataCusto(
                    "12/12/2025", "mariateste@karpos.com"
            ));
            out.flush();
            Object resp6 = in.readObject();

            out.writeObject(new PedidoBuscaSemFiltro(
                     "mariateste@karpos.com", "field-metrics"
            ));
            out.flush();
            Object resp7 = in.readObject();
            System.out.println(resp7);


            out.writeObject(new PedidoDelete("12/12/2025", "mariateste@karpos.com",
                    "imposto", "field-costs"));
            out.flush();
            Object rep8 = in.readObject();
        }
    }
}
