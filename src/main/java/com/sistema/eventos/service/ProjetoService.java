package com.sistema.eventos.service;

import com.sistema.eventos.model.Dashboard;
import com.sistema.eventos.model.Projeto;
import com.sistema.eventos.model.StatusProjeto;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.repository.ProjetoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjetoService {

    private static final Logger log = LoggerFactory.getLogger(ProjetoService.class);
    private ProjetoRepository projetoRepository;

    public ProjetoService() {
        this.projetoRepository = new ProjetoRepository();
    }

    public Projeto criarProjeto(Projeto projeto, Usuario gerente) {
        if (!gerente.isGerente() && !gerente.isAdmin()) {
            log.warn("Usuário {} tentou criar projeto sem permissão de GERENTE", gerente.getNome());
            return null;
        }

        projeto.setGerente(gerente);
        Dashboard dashboard = new Dashboard(projeto);
        projeto.setDashboard(dashboard);

        Projeto saved = projetoRepository.salvar(projeto);
        if (saved != null) {
            log.info("GERENTE {} criou projeto: {}", gerente.getNome(), projeto.getNome());
            projeto.criarProjeto(gerente);
        }
        return saved;
    }

    public boolean editarProjeto(Projeto projeto, Usuario gerente) {
        // Permissão: GERENTE pode editar qualquer projeto, ADMIN também
        if (!gerente.isGerente() && !gerente.isAdmin()) {
            log.warn("Usuário {} tentou editar projeto sem permissão. Perfil: {}",
                    gerente.getNome(), gerente.getPerfilAcesso());
            return false;
        }

        try {
            projetoRepository.atualizar(projeto);
            log.info("Projeto '{}' editado por {} ({})",
                    projeto.getNome(), gerente.getNome(), gerente.getPerfilAcesso().getDescricao());
            return true;
        } catch (Exception e) {
            log.error("Erro ao editar projeto: {}", e.getMessage());
            return false;
        }
    }

    public boolean excluirProjeto(int id, Usuario gerente) {
        // Permissão: GERENTE pode excluir qualquer projeto, ADMIN também
        if (!gerente.isGerente() && !gerente.isAdmin()) {
            log.warn("Usuário {} tentou excluir projeto sem permissão. Perfil: {}",
                    gerente.getNome(), gerente.getPerfilAcesso());
            return false;
        }

        Optional<Projeto> projeto = projetoRepository.buscarPorId(id);
        if (projeto.isPresent()) {
            try {
                projetoRepository.deletar(id);
                log.info("Projeto '{}' excluído por {} ({})",
                        projeto.get().getNome(), gerente.getNome(), gerente.getPerfilAcesso().getDescricao());
                return true;
            } catch (Exception e) {
                log.error("Erro ao excluir projeto: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }

    public List<Projeto> listarProjetos(Usuario usuario) {
        if (usuario.isAdmin() || usuario.isGerente()) {
            return projetoRepository.buscarTodos();
        }

        return usuario.getEquipes().stream()
                .flatMap(equipe -> equipe.getProjeto() != null ?
                        java.util.stream.Stream.of(equipe.getProjeto()) :
                        java.util.stream.Stream.empty())
                .distinct()
                .collect(Collectors.toList());
    }

    public Optional<Projeto> buscarPorId(int id) {
        return projetoRepository.buscarPorId(id);
    }

    public void gerarRelatorioProjeto(int id, Usuario usuario) {
        var projeto = projetoRepository.buscarPorId(id);
        if (projeto.isPresent()) {
            if (usuario.isAdmin() || usuario.isGerente()) {
                projeto.get().gerarRelatorio();
            } else {
                log.warn("Usuário {} tentou gerar relatório do projeto '{}' sem permissão",
                        usuario.getNome(), projeto.get().getNome());
            }
        }
    }

    public void atualizarStatusProjeto(int id, StatusProjeto novoStatus, Usuario gerente) {
        if (!gerente.isGerente() && !gerente.isAdmin()) {
            log.warn("Usuário {} não tem permissão para alterar status do projeto", gerente.getNome());
            return;
        }

        var projeto = projetoRepository.buscarPorId(id);
        if (projeto.isPresent()) {
            projeto.get().setStatus(novoStatus);
            projetoRepository.atualizar(projeto.get());
            log.info("Status do projeto '{}' atualizado para '{}' por {}",
                    projeto.get().getNome(), novoStatus.getDescricao(), gerente.getNome());
        }
    }
}
