package dbConection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DBConection {
    private static final String CONNECTION_STRING = "mongodb+srv://karpos-admin:karpos123@cluster0.vxlp7uc.mongodb.net/?appName=Cluster0";
    private static final String DATABASE_NAME = "Karpos-PI";
    private static MongoClient mongoClient = null;

    public static MongoDatabase conectarMongoDB() {
        try{
            if (mongoClient == null) {
                mongoClient = MongoClients.create(CONNECTION_STRING);
            }

            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);

            System.out.println("Conexão MongoDB estabelecida com sucesso!");

            return database;
        } catch (Exception e) {
            System.err.println("Erro ao conectar ou usar o MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public static void fecharConexao() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Conexão MongoDB fechada de forma limpa.");
        }
    }
}
