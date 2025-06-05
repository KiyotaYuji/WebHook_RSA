package com.mensagemrsa.Mensagem.chat.dto;

import java.util.List;

public class DecryptWithKeyRequestDto {
    private List<Long> dadosCriptografados;
    private ChaveDto chavePrivada;

    public DecryptWithKeyRequestDto(List<Long> dadosCriptografados, ChaveDto chavePrivada) {
        this.dadosCriptografados = dadosCriptografados;
        this.chavePrivada = chavePrivada;
    }

    public List<Long> getDadosCriptografados() { return dadosCriptografados; }
    public void setDadosCriptografados(List<Long> dadosCriptografados) { this.dadosCriptografados = dadosCriptografados; }
    public ChaveDto getChavePrivada() { return chavePrivada; }
    public void setChavePrivada(ChaveDto chavePrivada) { this.chavePrivada = chavePrivada; }
}