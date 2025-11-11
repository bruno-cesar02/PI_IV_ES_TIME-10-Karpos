public class CadastroService {
    private final RepositorioClientes repo;

    public CadastroService(RepositorioClientes repo) { this.repo = repo; }

    public Cliente cadastrar(String nome, String email, String senhaPura, String tel, String doc,
                             String empresa, String endereco, double ha, String cultura) throws Exception {
        ValidarCadastro.validar(nome, email, senhaPura, tel, doc, empresa, endereco, ha, cultura);
        if (repo.existePorEmail(email)) throw new IllegalArgumentException("E-mail já cadastrado");
        String hash = HashSenha.gerar(senhaPura);
        String docNum = ValidarCadastro.normalizarDoc(doc);
        Cliente c = new Cliente(nome, email.toLowerCase(), hash, tel, docNum, empresa, endereco, ha, cultura);
        repo.salvar(c);
        return c;
    }
}
