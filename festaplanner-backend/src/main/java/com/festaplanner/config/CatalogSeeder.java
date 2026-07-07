package com.festaplanner.config;

import com.festaplanner.model.CatalogoItem;
import com.festaplanner.model.TipoItem;
import com.festaplanner.repository.CatalogoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Popula o catalogo na primeira execucao com os MESMOS dados hoje
 * hardcoded em orcamento-component.ts do Angular (temas, buffet, bolo,
 * docinhos, salgadinhos, bebidas, decoracao, musica/animacao), para que o
 * backend seja imediatamente utilizavel e o front possa migrar de dados
 * locais para estes endpoints sem perder nenhum item (ver README).
 *
 * Roda apenas se a tabela catalogo_itens estiver vazia.
 */
@Component
@Order(2)
@RequiredArgsConstructor
public class CatalogSeeder implements CommandLineRunner {

    private final CatalogoItemRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        repository.saveAll(buffetEBolo());
        repository.saveAll(temasInfantis());
        repository.saveAll(temasQuinzeAnos());
        repository.saveAll(temasPastaEvento());
        repository.saveAll(docinhosClassicos());
        repository.saveAll(docesFinos());
        repository.saveAll(salgadinhosTradicionais());
        repository.saveAll(salgadosSofisticados());
        repository.saveAll(bebidas());
        repository.saveAll(decoracao());
        repository.saveAll(musicaAnimacao());

        System.out.println("=====================================================");
        System.out.println(" Catalogo inicial carregado: " + repository.count() + " itens");
        System.out.println("=====================================================");
    }

    // ============================================================
    // BUFFET / BOLO
    // ============================================================
    private List<CatalogoItem> buffetEBolo() {
        return List.of(
                buffet("Buffet Básico", "Salgados, frios, pratos quentes e estrutura essencial para os convidados.", 2500, true),
                buffet("Buffet Premium", "Opção gourmet com estações ao vivo e cardápio mais sofisticado.", 4200, false),
                buffet("Coquetel", "Finger foods, canapés e atendimento leve para eventos sociais.", 2600, false),
                bolo("Bolo Cenográfico", "Bolo decorativo para compor a mesa principal e valorizar as fotos.", 850),
                bolo("Bolo Real Padrão (1 Andar)", "Bolo real compacto com cobertura personalizada e acabamento delicado para eventos menores.", 700),
                bolo("Bolo Real Duplo (2 Andares)", "Bolo real com dois andares, recheios variados e decoração alinhada ao tema da festa.", 950),
                bolo("Bolo Real Majestoso (3 Andares)", "Bolo real com recheios variados, cobertura personalizada e acabamento temático.", 1200)
        );
    }

    private CatalogoItem buffet(String nome, String desc, double preco, boolean obrigatorio) {
        return CatalogoItem.builder().tipo(TipoItem.BUFFET).nome(nome).descricao(desc)
                .preco(preco).obrigatorio(obrigatorio).ativo(true).build();
    }

    private CatalogoItem bolo(String nome, String desc, double preco) {
        return CatalogoItem.builder().tipo(TipoItem.BOLO).nome(nome).descricao(desc)
                .preco(preco).ativo(true).build();
    }

    // ============================================================
    // TEMAS
    // ============================================================
    private List<CatalogoItem> temasInfantis() {
        return List.of(
                tema("Super Heróis", "Painel, mesa decorada e visual vibrante inspirado em heróis.", 1200, "menino"),
                tema("Dinossauro", "Tema aventureiro com folhagens, painel e mesa decorada.", 1250, "menino"),
                tema("Futebol e Times do Coração", "Decoração esportiva com painel e composição inspirada em futebol.", 1100, "menino"),
                tema("Veículos Hot Wheels", "Tema infantil com carros, pistas, velocidade e decoração inspirada no universo Hot Wheels.", 1300, "menino"),
                tema("Video Game / Universo Game", "Tema infantil gamer com controles, personagens, luzes e decoração inspirada no universo dos games.", 1350, "menino"),
                tema("Princesas da Disney", "Tema delicado com painel, mesa decorada e detalhes de princesas.", 1300, "menina"),
                tema("Barbie", "Tema rosa com painel, mesa decorada e clima fashion infantil.", 1250, "menina"),
                tema("Unicórnio", "Tema lúdico com tons suaves, arco-íris e composição encantada.", 1200, "menina"),
                tema("Bosque das Fadas", "Tema delicado com fadas, flores, luzes e elementos encantados para festa infantil.", 1400, "menina"),
                tema("Glow Party", "Tema moderno com luzes, neon, brilho e decoração colorida para uma festa animada.", 1450, "menina"),
                tema("Minnie Mouse", "Tema clássico e delicado inspirado na Minnie, com decoração vermelha, rosa, laços e mesa temática.", 1350, "menina"),
                tema("Patrulha Canina", "Tema divertido com painel, personagens e mesa decorada.", 1250, "unissex"),
                tema("Peppa Pig", "Tema infantil leve e colorido com composição para fotos.", 1200, "unissex")
        ).stream().peek(t -> t.setEventoTema("Infantil")).toList();
    }

    private CatalogoItem tema(String nome, String desc, double preco, String genero) {
        return CatalogoItem.builder().tipo(TipoItem.TEMA).nome(nome).descricao(desc)
                .preco(preco).generoTema(genero).ativo(true).build();
    }

    private List<CatalogoItem> temasQuinzeAnos() {
        return List.of(
                temaQuinze("Baile de Máscaras", "Tema elegante com máscaras, brilho, sofisticação e clima de baile para festa de 15 anos.", 1800, "classico"),
                temaQuinze("Glamour Neon", "Tema moderno com luzes neon, cores vibrantes e visual jovem para uma festa animada.", 1900, "moderno"),
                temaQuinze("Jardim Floral", "Tema romântico com flores, delicadeza, luzes e composição elegante para festa de 15 anos.", 2000, "romantico"),
                temaQuinze("Sunset Party - Boho Chic", "Tema sofisticado com clima sunset, elementos boho, tons quentes e decoração elegante.", 2100, "moderno")
        );
    }

    private CatalogoItem temaQuinze(String nome, String desc, double preco, String categoriaTema) {
        return CatalogoItem.builder().tipo(TipoItem.TEMA).nome(nome).descricao(desc)
                .preco(preco).eventoTema("15 Anos").categoriaTema(categoriaTema).ativo(true).build();
    }

    private List<CatalogoItem> temasPastaEvento() {
        return List.of(
                temaPasta("Casamento", "Tema elegante com ambientação clássica e composição visual para cerimônia e recepção.", 2200, "Casamento"),
                temaPasta("Floral", "Tema delicado com flores, leveza e decoração romântica para um evento sofisticado.", 1800, "Floral"),
                temaPasta("Corporativo", "Tema profissional com composição clean, identidade visual e ambientação para eventos corporativos.", 1900, "Corporativo")
        );
    }

    private CatalogoItem temaPasta(String nome, String desc, double preco, String evento) {
        return CatalogoItem.builder().tipo(TipoItem.TEMA).nome(nome).descricao(desc)
                .preco(preco).eventoTema(evento).ativo(true).build();
    }

    // ============================================================
    // DOCINHOS (linha classica) / DOCES FINOS (linha premium)
    // ============================================================
    private List<CatalogoItem> docinhosClassicos() {
        return List.of(
                doce("Beijinho", 2.5, 25, "tradicional"),
                doce("Branco Crocante", 3, 25, "tradicional"),
                doce("Brigadeiro", 3, 25, "tradicional"),
                doce("Brigadeiro Branco", 3, 25, "tradicional"),
                doce("Brigadeiro Confete", 3.2, 25, "tradicional"),
                doce("Casadinho", 3.2, 25, "tradicional"),
                doce("Choco Branco", 3.2, 25, "tradicional"),
                doce("Brigadeiro de KitKat", 3.8, 20, "gourmet"),
                doce("Brigadeiro de Oreo", 3.5, 20, "gourmet"),
                doce("Brigadeiro Sabor Churros", 3.5, 20, "gourmet"),
                doce("Chocoball", 3.5, 20, "gourmet"),
                doce("Choco Crocante", 3.5, 20, "gourmet"),
                doce("Chocolate com Cereja", 4, 20, "gourmet"),
                doce("Ferrero", 4, 20, "gourmet"),
                doce("Kinder com Avelã", 4, 20, "gourmet"),
                doce("Ninho com Nutella", 3.8, 20, "gourmet"),
                doce("Prestígio", 3.5, 20, "gourmet"),
                doce("Sensação", 3.5, 20, "gourmet")
        );
    }

    private CatalogoItem doce(String nome, double precoUnitario, int qtdMinima, String categoria) {
        return CatalogoItem.builder().tipo(TipoItem.DOCE).nome(nome)
                .precoUnitario(precoUnitario).quantidadeMinima(qtdMinima).incremento(5)
                .categoria(categoria).linha("classica").ativo(true).build();
    }

    private List<CatalogoItem> docesFinos() {
        return List.of(
                doceFino("Brigadeiro de Pistache", 5, 25),
                doceFino("Bombom Aberto de Pistache e Trufa Branca", 6, 25),
                doceFino("Bombom de Caramelo Salgado com Flor de Sal", 4, 25),
                doceFino("Bombom de Champagne com Frutas Vermelhas", 6, 25),
                doceFino("Bombom de Coco Queimado Trufado", 4.5, 25),
                doceFino("Caixinha de Chocolate com Physalis", 5, 25),
                doceFino("Camafeu de Nozes", 4.5, 25),
                doceFino("Copinho de Cappuccino com Ampola Saborizante", 7.5, 20),
                doceFino("Damasco Recheado com Creme de Amêndoas", 5.5, 25),
                doceFino("Fudge de Chocolate Belga com Macadâmias", 6, 25),
                doceFino("Macarons Gourmet", 4.5, 20),
                doceFino("Mini Tartelete de Limão Siciliano com Merengue Suíço", 7.5, 20),
                doceFino("Pão de Mel Fino com Banho de Chocolate Decorado", 5.5, 25)
        );
    }

    private CatalogoItem doceFino(String nome, double precoUnitario, int qtdMinima) {
        return CatalogoItem.builder().tipo(TipoItem.DOCE).nome(nome)
                .precoUnitario(precoUnitario).quantidadeMinima(qtdMinima).incremento(5)
                .categoria("fino").linha("premium").ativo(true).build();
    }

    // ============================================================
    // SALGADINHOS (linha classica) / SALGADOS SOFISTICADOS (linha premium)
    // ============================================================
    private List<CatalogoItem> salgadinhosTradicionais() {
        return List.of(
                salgadinho("Coxinha de Frango", 0.85, 50, "tradicional"),
                salgadinho("Risole de Carne", 0.9, 50, "tradicional"),
                salgadinho("Risole de Queijo", 0.9, 50, "tradicional"),
                salgadinho("Bolinha de Queijo", 0.95, 50, "tradicional"),
                salgadinho("Quibe", 1, 50, "tradicional"),
                salgadinho("Croquete de Carne", 0.9, 50, "tradicional"),
                salgadinho("Empada de Frango", 1.1, 50, "tradicional"),
                salgadinho("Pastel Assado de Carne", 0.9, 50, "tradicional"),
                salgadinho("Esfiha de Carne", 0.95, 50, "tradicional"),
                salgadinho("Mini Cachorro Quente", 1.2, 50, "tradicional")
        );
    }

    private CatalogoItem salgadinho(String nome, double precoUnitario, int qtdMinima, String categoria) {
        return CatalogoItem.builder().tipo(TipoItem.SALGADINHO).nome(nome)
                .precoUnitario(precoUnitario).quantidadeMinima(qtdMinima).incremento(10)
                .categoria(categoria).linha("classica").ativo(true).build();
    }

    private List<CatalogoItem> salgadosSofisticados() {
        return List.of(
                salgadoSofisticado("Camarão Empanado com Catupiry", 5),
                salgadoSofisticado("Risole de Camarão com Catupiry", 3.5),
                salgadoSofisticado("Mini Quiche de Alho-Poró com Bacon", 4.25),
                salgadoSofisticado("Espetinho de Filé Mignon ao Molho Madeira", 6),
                salgadoSofisticado("Coxinha de Costela com Páprica Defumada", 4.25),
                salgadoSofisticado("Mini Bruschetta de Tomate Seco com Brie", 4.75),
                salgadoSofisticado("Mini Croissant de Brie com Damasco", 5.25)
        );
    }

    private CatalogoItem salgadoSofisticado(String nome, double precoUnitario) {
        return CatalogoItem.builder().tipo(TipoItem.SALGADINHO).nome(nome)
                .precoUnitario(precoUnitario).quantidadeMinima(20).incremento(5)
                .categoria("sofisticado").linha("premium").ativo(true).build();
    }

    // ============================================================
    // BEBIDAS
    // ============================================================
    private List<CatalogoItem> bebidas() {
        return List.of(
                bebidaInclusa("Água"),
                bebidaInclusa("Refrigerante"),
                bebidaInclusa("Suco"),
                bebidaExtra("Cerveja", "cerveja", 9, 12, 6),
                bebidaExtra("Espumante", "espumante", 16, 10, 5),
                bebidaExtra("Drink Autoral", "drink", 22, 10, 5),
                bebidaExtra("Vinho", "vinho", 65, 2, 1),
                bebidaExtra("Energético", "energetico", 12, 6, 6)
        );
    }

    private CatalogoItem bebidaInclusa(String nome) {
        return CatalogoItem.builder().tipo(TipoItem.BEBIDA).nome(nome)
                .categoria(nome.toLowerCase()).precoUnitario(0.0).inclusoNoPacote(true).ativo(true).build();
    }

    private CatalogoItem bebidaExtra(String nome, String categoria, double precoUnitario, int qtdMinima, int incremento) {
        return CatalogoItem.builder().tipo(TipoItem.BEBIDA).nome(nome).categoria(categoria)
                .precoUnitario(precoUnitario).quantidadeMinima(qtdMinima).incremento(incremento)
                .ativo(true).build();
    }

    // ============================================================
    // DECORACAO
    // ============================================================
    private List<CatalogoItem> decoracao() {
        return List.of(
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("balao")
                        .nome("Arco de Balões Clássico")
                        .descricao("Arco de balões em até 2 cores, montado no local no dia do evento.")
                        .itensInclusos(List.of("Balões nas cores escolhidas", "Estrutura de sustentação", "Montagem no local"))
                        .itensNaoInclusos(List.of("Hélio", "Balões metalizados especiais", "Personagens licenciados"))
                        .fornecimento("parceiro").precoUnitario(90.0).unidadeMedida("metro")
                        .quantidadeMinima(3).incremento(1).ativo(true).build(),
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("balao")
                        .nome("Painel com Balões")
                        .descricao("Painel decorativo com composição de balões para fotos e mesa principal.")
                        .itensInclusos(List.of("Balões em cores combinadas", "Montagem no local", "Composição visual para fotos"))
                        .itensNaoInclusos(List.of("Painel 3D personalizado", "Personagens licenciados", "Iluminação especial"))
                        .fornecimento("parceiro").precoUnitario(350.0).unidadeMedida("pacote")
                        .quantidadeMinima(1).incremento(1).ativo(true).build(),
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("balao")
                        .nome("Coluna de Balões")
                        .descricao("Coluna decorativa de balões para entrada, salão ou mesa principal.")
                        .itensInclusos(List.of("Balões nas cores escolhidas", "Base de sustentação", "Montagem no local"))
                        .itensNaoInclusos(List.of("Hélio", "Balões personalizados", "Retirada fora do horário combinado"))
                        .fornecimento("parceiro").precoUnitario(120.0).unidadeMedida("unidade")
                        .quantidadeMinima(2).incremento(1).ativo(true).build(),
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("cenografia")
                        .nome("Cenografia Temática Personalizada")
                        .descricao("Ambientação temática completa do salão, sob orçamento conforme o tema escolhido.")
                        .itensInclusos(List.of("Projeto de ambientação", "Elementos cenográficos", "Montagem e desmontagem"))
                        .itensNaoInclusos(List.of("Mobiliário especial", "Iluminação cênica", "Itens fora do tema contratado"))
                        .fornecimento("parceiro").precoUnitario(0.0).unidadeMedida("pacote")
                        .quantidadeMinima(1).incremento(1).sobOrcamento(true).precoReferencia("a partir de R$ 800,00").ativo(true).build(),
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("cenografia")
                        .nome("Painel 3D Personalizado")
                        .descricao("Painel cenográfico personalizado para tema infantil, 15 anos, casamento ou evento corporativo.")
                        .itensInclusos(List.of("Criação visual do painel", "Estrutura decorativa", "Montagem no local"))
                        .itensNaoInclusos(List.of("Personagens licenciados", "Iluminação especial", "Mobiliário extra"))
                        .fornecimento("parceiro").precoUnitario(0.0).unidadeMedida("pacote")
                        .quantidadeMinima(1).incremento(1).sobOrcamento(true).precoReferencia("a partir de R$ 600,00").ativo(true).build(),
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("iluminacao")
                        .nome("Pacote de Iluminação Cênica")
                        .descricao("Iluminação para destacar a pista, mesa principal e ambiente do evento.")
                        .itensInclusos(List.of("Canhões de luz", "Efeitos de iluminação", "Instalação no local"))
                        .itensNaoInclusos(List.of("Gerador de energia", "Sonorização", "Operador exclusivo, se não informado no contrato"))
                        .fornecimento("parceiro").precoUnitario(450.0).unidadeMedida("diaria")
                        .quantidadeMinima(1).incremento(1).ativo(true).build(),
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("iluminacao")
                        .nome("Máquina de Fumaça")
                        .descricao("Efeito de fumaça para pista de dança, entrada especial ou momento do parabéns.")
                        .itensInclusos(List.of("Máquina de fumaça", "Fluido básico", "Instalação no local"))
                        .itensNaoInclusos(List.of("Operação contínua durante todo o evento", "Reposição extra de fluido"))
                        .fornecimento("parceiro").precoUnitario(180.0).unidadeMedida("diaria")
                        .quantidadeMinima(1).incremento(1).ativo(true).build(),
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("personalizacao")
                        .nome("Kit de Personalização — Arquivo Digital")
                        .descricao("Você recebe os arquivos digitais e providencia impressão e montagem.")
                        .itensInclusos(List.of("Arte digital no tema escolhido", "Arquivos em alta resolução"))
                        .itensNaoInclusos(List.of("Impressão física", "Material", "Montagem no local"))
                        .fornecimento("casa").nivelPersonalizacao("digital").precoUnitario(35.0).unidadeMedida("pacote")
                        .quantidadeMinima(1).incremento(1).ativo(true).build(),
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("personalizacao")
                        .nome("Kit de Personalização — Material Pronto")
                        .descricao("Material impresso e recortado entregue antes do evento.")
                        .itensInclusos(List.of("Material impresso", "Recorte dos itens", "Entrega antes do evento"))
                        .itensNaoInclusos(List.of("Montagem no local", "Reposição em caso de dano"))
                        .fornecimento("casa").nivelPersonalizacao("prontoMontar").precoUnitario(80.0).unidadeMedida("pacote")
                        .quantidadeMinima(1).incremento(1).ativo(true).build(),
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("personalizacao")
                        .nome("Kit de Personalização — All Inclusive")
                        .descricao("Tudo pronto e montado no local antes da festa começar.")
                        .itensInclusos(List.of("Material personalizado", "Montagem completa no local", "Equipe própria no dia do evento"))
                        .itensNaoInclusos(List.of("Alterações de última hora", "Itens fora do tema contratado"))
                        .fornecimento("casa").nivelPersonalizacao("allInclusive").precoUnitario(220.0).unidadeMedida("pacote")
                        .quantidadeMinima(1).incremento(1).ativo(true).build(),
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("mobiliario")
                        .nome("Mesa + 4 Cadeiras Padrão")
                        .descricao("Conjunto básico de mesa com 4 cadeiras para convidados.")
                        .itensInclusos(List.of("1 mesa", "4 cadeiras", "Montagem prévia"))
                        .itensNaoInclusos(List.of("Toalha decorada", "Capa de cadeira", "Mobiliário premium"))
                        .fornecimento("casa").precoUnitario(20.0).unidadeMedida("unidade")
                        .quantidadeMinima(5).incremento(1).ativo(true).build(),
                CatalogoItem.builder().tipo(TipoItem.DECORACAO).categoria("mobiliario")
                        .nome("Móveis Provençais")
                        .descricao("Móveis decorativos para mesa principal, doces, lembrancinhas ou cenário de fotos.")
                        .itensInclusos(List.of("Conjunto de móveis decorativos", "Montagem no local", "Organização visual básica"))
                        .itensNaoInclusos(List.of("Decoração floral", "Personalização temática", "Transporte fora da região combinada"))
                        .fornecimento("parceiro").precoUnitario(350.0).unidadeMedida("pacote")
                        .quantidadeMinima(1).incremento(1).ativo(true).build()
        );
    }

    // ============================================================
    // MUSICA / ANIMACAO
    // ============================================================
    private List<CatalogoItem> musicaAnimacao() {
        return List.of(
                musica("DJ & Música", "musica", "DJ profissional 6h", 800, 6),
                musica("Animadores", "animacao", "Equipe de entretenimento", 1200, 6),
                musica("Fotografia", "experiencia", "Fotógrafo profissional", 1500, 6),
                musica("Bartender", "show", "Open bar premium", 650, 6)
        );
    }

    private CatalogoItem musica(String nome, String categoria, String desc, double preco, int duracaoHoras) {
        return CatalogoItem.builder().tipo(TipoItem.MUSICA_ANIMACAO).nome(nome).categoria(categoria)
                .descricao(desc).preco(preco).duracaoHoras(duracaoHoras).ativo(true).build();
    }
}
