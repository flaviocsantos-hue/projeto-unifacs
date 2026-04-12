package com.sistema.eventos.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    private static final Logger log = LoggerFactory.getLogger(Usuario.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil_acesso", nullable = false)
    private PerfilAcesso perfilAcesso;

    @ManyToMany(mappedBy = "membros")
    private List<Equipe> equipes = new ArrayList<>();

    @OneToMany(mappedBy = "responsavel")
    private List<Tarefa> tarefas = new ArrayList<>();

    @Column(nullable = false)
    private String senha;

    @Column(name = "grupo", length = 100)
    private String grupo;



    // Construtor atualizado
    public Usuario(String nome, String email, String senha, PerfilAcesso perfilAcesso) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfilAcesso = perfilAcesso;
        this.grupo = "";
    }

    public void cadastrar() {
        log.info("Usuário '{}' com perfil '{}' cadastrado com sucesso!", nome, perfilAcesso.getDescricao());
    }

    public void gerenciarPermissoes() {
        log.info("Gerenciando permissões do usuário: {} - Perfil atual: {}", nome, perfilAcesso.getDescricao());
    }

    public boolean isAdmin() {
        return perfilAcesso == PerfilAcesso.ADMIN;
    }

    public boolean isGerente() {
        return perfilAcesso == PerfilAcesso.GERENTE;
    }

}