package com.mensagemrsa.Mensagem.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RustProcessResponseDto {
    @JsonProperty("mensagem_criptografada")
    private List<Long> mensagemCriptografada;
    private List<Long> assinatura;
    private String detalhes;

    // Getters e Setters
    public List<Long> getMensagemCriptografada() { return mensagemCriptografada; }
    public void setMensagemCriptografada(List<Long> mensagemCriptografada) { this.mensagemCriptografada = mensagemCriptografada; }
    public List<Long> getAssinatura() { return assinatura; }
    public void setAssinatura(List<Long> assinatura) { this.assinatura = assinatura; }
    public String getDetalhes() { return detalhes; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }
}