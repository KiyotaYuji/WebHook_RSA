package com.mensagemrsa.Mensagem.chat.dto;

import java.util.List;

public class VerifyWithKeyRequestDto {
    private String texto;
    private List<Long> assinatura;
    private ChaveDto chavePublica;

    public VerifyWithKeyRequestDto(String texto, List<Long> assinatura, ChaveDto chavePublica) {
        this.texto = texto;
        this.assinatura = assinatura;
        this.chavePublica = chavePublica;
    }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public List<Long> getAssinatura() { return assinatura; }
    public void setAssinatura(List<Long> assinatura) { this.assinatura = assinatura; }
    public ChaveDto getChavePublica() { return chavePublica; }
    public void setChavePublica(ChaveDto chavePublica) { this.chavePublica = chavePublica; }
}