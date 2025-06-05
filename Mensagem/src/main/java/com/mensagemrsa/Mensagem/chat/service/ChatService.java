package com.mensagemrsa.Mensagem.chat.service;

import com.mensagemrsa.Mensagem.chat.dto.ChaveDto;
import com.mensagemrsa.Mensagem.chat.dto.DecryptedTextResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.EncryptedDataResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.PacoteMensagemDto;
import com.mensagemrsa.Mensagem.chat.dto.RustChavesResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.SignatureResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.VerificationResponseDto;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final RustClientService rustClientService;
    private final RestTemplate restTemplate;
    private final String otherInstanceBaseUrl;
    private final ApplicationContext applicationContext;

    private ChaveDto minhaChavePublica;
    private ChaveDto minhaChavePrivada;
    private ChaveDto chavePublicaDestinatario;

    // Classe para armazenar mensagens recebidas
    public static class MensagemRecebida {
        private String texto;
        private LocalDateTime dataHora;
        private boolean assinaturaValida;

        public MensagemRecebida(String texto, boolean assinaturaValida) {
            this.texto = texto;
            this.dataHora = LocalDateTime.now();
            this.assinaturaValida = assinaturaValida;
        }

        public String getTexto() { return texto; }
        public LocalDateTime getDataHora() { return dataHora; }
        public boolean isAssinaturaValida() { return assinaturaValida; }
    }

    // Lista para armazenar mensagens recebidas
    private final List<MensagemRecebida> mensagensRecebidas = new ArrayList<>();

    public ChatService(RustClientService rustClientService,
                       RestTemplate restTemplate,
                       @Value("${app.other-instance.url}") String otherInstanceBaseUrl,
                       ApplicationContext applicationContext) {
        this.rustClientService = rustClientService;
        this.restTemplate = restTemplate;
        this.otherInstanceBaseUrl = otherInstanceBaseUrl;
        this.applicationContext = applicationContext;

        if (this.otherInstanceBaseUrl == null || this.otherInstanceBaseUrl.isEmpty()) {
            logger.error("A URL da outra instância ('app.other-instance.url') não está configurada!");
        }
    }

    @PostConstruct
    public void inicializar() {
        logger.info("Inicializando o ChatService...");
        gerarMinhasChaves();
        // A busca da chave do destinatário será feita pelo ChaveDestinatarioFetcher após a inicialização completa
    }

    // Método para obter mensagens recebidas
    public List<MensagemRecebida> getMensagensRecebidas() {
        return Collections.unmodifiableList(mensagensRecebidas);
    }

    // Método adicionado para ser chamado pelo ChaveDestinatarioFetcher
    public void setChaveDestinatario(ChaveDto chaveDestinatario) {
        this.chavePublicaDestinatario = chaveDestinatario;
        logger.info("Chave do destinatário definida com sucesso: {}", chaveDestinatario);
    }

    private void gerarMinhasChaves() {
        logger.info("Tentando gerar chaves da aplicação via serviço Rust...");
        try {
            RustChavesResponseDto chavesDto = rustClientService.gerarChavesAplicacao();
            if (chavesDto != null && chavesDto.getChavePublica() != null && chavesDto.getChavePrivada() != null) {
                this.minhaChavePublica = chavesDto.getChavePublica();
                this.minhaChavePrivada = chavesDto.getChavePrivada();
                logger.info("Chaves da aplicação geradas e carregadas com sucesso.");
                logger.info("Minha Chave Pública: {}", minhaChavePublica);
                // Não logar a chave privada em produção por motivos de segurança
            } else {
                logger.error("Falha ao obter chaves da aplicação do serviço Rust. Resposta nula ou incompleta.");
            }
        } catch (Exception e) {
            logger.error("Erro ao gerar chaves da aplicação: {}", e.getMessage(), e);
        }
    }

    private void buscarChavePublicaDestinatarioComRetentativas() {
        if (this.otherInstanceBaseUrl == null || this.otherInstanceBaseUrl.isEmpty()) {
            logger.warn("URL da outra instância não configurada. Não será possível buscar a chave pública do destinatário.");
            return;
        }
        try {
            ChaveDestinatarioFetcher fetcher = applicationContext.getBean(ChaveDestinatarioFetcher.class);
            this.chavePublicaDestinatario = fetcher.fetchChavePublicaDestinatario(this.otherInstanceBaseUrl);
            if (this.chavePublicaDestinatario != null) {
                logger.info("Chave pública do destinatário obtida com sucesso: {}", this.chavePublicaDestinatario);
            } else {
                logger.warn("Não foi possível obter a chave pública do destinatário após tentativas.");
            }
        } catch (Exception e) {
            logger.error("Falha ao buscar chave pública do destinatário com tentativas: {}", e.getMessage(), e);
        }
    }

    public void enviarMensagem(String textoOriginal) {
        if (minhaChavePrivada == null || minhaChavePublica == null) {
            logger.error("Não é possível enviar mensagem: Chaves desta instância não estão configuradas.");
            gerarMinhasChaves(); // Tenta gerar novamente
            if (minhaChavePrivada == null || minhaChavePublica == null) {
                logger.error("Falha ao gerar chaves. Abortando envio.");
                return;
            }
        }
        if (chavePublicaDestinatario == null) {
            logger.warn("Chave pública do destinatário não está configurada. Tentando buscar novamente...");
            buscarChavePublicaDestinatarioComRetentativas();
            if (chavePublicaDestinatario == null) {
                logger.error("Não é possível enviar mensagem: Chave pública do destinatário ainda não está configurada após nova tentativa.");
                return;
            }
        }
        if (otherInstanceBaseUrl == null || otherInstanceBaseUrl.isEmpty()) {
            logger.error("URL da outra instância não configurada. Não é possível enviar mensagem.");
            return;
        }

        logger.info("Preparando para enviar mensagem: '{}'", textoOriginal);
        logger.info("Usando chave pública do destinatário: {}", chavePublicaDestinatario);

        try {
            EncryptedDataResponseDto dadosCriptografadosContainer = rustClientService.criptografarComChave(textoOriginal, chavePublicaDestinatario);
            if (dadosCriptografadosContainer == null || dadosCriptografadosContainer.getDadosCriptografados() == null) {
                logger.error("Falha ao criptografar mensagem. Resposta do serviço Rust foi nula ou incompleta.");
                return;
            }
            logger.info("Texto criptografado com sucesso ({} blocos).", dadosCriptografadosContainer.getDadosCriptografados().size());

            SignatureResponseDto assinaturaContainer = rustClientService.assinarComChave(textoOriginal, minhaChavePrivada);
            if (assinaturaContainer == null || assinaturaContainer.getAssinatura() == null) {
                logger.error("Falha ao assinar mensagem. Resposta do serviço Rust foi nula ou incompleta.");
                return;
            }
            logger.info("Assinatura gerada com sucesso ({} blocos).", assinaturaContainer.getAssinatura().size());

            PacoteMensagemDto pacote = new PacoteMensagemDto();
            pacote.setDadosCriptografados(dadosCriptografadosContainer.getDadosCriptografados());
            pacote.setAssinatura(assinaturaContainer.getAssinatura());
            pacote.setChavePublicaRemetente(this.minhaChavePublica);

            String urlEnvio = UriComponentsBuilder.fromHttpUrl(this.otherInstanceBaseUrl)
                    .pathSegment("receber")
                    .toUriString();

            logger.info("Enviando pacote para: {}", urlEnvio);
            restTemplate.postForEntity(urlEnvio, pacote, String.class);
            logger.info("Pacote enviado com sucesso para {}", urlEnvio);

        } catch (Exception e) {
            logger.error("Erro ao criptografar, assinar ou enviar a mensagem: {}", e.getMessage(), e);
        }
    }

    public void receberMensagem(PacoteMensagemDto pacote) {
        if (minhaChavePrivada == null) {
            logger.error("Não é possível processar mensagem recebida: Minha chave privada não está configurada.");
            gerarMinhasChaves(); // Tenta gerar novamente
            if (minhaChavePrivada == null) {
                logger.error("Falha ao gerar chaves. Abortando recebimento.");
                return;
            }
        }
        if (pacote == null || pacote.getDadosCriptografados() == null || pacote.getAssinatura() == null) {
            logger.error("Pacote recebido é inválido (nulo ou com dados/assinatura faltando).");
            return;
        }
        if (pacote.getChavePublicaRemetente() == null) {
            logger.error("Não é possível processar mensagem recebida: Chave pública do remetente não fornecida no pacote.");
            return;
        }

        ChaveDto chavePublicaRemetente = pacote.getChavePublicaRemetente();
        logger.info("Pacote recebido. Tentando descriptografar e verificar assinatura.");
        logger.info("Usando chave pública do remetente para verificar assinatura: {}", chavePublicaRemetente);

        try {
            DecryptedTextResponseDto textoDescriptografadoContainer = rustClientService.descriptografarComChave(pacote.getDadosCriptografados(), minhaChavePrivada);

            if (textoDescriptografadoContainer == null) {
                logger.error("Falha ao descriptografar mensagem: resposta do serviço Rust foi nula.");
                return;
            }
            if (textoDescriptografadoContainer.getErro() != null) {
                logger.error("Erro ao descriptografar mensagem: {}", textoDescriptografadoContainer.getErro());
                return;
            }
            String textoDescriptografado = textoDescriptografadoContainer.getTextoDescriptografado();
            if (textoDescriptografado == null) {
                logger.error("Falha ao descriptografar mensagem: texto descriptografado é nulo.");
                return;
            }
            logger.info("Texto descriptografado: {}", textoDescriptografado);

            VerificationResponseDto verificacao = rustClientService.verificarComChave(textoDescriptografado, pacote.getAssinatura(), chavePublicaRemetente);

            if (verificacao == null) {
                logger.error("Falha ao verificar assinatura: resposta do serviço Rust foi nula.");
                return;
            }

            if (verificacao.isValida()) {
                logger.info("ASSINATURA VÁLIDA. Mensagem: '{}'. Detalhes: {}", textoDescriptografado, verificacao.getMensagem());
                // Armazena a mensagem recebida
                mensagensRecebidas.add(new MensagemRecebida(textoDescriptografado, true));
            } else {
                logger.warn("ASSINATURA INVÁLIDA. Mensagem: '{}'. Detalhes: {}", textoDescriptografado, verificacao.getMensagem());
                // Armazena a mensagem, mas marca como assinatura inválida
                mensagensRecebidas.add(new MensagemRecebida(textoDescriptografado, false));
            }
        } catch (Exception e) {
            logger.error("Erro ao descriptografar ou verificar assinatura da mensagem recebida: {}", e.getMessage(), e);
        }
    }

    public ChaveDto getMinhaChavePublica() {
        if (minhaChavePublica == null) {
            logger.warn("Minha chave pública ainda não foi gerada. Tentando gerar agora.");
            gerarMinhasChaves();
        }
        return minhaChavePublica;
    }
}