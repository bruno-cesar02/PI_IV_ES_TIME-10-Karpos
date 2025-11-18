package servidor.dbConection;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DBUse {
    public static MongoCollection<Document> makeCollection(String collectionName , String dataBaseName) {

        MongoDatabase mongoDatabase = DBConection.conectarMongoDB(dataBaseName);

        assert mongoDatabase != null;

        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        assert collection != null;

        return collection;
    }
    public static void inserirUsuario (String nome, String email, String senha , String telefone , String documento , String nomeEmpresa, String endereco, double tamanhoHectares, String categoria){

        MongoCollection<Document> collection = DBUse.makeCollection("usuario" , "Karpos-PI");
        Document document = new Document("nome",nome).append("email",email).append("senha",senha).append("telefone",telefone).append("documento",documento).append("nomeEmpresa",nomeEmpresa).append("endereco",endereco).append("tamanhoHectares",tamanhoHectares).append("categoria",categoria);

        Document filtroBusca = new Document("email", email);

        Document usuarioExistente = collection.find(filtroBusca).first();

        if (usuarioExistente == null ){

            collection.insertOne(document);
            System.out.println("Usuário " + nome + " inserido com sucesso na coleção.");

            return;
        }
        System.out.println("Usuário já cadastrado, não pode cadastrar dois");
    }
    public static Document loginUsuario (String email, String senha){
        MongoCollection<Document> collection = DBUse.makeCollection("usuario" ,  "Karpos-PI");


        Document filtroBusca = new Document("email", email);

        Document usuarioExistente = collection.find(filtroBusca).first();

        if (usuarioExistente != null){

            String nome = usuarioExistente.getString("nome");
            String emailBD = usuarioExistente.getString("email");
            String senhaBD = usuarioExistente.getString("senha");
            String telefoneBD = usuarioExistente.getString("telefone");
            String documentoBD = usuarioExistente.getString("documento");
            String nomeEmpresaBD = usuarioExistente.getString("nomeEmpresa");
            String enderecoBD = usuarioExistente.getString("endereco");
            double tamanhoHectaresBD = usuarioExistente.getDouble("tamanhoHectares");
            String categoriaBD = usuarioExistente.getString("categoria");


            if (emailBD.equals(email)){

                System.out.println("usuário encontrado no Banco de Dados");

                return new Document("nome",nome).append("email",emailBD).append("senha",senhaBD).append("telefone",telefoneBD).append("documento",documentoBD).append("nomeEmpresa",nomeEmpresaBD).append("endereco",enderecoBD).append("tamanhoHectares",tamanhoHectaresBD).append("categoria",categoriaBD);
            }
            else {

                System.out.println("Email ou Senha incorretos");

                return null;
            }
        }
        return  null;
    }
}
