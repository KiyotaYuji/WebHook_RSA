package com.mensagemrsa.Mensagem.chat.dto;

import java.util.List;

public class EncryptedDataResponseDto {
    private List<Long> dadosCriptografados;

    public List<Long> getDadosCriptografados() { return dadosCriptografados; }
    public void setDadosCriptografados(List<Long> dadosCriptografados) { this.dadosCriptografados = dadosCriptografados; }
}