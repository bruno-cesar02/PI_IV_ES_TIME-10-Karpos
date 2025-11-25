package servidor;

import comum.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TratadoraDePedidos implements Runnable {
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final CadastroService cadastro;
    private final LoginService login;
    private final CadernoDeCampoServise caderno;

    public TratadoraDePedidos(ObjectInputStream in, ObjectOutputStream out, RepositorioClientes repo) {
        this.in = in;
        this.out = out;
        this.cadastro = new CadastroService(repo);
        this.login = new LoginService(repo);
        this.caderno = new CadernoDeCampoServise();
    }

    @Override
    public void run() {
        System.out.println("[Tratadora] Thread iniciada para conexão");
        try {
            while (true) {
                System.out.println("[Tratadora] Aguardando objeto do cliente...");
                Object msg = in.readObject();
                System.out.println("[Tratadora] Objeto recebido: " + msg.getClass().getName());

                if (msg instanceof PedidoDeCadastro pc) {
                    tratarCadastro(pc);
                } else if (msg instanceof PedidoDeLogin pl) {
                    tratarLogin(pl);
                } else if (msg instanceof PedidoCadastroCadernoCampo pccd){
                    tratarCaderno(pccd);
                }
                else {
                    System.out.println("[Tratadora] Comando não reconhecido: " + msg.getClass().getName());
                    out.writeObject(new RespostaErro("Comando não reconhecido"));
                    out.flush();
                }
            }
        } catch (Exception e) {
            System.err.println("[Tratadora] Erro na conexão/pedido:");
            e.printStackTrace();
            // aqui não relança, só encerra a thread da conexão
        }
    }

    private void tratarCadastro(PedidoDeCadastro pc) {
        try {
            System.out.println("[Tratadora] Iniciando cadastro para: " + pc.getEmail());
            cadastro.cadastrar(
                    pc.nomeCompleto,
                    pc.email,
                    pc.senha,
                    pc.telefone,
                    pc.cpfCnpj,
                    pc.hectares
            );

            // LOG bonito
            System.out.println("\n=== Novo Cadastro Recebido ===");
            System.out.println("Nome: " + pc.getNomeCompleto());
            System.out.println("E-mail: " + pc.getEmail());
            System.out.println("Telefone: " + pc.getTelefone());
            System.out.println("CPF/CNPJ: " + pc.getCpfCnpj());
            System.out.println("Tamanho (ha): " + pc.getHectares());
            System.out.println("================================\n");

            out.writeObject(new RespostaOk("Cadastro ok: " + pc.getEmail()));
            out.flush();
        } catch (Exception e) {
            System.err.println("[Tratadora] Erro ao cadastrar: " + e.getMessage());
            e.printStackTrace();
            try {
                out.writeObject(new RespostaErro(e.getMessage()));
                out.flush();
            } catch (Exception ex) {
                System.err.println("[Tratadora] Falha ao enviar RespostaErro ao cliente:");
                ex.printStackTrace();
            }
        }
    }

    private void tratarLogin(PedidoDeLogin pl) {
        try {
            System.out.println("[Tratadora] Iniciando login para: " + pl.email);
            var cli = login.autenticar(pl.email, pl.senha, pl.cpfCnpj);

            System.out.println("[Tratadora] Login bem-sucedido: " + cli.getEmail());
            out.writeObject(new ClienteLogado(cli));
            out.flush();
        } catch (Exception e) {
            System.out.println("[Tratadora] Falha de login para: " + pl.email + " (" + e.getMessage() + ")");
            try {
                out.writeObject(new RespostaErro(e.getMessage()));
                out.flush();
            } catch (Exception ex) {
                System.err.println("[Tratadora] Falha ao enviar RespostaErro de login ao cliente:");
                ex.printStackTrace();
            }
        }
    }
    private void tratarCaderno(PedidoCadastroCadernoCampo pccd) {
        try{
            System.out.println("[Tratadora] Iniciando login para: " + pccd.getUsuarioEmail());
            caderno.addAtividade(pccd.getData(), pccd.getTipoAtividade(), pccd.getTexto(), pccd.getUsuarioEmail());
        }
        catch (Exception e){
            System.err.println("[Tratadora] Erro ao cadastar atividade do caderno de campo " + e.getMessage());
            e.printStackTrace();
            try {
                out.writeObject(new RespostaErro(e.getMessage()));
                out.flush();
            } catch (Exception ex) {
                System.err.println("[Tratadora] Falha ao enviar RespostaErro ao cliente:");
                ex.printStackTrace();
            }
        }
    }
}
