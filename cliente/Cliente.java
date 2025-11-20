package cliente;

import comum.*;

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

        try (Socket s = new Socket("127.0.0.1", 5050);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {

            switch (acao) {
                case "inserir":
                    processarInsercao(args, out, in);
                    break;

                case "login":
                    processarLogin(args, out, in);
                    break;

                default:
                    printJsonErro("acao_invalida");
            }

        } catch (Exception e) {
            // JSON de erro genérico se der pau na conexão
            printJsonErro("erro_conexao_servidor: " + e.getMessage());
        }
    }

    // ================== AÇÕES ==================

    /**
     * Espera:
     * args[0] = "inserir"
     * args[1] = nomeCompleto
     * args[2] = email
     * args[3] = senha
     * args[4] = telefone
     * args[5] = documento (CPF/CNPJ)
     * args[6] = nomeEmpresa
     * args[7] = endereco
     * args[8] = tamanhoHectares (double)
     * args[9] = cultura
     */
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
        String nomeEmpresa    = args[6];
        String endereco       = args[7];
        double tamanhoHectares;
        try {
            tamanhoHectares = Double.parseDouble(args[8]);
        } catch (NumberFormatException e) {
            printJsonErro("tamanhoHectares_invalido");
            return;
        }
        String cultura        = args[9];

        // Validações locais simples (opcional — regra forte fica no servidor)
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
                nomeEmpresa,
                endereco,
                tamanhoHectares,
                cultura
        );

        out.writeObject(pedido);
        out.flush();

        Object resposta = in.readObject();

        if (resposta instanceof RespostaErro) {
            RespostaErro erro = (RespostaErro) resposta;
            printJsonErro(erro.erro); // campo public final String erro;
        } else if (resposta instanceof RespostaOk) {
            // Cadastro ok. Monta JSON de sucesso com os dados enviados
            printJsonSucessoCadastro(
                    nomeCompleto, email, telefone, documento,
                    nomeEmpresa, endereco, tamanhoHectares, cultura
            );
        } else if (resposta instanceof ClienteLogado) {
            ClienteLogado logado = (ClienteLogado) resposta;
            comum.Cliente cli = logado.cliente; // campo public final Cliente cliente;
            printJsonSucessoCliente(cli);
        } else {
            printJsonErro("resposta_desconhecida_do_servidor");
        }
    }

    /**
     * Espera:
     * args[0] = "login"
     * args[1] = email
     * args[2] = senha
     */
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
        out.writeObject(pedido);
        out.flush();

        Object resposta = in.readObject();

        if (resposta instanceof RespostaErro) {
            RespostaErro erro = (RespostaErro) resposta;
            printJsonErro(erro.erro);
        } else if (resposta instanceof ClienteLogado) {
            ClienteLogado logado = (ClienteLogado) resposta;
            comum.Cliente cli = logado.cliente;
            printJsonSucessoCliente(cli);
        } else if (resposta instanceof RespostaOk) {
            // Caso o servidor use RespostaOk pra login bem-sucedido sem devolver o Cliente
            printJsonSucessoLoginSimples(email);
        } else {
            printJsonErro("resposta_desconhecida_do_servidor");
        }
    }

    // ================== HELPERS JSON ==================

    // Erro:
    // {"loginPermitido": "false", "msg": "email inválido"}
    private static void printJsonErro(String msg) {
        System.out.println(
                "{\"loginPermitido\": \"false\", " +
                        "\"msg\": \"" + escapar(msg) + "\"}"
        );
    }

    // Sucesso com objeto Cliente completo (do servidor)
    private static void printJsonSucessoCliente(comum.Cliente cli) {
        if (cli == null) {
            printJsonErro("cliente_nulo_na_resposta");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"loginPermitido\": \"true\", \"usuario\": {");
        sb.append("\"nomeCompleto\": \"").append(escapar(cli.getNomeCompleto())).append("\", ");
        sb.append("\"email\": \"").append(escapar(cli.getEmail())).append("\", ");
        sb.append("\"telefone\": \"").append(escapar(cli.getTelefone())).append("\", ");
        sb.append("\"documento\": \"").append(escapar(cli.getDocumento())).append("\", ");
        sb.append("\"nomeEmpresa\": \"").append(escapar(cli.getNomeEmpresa())).append("\", ");
        sb.append("\"endereco\": \"").append(escapar(cli.getEndereco())).append("\", ");
        sb.append("\"tamanhoHectares\": ").append(cli.getTamanhoHectares()).append(", ");
        sb.append("\"cultura\": \"").append(escapar(cli.getCultura())).append("\"");
        sb.append("}}");

        System.out.println(sb.toString());
    }

    // Sucesso de cadastro usando os dados enviados (se o servidor só devolve RespostaOk)
    private static void printJsonSucessoCadastro(String nomeCompleto,
                                                 String email,
                                                 String telefone,
                                                 String documento,
                                                 String nomeEmpresa,
                                                 String endereco,
                                                 double tamanhoHectares,
                                                 String cultura) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"loginPermitido\": \"true\", \"usuario\": {");
        sb.append("\"nomeCompleto\": \"").append(escapar(nomeCompleto)).append("\", ");
        sb.append("\"email\": \"").append(escapar(email)).append("\", ");
        sb.append("\"telefone\": \"").append(escapar(telefone)).append("\", ");
        sb.append("\"documento\": \"").append(escapar(documento)).append("\", ");
        sb.append("\"nomeEmpresa\": \"").append(escapar(nomeEmpresa)).append("\", ");
        sb.append("\"endereco\": \"").append(escapar(endereco)).append("\", ");
        sb.append("\"tamanhoHectares\": ").append(tamanhoHectares).append(", ");
        sb.append("\"cultura\": \"").append(escapar(cultura)).append("\"");
        sb.append("}}");

        System.out.println(sb.toString());
    }

    // Sucesso simples de login, se só tiver o email
    private static void printJsonSucessoLoginSimples(String email) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"loginPermitido\": \"true\", \"usuario\": {");
        sb.append("\"email\": \"").append(escapar(email)).append("\"");
        sb.append("}}");

        System.out.println(sb.toString());
    }

    // Escape bem simples pra não quebrar o JSON
    private static String escapar(String texto) {
        if (texto == null) return "";
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
