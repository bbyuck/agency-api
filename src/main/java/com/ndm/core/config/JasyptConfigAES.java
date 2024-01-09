package com.ndm.core.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableEncryptableProperties
public class JasyptConfigAES {

    @Value("${jasypt.password-key}")
    private String passwordKey;

    @Value("${jasypt.algorithm}")
    private String algorithm;

    @Value("${jasypt.hash-count}")
    private String hashCount;

    @Value("${jasypt.provider-name}")
    private String providerName;

    @Value("${jasypt.class.salt-generator}")
    private String saltGeneratorClass;

    @Value("${jasypt.class.iv-generator}")
    private String ivGeneratorClass;

    @Value("${jasypt.encoding}")
    private String encoding;


    @Bean
    public StringEncryptor jasyptEncryptorAES() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword(passwordKey); // 암호화키
        config.setAlgorithm(algorithm); // 알고리즘
        config.setKeyObtentionIterations(hashCount); // 반복할 해싱 회수
        config.setPoolSize("1"); // 인스턴스 pool
        config.setProviderName(providerName);
        config.setSaltGeneratorClassName(saltGeneratorClass); // salt 생성 클래스
        config.setIvGeneratorClassName(ivGeneratorClass);
        config.setStringOutputType(encoding); //인코딩 방식
        encryptor.setConfig(config);
        return encryptor;
    }
}
