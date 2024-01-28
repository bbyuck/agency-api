package com.ndm.core.common.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor
public class RSACrypto {

    public RSACrypto(String publicKey) {
        this.publicKey = publicKey;
    }

    @Value("${key.rsa.public}")
    public String publicKey;

    @Value("${key.rsa.private}")
    public String privateKey;

    /*
     * 암호화 : 공개키로 진행
     */
    public String encrypt(String plainText) throws Exception {
        // 평문으로 전달받은 공개키를 사용하기 위해 공개키 객체 생성
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] bytePublicKey = Base64.getDecoder().decode(publicKey.getBytes());
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        // 만들어진 공개키 객체로 암호화 설정
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);

        return encryptedText;
    }

    /*
     * 복호화 : 개인키로 진행
     */
    public String decrypt(String encryptedText) throws Exception {
        // 평문으로 전달받은 공개키를 사용하기 위해 공개키 객체 생성
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] bytePrivateKey = Base64.getDecoder().decode(privateKey.getBytes());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        // 만들어진 공개키 객체로 복호화 설정
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // 암호문을 평문화하는 과정
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText.getBytes());
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        String decryptedText = new String(decryptedBytes);

        return decryptedText;
    }

    /*
     * 공개키와 개인키 한 쌍 생성
     */
    public Map<String, String> createKeypairAsString() {
        Map<String, String> stringKeypair = new HashMap<>();

        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512, secureRandom);
            KeyPair keyPair = keyPairGenerator.genKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            stringKeypair.put("publicKey", stringPublicKey);
            stringKeypair.put("privateKey", stringPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringKeypair;
    }

}
