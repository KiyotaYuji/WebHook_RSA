use actix_web::{web, App, HttpServer, Responder, HttpResponse, post};
use serde::{Deserialize, Serialize};
use rand::Rng;

// --- Implementação manual do SHA-256 ---
fn sha256_manual(input: &str) -> String {
    let mut hash = [0u8; 32];
    let mut data = input.as_bytes().to_vec();
    data.push(0x80); // Adiciona o bit '1' de padding

    // Adiciona padding '0' até que o comprimento dos dados em bits seja congruente a 448 (mod 512)
    // Comprimento em bytes: data.len() * 8 % 512 == 448 --> data.len() % 64 == 56
    while data.len() % 64 != 56 {
        data.push(0);
    }

    // Adiciona o comprimento original da mensagem (antes do padding) como um inteiro de 64 bits big-endian
    let bit_len = (input.len() * 8) as u64;
    data.extend_from_slice(&bit_len.to_be_bytes());

    // Constantes de hash iniciais (primeiros 32 bits das partes fracionárias das raízes quadradas dos primeiros 8 primos 2..19)
    let mut h_constants = [
        0x6a09e667u32, 0xbb67ae85u32, 0x3c6ef372u32, 0xa54ff53au32,
        0x510e527fu32, 0x9b05688cu32, 0x1f83d9abu32, 0x5be0cd19u32,
    ];

    // Constantes de round (primeiros 32 bits das partes fracionárias das raízes cúbicas dos primeiros 64 primos 2..311)
    let k_constants = [
        0x428a2f98u32, 0x71374491u32, 0xb5c0fbcfu32, 0xe9b5dba5u32, 0x3956c25bu32, 0x59f111f1u32,
        0x923f82a4u32, 0xab1c5ed5u32, 0xd807aa98u32, 0x12835b01u32, 0x243185beu32, 0x550c7dc3u32,
        0x72be5d74u32, 0x80deb1feu32, 0x9bdc06a7u32, 0xc19bf174u32, 0xe49b69c1u32, 0xefbe4786u32,
        0x0fc19dc6u32, 0x240ca1ccu32, 0x2de92c6fu32, 0x4a7484aau32, 0x5cb0a9dcu32, 0x76f988dau32,
        0x983e5152u32, 0xa831c66du32, 0xb00327c8u32, 0xbf597fc7u32, 0xc6e00bf3u32, 0xd5a79147u32,
        0x06ca6351u32, 0x14292967u32, 0x27b70a85u32, 0x2e1b2138u32, 0x4d2c6dfcu32, 0x53380d13u32,
        0x650a7354u32, 0x766a0abbu32, 0x81c2c92eu32, 0x92722c85u32, 0xa2bfe8a1u32, 0xa81a664bu32,
        0xc24b8b70u32, 0xc76c51a3u32, 0xd192e819u32, 0xd6990624u32, 0xf40e3585u32, 0x106aa070u32,
        0x19a4c116u32, 0x1e376c08u32, 0x2748774cu32, 0x34b0bcb5u32, 0x391c0cb3u32, 0x4ed8aa4au32,
        0x5b9cca4fu32, 0x682e6ff3u32, 0x748f82eeu32, 0x78a5636fu32, 0x84c87814u32, 0x8cc70208u32,
        0x90befffau32, 0xa4506cebu32, 0xbef9a3f7u32, 0xc67178f2u32,
    ];

    // Processa a mensagem em blocos de 512 bits (64 bytes)
    for chunk in data.chunks(64) {
        let mut w = [0u32; 64]; // Message schedule array

        // Prepara o message schedule (primeiras 16 palavras são do bloco)
        for i in 0..16 {
            w[i] = u32::from_be_bytes([chunk[4 * i], chunk[4 * i + 1], chunk[4 * i + 2], chunk[4 * i + 3]]);
        }

        // Estende as 16 palavras para 64 palavras
        for i in 16..64 {
            let s0 = w[i - 15].rotate_right(7) ^ w[i - 15].rotate_right(18) ^ (w[i - 15] >> 3);
            let s1 = w[i - 2].rotate_right(17) ^ w[i - 2].rotate_right(19) ^ (w[i - 2] >> 10);
            w[i] = w[i - 16].wrapping_add(s0).wrapping_add(w[i - 7]).wrapping_add(s1);
        }

        // Inicializa variáveis de trabalho com os valores de hash atuais
        let mut a = h_constants[0];
        let mut b = h_constants[1];
        let mut c = h_constants[2];
        let mut d = h_constants[3];
        let mut e = h_constants[4];
        let mut f = h_constants[5];
        let mut g = h_constants[6];
        let mut h_val = h_constants[7]; // Renomeado para h_val para evitar conflito com a variável de loop

        // Loop principal de compressão
        for i in 0..64 {
            let s1_rot = e.rotate_right(6) ^ e.rotate_right(11) ^ e.rotate_right(25);
            let ch = (e & f) ^ ((!e) & g); // Função de escolha
            let temp1 = h_val.wrapping_add(s1_rot).wrapping_add(ch).wrapping_add(k_constants[i]).wrapping_add(w[i]);
            let s0_rot = a.rotate_right(2) ^ a.rotate_right(13) ^ a.rotate_right(22);
            let maj = (a & b) ^ (a & c) ^ (b & c); // Função de maioria
            let temp2 = s0_rot.wrapping_add(maj);

            h_val = g;
            g = f;
            f = e;
            e = d.wrapping_add(temp1);
            d = c;
            c = b;
            b = a;
            a = temp1.wrapping_add(temp2);
        }

        // Adiciona o valor comprimido do bloco ao valor de hash atual
        h_constants[0] = h_constants[0].wrapping_add(a);
        h_constants[1] = h_constants[1].wrapping_add(b);
        h_constants[2] = h_constants[2].wrapping_add(c);
        h_constants[3] = h_constants[3].wrapping_add(d);
        h_constants[4] = h_constants[4].wrapping_add(e);
        h_constants[5] = h_constants[5].wrapping_add(f);
        h_constants[6] = h_constants[6].wrapping_add(g);
        h_constants[7] = h_constants[7].wrapping_add(h_val);
    }

    // Concatena os valores de hash para produzir o digest final
    for (i, val) in h_constants.iter().enumerate() {
        hash[4 * i..4 * i + 4].copy_from_slice(&val.to_be_bytes());
    }

    hash.iter().map(|byte| format!("{:02x}", byte)).collect()
}


// --- Funções RSA ---
fn mdc(a: u64, b: u64) -> u64 {
    if b == 0 {
        a
    } else {
        mdc(b, a % b)
    }
}

fn e_primo(num: u64) -> bool {
    if num < 2 {
        return false;
    }
    for i in 2..=((num as f64).sqrt() as u64) {
        if num % i == 0 {
            return false;
        }
    }
    true
}

fn gerar_primo() -> u64 {
    let mut rng = rand::thread_rng();
    loop {
        // Primos pequenos para exemplo. Para segurança real, use primos muito maiores (ex: 1024 bits ou mais).
        // Aumentar o range aqui pode tornar a geração de 'e' mais estável.
        let num = rng.gen_range(100u64..500u64); // Aumentado um pouco o limite superior
        if e_primo(num) {
            return num;
        }
    }
}

fn inverso_modular(e: u64, phi: u64) -> u64 {
    let mut t = 0i64;
    let mut new_t = 1i64;
    let mut r = phi as i64;
    let mut new_r = e as i64;

    while new_r != 0 {
        let quotient = r / new_r;

        let temp_t = t;
        t = new_t;
        new_t = temp_t - quotient * new_t;

        let temp_r = r;
        r = new_r;
        new_r = temp_r - quotient * new_r;
    }

    if r > 1 {
        // Este pânico indica que 'e' e 'phi' não são coprimos, o que não deveria acontecer se 'e' for escolhido corretamente.
        panic!("e ({}) não é invertível modulo phi ({}), mdc não é 1", e, phi);
    }

    let resultado_i64 = if t < 0 {
        t + phi as i64 // Garante que o resultado seja positivo
    } else {
        t
    };
    resultado_i64 as u64
}

// Estruturas para representar as chaves como objetos JSON
#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct ChavePublicaDTO {
    pub e: u64,
    pub n: u64,
}

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct ChavePrivadaDTO {
    pub d: u64,
    pub n: u64,
}

// Modificado para usar as DTOs acima
#[derive(Serialize, Clone, Debug)]
pub struct ChavesResponseDTO {
    #[serde(rename = "chavePublica")] // Para corresponder ao Java DTO
    pub chave_publica: ChavePublicaDTO,
    #[serde(rename = "chavePrivada")] // Para corresponder ao Java DTO
    pub chave_privada: ChavePrivadaDTO,
}


fn gerar_chaves_rsa() -> (ChavePublicaDTO, ChavePrivadaDTO) {
    let p = gerar_primo();
    let mut q = gerar_primo();

    while p == q { // Garantir que p e q sejam diferentes
        q = gerar_primo();
    }

    let n = p * q;
    let phi = (p - 1) * (q - 1);

    let mut e_val = 65537;
    if e_val >= phi || mdc(e_val, phi) != 1 {
        e_val = 3; // Tenta um 'e' menor
        while mdc(e_val, phi) != 1 {
            e_val += 2; // Próximo ímpar
            if e_val >= phi {
                // Se phi for muito pequeno (ex: p=2, q=3 -> phi=2), pode não encontrar 'e'
                // Neste caso, é melhor gerar p e q novamente.
                // Para simplificar, vamos entrar em pânico, mas em produção, deveria tentar novamente.
                println!("Falha ao encontrar 'e' para p={}, q={}, phi={}. Tentando gerar novas chaves.", p, q, phi);
                return gerar_chaves_rsa(); // Tenta gerar um novo par de chaves recursivamente
            }
        }
    }

    let d_val = inverso_modular(e_val, phi);

    let chave_publica = ChavePublicaDTO { e: e_val, n };
    let chave_privada = ChavePrivadaDTO { d: d_val, n };
    (chave_publica, chave_privada)
}


fn mod_exp(mut base: u64, mut exp: u64, modu: u64) -> u64 {
    let mut resultado = 1;
    base %= modu;

    while exp > 0 {
        if exp % 2 == 1 { // Se exp é ímpar
            resultado = (resultado as u128 * base as u128 % modu as u128) as u64;
        }
        exp >>= 1; // exp = exp / 2
        base = (base as u128 * base as u128 % modu as u128) as u64; // base = base^2 % modu
    }
    resultado
}

fn criptografar_texto(texto: &str, chave_publica: &ChavePublicaDTO) -> Vec<u64> {
    println!("[Rust - Criptografia] Texto original para criptografar: \"{}\"", texto);
    println!("[Rust - Criptografia] Usando chave pública N: {}, E: {}", chave_publica.n, chave_publica.e);
    let dados_criptografados: Vec<u64> = texto.bytes()
        .map(|byte| mod_exp(byte as u64, chave_publica.e, chave_publica.n))
        .collect();
    println!("[Rust - Criptografia] Dados criptografados gerados: {:?}", dados_criptografados);
    dados_criptografados
}

fn descriptografar_texto(criptografado: &[u64], chave_privada: &ChavePrivadaDTO) -> Result<String, std::string::FromUtf8Error> {
    println!("[Rust - Descriptografia] Dados criptografados recebidos: {:?}", criptografado);
    // Novamente, evite logar 'd'. Logar 'n' pode ser útil para debug.
    println!("[Rust - Descriptografia] Usando chave privada com N: {}", chave_privada.n);
    let bytes_descriptografados: Vec<u8> = criptografado.iter()
        .map(|&valor| mod_exp(valor, chave_privada.d, chave_privada.n) as u8)
        .collect();

    let resultado_string = String::from_utf8(bytes_descriptografados);
    match &resultado_string {
        Ok(texto) => println!("[Rust - Descriptografia] Texto descriptografado: \"{}\"", texto),
        Err(e) => println!("[Rust - Descriptografia] Erro ao converter bytes descriptografados para UTF-8: {}", e),
    }
    resultado_string
}

fn gerar_hash(texto: &str) -> String {
    // A sua implementação do sha256_manual é chamada aqui
    let hash_calculado = sha256_manual(texto);
    println!("[Rust - Hash SHA256] Texto original para hash: \"{}\"", texto);
    println!("[Rust - Hash SHA256] Hash SHA256 gerado: {}", hash_calculado);
    hash_calculado
}

fn assinar_hash(hash: &str, chave_privada: &ChavePrivadaDTO) -> Vec<u64> {
    println!("[Rust - Assinatura] Hash recebido para assinar: \"{}\"", hash);
    // Por segurança, não é ideal logar o valor de 'd' da chave privada.
    // Logar 'n' é geralmente aceitável se precisar confirmar a chave.
    println!("[Rust - Assinatura] Usando chave privada com N: {}", chave_privada.n);
    let assinatura_calculada: Vec<u64> = hash.bytes()
        .map(|byte| mod_exp(byte as u64, chave_privada.d, chave_privada.n))
        .collect();
    println!("[Rust - Assinatura] Assinatura RSA gerada (blocos): {:?}", assinatura_calculada);
    assinatura_calculada
}

fn verificar_assinatura(hash: &str, assinatura: &[u64], chave_publica: &ChavePublicaDTO) -> bool {
    println!("[Rust - Verificação de Assinatura] Hash original esperado: \"{}\"", hash);
    println!("[Rust - Verificação de Assinatura] Assinatura recebida para verificação: {:?}", assinatura);
    println!("[Rust - Verificação de Assinatura] Usando chave pública N: {}, E: {}", chave_publica.n, chave_publica.e);

    let hash_descriptografado_bytes: Vec<u8> = assinatura
        .iter()
        .map(|&valor| mod_exp(valor, chave_publica.e, chave_publica.n) as u8)
        .collect();

    match String::from_utf8(hash_descriptografado_bytes) {
        Ok(hash_descriptografado_str) => {
            println!("[Rust - Verificação de Assinatura] Hash obtido da assinatura (descriptografado): \"{}\"", hash_descriptografado_str);
            let eh_valida = hash_descriptografado_str == hash;
            println!("[Rust - Verificação de Assinatura] Resultado da comparação dos hashes: {}", if eh_valida { "IGUAIS (VÁLIDA)" } else { "DIFERENTES (INVÁLIDA)" });
            eh_valida
        },
        Err(e) => {
            println!("[Rust - Verificação de Assinatura] Erro ao converter hash da assinatura para UTF-8: {}", e);
            println!("[Rust - Verificação de Assinatura] Resultado da verificação: INVÁLIDA (devido a erro de conversão)");
            false
        },
    }
}
// --- Estruturas DTO para os endpoints ---

#[derive(Deserialize)]
pub struct EncryptWithKeyRequest {
    pub texto: String,
    #[serde(rename = "chavePublica")]
    pub chave_publica: ChavePublicaDTO,
}

#[derive(Serialize)]
pub struct EncryptedDataResponse {
    #[serde(rename = "dadosCriptografados")]
    pub dados_criptografados: Vec<u64>,
}

#[derive(Deserialize)]
pub struct DecryptWithKeyRequest {
    #[serde(rename = "dadosCriptografados")]
    pub dados_criptografados: Vec<u64>,
    #[serde(rename = "chavePrivada")]
    pub chave_privada: ChavePrivadaDTO,
}

#[derive(Serialize)]
pub struct DecryptedTextResponse {
    #[serde(rename = "textoDescriptografado")]
    pub texto_descriptografado: String,
    pub erro: Option<String>,
}

#[derive(Deserialize)]
pub struct SignWithKeyRequest {
    pub texto: String,
    #[serde(rename = "chavePrivada")]
    pub chave_privada: ChavePrivadaDTO,
}

#[derive(Serialize)]
pub struct SignatureResponse {
    pub assinatura: Vec<u64>,
}

#[derive(Deserialize)]
pub struct VerifyWithKeyRequest {
    pub texto: String,
    pub assinatura: Vec<u64>,
    #[serde(rename = "chavePublica")]
    pub chave_publica: ChavePublicaDTO,
}

#[derive(Serialize)]
pub struct VerificationResponse {
    pub valida: bool,
    pub mensagem: String,
}


// --- Handlers dos Endpoints ---

#[post("/api/gerar-chaves-usuario")]
async fn gerar_chaves_usuario_handler() -> impl Responder {
    let (chave_publica_dto, chave_privada_dto) = gerar_chaves_rsa();
    println!("Chave Pública Gerada (DTO): {:?}", chave_publica_dto);
    println!("Chave Privada Gerada (DTO): {:?}", chave_privada_dto);
    let resposta = ChavesResponseDTO {
        chave_publica: chave_publica_dto,
        chave_privada: chave_privada_dto,
    };
    HttpResponse::Ok().json(resposta)
}

#[post("/api/criptografar-com-chave")]
async fn criptografar_com_chave_handler(item: web::Json<EncryptWithKeyRequest>) -> impl Responder {
    let dados_criptografados = criptografar_texto(&item.texto, &item.chave_publica);
    println!("Dados criptografados: {:?}", dados_criptografados);
    HttpResponse::Ok().json(EncryptedDataResponse { dados_criptografados })
}

#[post("/api/descriptografar-com-chave")]
async fn descriptografar_com_chave_handler(item: web::Json<DecryptWithKeyRequest>) -> impl Responder {
    match descriptografar_texto(&item.dados_criptografados, &item.chave_privada) {
        Ok(texto) => HttpResponse::Ok().json(DecryptedTextResponse {
            texto_descriptografado: texto,
            erro: None
        }),
        Err(e) => {
            eprintln!("Erro ao descriptografar no handler: {}", e);
            HttpResponse::BadRequest().json(DecryptedTextResponse {
                texto_descriptografado: String::new(),
                erro: Some(format!("Erro ao descriptografar: {}", e)),
            })
        }
    }
}

#[post("/api/assinar-com-chave")]
async fn assinar_com_chave_handler(item: web::Json<SignWithKeyRequest>) -> impl Responder {
    let hash_original = gerar_hash(&item.texto);
    let assinatura = assinar_hash(&hash_original, &item.chave_privada);
    HttpResponse::Ok().json(SignatureResponse { assinatura })
}

#[post("/api/verificar-com-chave")]
async fn verificar_com_chave_handler(item: web::Json<VerifyWithKeyRequest>) -> impl Responder {
    let hash_texto_original = gerar_hash(&item.texto);
    let assinatura_valida = verificar_assinatura(&hash_texto_original, &item.assinatura, &item.chave_publica);

    if assinatura_valida {
        HttpResponse::Ok().json(VerificationResponse {
            valida: true,
            mensagem: "Assinatura válida.".to_string()
        })
    } else {
        HttpResponse::Ok().json(VerificationResponse {
            valida: false,
            mensagem: "Assinatura inválida.".to_string()
        })
    }
}

// --- Main ---
#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let server_address = "127.0.0.1:8000";
    println!("Servidor Rust rodando em http://{}", server_address);

    HttpServer::new(move || {
        App::new()
            .service(gerar_chaves_usuario_handler)
            .service(criptografar_com_chave_handler)
            .service(descriptografar_com_chave_handler)
            .service(assinar_com_chave_handler)
            .service(verificar_com_chave_handler)
    })
        .bind(server_address)?
        .run()
        .await
}