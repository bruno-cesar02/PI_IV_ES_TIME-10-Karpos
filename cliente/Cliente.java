package cliente;

import comum.*;
import servidor.*; // só se precisar de tipos comuns, mas NÃO de Parceiro
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class Cliente {

    public static void main(String[] args) {
        // Nenhuma ação passada
        if (args.length < 1) {
            printJsonErro("acao_obrigatoria");
            return;
        }

        String acao = args[0].toLowerCase();

        try (Socket s = new Socket("127.0.0.1", 5050);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream  in  = new ObjectInputStream(s.getInputStream())) {

            switch (acao) {
                case "inserir":
                    processarInsercao(args, out, in);
                    break;

                case "login":
                    processarLogin(args, out, in);
                    break;

                case "addatividade":
                    processarAddAtividade(args, out, in);
                    break;

                case "listasemiltro":
                    processarBuscaSemFiltro(args, out, in);
                    break;

                /*case "buscar":
                    processarBuscaData(args, out, in);
                    break;*/

                default:
                    printJsonErro("acao_invalida");
            }

        } catch (Exception e) {
            // JSON de erro genérico se der pau na conexão
            printJsonErro("erro_conexao_servidor: " + escapar(String.valueOf(e.getMessage())));
        }
    }

    // ================== AÇÕES ==================

    private static void processarBuscaSemFiltro(String[] args,
                                              ObjectOutputStream out,
                                              ObjectInputStream in) throws Exception {

        if (args.length < 2) {
            printJsonErro("parametros_insuficientes_para_busca");
            return;
        }

        String email = args[1];
        String colecao = args[2];

        PedidoBuscaSemFiltro pedido = new PedidoBuscaSemFiltro(email, colecao);

        // ENVIA PEDIDO
        out.writeObject(pedido);
        out.flush();

        // LÊ RESPOSTA
        Object resposta = in.readObject();

        if (resposta instanceof RespostaErro erro) {
            printJsonErro(erro.erro);
        } else if (resposta instanceof BuscaSemFiltro buscaSemFiltro) {
            printJsonBuscaSemFiltro(buscaSemFiltro.getResultado());
        }else {
            printJsonErro("resposta_desconhecida_do_servidor");
        }
    }

    private static void processarInsercao(String[] args,
                                          ObjectOutputStream out,
                                          ObjectInputStream in) throws Exception {
        if (args.length < 10) {
            printJsonErro("parametros_insuficientes_para_inserir");
            return;
        }

        String nomeCompleto   = args[1];
        String email          = args[2];
        String senha          = args[3];
        String telefone       = args[4];
        String documento      = args[5];
        String data = args[6];
        double tamanhoHectares;
        try {
            tamanhoHectares = Double.parseDouble(args[8]);
        } catch (NumberFormatException e) {
            printJsonErro("tamanhoHectares_invalido");
            return;
        }
        String cultura        = args[9];

        // Validações locais simples
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
                data,
                tamanhoHectares
        );

        // ENVIA PEDIDO DIRETO PELO OUT
        out.writeObject(pedido);
        out.flush();

        // LÊ RESPOSTA DIRETO DO IN
        Object resposta = in.readObject();

        if (resposta instanceof RespostaErro erro) {
            printJsonErro(erro.erro);
        } else if (resposta instanceof RespostaOk) {
            printJsonSucessoCadastro(
                    nomeCompleto, email, telefone, documento, tamanhoHectares
            );
        } else if (resposta instanceof ClienteLogado logado) {
            comum.Cliente cli = logado.cliente;
            printJsonSucessoCliente(cli);
        } else {
            printJsonErro("resposta_desconhecida_do_servidor");
        }
    }

    private static void processarLogin(String[] args,
                                       ObjectOutputStream out,
                                       ObjectInputStream in) throws Exception {
        if (args.length < 3) {
            printJsonErro("parametros_insuficientes_para_login");
            return;
        }

        String email = args[1];
        String senha = args[2];

        PedidoDeLogin pedido = new PedidoDeLogin(email, senha);

        // ENVIA PEDIDO
        out.writeObject(pedido);
        out.flush();

        // LÊ RESPOSTA
        Object resposta = in.readObject();

        if (resposta instanceof RespostaErro erro) {
            printJsonErro(erro.erro);
        } else if (resposta instanceof ClienteLogado logado) {
            comum.Cliente cli = logado.cliente;
            printJsonSucessoCliente(cli);
        } else if (resposta instanceof RespostaOk) {
            printJsonSucessoLoginSimples(email);
        } else {
            printJsonErro("resposta_desconhecida_do_servidor");
        }
    }

    private static void processarAddAtividade(String[] args,
                                              ObjectOutputStream out,
                                              ObjectInputStream in) throws Exception {

        if (args.length < 4) {
            printJsonErro("parametros_insuficientes_para_login");
            return;
        }

        String email = args[1];
        String data = args[2];
        String tipo =  args[3];
        String texto = args[4];

        PedidoCadastroCadernoCampo pedido = new PedidoCadastroCadernoCampo(data, tipo, texto, email);

        // ENVIA PEDIDO
        out.writeObject(pedido);
        out.flush();

        // LÊ RESPOSTA
        Object resposta = in.readObject();

        if (resposta instanceof RespostaErro erro) {
            printJsonErro(erro.erro);
        } else if (resposta instanceof RespostaOk) {
            printJsonSucessoCadastroCaderno();
        }else {
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
        sb.append("\\\"tamanhoHectares\\\": ").append(cli.getTamanhoHectares());
        sb.append("}}");

        System.out.println(sb.toString());
    }

    private static void printJsonSucessoCadastro(String nomeCompleto,
                                                 String email,
                                                 String telefone,
                                                 String documento,
                                                 double tamanhoHectares) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\\\"loginPermitido\\\": \\\"true\\\", \\\"usuario\\\": {");
        sb.append("\\\"nomeCompleto\\\": \\\"").append(escapar(nomeCompleto)).append("\\\", ");
        sb.append("\\\"email\\\": \\\"").append(escapar(email)).append("\\\", ");
        sb.append("\\\"telefone\\\": \\\"").append(escapar(telefone)).append("\\\", ");
        sb.append("\\\"documento\\\": \\\"").append(escapar(documento)).append("\\\", ");
        sb.append("\\\"tamanhoHectares\\\": ").append(tamanhoHectares);
        sb.append("}}");

        System.out.println(sb.toString());

        // se quiser manter esse "delay" pode deixar, mas não é obrigatório:
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
    }
    private static void printJsonBuscaSemFiltro(List<String> listaDeJsons) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (!listaDeJsons.isEmpty()) {
            String json = listaDeJsons.get(i);
            System.out.println(json);
            sb.append("{\\\"BuscExecutada\\\": \\\"true\\\", \\\"busca\\\": \\\"" + json + "\\\" ");
            sb.append("}");
            i ++;
        }
        System.out.println(sb.toString());
        // se quiser manter esse "delay" pode deixar, mas não é obrigatório:
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
    }
    private static void printJsonSucessoCadastroCaderno() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\\\"cadernoPermitido\\\": \\\"true\\\"}");

        System.out.println(sb.toString());

        // se quiser manter esse "delay" pode deixar, mas não é obrigatório:
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
    }

    private static void printJsonSucessoLoginSimples(String email) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\\\"loginPermitido\\\": \\\"true\\\", \\\"usuario\\\": {");
        sb.append("\\\"email\\\": \\\"").append(escapar(email)).append("\\\"");
        sb.append("}}");

        System.out.println(sb.toString());
    }

    private static String escapar(String texto) {
        if (texto == null) return "";
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}