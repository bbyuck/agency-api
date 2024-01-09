package com.ndm.core.develop;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import java.util.Properties;

import static java.lang.System.exit;

@Slf4j
@Profile("local")
public class SSHTunnelingInitializer {

    @Value("${bastion.host}")
    private String bastionHost;

    @Value("${bastion.user}")
    private String bastionUser;

    @Value("${bastion.port}")
    private int bastionPort;
    private String privateKey;

    @Value("${bastion.db.port}")
    private int dbPort;
    private Session session;

    @PostConstruct
    public void init() {
        privateKey = System.getProperty("key.path");
        log.info("path === {}", privateKey);
    }

    @PreDestroy
    public void closeSSH() {
        if (session.isConnected())
            session.disconnect();
    }

    public Integer buildSshConnection() {

        Integer forwardedPort = null;

        try {
            log.info("{}@{}:{}:{} with privateKey", bastionUser, bastionHost, bastionPort, dbPort);

            log.info("start ssh tunneling..");
            JSch jSch = new JSch();

            log.info("creating ssh session");
            jSch.addIdentity(privateKey);  // 개인키
            session = jSch.getSession(bastionUser, bastionHost, bastionPort);  // 세션 설정
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            log.info("complete creating ssh session");

            log.info("start connecting ssh connection");
            session.connect();  // ssh 연결
            log.info("success connecting ssh connection ");

            // 로컬pc의 남는 포트 하나와 원격 접속한 pc의 db포트 연결
            log.info("start forwarding");
            forwardedPort = session.setPortForwardingL(0, "localhost", dbPort);
            log.info("successfully connected to database");

        } catch (Exception e) {
            log.error("fail to make ssh tunneling");
            this.closeSSH();
            e.printStackTrace();
            exit(1);
        }

        return forwardedPort;
    }
}