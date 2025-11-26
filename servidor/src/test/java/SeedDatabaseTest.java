import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

public class SeedDatabaseTest {

    private static final String CONNECTION_STRING = "mongodb+srv://karpos-admin:karpos123@cluster0.vxlp7uc.mongodb.net/?appName=Cluster0";
    private static final String DB_NAME = "karpos_db";

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> usuariosCol;
    private static MongoCollection<Document> propriedadesCol;

    @BeforeAll
    public static void setup() {
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DB_NAME);
            usuariosCol = database.getCollection("usuarios");
            propriedadesCol = database.getCollection("propriedades");
            System.out.println("--- CONEXÃO MONGODB ESTABELECIDA ---");
        } catch (Exception e) {
            Assertions.fail("Falha ao conectar no Mongo: " + e.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Test
    public void seedDadosIniciais() {
        System.out.println("--- INICIANDO SEED DE DADOS (SOCKET SERVER CONTEXT) ---");
        usuariosCol.deleteMany(new Document("email", "teste.socket@karpos.com"));


        ObjectId produtorId = new ObjectId();
        Document produtor = new Document("_id", produtorId)
                .append("nome", "Teste Via Socket")
                .append("email", "teste.socket@karpos.com")
                .append("senha", "123456") // Idealmente hash
                .append("tipo", "PEQUENO_PRODUTOR")
                .append("data_cadastro", new Date());

        // Inserir no Banco
        usuariosCol.insertOne(produtor);

        // Criar Propriedade vinculada
        Document propriedade = new Document()
                .append("nome", "Fazenda do Teste")
                .append("tamanho", 50.5)
                .append("cultura", "Milho")
                .append("dono_id", produtorId); // Referência ao ID acima

        propriedadesCol.insertOne(propriedade);

        // 6. Validação
        Document busca = usuariosCol.find(new Document("email", "teste.socket@karpos.com")).first();

        Assertions.assertNotNull(busca, "O usuário deveria ter sido salvo no banco.");
        Assertions.assertEquals(produtorId, busca.getObjectId("_id"));

        System.out.println("--- SEED EXECUTADO COM SUCESSO: 1 Usuário e 1 Propriedade criados ---");
    }
}