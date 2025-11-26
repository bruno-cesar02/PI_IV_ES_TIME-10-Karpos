package servidor;

import comum.Cliente;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CadastroServiceTest {

    private CadastroService cadastroService;
    private RepositorioClientesFake repoFake;

    @BeforeEach
    void setUp() {
        repoFake = new RepositorioClientesFake();
        cadastroService = new CadastroService(repoFake);
    }

    @Test
    @DisplayName("CENÁRIO NORMAL: Cadastrar usuário com dados válidos")
    void testCenarioNormal_CadastroComSucesso() {
        String nome = "João da Silva bruno lenda";
        String email = "joaoso1partida" + System.currentTimeMillis() + "@teste.com";
        String senha = "senha12345";
        String telefone = "19999998889";
        String cpfCnpj = "12345632801";
        double hectares = 40.0;

        assertDoesNotThrow(() -> {
            cadastroService.cadastrar(nome, email, senha, telefone, cpfCnpj, hectares);
        });

        Cliente salvo = repoFake.getPorEmail(email);
        assertNotNull(salvo);
        assertEquals(nome, salvo.getNomeCompleto());
        assertEquals(email, salvo.getEmail());
        assertNotEquals(senha, salvo.getHashSenha());
        assertTrue(HashSenha.confere(senha, salvo.getHashSenha()));
    }

    @Test
    @DisplayName("VARIAÇÃO 1: Cadastrar com email inválido deve falhar")
    void testVariacao1_EmailInvalido() {
        String nome = "Maria";
        String emailInvalido = "maria.sem.arroba.com";
        String senha = "senha123";
        String telefone = "19999999999";
        String cpfCnpj = "12345678901";
        double hectares = 20.0;

        Exception e = assertThrows(Exception.class, () -> {
            cadastroService.cadastrar(nome, emailInvalido, senha, telefone, cpfCnpj, hectares);
        });

        assertTrue(e.getMessage().contains("Email inválido"));
        assertNull(repoFake.getPorEmail(emailInvalido));
    }

    @Test
    @DisplayName("VARIAÇÃO 2: Cadastrar com senha curta deve falhar")
    void testVariacao2_SenhaCurta() {
        String nome = "Juliano ";
        String email = "lendaa" + System.currentTimeMillis() + "@teste.com";
        String senhaCurta = "123";
        String telefone = "19999999999";
        String cpfCnpj = "12345678901";
        double hectares = 10.0;

        Exception e = assertThrows(Exception.class, () -> {
            cadastroService.cadastrar(nome, email, senhaCurta, telefone, cpfCnpj, hectares);
        });

        assertTrue(e.getMessage().contains("Senha muito curta"));
        assertNull(repoFake.getPorEmail(email));
    }

    @Test
    @DisplayName("EXTRA: Cadastrar com email já cadastrado deve falhar")
    void testVariacaoExtra_EmailJaCadastrado() throws Exception {
        String email = "repetido" + System.currentTimeMillis() + "@teste.com";

        cadastroService.cadastrar(
                "Primeiro Usuário",
                email,
                "senha123",
                "19999999999",
                "12345678901",
                30.0
        );

        Exception e = assertThrows(Exception.class, () -> {
            cadastroService.cadastrar(
                    "Segundo Usuário",
                    email,
                    "outrasenha",
                    "18888888888",
                    "98765432100",
                    40.0
            );
        });

        assertTrue(e.getMessage().contains("Email já cadastrado"));
    }
}
