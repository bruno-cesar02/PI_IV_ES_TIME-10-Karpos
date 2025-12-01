package servidor.dbConection;

import com.mongodb.client.*;
import org.bson.Document;
import servidor.HashSenha;

import java.util.List;
import java.util.ArrayList;

public class DBUse {
    public static MongoCollection<Document> makeCollection(String collectionName , String dataBaseName) {

        MongoDatabase mongoDatabase = DBConection.conectarMongoDB(dataBaseName);

        assert mongoDatabase != null;

        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        assert collection != null;

        return collection;
    }
    public static void inserirUsuario (String nome, String email, String senha,
                                       String telefone , String cpfCnpj, String data,
                                       double tamanhoHectares){

        System.out.println("[DBUse] inserirUsuario chamado para: " + email);

        MongoCollection<Document> collection = DBUse.makeCollection("user-data" , "Karpos-BD");

        long qtd = collection.countDocuments();

        Document document = new Document("nome",nome)
                .append("email",email)
                .append("data", data)
                .append("senha",senha)
                .append("telefone",telefone)
                .append("documento",cpfCnpj)
                .append("tamanhoHectares",tamanhoHectares)
                .append("userID", qtd+1);

        Document filtroEmail = new Document("email", email);
        Document filtroCpf = new Document("documento", cpfCnpj);

        Document usuarioExistente = collection.find(filtroEmail).first();
        Document CpfExistente = collection.find(filtroCpf).first();

        if (usuarioExistente == null && CpfExistente == null){
            collection.insertOne(document);
            System.out.println("[DBUse] Usuário " + nome + " inserido com sucesso na coleção.");
            return;
        }

        System.out.println("[DBUse] Usuário já cadastrado, não pode cadastrar dois: " + usuarioExistente == null ? email : cpfCnpj);
    }


    public static Document loginUsuario (String email, String senha) throws Exception{
        MongoCollection<Document> collection = DBUse.makeCollection("user-data" ,  "Karpos-BD");


        Document filtroBusca = new Document("email", email);
        Document usuarioExistente = collection.find(filtroBusca).first();

        if (usuarioExistente != null){

            String nome = usuarioExistente.getString("nome");
            String emailBD = usuarioExistente.getString("email");
            String senhaBD = usuarioExistente.getString("senha");
            String telefoneBD = usuarioExistente.getString("telefone");
            String data = usuarioExistente.getString("data");
            String documentoBD = usuarioExistente.getString("documento");
            double tamanhoHectaresBD = usuarioExistente.getDouble("tamanhoHectares");
            long userIDBD = usuarioExistente.getLong("userID");

            if (!HashSenha.confere(senha, senhaBD)){
                throw new Exception("senha imcomparivel");
            }

            if (emailBD.equals(email)){

                System.out.println("usuário encontrado no Banco de Dados");

                return new Document("nome",nome)
                        .append("email",emailBD)
                        .append("senha",senhaBD)
                        .append("telefone",telefoneBD)
                        .append("data",data)
                        .append("documento",documentoBD)
                        .append("tamanhoHectares",tamanhoHectaresBD)
                        .append("userID", userIDBD);
            }
            else {

                System.out.println("Email ou Senha incorretos");

                return null;
            }
        }
        return  null;
    }
    public static List<Document> buscarPorData(String nomeColecao, String dataBusca, String userEmail) {
        MongoCollection<Document> collection = DBUse.makeCollection(nomeColecao, "Karpos-BD");
        MongoCollection<Document> userCollection = DBUse.makeCollection("user-data" , "Karpos-BD");

        Document filtroBusca = new Document("data", dataBusca);
        Document filtroUser = new Document("email", userEmail);

        Document usuárioExistente = userCollection.find(filtroUser).first();

        if (usuárioExistente != null){

            filtroBusca.append("userID", usuárioExistente.getLong("userID"));

            FindIterable<Document> resultados = collection.find(filtroBusca);

            List<Document> documentosEncontrados = new ArrayList<>();
            for (Document documento : resultados) {
                documentosEncontrados.add(documento);
            }

            if (documentosEncontrados.isEmpty()) {
                System.out.println("Nenhum documento encontrado na coleção '" + nomeColecao + "' com a data: " + dataBusca);
                return documentosEncontrados;
            } else {
                System.out.println(documentosEncontrados.size() + " documento(s) encontrado(s) na coleção '" + nomeColecao + "' para a data: " + dataBusca);
                return documentosEncontrados;
            }
        }
        System.out.println("Usuário não encontrado");
        return null;
    }
    public static boolean inserirAtividade (String data, String tipoAtividade, String texto, String usuarioEmail){

        MongoCollection<Document> collection = DBUse.makeCollection("field-metrics" , "Karpos-BD");
        MongoCollection<Document> userCollection = DBUse.makeCollection("user-data" , "Karpos-BD");

        Document document = new Document("data",data).append("atividade",tipoAtividade).append("texto",texto);
        Document filtroUser = new Document("email", usuarioEmail);

        Document usuarioExistente = userCollection.find(filtroUser).first();

        if (usuarioExistente != null){
            long userIDBD = usuarioExistente.getLong("userID");
            System.out.println(userIDBD);

            Document filtroBusca = new Document("data", data).append("atividade", tipoAtividade).append("userID", userIDBD);

            document.append("userID", userIDBD);

            Document atividadeExistente = collection.find(filtroBusca).first();

            if (atividadeExistente == null ){

                collection.insertOne(document);
                System.out.println("atividade " + tipoAtividade + " inserido com sucesso na coleção.");
                return true;
            }
            System.out.println("Atividade já cadastrado, não pode cadastrar dois");
            return false;
        }
        System.out.println("Usuário não encontrado");
        return false;

    }
    public static Boolean inserirCusto (String data, String categoria, String descricao, String email , double custo, String aa) {
        MongoCollection<Document> usercollection = DBUse.makeCollection("user-data", "Karpos-BD");
        MongoCollection<Document> collection = DBUse.makeCollection("field-costs", "Karpos-BD");
        Document document = new Document("data", data)
                .append("atividade", categoria)
                .append("descricao", descricao)
                .append("custo", custo)
                .append("aa", aa);

        Document filtroUser = new Document("email", email);

        Document usuarioExistente = usercollection.find(filtroUser).first();

        if (usuarioExistente != null) {

            document.append("userID", usuarioExistente.getLong("userID"));
            Document atividadeExistente = collection.find(document).first();

            if (atividadeExistente == null) {
                collection.insertOne(document);
                System.out.println("custo de categoria " + categoria + "com valor de RS" + custo + " inserido com sucesso na coleção.");
                return false;
            }
            System.out.println("Custo já cadastrado, não pode cadastrar dois");
            return false;
        }

        return false;
    }
    public static List<Document> buscarPorAtividade(String nomeColecao, String atividadeBusca, String userEmail, String data) {
        MongoCollection<Document> collection = DBUse.makeCollection(nomeColecao, "Karpos-BD");
        MongoCollection<Document> userCollection = DBUse.makeCollection("user-data" , "Karpos-BD");

        Document filtroBusca = new Document("atividade", atividadeBusca);
        Document filtroUser = new Document("email", userEmail);

        Document usuárioExistente = userCollection.find(filtroUser).first();

        if (usuárioExistente != null){

            filtroBusca.append("userID", usuárioExistente.getLong("userID")).append("data", data);

            FindIterable<Document> resultados = collection.find(filtroBusca);

            List<Document> documentosEncontrados = new ArrayList<>();
            for (Document documento : resultados) {
                documentosEncontrados.add(documento);
            }

            if (documentosEncontrados.isEmpty()) {
                System.out.println("Nenhum documento encontrado na coleção '" + nomeColecao + "' com a data: " + atividadeBusca);
                return documentosEncontrados;
            } else {
                System.out.println(documentosEncontrados.size() + " documento(s) encontrado(s) na coleção '" + nomeColecao + "' para a data: " + atividadeBusca);
                return documentosEncontrados;
            }
        }
        return null;
    }
    public static List<Document> buscarSemFiltro(String nomeColecao,String userEmail) {
        MongoCollection<Document> collection = DBUse.makeCollection(nomeColecao, "Karpos-BD");
        MongoCollection<Document> userCollection = DBUse.makeCollection("user-data" , "Karpos-BD");

        Document filtroUser = new Document("email", userEmail);
        Document usuarioExistente = userCollection.find(filtroUser).first();

        if (usuarioExistente != null){

            Document filtroBusca = new Document("userID", usuarioExistente.getLong("userID"));

            FindIterable<Document> resultados = collection.find(filtroBusca);

            List<Document> documentosEncontrados = new ArrayList<>();
            for (Document documento : resultados) {
                documentosEncontrados.add(documento);
            }

            if (documentosEncontrados.isEmpty()) {
                System.out.println("Nenhum documento encontrado na coleção '" + nomeColecao + "' com a data: " );
                return documentosEncontrados;
            } else {
                System.out.println(documentosEncontrados.size() + " documento(s) encontrado(s) na coleção '" + nomeColecao + "' para a data: ");
                return documentosEncontrados;
            }
        }
        System.out.println("Usuário não encontrado");
        return null;
    }
    public static boolean deleteItem(String data, String categoria, String atividade, String email ){

        List<Document> documentosEncontrados = buscarPorAtividade(categoria, atividade, email, data);

        if (documentosEncontrados == null){
            System.out.println("erroDaprocuraListaAtividade");
            return false;
        }
        System.out.println("busca da lista correta");
        MongoCollection<Document> userCollection = DBUse.makeCollection("user-data" ,  "Karpos-BD");
        Document filtroUser = new Document("email", email);
        Document usuárioExiste = userCollection.find(filtroUser).first();

        if (usuárioExiste == null){
            System.out.println("erro Procura usuário");
            return false;
        }
        System.out.println("busca de usuário correta");
        MongoCollection<Document> fieldCollection = DBUse.makeCollection(categoria ,  "Karpos-BD");
        Document filtroDocument = new Document("data", data).append("atividade", atividade).append("userID", usuárioExiste.getLong("userID"));
        Document documentoExiste = fieldCollection.find(filtroDocument).first();

        if (documentoExiste == null){
            return false;
        }

        fieldCollection.deleteOne(filtroDocument);
        return true;

    }
}
