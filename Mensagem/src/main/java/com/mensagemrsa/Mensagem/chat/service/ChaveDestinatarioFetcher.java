package com.mensagemrsa.Mensagem.chat.service;

import com.mensagemrsa.Mensagem.chat.dto.ChaveDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@EnableAsync
public class ChaveDestinatarioFetcher implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ChaveDestinatarioFetcher.class);
    private final RestTemplate restTemplate;
    private ChaveDto chaveDestinatario;
    private final ApplicationContext applicationContext;
    private final AtomicBoolean chaveEncontrada = new AtomicBoolean(false);

    @Value("${app.other-instance.url}")
    private String otherInstanceUrl;

    public ChaveDestinatarioFetcher(RestTemplate restTemplate, ApplicationContext applicationContext) {
        this.restTemplate = restTemplate;
        this.applicationContext = applicationContext;
    }

    @Override
    @Async
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("Aplicação completamente inicializada. Iniciando busca da chave do destinatário assincronamente...");
        tentarBuscarChave();
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void verificarChavePeriodicamente() {
        if (!chaveEncontrada.get()) {
            tentarBuscarChave();
        }
    }

    private void tentarBuscarChave() {
        if (chaveEncontrada.get()) {
            return; // Já encontrou a chave, não precisa tentar novamente
        }

        try {
            ChaveDto chave = fetchChavePublicaDestinatario(otherInstanceUrl);
            if (chave != null) {
                this.chaveDestinatario = chave;
                chaveEncontrada.set(true);

                // Obter o ChatService do contexto para evitar referência circular
                ChatService chatService = applicationContext.getBean(ChatService.class);
                chatService.setChaveDestinatario(this.chaveDestinatario);

                logger.info("Chave do destinatário encontrada e configurada no ChatService");
            }
        } catch (Exception e) {
            logger.warn("Tentativa de buscar chave falhou, tentará novamente: {}", e.getMessage());
            // Não propaga a exceção para não interromper a inicialização
        }
    }

    @Retryable(
            value = { RestClientException.class, IllegalStateException.class },
            maxAttempts = 3, // Número menor de tentativas por chamada
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 5000)
    )
    public ChaveDto fetchChavePublicaDestinatario(String otherInstanceBaseUrlWithPath) throws IllegalStateException, RestClientException {
        if (otherInstanceBaseUrlWithPath == null || otherInstanceBaseUrlWithPath.isEmpty()) {
            logger.warn("URL da outra instância não configurada. Não é possível buscar a chave pública do destinatário.");
            throw new IllegalStateException("URL da outra instância não configurada para buscar chave.");
        }

        String urlParaChaveDestinatario = UriComponentsBuilder.fromHttpUrl(otherInstanceBaseUrlWithPath)
                .pathSegment("minha-chave-publica")
                .toUriString();

        logger.info("Buscando chave pública do destinatário de: {}", urlParaChaveDestinatario);
        ChaveDto chave = restTemplate.getForObject(urlParaChaveDestinatario, ChaveDto.class);
        if (chave != null && chave.getN() != null && chave.getE() != null) {
            logger.info("Chave pública do destinatário carregada dinamicamente: {}", chave);
            return chave;
        } else {
            logger.warn("Resposta da chave pública do destinatário foi nula ou incompleta de {}", urlParaChaveDestinatario);
            throw new IllegalStateException("Resposta da chave pública do destinatário foi nula ou incompleta de " + urlParaChaveDestinatario);
        }
    }

    public ChaveDto getChaveDestinatario() {
        return this.chaveDestinatario;
    }

    public boolean isChaveEncontrada() {
        return chaveEncontrada.get();
    }
}