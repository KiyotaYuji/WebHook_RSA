package com.mensagemrsa.Mensagem.chat.dto;

import java.util.List;

public class SignatureResponseDto {
    private List<Long> assinatura;

    public List<Long> getAssinatura() { return assinatura; }
    public void setAssinatura(List<Long> assinatura) { this.assinatura = assinatura; }
}