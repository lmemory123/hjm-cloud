package org.dromara.common.core;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimplePBEConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class EncryptTest {

    @Tag("dev")
    @Test
    public void testEncryptDefault() {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        SimplePBEConfig config = new SimplePBEConfig();

        // Jasypt 3.0.5 Default: PBEWITHHMACSHA512ANDAES_256
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setPassword("hajihami-secret-key-2026");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        standardPBEStringEncryptor.setConfig(config);
        
        System.out.println("--- Jasypt Default ---");
        System.out.println("ruoyi123 -> " + standardPBEStringEncryptor.encrypt("ruoyi123"));
        System.out.println("abcdefghijklmnopqrstuvwxyz -> " + standardPBEStringEncryptor.encrypt("abcdefghijklmnopqrstuvwxyz"));
        System.out.println("hajihami-db-secret-2026- -> " + standardPBEStringEncryptor.encrypt("hajihami-db-secret-2026-"));
    }
}
