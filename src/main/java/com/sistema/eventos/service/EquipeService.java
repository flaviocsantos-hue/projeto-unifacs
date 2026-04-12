package com.sistema.eventos.service;

import com.sistema.eventos.model.Equipe;
import com.sistema.eventos.model.Projeto;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.repository.EquipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


public class EquipeService {

    private static final Logger log = LoggerFactory.getLogger(EquipeService.class);
    private EquipeRepository equipeRepository;

    public EquipeService() {
        this.equipeRepository = new EquipeRepository();
    }

    public Equipe criarEquipe(Equipe equipe, Usuario gerente) {
        log.info("Criando equipe: {} por {}", equipe.getNomeEquipe(), gerente.getNome());

        if (!gerente.isGerente() && !gerente.isAdmin()) {
            log.warn("Usuario sem permissao para criar equipe");
            return null;
        }

        if (equipe.getNomeEquipe() == null || equipe.getNomeEquipe().trim().isEmpty()) {
            log.error("Nome da equipe é obrigatorio");
            return null;
        }

        return equipeRepository.salvarEquipe(equipe);
    }

    public boolean excluirEquipe(int id, Usuario gerente) {
        log.info("Excluindo equipe ID: {} por {}", id, gerente.getNome());

        if (!gerente.isGerente() && !gerente.isAdmin()) {
            log.warn("Usuario sem permissao para excluir equipe");
            return false;
        }

        return equipeRepository.deletarEquipeComCascata(id);
    }

    public void alocarEquipeProjeto(Equipe equipe, Projeto projeto, Usuario gerente) {
        log.info("Alocando equipe '{}' ao projeto '{}' por {}",
                equipe.getNomeEquipe(), projeto.getNome(), gerente.getNome());

        if (!gerente.isGerente() && !gerente.isAdmin()) {
            log.warn("Usuario {} nao tem permissao para alocar equipe", gerente.getNome());
            return;
        }

        if (equipe == null || projeto == null) {
            log.error("Equipe ou Projeto nulo para alocacao");
            return;
        }

        try {
            equipe.setProjeto(projeto);
            equipeRepository.atualizarEquipeVoid(equipe);
            log.info("Equipe '{}' alocada com sucesso ao projeto '{}'",
                    equipe.getNomeEquipe(), projeto.getNome());
        } catch (Exception e) {
            log.error("Falha ao alocar equipe: {}", e.getMessage(), e);
        }
    }

    public void adicionarMembroEquipe(Equipe equipe, Usuario usuario, Usuario gerente) {
        log.info("Adicionando membro '{}' a equipe '{}' por {}",
                usuario.getNome(), equipe.getNomeEquipe(), gerente.getNome());

        if (!gerente.isGerente() && !gerente.isAdmin()) {
            log.warn("Usuario {} nao tem permissao para adicionar membro", gerente.getNome());
            return;
        }

        if (equipe == null || usuario == null) {
            log.error("Equipe ou Usuario nulo");
            return;
        }

        try {
            boolean adicionado = equipeRepository.adicionarMembro(equipe.getId(), usuario.getId());

            if (adicionado) {
                log.info("Membro '{}' adicionado com sucesso a equipe '{}'",
                        usuario.getNome(), equipe.getNomeEquipe());
            } else {
                log.warn("Falha ao adicionar membro '{}' a equipe '{}'",
                        usuario.getNome(), equipe.getNomeEquipe());
            }
        } catch (Exception e) {
            log.error("Falha ao adicionar membro: {}", e.getMessage(), e);
        }
    }

    public void removerMembroEquipe(Equipe equipe, Usuario usuario, Usuario gerente) {
        log.info("Removendo membro '{}' da equipe '{}' por {}",
                usuario.getNome(), equipe.getNomeEquipe(), gerente.getNome());

        if (!gerente.isGerente() && !gerente.isAdmin()) {
            log.warn("Usuario {} nao tem permissao para remover membro", gerente.getNome());
            return;
        }

        if (equipe == null || usuario == null) {
            log.error("Equipe ou Usuario nulo");
            return;
        }

        try {
            boolean removido = equipeRepository.removerMembro(equipe.getId(), usuario.getId());

            if (removido) {
                log.info("Membro '{}' removido com sucesso da equipe '{}'",
                        usuario.getNome(), equipe.getNomeEquipe());
            } else {
                log.warn("Falha ao remover membro '{}' da equipe '{}'",
                        usuario.getNome(), equipe.getNomeEquipe());
            }
        } catch (Exception e) {
            log.error("Falha ao remover membro: {}", e.getMessage(), e);
        }
    }

    public Optional<Equipe> buscarPorId(int id) {
        log.info("Buscando equipe por ID: {}", id);
        return equipeRepository.buscarPorId(id);
    }

    public List<Equipe> listarEquipes(Usuario usuario) {
        log.info("Listando equipes para usuario: {}", usuario.getNome());

        if (usuario.isAdmin() || usuario.isGerente()) {
            return equipeRepository.buscarTodos();
        }

        return usuario.getEquipes() != null ? usuario.getEquipes() : List.of();
    }

    public List<Equipe> buscarPorProjeto(int projetoId) {
        log.info("Buscando equipes do projeto ID: {}", projetoId);
        return equipeRepository.buscarPorProjeto(projetoId);
    }

    public List<Usuario> buscarMembrosDaEquipe(int equipeId) {
        log.info("Buscando membros da equipe ID: {}", equipeId);
        return equipeRepository.buscarMembrosDaEquipe(equipeId);
    }
}