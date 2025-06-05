package com.mensagemrsa.Mensagem.chat.dto;

public class SignWithKeyRequestDto {
    private String texto;
    private ChaveDto chavePrivada;

    public SignWithKeyRequestDto(String texto, ChaveDto chavePrivada) {
        this.texto = texto;
        this.chavePrivada = chavePrivada;
    }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public ChaveDto getChavePrivada() { return chavePrivada; }
    public void setChavePrivada(ChaveDto chavePrivada) { this.chavePrivada = chavePrivada; }
}