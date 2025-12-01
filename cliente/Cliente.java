package cliente;

import comum.*;
//import servidor.*; // só se precisar de tipos comuns, mas NÃO de Parceiro
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                case "addatividadecusto":
                    processarAddAtividadeCusto(args,out,in);
                    break;

                case "listasemfiltro":
                    processarBuscaSemFiltro(args, out, in);
                    break;

                case "listacomfiltro":
                    processarBuscaComFiltro(args, out, in);
                    break;

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
            printJsonBuscaSemFiltro(buscaSemFiltro.getResultado(), args);
        }else {
            printJsonErro("resposta_desconhecida_do_servidor");
        }
    }

    private static void processarBuscaComFiltro(String[] args,
                                                ObjectOutputStream out,
                                                ObjectInputStream in) throws Exception {
        if (args.length < 4) {
            printJsonErro("parametros_insuficientes_para_busca_com_filtro");
            return;
        }

        String email      = args[1];
        String colecao    = args[2];
        String dataFiltro = args[3];

        Object pedido;

        if (colecao.equalsIgnoreCase("field-metrics")) {
            // busca atividades por data
            pedido = new PedidoBuscaDataAtividade(dataFiltro, email);
        } else if (colecao.equalsIgnoreCase("field-costs")) {
            // busca custos por data
            pedido = new PedidoBuscaDataCusto(dataFiltro, email);
        } else {
            printJsonErro("colecao_invalida_para_busca_com_filtro");
            return;
        }

        // ENVIA PEDIDO
        out.writeObject(pedido);
        out.flush();

        // LÊ RESPOSTA
        Object resposta = in.readObject();

        if (resposta instanceof RespostaErro erro) {
            printJsonErro(erro.erro);
        }
        // resposta com atividades filtradas
        else if (resposta instanceof BuscaDataAtividade buscaDataAtividade) {
            printJsonBuscaSemFiltro(buscaDataAtividade.getResultado(), args);
        }
        // resposta com custos filtrados
        else if (resposta instanceof BuscaDataCusto buscaDataCusto) {
            printJsonBuscaSemFiltro(buscaDataCusto.getResultado(), args);
        }
        else {
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

    private static void processarAddAtividadeCusto(String[] args,
                                              ObjectOutputStream out,
                                              ObjectInputStream in) throws Exception {

        if (args.length < 6) {
            printJsonErro("parametros_insuficientes_para_login");
            return;
        }

        String email = args[1];
        String data = args[2];
        String tipo =  args[3];
        String texto = args[4];
        double valor = Double.parseDouble(args[5]);
        String atividade = args[6];

        PedidoCadastroCusto pedido = new PedidoCadastroCusto(data, tipo, texto, email, valor, atividade);

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
    private static void printJsonBuscaSemFiltro(List<String> listaDeJsons, String[] args) {
        StringBuilder sb = new StringBuilder();

        // Cabeçalho (mantendo seu estilo original)
        sb.append("{\\\"BuscExecutada\\\": \\\"true\\\", \\\"resultados\\\": [");

        int i = 0;
        while (i < listaDeJsons.size()) {
            String json = listaDeJsons.get(i);


            if (args[2].equalsIgnoreCase("field-metrics")) {
                String data = extrairCampo(json, "data");
                String tipo = extrairCampo(json, "atividade");
                String texto = extrairCampo(json, "texto");

                sb.append("{");
                sb.append("\\\"data\\\": \\\"" + data + "\\\", ");
                sb.append("\\\"tipo\\\": \\\"" + tipo + "\\\", ");
                sb.append("\\\"dados\\\": \\\"" + texto + "\\\"");
                sb.append("}");
            } else{
                String data = extrairCampo(json, "data");
                String tipo = extrairCampo(json, "atividade");
                String texto = extrairCampo(json, "descricao");
                String custo = extrairCampo(json, "custo");
                String aa = extrairCampo(json, "aa");


                sb.append("{");
                sb.append("\\\"data\\\": \\\"" + data + "\\\", ");
                sb.append("\\\"tipo\\\": \\\"" + tipo + "\\\", ");
                sb.append("\\\"dados\\\": \\\"" + texto + "\\\",");
                sb.append("\\\"custo\\\": \\\"" + custo + "\\\",");
                sb.append("\\\"aa\\\": \\\"" + aa + "\\\"");
                sb.append("}");
            }

            // Adiciona vírgula se não for o último item (para manter JSON válido)
            if (i < listaDeJsons.size() - 1) {
                sb.append(", ");
            }

            i++;
        }

        sb.append("]}");

        // Printa TUDO de uma vez no final, conforme solicitado
        System.out.println(sb.toString());
    }

    // Método auxiliar para não repetir o código do Regex 3 vezes
    private static String extrairCampo(String json, String chave) {
        // Regex explicada:
        // Parte 1 (String): \"(.*?)\"  -> Pega conteúdo entre aspas
        // OU (|)
        // Parte 2 (Número): (-?\\d+(?:\\.\\d+)?)
        //      -?          -> Sinal de negativo opcional
        //      \\d+        -> Um ou mais dígitos
        //      (?:\\.\\d+)? -> Grupo opcional: Um ponto seguido de mais dígitos (para double)

        String regex = "\"" + chave + "\"\\s*:\\s*(?:\"(.*?)\"|(-?\\d+(?:\\.\\d+)?))";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            // Grupo 1: Achou String (estava entre aspas)
            if (matcher.group(1) != null) {
                return matcher.group(1);
            }
            // Grupo 2: Achou Número (Int ou Double válido)
            if (matcher.group(2) != null) {
                return matcher.group(2);
            }
        }
        return ""; // Retorna vazio se não encontrar
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