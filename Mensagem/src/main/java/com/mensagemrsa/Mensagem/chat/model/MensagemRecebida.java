// src/main/java/com/mensagemrsa/Mensagem/chat/model/MensagemRecebida.java
package com.mensagemrsa.Mensagem.chat.model;

import java.time.LocalDateTime;

public class MensagemRecebida {
    private String texto;
    //private LocalDateTime dataHora;
    private boolean assinaturaValida;

    public MensagemRecebida(String texto, boolean assinaturaValida) {
        this.texto = texto;
        //this.dataHora = LocalDateTime.now();
        this.assinaturaValida = assinaturaValida;
    }

    // Getters
    public String getTexto() { return texto; }
    //public LocalDateTime getDataHora() { return dataHora; }
    public boolean isAssinaturaValida() { return assinaturaValida; }
}