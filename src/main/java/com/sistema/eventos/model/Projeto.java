package com.sistema.eventos.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "projetos")
public class Projeto {

    private static final Logger log = LoggerFactory.getLogger(Projeto.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_inicio")
    private Date dataInicio;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_fim")
    private Date dataFim;

    @Enumerated(EnumType.STRING)
    private StatusProjeto status;

    private float progresso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gerente_id")
    private Usuario gerente;

    @OneToOne(mappedBy = "projeto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Dashboard dashboard;

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Equipe> equipes = new ArrayList<>();

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tarefa> tarefas = new ArrayList<>();

    public Projeto() {}

    public Projeto(String nome, Date dataInicio, Date dataFim, StatusProjeto status, float progresso) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.progresso = progresso;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

    public Date getDataFim() { return dataFim; }
    public void setDataFim(Date dataFim) { this.dataFim = dataFim; }

    public StatusProjeto getStatus() { return status; }
    public void setStatus(StatusProjeto status) { this.status = status; }

    public float getProgresso() { return progresso; }
    public void setProgresso(float progresso) { this.progresso = progresso; }

    public Usuario getGerente() { return gerente; }
    public void setGerente(Usuario gerente) { this.gerente = gerente; }

    public Dashboard getDashboard() { return dashboard; }
    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
        if (dashboard != null && dashboard.getProjeto() != this) {
            dashboard.setProjeto(this);
        }
    }

    public List<Equipe> getEquipes() { return equipes; }
    public void setEquipes(List<Equipe> equipes) { this.equipes = equipes; }

    public List<Tarefa> getTarefas() { return tarefas; }
    public void setTarefas(List<Tarefa> tarefas) { this.tarefas = tarefas; }

    public void criarProjeto(Usuario gerente) {
        this.gerente = gerente;
        log.info("Projeto '{}' criado pelo gerente: {}", nome, gerente.getNome());
    }

    public void gerarRelatorio() {
        log.info("=== RELATÓRIO DO PROJETO: {} ===", nome);
        log.info("Progresso: {}%", progresso);
        log.info("Status: {}", status != null ? status.getDescricao() : "N/A");
        log.info("Data Início: {}", dataInicio);
        log.info("Data Fim: {}", dataFim);
        log.info("Gerente: {}", gerente != null ? gerente.getNome() : "Não atribuído");
        log.info("Total de Equipes: {}", equipes != null ? equipes.size() : 0);
        log.info("Total de Tarefas: {}", tarefas != null ? tarefas.size() : 0);
    }

    public void ajustarRota() {
        log.warn("Ajustando rota/planejamento do projeto: {}", nome);
        log.info("Motivo: Replanejamento necessário devido ao progresso atual de {}%", progresso);
    }

    public void atualizarProgresso() {
        if (tarefas == null || tarefas.isEmpty()) {
            log.warn("Projeto '{}' não possui tarefas para calcular progresso", nome);
            return;
        }

        int totalHorasEstimadas = tarefas.stream().mapToInt(Tarefa::getHorasEstimadas).sum();
        int totalHorasTrabalhadas = tarefas.stream().mapToInt(Tarefa::getHorasTrabalhadas).sum();

        if (totalHorasEstimadas > 0) {
            this.progresso = (float) totalHorasTrabalhadas / totalHorasEstimadas * 100;
            log.info("Progresso do projeto '{}' atualizado para: {}%", nome, progresso);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Projeto projeto = (Projeto) o;
        return id == projeto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Projeto{id=" + id + ", nome='" + nome + "'}";
    }
}