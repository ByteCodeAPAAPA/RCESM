package com.example.rces;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = {
        "com.example.rces.models",
        "com.example.rces.spm.models"
})
@EnableJpaRepositories(basePackages = {
        "com.example.rces.spm.services", "com.example.rces.service"
})
@EnableAsync
public class RcesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RcesApplication.class, args);
    }

//    @Bean
//    public TelegramBotsApi telegramBotsApi(TelegramServiceImpl bot) throws TelegramApiException {
//        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
//        api.registerBot(bot);
//        return api;
//    }


}
