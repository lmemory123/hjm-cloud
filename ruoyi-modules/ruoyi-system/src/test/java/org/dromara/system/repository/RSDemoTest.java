package org.dromara.system.repository;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.dromara.system.domain.SysUser;
import org.dromara.system.mapper.SysUserMapper;
import org.dromara.system.service.ISysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: TokyoMomao
 * DateTime: 2025-11-19 14:53
 */

@SpringBootTest
public class RSDemoTest {


    @Resource
    private SysUserMapper sysUserService;


    @Test
    void testOne(){
        List<SysUser> sysUsers = sysUserService.selectList();
        System.out.println(sysUsers.size());


    }
}
