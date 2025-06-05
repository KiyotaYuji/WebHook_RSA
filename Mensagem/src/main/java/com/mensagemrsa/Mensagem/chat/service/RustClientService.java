package com.mensagemrsa.Mensagem.chat.service;

import com.mensagemrsa.Mensagem.chat.dto.ChaveDto;
import com.mensagemrsa.Mensagem.chat.dto.DecryptedTextResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.EncryptedDataResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.RustChavesResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.SignatureResponseDto;
import com.mensagemrsa.Mensagem.chat.dto.VerificationResponseDto;

import java.util.List;

public interface RustClientService {

    /**
     * Chama o endpoint /api/gerar-chaves-usuario no serviço Rust.
     * @return RustChavesResponseDto contendo a chave pública e privada geradas.
     */
    RustChavesResponseDto gerarChavesAplicacao();

    /**
     * Chama o endpoint /api/criptografar-com-chave no serviço Rust.
     * @param texto O texto a ser criptografado.
     * @param chavePublica A chave pública a ser usada para criptografia.
     * @return EncryptedDataResponseDto contendo os dados criptografados.
     */
    EncryptedDataResponseDto criptografarComChave(String texto, ChaveDto chavePublica);

    /**
     * Chama o endpoint /api/descriptografar-com-chave no serviço Rust.
     * @param dadosCriptografados A lista de longs representando os dados criptografados.
     * @param chavePrivada A chave privada a ser usada para descriptografia.
     * @return DecryptedTextResponseDto contendo o texto descriptografado ou um erro.
     */
    DecryptedTextResponseDto descriptografarComChave(List<Long> dadosCriptografados, ChaveDto chavePrivada);

    /**
     * Chama o endpoint /api/assinar-com-chave no serviço Rust.
     * @param texto O texto a ser assinado.
     * @param chavePrivada A chave privada a ser usada para gerar a assinatura.
     * @return SignatureResponseDto contendo a assinatura.
     */
    SignatureResponseDto assinarComChave(String texto, ChaveDto chavePrivada);

    /**
     * Chama o endpoint /api/verificar-com-chave no serviço Rust.
     * @param texto O texto original.
     * @param assinatura A assinatura a ser verificada.
     * @param chavePublica A chave pública do remetente para verificar a assinatura.
     * @return VerificationResponseDto indicando se a assinatura é válida e uma mensagem.
     */
    VerificationResponseDto verificarComChave(String texto, List<Long> assinatura, ChaveDto chavePublica);
}