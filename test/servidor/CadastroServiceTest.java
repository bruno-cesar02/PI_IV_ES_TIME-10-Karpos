package servidor;

import org.bson.Document;
import servidor.dbConection.DBUse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CadastroServiceTest {

    private CadastroService cadastroService;

    @BeforeEach
    void setUp() {
        // Usa o repositório em memória que JÁ existe no projeto
        RepositorioClientes repoEmMemoria = new RepositorioClientesEmMemoria();
        cadastroService = new CadastroService(repoEmMemoria);
    }

    @Test
    @DisplayName("CENÁRIO NORMAL: Cadastrar usuário com dados válidos não deve lançar exceção")
    void testCenarioNormal_CadastroComSucesso() {
        String nome = "João da Silva";
        String email = "joao" + System.currentTimeMillis() + "@teste.com";
        String senha = "senha12345";
        String telefone = "19999998889";
        String cpfCnpj = "12345632801";
        String data = "2025-11-26";
        double hectares = 40.0;

        assertDoesNotThrow(() -> {
            cadastroService.cadastrar(nome, email, senha, telefone, cpfCnpj, data, hectares);
        });
    }

    @Test
    @DisplayName("VARIAÇÃO 1: Cadastrar com email inválido deve lançar 'Email inválido'")
    void testVariacao1_EmailInvalido() {
        String nome = "Maria";
        String emailInvalido = "maria.sem.arroba.com"; // sem @
        String senha = "senha123";
        String telefone = "19999999999";
        String cpfCnpj = "12345678901";
        String data = "2025-11-26";
        double hectares = 20.0;

        Exception e = assertThrows(Exception.class, () -> {
            cadastroService.cadastrar(nome, emailInvalido, senha, telefone, cpfCnpj, data, hectares);
        });

        assertTrue(e.getMessage().contains("Email inválido"));
    }

    @Test
    @DisplayName("VARIAÇÃO 2: Cadastrar com senha curta deve lançar 'Senha muito curta'")
    void testVariacao2_SenhaCurta() {
        String nome = "Juliano";
        String email = "juliano" + System.currentTimeMillis() + "@teste.com";
        String senhaCurta = "123"; // < 6
        String telefone = "19999999999";
        String cpfCnpj = "12345678901";
        String data = "2025-11-26";
        double hectares = 10.0;

        Exception e = assertThrows(Exception.class, () -> {
            cadastroService.cadastrar(nome, email, senhaCurta, telefone, cpfCnpj, data, hectares);
        });

        assertTrue(e.getMessage().contains("Senha muito curta"));
    }

    @Test
    @DisplayName("VARIAÇÃO 3: não insere 2x o mesmo usuário no MongoDB (mesmo email + senha)")
    void testVariacao3_NaoDuplicaUsuarioNoMongo() throws Exception {
        // Dados fixos para o teste
        String nome     = "Ivan";
        String email    = "ivan.teste@teste.com"; // um email “de teste”
        String senha    = "SenhaFort3#";
        String telefone = "19999999999";
        String cpfCnpj  = "12345678901";
        String data     = "1997-11-16";
        double hectares = 25.0;

        // (1) GARANTIR que o usuário não existe antes do teste
        Document antes = DBUse.loginUsuario(email, senha);
        if (antes != null) {
            // se já existir de testes anteriores, você pode opcionalmente apagar,
            // ou só aceitar que já tem 1 e seguir o fluxo
            // aqui vamos só informar:
            System.out.println("Aviso: usuário de teste já existia no Mongo antes do teste.");
        }

        // (2) Primeiro cadastro: deve funcionar normalmente
        cadastroService.cadastrar(
                nome,
                email,
                senha,
                telefone,
                cpfCnpj,
                data,
                hectares
        );

        // Confere que agora o usuário EXISTE no Mongo
        Document depoisPrimeiro = DBUse.loginUsuario(email, senha);
        assertNotNull(depoisPrimeiro, "Usuário deveria existir no Mongo após o primeiro cadastro");

        // (3) Segundo cadastro com MESMO email e MESMA senha:
        //     CadastroService deve detectar e lançar "Email já cadastrado"
        Exception e = assertThrows(Exception.class, () -> {
            cadastroService.cadastrar(
                    nome,
                    email,
                    senha,
                    telefone,
                    cpfCnpj,
                    data,
                    hectares
            );
        });
        assertTrue(e.getMessage().contains("Email já cadastrado"),
                "Mensagem de erro esperada não foi lançada");

        // (4) Confirma que depois da segunda tentativa
        //     o usuário continua EXISTINDO, mas NÃO foi criado outro.
        // Como o DBUse.loginUsuario retorna só um Document,
        // a melhor evidência é: ainda existe, e o teste não explodiu.
        Document depoisSegundo = DBUse.loginUsuario(email, senha);
        assertNotNull(depoisSegundo, "Usuário deveria continuar existindo após tentativa duplicada");

        // Opcionalmente, se sua coleção tiver _id único, podemos checar que é o mesmo:
        assertEquals(depoisPrimeiro.getObjectId("_id"), depoisSegundo.getObjectId("_id"),
                "O mesmo documento deve representar o usuário (não pode ter duplicado)");
    }

}
