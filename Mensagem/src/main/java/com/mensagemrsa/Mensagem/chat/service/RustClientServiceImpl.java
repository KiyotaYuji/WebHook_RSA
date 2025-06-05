package com.mensagemrsa.Mensagem.chat.service;

import com.mensagemrsa.Mensagem.chat.dto.ChaveDto;
import com.mensagemrsa.Mensagem.chat.dto.DecryptWithKeyRequestDto;
import com.mensagemrsa.Mensagem.chat.dto.DecryptedTextResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.EncryptWithKeyRequestDto;
import com.mensagemrsa.Mensagem.chat.dto.EncryptedDataResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.RustChavesResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.SignWithKeyRequestDto;
import com.mensagemrsa.Mensagem.chat.dto.SignatureResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.VerificationResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.VerifyWithKeyRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class RustClientServiceImpl implements RustClientService {

    private static final Logger logger = LoggerFactory.getLogger(RustClientServiceImpl.class);

    private final RestTemplate restTemplate;
    private final String rustApiBaseUrl;

    public RustClientServiceImpl(RestTemplate restTemplate,
                                 @Value("${rust.api.base-url}") String rustApiBaseUrl) {
        this.restTemplate = restTemplate;
        this.rustApiBaseUrl = rustApiBaseUrl;
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    public RustChavesResponseDto gerarChavesAplicacao() {
        String url = UriComponentsBuilder.fromHttpUrl(rustApiBaseUrl)
                .path("/api/gerar-chaves-usuario")
                .toUriString();
        logger.info("Chamando serviço Rust para gerar chaves (POST): {}", url);
        try {
            // 1. Obter a resposta como String
            String jsonResponse = restTemplate.postForObject(url, null, String.class);
            logger.info("Resposta JSON do serviço Rust: {}", jsonResponse);

            // 2. Desserializar manualmente usando ObjectMapper
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            try {
                RustChavesResponseDto resultado = mapper.readValue(jsonResponse, RustChavesResponseDto.class);
                // Log para confirmar que a desserialização funcionou
                logger.info("Desserialização bem-sucedida. Chave pública: {}, Chave privada: {}",
                        resultado.getChavePublica(), resultado.getChavePrivada());
                return resultado;
            } catch (Exception e) {
                logger.error("Erro ao desserializar JSON: {}", e.getMessage());
                e.printStackTrace(System.err);
                return null;
            }
        } catch (RestClientException e) {
            logger.error("Erro ao chamar serviço Rust para gerar chaves: {}", e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public EncryptedDataResponseDto criptografarComChave(String texto, ChaveDto chavePublica) {
        String url = UriComponentsBuilder.fromHttpUrl(rustApiBaseUrl)
                .path("/api/criptografar-com-chave")
                .toUriString();
        EncryptWithKeyRequestDto requestDto = new EncryptWithKeyRequestDto(texto, chavePublica);
        HttpEntity<EncryptWithKeyRequestDto> entity = new HttpEntity<>(requestDto, createJsonHeaders());
        logger.info("Chamando serviço Rust para criptografar texto. URL: {}", url);
        try {
            return restTemplate.postForObject(url, entity, EncryptedDataResponseDto.class);
        } catch (RestClientException e) {
            logger.error("Erro ao chamar serviço Rust para criptografar: {}", e.getMessage(), e);
            return null; // Ou lançar uma exceção customizada
        }
    }

    @Override
    public DecryptedTextResponseDto descriptografarComChave(List<Long> dadosCriptografados, ChaveDto chavePrivada) {
        String url = UriComponentsBuilder.fromHttpUrl(rustApiBaseUrl)
                .path("/api/descriptografar-com-chave")
                .toUriString();
        DecryptWithKeyRequestDto requestDto = new DecryptWithKeyRequestDto(dadosCriptografados, chavePrivada);
        HttpEntity<DecryptWithKeyRequestDto> entity = new HttpEntity<>(requestDto, createJsonHeaders());
        logger.info("Chamando serviço Rust para descriptografar dados. URL: {}", url);
        try {
            return restTemplate.postForObject(url, entity, DecryptedTextResponseDto.class);
        } catch (RestClientException e) {
            logger.error("Erro ao chamar serviço Rust para descriptografar: {}", e.getMessage(), e);
            DecryptedTextResponseDto errorResponse = new DecryptedTextResponseDto();
            errorResponse.setErro("Falha na comunicação com o serviço de descriptografia: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public SignatureResponseDto assinarComChave(String texto, ChaveDto chavePrivada) {
        String url = UriComponentsBuilder.fromHttpUrl(rustApiBaseUrl)
                .path("/api/assinar-com-chave")
                .toUriString();
        SignWithKeyRequestDto requestDto = new SignWithKeyRequestDto(texto, chavePrivada);
        HttpEntity<SignWithKeyRequestDto> entity = new HttpEntity<>(requestDto, createJsonHeaders());
        logger.info("Chamando serviço Rust para assinar texto. URL: {}", url);
        try {
            return restTemplate.postForObject(url, entity, SignatureResponseDto.class);
        } catch (RestClientException e) {
            logger.error("Erro ao chamar serviço Rust para assinar: {}", e.getMessage(), e);
            return null; // Ou lançar uma exceção customizada
        }
    }

    @Override
    public VerificationResponseDto verificarComChave(String texto, List<Long> assinatura, ChaveDto chavePublica) {
        String url = UriComponentsBuilder.fromHttpUrl(rustApiBaseUrl)
                .path("/api/verificar-com-chave")
                .toUriString();
        VerifyWithKeyRequestDto requestDto = new VerifyWithKeyRequestDto(texto, assinatura, chavePublica);
        HttpEntity<VerifyWithKeyRequestDto> entity = new HttpEntity<>(requestDto, createJsonHeaders());
        logger.info("Chamando serviço Rust para verificar assinatura. URL: {}", url);
        try {
            return restTemplate.postForObject(url, entity, VerificationResponseDto.class);
        } catch (RestClientException e) {
            logger.error("Erro ao chamar serviço Rust para verificar assinatura: {}", e.getMessage(), e);
            VerificationResponseDto errorResponse = new VerificationResponseDto();
            errorResponse.setValida(false);
            errorResponse.setMensagem("Falha na comunicação com o serviço de verificação: " + e.getMessage());
            return errorResponse;
        }
    }
}