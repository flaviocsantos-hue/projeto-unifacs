package com.sistema.eventos.model;



public enum PerfilAcesso {
    ADMIN("Administrador", "Acesso total ao sistema"),
    GERENTE("Gerente", "Gerencia projetos e equipes"),
    USUARIO("Usuário", "Visualiza e gerencia suas tarefas");

    private String descricao;
    private String permissoes;

    PerfilAcesso(String descricao, String permissoes) {
        this.descricao = descricao;
        this.permissoes = permissoes;
    }

    public String getDescricao() { return descricao; }
    public String getPermissoes() { return permissoes; }
}



