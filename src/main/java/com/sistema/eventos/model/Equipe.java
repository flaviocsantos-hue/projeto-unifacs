package com.sistema.eventos.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


import java.util.Objects;

@Entity
@Table(name = "equipes")
public class Equipe {

    private static final Logger log = LoggerFactory.getLogger(Equipe.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nome_equipe", nullable = false, length = 100)
    private String nomeEquipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "equipe_usuarios",
            joinColumns = @JoinColumn(name = "equipe_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> membros = new ArrayList<>();

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tarefa> tarefas = new ArrayList<>();

    public Equipe() {}

    public Equipe(String nomeEquipe) {
        this.nomeEquipe = nomeEquipe;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomeEquipe() { return nomeEquipe; }
    public void setNomeEquipe(String nomeEquipe) { this.nomeEquipe = nomeEquipe; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }

    public List<Usuario> getMembros() { return membros; }
    public void setMembros(List<Usuario> membros) { this.membros = membros; }

    public List<Tarefa> getTarefas() { return tarefas; }
    public void setTarefas(List<Tarefa> tarefas) { this.tarefas = tarefas; }

    public void alocarNoProjeto(Projeto projeto, Usuario responsavel) {
        if (!responsavel.isGerente() && !responsavel.isAdmin()) {
            log.warn("Usuário {} não tem permissão para alocar equipe em projeto", responsavel.getNome());
            return;
        }

        this.projeto = projeto;
        log.info("Equipe '{}' alocada no projeto '{}' por {}", nomeEquipe, projeto.getNome(), responsavel.getNome());
    }

    public void adicionarMembro(Usuario usuario, Usuario responsavel) {
        if (!responsavel.isGerente() && !responsavel.isAdmin()) {
            log.warn("Usuário {} não tem permissão para adicionar membros à equipe", responsavel.getNome());
            return;
        }

        if (!membros.contains(usuario)) {
            membros.add(usuario);
            if (usuario.getEquipes() != null && !usuario.getEquipes().contains(this)) {
                usuario.getEquipes().add(this);
            }
            log.info("Usuário '{}' adicionado à equipe '{}' por {}", usuario.getNome(), nomeEquipe, responsavel.getNome());
        }
    }

    public void removerMembro(Usuario usuario, Usuario responsavel) {
        if (!responsavel.isGerente() && !responsavel.isAdmin()) {
            log.warn("Usuário {} não tem permissão para remover membros da equipe", responsavel.getNome());
            return;
        }

        membros.remove(usuario);
        if (usuario.getEquipes() != null) {
            usuario.getEquipes().remove(this);
        }
        log.info("Usuário '{}' removido da equipe '{}' por {}", usuario.getNome(), nomeEquipe, responsavel.getNome());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipe equipe = (Equipe) o;
        return id == equipe.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Equipe{id=" + id + ", nomeEquipe='" + nomeEquipe + "'}";
    }
}