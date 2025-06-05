package com.mensagemrsa.Mensagem.chat.dto;

public class EncryptWithKeyRequestDto {
    private String texto;
    private ChaveDto chavePublica;

    public EncryptWithKeyRequestDto(String texto, ChaveDto chavePublica) {
        this.texto = texto;
        this.chavePublica = chavePublica;
    }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public ChaveDto getChavePublica() { return chavePublica; }
    public void setChavePublica(ChaveDto chavePublica) { this.chavePublica = chavePublica; }
}