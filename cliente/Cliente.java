package cliente;

import comum.*;
import servidor.Parceiro;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args) {
        // Nenhuma ação passada
        if (args.length < 1) {
            printJsonErro("acao_obrigatoria");
            return;
        }

        String acao = args[0].toLowerCase();

        Socket conexao = null;
        ObjectOutputStream transmissor = null;
        ObjectInputStream receptor = null;
        Parceiro servidor = null;

        try {
            conexao = new Socket("127.0.0.1", 5050);

            // Ordem correta para evitar deadlock: primeiro ObjectOutputStream, depois ObjectInputStream
            transmissor = new ObjectOutputStream(conexao.getOutputStream());
            receptor    = new ObjectInputStream(conexao.getInputStream());

            servidor = new Parceiro(conexao, receptor, transmissor);

            // Sobe a thread que fica vigiando ComunicadoDeDesligamento
            TratadoraDeComunicadoDeDesligamento tratadora =
                    new TratadoraDeComunicadoDeDesligamento(servidor);
            tratadora.setDaemon(true);
            tratadora.start();

            switch (acao) {
                case "inserir":
                    processarInsercao(args, servidor);
                    break;

                case "login":
                    processarLogin(args, servidor);
                    break;

                default:
                    printJsonErro("acao_invalida");
            }

        } catch (Exception e) {
            printJsonErro("erro_conexao_servidor: " + escapar(String.valueOf(e.getMessage())));
        } finally {
            if (servidor != null) {
                try { servidor.adeus(); } catch (Exception ignore) {}
            } else {
                try { if (receptor    != null) receptor.close();    } catch (Exception ignore) {}
                try { if (transmissor != null) transmissor.close(); } catch (Exception ignore) {}
                try { if (conexao     != null) conexao.close();     } catch (Exception ignore) {}
            }
        }
    }

    // ================== AÇÕES ==================

    private static void processarInsercao(String[] args,
                                          Parceiro servidor) throws Exception {
        if (args.length < 10) {
            printJsonErro("parametros_insuficientes_para_inserir");
            return;
        }

        String nomeCompleto   = args[1];
        String email          = args[2];
        String senha          = args[3];
        String telefone       = args[4];
        String documento      = args[5];
        String nomeEmpresa    = args[6];
        String endereco       = args[7];
        double tamanhoHectares;
        try {
            tamanhoHectares = Double.parseDouble(args[8]);
        } catch (NumberFormatException e) {
            printJsonErro("tamanhoHectares_invalido");
            return;
        }
        String cultura = args[9];

        if (senha == null || senha.length() < 6) {
            printJsonErro("senha_muito_curta");
            return;
        }
        if (email == null || !email.contains("@")) {
            printJsonErro("email_invalido");
            return;
        }

        PedidoDeCadastro pedido = new PedidoDeCadastro(
                nomeCompleto,
                email,
                senha,
                telefone,
                documento,
                tamanhoHectares
        );

        servidor.receba(pedido);

        Comunicado resposta = servidor.envie();

        if (resposta instanceof RespostaErro) {
            RespostaErro erro = (RespostaErro) resposta;
            printJsonErro(erro.erro);
        } else if (resposta instanceof RespostaOk) {
            printJsonSucessoCadastro(
                    nomeCompleto, email, telefone, documento,
                    nomeEmpresa, endereco, tamanhoHectares, cultura
            );
        } else if (resposta instanceof ClienteLogado) {
            ClienteLogado logado = (ClienteLogado) resposta;
            comum.Cliente cli = logado.cliente;
            printJsonSucessoCliente(cli);
        } else {
            printJsonErro("resposta_desconhecida_do_servidor");
        }
    }

    private static void processarLogin(String[] args,
                                       Parceiro servidor) throws Exception {
        if (args.length < 3) {
            printJsonErro("parametros_insuficientes_para_login");
            return;
        }

        String email = args[1];
        String senha = args[2];
        String cpfCnpj = args[3];

        PedidoDeLogin pedido = new PedidoDeLogin(email, senha);
        servidor.receba(pedido);

        Comunicado resposta = servidor.envie();

        if (resposta instanceof RespostaErro) {
            RespostaErro erro = (RespostaErro) resposta;
            printJsonErro(erro.erro);
        } else if (resposta instanceof ClienteLogado) {
            ClienteLogado logado = (ClienteLogado) resposta;
            comum.Cliente cli = logado.cliente;
            printJsonSucessoCliente(cli);
        } else if (resposta instanceof RespostaOk) {
            printJsonSucessoLoginSimples(email);
        } else {
            printJsonErro("resposta_desconhecida_do_servidor");
        }
    }

    // ================== HELPERS JSON ==================

    private static void printJsonErro(String msg) {
        System.out.println(
                "{\\\"loginPermitido\\\": \\\"false\\\", " +
                        "\\\"msg\\\": \\\"" + escapar(msg) + "\\\"}"
        );
    }

    private static void printJsonSucessoCliente(comum.Cliente cli) {
        if (cli == null) {
            printJsonErro("cliente_nulo_na_resposta");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\\\"loginPermitido\\\": \\\"true\\\", \\\"usuario\\\": {");
        sb.append("\\\"nomeCompleto\\\": \\\"").append(escapar(cli.getNomeCompleto())).append("\\\", ");
        sb.append("\\\"email\\\": \\\"").append(escapar(cli.getEmail())).append("\\\", ");
        sb.append("\\\"telefone\\\": \\\"").append(escapar(cli.getTelefone())).append("\\\", ");
        sb.append("\\\"documento\\\": \\\"").append(escapar(cli.getDocumento())).append("\\\", ");
        sb.append("\\\"tamanhoHectares\\\": ").append(cli.getTamanhoHectares()).append(", ");
        sb.append("}}");

        System.out.println(sb.toString());
        try{
            Thread.sleep(500);
        } catch (InterruptedException e){

        }
    }

    private static void printJsonSucessoCadastro(String nomeCompleto,
                                                 String email,
                                                 String telefone,
                                                 String documento,
                                                 String nomeEmpresa,
                                                 String endereco,
                                                 double tamanhoHectares,
                                                 String cultura) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\\\"loginPermitido\\\": \\\"true\\\", \\\"usuario\\\": {");
        sb.append("\\\"nomeCompleto\\\": \\\"").append(escapar(nomeCompleto)).append("\\\", ");
        sb.append("\\\"email\\\": \\\"").append(escapar(email)).append("\\\", ");
        sb.append("\\\"telefone\\\": \\\"").append(escapar(telefone)).append("\\\", ");
        sb.append("\\\"documento\\\": \\\"").append(escapar(documento)).append("\\\", ");
        sb.append("\\\"nomeEmpresa\\\": \\\"").append(escapar(nomeEmpresa)).append("\\\", ");
        sb.append("\\\"endereco\\\": \\\"").append(escapar(endereco)).append("\\\", ");
        sb.append("\\\"tamanhoHectares\\\": ").append(tamanhoHectares).append(", ");
        sb.append("\\\"cultura\\\": \\\"").append(escapar(cultura)).append("\\\"");
        sb.append("}}");

        System.out.println(sb.toString());
        try{
            Thread.sleep(500);
        } catch (InterruptedException e){

        }
    }

    private static void printJsonSucessoLoginSimples(String email) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\\\"loginPermitido\\\": \\\"true\\\", \\\"usuario\\\": {");
        sb.append("\\\"email\\\": \\\"").append(escapar(email)).append("\\\"");
        sb.append("}}");

        System.out.println(sb.toString());
        try{
            Thread.sleep(500);
        } catch (InterruptedException e){

        }
    }

    private static String escapar(String texto) {
        if (texto == null) return "";
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
