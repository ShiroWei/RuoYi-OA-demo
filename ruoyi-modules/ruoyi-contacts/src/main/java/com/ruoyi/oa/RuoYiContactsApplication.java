package com.ruoyi.oa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;

/**
 * OA 通讯录服务
 * 
 * @author oa
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class RuoYiContactsApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(RuoYiContactsApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  OA 通讯录服务启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " |  | \\    /  \\      /           \n" +
                " |  | \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}

