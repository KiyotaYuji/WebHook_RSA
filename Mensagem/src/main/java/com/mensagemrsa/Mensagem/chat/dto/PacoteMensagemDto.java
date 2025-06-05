package com.mensagemrsa.Mensagem.chat.dto;

import java.util.List;

public class PacoteMensagemDto {
    private List<Long> dadosCriptografados;
    private List<Long> assinatura;
    private ChaveDto chavePublicaRemetente; // Adicionado

    public PacoteMensagemDto() {}

    // Construtor atualizado se desejar
    public PacoteMensagemDto(List<Long> dadosCriptografados, List<Long> assinatura, ChaveDto chavePublicaRemetente) {
        this.dadosCriptografados = dadosCriptografados;
        this.assinatura = assinatura;
        this.chavePublicaRemetente = chavePublicaRemetente;
    }

    public List<Long> getDadosCriptografados() { return dadosCriptografados; }
    public void setDadosCriptografados(List<Long> dadosCriptografados) { this.dadosCriptografados = dadosCriptografados; }

    public List<Long> getAssinatura() { return assinatura; }
    public void setAssinatura(List<Long> assinatura) { this.assinatura = assinatura; }

    // Getters e Setters para chavePublicaRemetente
    public ChaveDto getChavePublicaRemetente() { return chavePublicaRemetente; }
    public void setChavePublicaRemetente(ChaveDto chavePublicaRemetente) { this.chavePublicaRemetente = chavePublicaRemetente; }
}