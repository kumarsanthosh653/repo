package com.ozonetel.occ.util;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.ozonetel.occ.model.AppProperty;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.context.ApplicationContext;

/**
 * @author asoke on 23/06/22
 * @projectName MessageServer
 **/
public class SecurityUtil {

    static ApplicationContext webApplicationContext = AppContext.getApplicationContext();
    private static AppProperty appProperty = (AppProperty) webApplicationContext.getBean("appProperty");
    private static Log log = LogFactory.getLog(SecurityUtil.class);
    private static final  String Key = StringUtils.isNotEmpty(appProperty.getSecreteKey()) ? appProperty.getSecreteKey() : "9747797f06ccc5a12898c029c0944bca";
    public static String encryptUsingAes256Key(String message) throws Exception {
        System.out.println("message came for encryption :"+message);
        byte[] messageBytes = message.getBytes();
        byte[] keyBytes = Hex.decode(Key);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(messageBytes);
        String encryptedText = Base64.encodeBase64String(encryptedByte);
        log.debug("in security encrypted byte is"+encryptedByte.toString());
        log.debug("in security encrypted text is"+encryptedText.toString());

        //System.out.println("in security encrypted byte is"+encryptedByte.toString());
        //System.out.println("in security encrypted text is "+encryptedText.toString());
        return encryptedText;
    }

    public static String decryptUsingAes256Key(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("message came for decryption : "+message);
        byte[] messageBytes = Base64.decodeBase64(message);
        byte[] keyBytes = Hex.decode(Key);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(messageBytes);
        String decryptedMessage = new String(decryptedBytes);
        log.debug("in security decripted messageBytes is"+messageBytes.toString());
        log.debug("in security decrypted text is :"+decryptedMessage);
        //System.out.println("in security decripted messageBytes is"+messageBytes.toString());
        //System.out.println("in security decrypted text is :"+decryptedMessage);
        return decryptedMessage;
    }

    /*public static String generateKey() {
        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Hex.toHexString(secretKey.getEncoded());
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }*/

   /* public static void main(String[] args) throws Exception {
      //  String KEY = "ba99d197577554a065dde66476ecfc96f5544cf5dea7e9e3700ffa9442a53806";
        System.out.println(Arrays.toString(Hex.decode(Key)));
        String e = new String(encryptUsingAes256Key("9967484849"));
        System.out.println("encrypted string : "+e);
        String d = new String(decryptUsingAes256Key(e));
        System.out.println("decrypted string : "+d);
    }*/

}
