package com.mine.study;

import com.mine.study.system.SystemMonitor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@SpringBootApplication
//@EnableAspectJAutoProxy(proxyTargetClass = true)
//@ImportResource({"classpath:config/spring.xml"})
public class MainApplication {
    public static void main(String[] args) throws Exception {
        String myConfig = System.getProperty("my.config");
        final Properties properties = new Properties();
        final AtomicBoolean customPropertiesLoaded = new AtomicBoolean(false);

        if (StringUtils.isNotBlank(myConfig)) {
            File configFile = new File(myConfig);
            if (configFile.exists()) {
                properties.load(new FileReader(configFile));
                customPropertiesLoaded.set(true);
            }
        }

        SpringApplicationBuilder builder = new SpringApplicationBuilder()
                .sources(MainApplication.class)
                .registerShutdownHook(true);

        builder.listeners(event -> {

            log.info("Application Listener Event: {}", event);
            if (event instanceof ApplicationEnvironmentPreparedEvent) {

                ConfigurableEnvironment environment = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
                String mavenProfile = environment.getProperty("application.mavenProfile");
                String[] activeProfiles = environment.getActiveProfiles();

                if (activeProfiles.length == 0) {
                    environment.addActiveProfile(mavenProfile);
                }

                if (customPropertiesLoaded.get()) {
                    environment.getPropertySources().addFirst(new PropertiesPropertySource("my-config", properties));
                }
            }
        });
        ConfigurableApplicationContext context = builder.run(args);
        ConfigurableEnvironment env = context.getEnvironment();

        if (customPropertiesLoaded.get()) {
            log.info("Customized config specified: {}", myConfig);
        } else {
            log.info("No Custom config specified");
        }

        int monitorPort = env.getProperty("server.monitor.port", int.class, 6899);
        SystemMonitor monitor = new SystemMonitor(monitorPort, context);
        monitor.start();

        log.info("\n" +
                        "============================= APPLICATION INFORMATION =============================\n" +
                        ":: Application Name:       {}\n" +
                        ":: Application Version:    {}\n" +
                        ":: Maven Package Profile:  {}\n" +
                        ":: Spring Active Profiles: {}\n" +
                        ":: Logging Config:         {}\n" +
                        ":: Logging Path:           {}\n" +
                        ":: Logging File:           {}\n" +
                        ":: Server Port:            {}\n" +
                        ":: System Monitor Port:    {}\n" +
                        "============================== APPLICATION STARTED!! ==============================\n",
                env.getProperty("application.name"),
                env.getProperty("application.version"),
                env.getProperty("application.mavenProfile"),
                StringUtils.join(env.getActiveProfiles(), ','),
                env.getProperty("logging.config"),
                env.getProperty("logging.file.path"),
                env.getProperty("logging.file.name"),
                env.getProperty("server.port"),
                monitorPort
        );
    }
}