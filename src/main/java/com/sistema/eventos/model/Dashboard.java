package com.sistema.eventos.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

@Entity
@Table(name = "dashboards")
public class Dashboard {

    private static final Logger log = LoggerFactory.getLogger(Dashboard.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ElementCollection
    @CollectionTable(name = "dashboard_indicadores")
    @Column(name = "indicador")
    private List<String> indicadores = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "dashboard_portfolio")
    @Column(name = "portfolio_projeto")
    private List<String> portfolioProjetos = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    public Dashboard() {}

    public Dashboard(Projeto projeto) {
        this.projeto = projeto;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public List<String> getIndicadores() { return indicadores; }
    public void setIndicadores(List<String> indicadores) { this.indicadores = indicadores; }

    public List<String> getPortfolioProjetos() { return portfolioProjetos; }
    public void setPortfolioProjetos(List<String> portfolioProjetos) { this.portfolioProjetos = portfolioProjetos; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }

    public void visualizarDashboards(Usuario usuario) {
        log.info("=== DASHBOARD - Visualizado por: {} ({}) ===", usuario.getNome(), usuario.getPerfilAcesso().getDescricao());

        if (projeto != null) {
            log.info("Projeto: {}", projeto.getNome());
            log.info("Progresso: {}%", projeto.getProgresso());
            log.info("Status: {}", projeto.getStatus() != null ? projeto.getStatus().getDescricao() : "N/A");
            log.info("Gerente: {}", projeto.getGerente() != null ? projeto.getGerente().getNome() : "Não atribuído");
        }

        log.info("Indicadores:");
        for (String indicador : indicadores) {
            log.info("  - {}", indicador);
        }

        log.info("Portfólio de Projetos:");
        for (String projetoNome : portfolioProjetos) {
            log.info("  - {}", projetoNome);
        }
    }

    public void tomarDecisao(Usuario usuario) {
        log.info("=== ANÁLISE PARA TOMADA DE DECISÃO - Usuário: {} ===", usuario.getNome());

        if (usuario.isGerente() || usuario.isAdmin()) {
            if (projeto != null && projeto.getProgresso() < 50) {
                log.warn("ALERTA: Projeto '{}' com progresso abaixo de 50%!", projeto.getNome());
                log.warn("Recomendação: Ajustar rota, realocar recursos e revisar cronograma.");
                projeto.ajustarRota();
            } else if (projeto != null && projeto.getProgresso() >= 90) {
                log.info("Projeto '{}' quase concluído! Preparando para entrega.", projeto.getNome());
            } else if (projeto != null) {
                log.info("Projeto '{}' dentro do esperado. Progresso: {}%", projeto.getNome(), projeto.getProgresso());
            }
        } else {
            log.info("Usuário comum - Visualização apenas. Para decisões, consulte o gerente.");
        }
    }

    public void adicionarIndicador(String indicador, Usuario usuario) {
        if (usuario.isGerente() || usuario.isAdmin()) {
            indicadores.add(indicador);
            log.info("Indicador '{}' adicionado ao dashboard por {}", indicador, usuario.getNome());
        } else {
            log.warn("Usuário {} não tem permissão para adicionar indicadores", usuario.getNome());
        }
    }

    public void adicionarProjetoPortfolio(String projetoNome, Usuario usuario) {
        if (usuario.isAdmin()) {
            portfolioProjetos.add(projetoNome);
            log.info("Projeto '{}' adicionado ao portfólio por {}", projetoNome, usuario.getNome());
        } else {
            log.warn("Usuário {} não tem permissão para adicionar projetos ao portfólio", usuario.getNome());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dashboard dashboard = (Dashboard) o;
        return id == dashboard.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Dashboard{id=" + id + "}";
    }
}