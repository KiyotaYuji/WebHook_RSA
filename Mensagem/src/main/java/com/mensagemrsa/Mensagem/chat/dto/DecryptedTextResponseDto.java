package com.mensagemrsa.Mensagem.chat.dto;

public class DecryptedTextResponseDto {
    private String textoDescriptografado;
    private String erro; // Pode ser null

    public String getTextoDescriptografado() { return textoDescriptografado; }
    public void setTextoDescriptografado(String textoDescriptografado) { this.textoDescriptografado = textoDescriptografado; }
    public String getErro() { return erro; }
    public void setErro(String erro) { this.erro = erro; }
}