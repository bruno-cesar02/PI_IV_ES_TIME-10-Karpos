package servidor;

import comum.*;

import java.util.regex.Pattern;

public final class ValidarCadastro {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    private static final Pattern TEL   = Pattern.compile("^[0-9()+\\-\\s]{8,20}$");

    private ValidarCadastro(){}

    public static String normalizarDoc(String doc){ return doc==null?null:doc.replaceAll("\\D",""); }

    public static void validar(String nome, String email, String senha, String tel, String doc,
                               String empresa, String endereco, double ha, String cultura) {
        if (vazio(nome)||vazio(email)||vazio(senha)||vazio(tel)||vazio(doc)
                ||vazio(empresa)||vazio(endereco)||vazio(cultura))
            throw new IllegalArgumentException("Campos obrigatórios ausentes");
        if (!EMAIL.matcher(email).matches()) throw new IllegalArgumentException("Email inválido");
        if (!TEL.matcher(tel).matches())     throw new IllegalArgumentException("Telefone inválido");
        String d = normalizarDoc(doc);
        if (d==null || !(d.length()==11 || d.length()==14)) throw new IllegalArgumentException("CPF/CNPJ inválido");
        if (ha < 0) throw new IllegalArgumentException("Hectares não pode ser negativo");
    }
    private static boolean vazio(String s){ return s==null || s.trim().isEmpty(); }
}

