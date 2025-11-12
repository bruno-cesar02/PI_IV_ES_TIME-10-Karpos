package dbConection;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DBUse {
    public static MongoCollection<Document> makeCollection(String collectionName) {

        MongoDatabase mongoDatabase = DBConection.conectarMongoDB();
        assert mongoDatabase != null;

        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        assert collection != null;

        return collection;
    }
    public static void inserirUsuario (String nome, String email, String senha){

        MongoCollection<Document> collection = DBUse.makeCollection("usuario");
        Document document = new Document("nome", nome).append("email", email).append("senha", senha);

        Document filtroBusca = new Document("email", email).append("senha", senha);

        Document usuarioExistente = collection.find(filtroBusca).first();

        if (usuarioExistente == null ){

            collection.insertOne(document);
            System.out.println("Usuário " + nome + " inserido com sucesso na coleção.");

            return;
        }
        System.out.println("Usuário já cadastrado, não pode cadastrar dois");
    }
}
