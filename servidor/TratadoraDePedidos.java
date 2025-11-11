import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TratadoraDePedidos implements Runnable {
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final CadastroService cadastro;
    private final LoginService login;

    public TratadoraDePedidos(ObjectInputStream in, ObjectOutputStream out, RepositorioClientes repo) {
        this.in = in; this.out = out;
        this.cadastro = new CadastroService(repo);
        this.login = new LoginService(repo);
    }

    @Override public void run() {
        try {
            while (true) {
                Object msg = in.readObject();

                if (msg instanceof PedidoDeCadastro pc) {
                    try {
                        var cli = cadastro.cadastrar(
                                pc.nomeCompleto, pc.email, pc.senha,
                                pc.telefone, pc.cpfCnpj, pc.nomeEmpresa,
                                pc.endereco, pc.hectares, pc.cultura
                        );

                        // >>> LOG NO SERVIDOR <<<
                        System.out.println("\n=== Novo Cadastro Recebido ===");
                        System.out.println("Nome: " + cli.getNomeCompleto());
                        System.out.println("E-mail: " + cli.getEmail());
                        System.out.println("Telefone: " + cli.getTelefone());
                        System.out.println("CPF/CNPJ: " + cli.getDocumento());
                        System.out.println("Empresa/Propriedade: " + cli.getNomeEmpresa());
                        System.out.println("Endereço: " + cli.getEndereco());
                        System.out.println("Tamanho (ha): " + cli.getTamanhoHectares());
                        System.out.println("Cultura: " + cli.getCultura());
                        System.out.println("===============================\n");

                        out.writeObject(new RespostaOk("Cadastro ok: " + cli.getEmail()));
                        out.flush();
                    } catch (Exception e) {
                        out.writeObject(new RespostaErro(e.getMessage()));
                        out.flush();
                    }
                }
                else if (msg instanceof PedidoDeLogin pl) {
                    try {
                        var cli = login.autenticar(pl.email, pl.senha);

                        // >>> LOG DE LOGIN <<<
                        System.out.println("Login bem-sucedido: " + cli.getEmail());

                        out.writeObject(new ClienteLogado(cli));
                        out.flush();
                    } catch (Exception e) {
                        System.out.println("Falha de login para: " + pl.email + " (" + e.getMessage() + ")");
                        out.writeObject(new RespostaErro(e.getMessage()));
                        out.flush();
                    }
                }
                else {
                    out.writeObject(new RespostaErro("Comando não reconhecido"));
                    out.flush();
                }
            }
        } catch (Exception ignored) { /* conexão encerrada */ }
    }
}
