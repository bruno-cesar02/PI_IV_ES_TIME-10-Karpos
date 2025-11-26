package servidor;

import comum.Cliente;
import servidor.dbConection.DBUse;
import org.bson.Document;

public class CadastroService {
    private final RepositorioClientes repo;

    public CadastroService(RepositorioClientes repo) {
        this.repo = repo;
    }

    public void cadastrar(
            String nomeCompleto,
            String email,
            String senhaPura,
            String telefone,
            String cpfCnpj,
            String data,
            double hectares
    ) throws Exception {

        System.out.println("[CadastroService] Iniciando cadastrar() para: " + email);

        // ---- Validações simples ----
        if (nomeCompleto == null || nomeCompleto.isBlank())
            throw new Exception("Nome é obrigatório");

        if (email == null || !email.contains("@"))
            throw new Exception("Email inválido");

        if (senhaPura == null || senhaPura.length() < 6)
            throw new Exception("Senha muito curta (mínimo 6 caracteres)");

        System.out.println("[CadastroService] Passou validações básicas");

        Document jaExiste = DBUse.loginUsuario(email, senhaPura);

        if (jaExiste != null)
            throw new Exception("Email já cadastrado");

        // Gera hash
        System.out.println("[CadastroService] Gerando hash...");
        String hash = HashSenha.gerar(senhaPura);
        System.out.println("[CadastroService] Hash gerado com sucesso");

        // Cria cliente em memória
        System.out.println("[CadastroService] Criando objeto Cliente...");
        Cliente cliente = new Cliente(
                nomeCompleto,
                email,
                data,
                hash,
                telefone,
                cpfCnpj,
                hectares
        );
        System.out.println("[CadastroService] Cliente criado: " + cliente.getEmail());

        // Salva no repositório em memória
        System.out.println("[CadastroService] Salvando no repositorio em memória...");
        repo.salvar(cliente);
        System.out.println("[CadastroService] Salvo no repositorio em memória");

        // Tenta salvar no Mongo
        try {
            System.out.println("[CadastroService] Chamando DBUse.inserirUsuario...");
            DBUse.inserirUsuario(
                    cliente.getNomeCompleto(),
                    cliente.getEmail(),
                    cliente.getHashSenha(),
                    cliente.getTelefone(),
                    cliente.getDocumento(),
                    cliente.getData(),
                    cliente.getTamanhoHectares()
            );
            System.out.println(">>> Cadastro salvo no MongoDB para: " + cliente.getEmail());
        } catch (Exception e) {
            System.err.println(">>> Falha ao salvar no MongoDB (cadastro em memória continua válido): "
                    + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[CadastroService] cadastrar() finalizado com sucesso para: " + email);
    }
}
