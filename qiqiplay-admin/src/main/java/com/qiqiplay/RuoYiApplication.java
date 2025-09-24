package com.qiqiplay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RuoYiApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        sout();
    }
    public static void sout() {
        System.out.println("   ____    _    ____    _   _____    _                 ");
        System.out.println("  / __ \\  (_)  / __ \\  (_) |  __ \\  | |                ");
        System.out.println(" | |  | |  _  | |  | |  _  | |__) | | |   __ _   _   _ ");
        System.out.println(" | |  | | | | | |  | | | | |  ___/  | |  / _` | | | | |");
        System.out.println(" | |__| | | | | |__| | | | | |      | | | (_| | | |_| |");
        System.out.println("  \\___\\_\\ |_|  \\___\\_\\ |_| |_|      |_|  \\__,_|  \\__, |");
        System.out.println("                                                  __/ |");
        System.out.println("                                                 |___/ ");
    }
}
