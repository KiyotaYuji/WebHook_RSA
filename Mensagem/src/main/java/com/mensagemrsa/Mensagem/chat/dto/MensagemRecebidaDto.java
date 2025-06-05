package com.mensagemrsa.Mensagem.chat.dto;

public class MensagemRecebidaDto {
    private String texto;
    private boolean assinaturaValida;

    public MensagemRecebidaDto(String texto, boolean assinaturaValida) {
        this.texto = texto;
        this.assinaturaValida = assinaturaValida;
    }

    public String getTexto() { return texto; }
    public boolean isAssinaturaValida() { return assinaturaValida; }
}