package org.dromara.music;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 音乐前台模块
 *
 * @author ruoyi
 */
@SpringBootApplication(exclude = {
    com.momao.valkey.autoconfigure.ValkeyQueryAutoConfiguration.class
})
public class MusicWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MusicWebApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  音乐模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
