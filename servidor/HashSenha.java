import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class HashSenha {
    private static final int ITERACOES = 120_000;
    private static final int BITS = 256;
    private static final String ALG = "PBKDF2WithHmacSHA256";
    private static final SecureRandom RNG = new SecureRandom();

    private HashSenha() {}

    public static String gerar(String senhaPura) {
        byte[] salt = new byte[16]; RNG.nextBytes(salt);
        byte[] hash = pbkdf2(senhaPura.toCharArray(), salt, ITERACOES);
        return "pbkdf2$" + ITERACOES + "$" +
                Base64.getEncoder().encodeToString(salt) + "$" +
                Base64.getEncoder().encodeToString(hash);
    }

    public static boolean confere(String senhaPura, String armazenado) {
        try {
            String[] p = armazenado.split("\\$");
            int it = Integer.parseInt(p[1]);
            byte[] salt = Base64.getDecoder().decode(p[2]);
            byte[] esperado = Base64.getDecoder().decode(p[3]);
            byte[] hash = pbkdf2(senhaPura.toCharArray(), salt, it);
            int diff = 0; for (int i=0;i<hash.length;i++) diff |= hash[i]^esperado[i];
            return diff == 0;
        } catch (Exception e) { return false; }
    }

    private static byte[] pbkdf2(char[] senha, byte[] salt, int it) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha, salt, it, BITS);
            return SecretKeyFactory.getInstance(ALG).generateSecret(spec).getEncoded();
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
}
