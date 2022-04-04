package com.mario.rayas.util;

import android.util.Base64;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Seguridad {

    private static final String ALGORITMO = "AES";
    //private static final byte[] valor_clave = "P@ssw0rdrootSTAG".getBytes();
    private static byte[] valor_clave =null;

    public static String fEncriptar (String pTexto,String pPassword) throws Exception
    {
        valor_clave=pPassword.getBytes();
        Key key = new SecretKeySpec(valor_clave, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key );

        byte[] encrypted = cipher.doFinal(pTexto.getBytes("UTF-8"));
        String texto_encriptado = Base64.encodeToString(encrypted, Base64.DEFAULT);//new String(encrypted, "UTF-8");

        return texto_encriptado;


    }

    public static String fDesencriptar(String pTextoEncriptado,String pPassword) throws Exception
    {
        valor_clave=pPassword.getBytes();
        Key key = new SecretKeySpec(valor_clave, ALGORITMO);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decodificar_texto = Base64.decode(pTextoEncriptado.getBytes("UTF-8"), Base64.DEFAULT);
        byte[] desencriptado = cipher.doFinal(decodificar_texto);

        return new String(desencriptado, "UTF-8");
    }

}
