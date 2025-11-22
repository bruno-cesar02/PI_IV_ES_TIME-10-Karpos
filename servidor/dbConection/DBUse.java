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
    public static void inserirUsuario (String nome, String email, String senha,
                                       String telefone , String cpfCnpj,
                                       String nomeEmpresa, String endereco,
                                       double tamanhoHectares, String categoria){

        System.out.println("[DBUse] inserirUsuario chamado para: " + email);

        MongoCollection<Document> collection = DBUse.makeCollection("user-date" , "Karpos-BD");

        Document document = new Document("nome",nome)
                .append("email",email)
                .append("senha",senha)
                .append("telefone",telefone)
                .append("documento",cpfCnpj)
                .append("nomeEmpresa",nomeEmpresa)
                .append("endereco",endereco)
                .append("tamanhoHectares",tamanhoHectares)
                .append("cultura",categoria);

        Document filtroBusca = new Document("email", email);

        Document usuarioExistente = collection.find(filtroBusca).first();

        if (usuarioExistente == null ){
            collection.insertOne(document);
            System.out.println("[DBUse] Usuário " + nome + " inserido com sucesso na coleção.");
            return;
        }

        System.out.println("[DBUse] Usuário já cadastrado, não pode cadastrar dois: " + email);
    }


    public static Document loginUsuario (String email, String senha){
        MongoCollection<Document> collection = DBUse.makeCollection("user-data" ,  "Karpos-BD");


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
    public static List<Document> buscarPorData(String nomeColecao, String dataBusca) {
        MongoCollection<Document> collection = DBUse.makeCollection(nomeColecao, "Karpos-BD");

        Document filtroBusca = new Document("data", dataBusca);

        FindIterable<Document> resultados = collection.find(filtroBusca);

        List<Document> documentosEncontrados = new ArrayList<>();
        for (Document documento : resultados) {
            documentosEncontrados.add(documento);
        }

        if (documentosEncontrados.isEmpty()) {
            System.out.println("Nenhum documento encontrado na coleção '" + nomeColecao + "' com a data: " + dataBusca);
            return null;
        } else {
            System.out.println(documentosEncontrados.size() + " documento(s) encontrado(s) na coleção '" + nomeColecao + "' para a data: " + dataBusca);
            return documentosEncontrados;
        }
    }
    public static void inserirAtividade (String data, String tipoAtividade, String texto){

        MongoCollection<Document> collection = DBUse.makeCollection("field-metrics" , "Karpos-BD");
        Document document = new Document("data",data).append("tipoAtividade",tipoAtividade).append("texto",texto);

        Document filtroBusca = new Document("data", data).append("tipoAtividade", tipoAtividade);

        Document atividadeExistente = collection.find(filtroBusca).first();

        if (usuarioExistente == null ){

            collection.insertOne(document);
            System.out.println("atividade " + tipoAtividade + " inserido com sucesso na coleção.");
            return;
        }
        System.out.println("Atividade já cadastrado, não pode cadastrar dois");
    }
    public static void inserirCusto (String data, String categoria, String atividade, String descricao , double custo){

        MongoCollection<Document> collection = DBUse.makeCollection("field-costs" , "Karpos-BD");
        Document document = new Document("data",data)
                .append("categoria",categoria)
                .append("atividade",atividade)
                .append("descricao", descricao)
                .append("custo", custo);

        Document atividadeExistente = collection.find(document).first();

        if (usuarioExistente == null ){

            collection.insertOne(document);
            System.out.println("custo de categoria " + categoria + "com valor de RS" + custo +  " inserido com sucesso na coleção.");
            return;
        }
        System.out.println("Custo já cadastrado, não pode cadastrar dois");
    }
}
