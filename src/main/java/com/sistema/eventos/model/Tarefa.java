package com.sistema.eventos.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "tarefas")
public class Tarefa {

    private static final Logger log = LoggerFactory.getLogger(Tarefa.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    private StatusTarefa status;

    @Column(name = "horas_estimadas")
    private int horasEstimadas;

    @Column(name = "horas_trabalhadas")
    private int horasTrabalhadas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

    public Tarefa() {
        this.status = StatusTarefa.PENDENTE;
        this.horasTrabalhadas = 0;
    }

    public Tarefa(String descricao, StatusTarefa status, int horasEstimadas) {
        this.descricao = descricao;
        this.status = status;
        this.horasEstimadas = horasEstimadas;
        this.horasTrabalhadas = 0;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public StatusTarefa getStatus() { return status; }
    public void setStatus(StatusTarefa status) { this.status = status; }

    public int getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(int horasEstimadas) { this.horasEstimadas = horasEstimadas; }

    public int getHorasTrabalhadas() { return horasTrabalhadas; }
    public void setHorasTrabalhadas(int horasTrabalhadas) { this.horasTrabalhadas = horasTrabalhadas; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public Usuario getResponsavel() { return responsavel; }
    public void setResponsavel(Usuario responsavel) { this.responsavel = responsavel; }

    public void distribuir(Equipe equipe, Usuario responsavel) {
        if (!responsavel.isGerente() && !responsavel.isAdmin()) {
            log.warn("Usuário {} não tem permissão para distribuir tarefas", responsavel.getNome());
            return;
        }

        this.equipe = equipe;
        this.responsavel = responsavel;
        if (equipe != null) {
            equipe.getTarefas().add(this);
        }
        log.info("Tarefa '{}' distribuída para equipe '{}' por {}", descricao,
                equipe != null ? equipe.getNomeEquipe() : "sem equipe", responsavel.getNome());
    }

    public void atualizarStatus(StatusTarefa novoStatus, Usuario responsavel) {
        if (!responsavel.equals(this.responsavel) && !responsavel.isGerente() && !responsavel.isAdmin()) {
            log.warn("Usuário {} não tem permissão para atualizar esta tarefa", responsavel.getNome());
            return;
        }

        StatusTarefa statusAntigo = this.status;
        this.status = novoStatus;
        log.info("Status da tarefa '{}' atualizado de '{}' para '{}' por {}",
                descricao, statusAntigo != null ? statusAntigo.getDescricao() : "N/A",
                novoStatus.getDescricao(), responsavel.getNome());
    }

    public void registrarHoras(int horas, Usuario responsavel) {
        if (!responsavel.equals(this.responsavel) && !responsavel.isGerente() && !responsavel.isAdmin()) {
            log.warn("Usuário {} não tem permissão para registrar horas nesta tarefa", responsavel.getNome());
            return;
        }

        this.horasTrabalhadas += horas;
        log.info("Horas registradas na tarefa '{}': +{} horas | Total: {}/{} horas",
                descricao, horas, horasTrabalhadas, horasEstimadas);

        if (horasTrabalhadas >= horasEstimadas && status != StatusTarefa.CONCLUIDA) {
            atualizarStatus(StatusTarefa.CONCLUIDA, responsavel);
            log.info("Tarefa '{}' concluída! Horas totais: {}", descricao, horasTrabalhadas);
        }

        if (projeto != null) {
            projeto.atualizarProgresso();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarefa tarefa = (Tarefa) o;
        return id == tarefa.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tarefa{id=" + id + ", descricao='" + descricao + "'}";
    }
}