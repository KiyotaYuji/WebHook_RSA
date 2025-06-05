package com.mensagemrsa.Mensagem.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RustChavesResponseDto {
    @JsonProperty("chavePublica")
    private ChaveDto chavePublica;

    @JsonProperty("chavePrivada")
    private ChaveDto chavePrivada;

    // Getters e Setters
    public ChaveDto getChavePublica() { return chavePublica; }
    public void setChavePublica(ChaveDto chavePublica) { this.chavePublica = chavePublica; }
    public ChaveDto getChavePrivada() { return chavePrivada; }
    public void setChavePrivada(ChaveDto chavePrivada) { this.chavePrivada = chavePrivada; }
}