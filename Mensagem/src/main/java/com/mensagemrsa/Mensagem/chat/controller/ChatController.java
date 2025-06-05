package com.mensagemrsa.Mensagem.chat.controller;

import com.mensagemrsa.Mensagem.chat.dto.MensagemParaEnviarDto;
import com.mensagemrsa.Mensagem.chat.dto.PacoteMensagemDto;
import com.mensagemrsa.Mensagem.chat.service.ChatService;
import com.mensagemrsa.Mensagem.chat.dto.ChaveDto;
import com.mensagemrsa.Mensagem.chat.dto.MensagemRecebidaDto;
import com.mensagemrsa.Mensagem.chat.service.ChatService.MensagemRecebida;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<Map<String, String>> enviarMensagem(@RequestBody MensagemParaEnviarDto mensagemDto) {
        Map<String, String> response = new HashMap<>();

        if (mensagemDto == null || mensagemDto.getTexto() == null || mensagemDto.getTexto().isEmpty()) {
            response.put("status", "erro");
            response.put("mensagem", "O texto da mensagem não pode ser vazio.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            logger.info("Recebida requisição para /api/chat/enviar com texto: {}", mensagemDto.getTexto());
            chatService.enviarMensagem(mensagemDto.getTexto());

            response.put("status", "sucesso");
            response.put("mensagem", "Mensagem enviada para processamento.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro no controller ao tentar enviar mensagem: {}", e.getMessage(), e);

            response.put("status", "erro");
            response.put("mensagem", "Erro ao processar envio da mensagem.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/minha-chave-publica")
    public ResponseEntity<ChaveDto> getMinhaChavePublica() {
        logger.info("Endpoint /minha-chave-publica chamado.");
        ChaveDto chavePublica = chatService.getMinhaChavePublica();
        if (chavePublica != null) {
            logger.info("Chave pública obtida do ChatService e será retornada: {}", chavePublica);
            return ResponseEntity.ok(chavePublica);
        } else {
            logger.error("ChatService retornou null para getMinhaChavePublica. Chave não disponível. Retornando HTTP 500.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/receber")
    public ResponseEntity<String> receberMensagem(@RequestBody PacoteMensagemDto pacote) {
        if (pacote == null) {
            return ResponseEntity.badRequest().body("Pacote da mensagem não pode ser nulo.");
        }
        try {
            logger.info("Recebida requisição para /api/chat/receber");
            chatService.receberMensagem(pacote);
            return ResponseEntity.ok("Pacote recebido e processado.");
        } catch (Exception e) {
            logger.error("Erro no controller ao tentar receber mensagem: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar recebimento do pacote.");
        }
    }

    @GetMapping("/mensagens")
    public ResponseEntity<List<MensagemRecebidaDto>> getMensagens() {
        List<MensagemRecebidaDto> dtos = chatService.getMensagensRecebidas().stream()
                .map(m -> new MensagemRecebidaDto(m.getTexto(), m.isAssinaturaValida()))
                .toList();
        return ResponseEntity.ok(dtos);
    }
}