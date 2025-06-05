package com.mensagemrsa.Mensagem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableAsync
@EnableScheduling // Uncomment if you need scheduling capabilities
public class MensagemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MensagemApplication.class, args);
	}

}
