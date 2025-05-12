package cn.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author default
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class ProjectApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ProjectApplication.class, args);
    }
}
