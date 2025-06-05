package com.mensagemrsa.Mensagem.chat.dto;

public class VerificationResponseDto {
    private boolean valida;
    private String mensagem;

    public boolean isValida() { return valida; }
    public void setValida(boolean valida) { this.valida = valida; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}