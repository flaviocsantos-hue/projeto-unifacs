package com.sistema.eventos.service;

import com.sistema.eventos.model.*;
import com.sistema.eventos.repository.TarefaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



public class TarefaService {

    private static final Logger log = LoggerFactory.getLogger(TarefaService.class);
    private TarefaRepository tarefaRepository;

    public TarefaService() {
        this.tarefaRepository = new TarefaRepository();
    }

    public Tarefa criarTarefa(Tarefa tarefa, Projeto projeto, Usuario responsavel) {
        log.info("Tentativa de criar tarefa: {} por {}", tarefa.getDescricao(), responsavel.getNome());

        // Validações
        if (tarefa.getDescricao() == null || tarefa.getDescricao().trim().isEmpty()) {
            log.error("Descrição da tarefa é obrigatória");
            return null;
        }

        if (projeto == null) {
            log.error("Projeto é obrigatório para criar tarefa");
            return null;
        }

        // Configurar a tarefa
        tarefa.setProjeto(projeto);
        tarefa.setResponsavel(responsavel);

        if (tarefa.getStatus() == null) {
            tarefa.setStatus(StatusTarefa.PENDENTE);
        }

        if (tarefa.getHorasTrabalhadas() == 0) {
            tarefa.setHorasTrabalhadas(0);
        }

        // Salvar a tarefa
        Tarefa saved = tarefaRepository.salvar(tarefa);

        if (saved != null && saved.getId() > 0) {
            log.info("Tarefa '{}' criada no projeto '{}' por {}",
                    tarefa.getDescricao(), projeto.getNome(), responsavel.getNome());

            // Atualizar progresso do projeto
            if (projeto != null) {
                projeto.atualizarProgresso();
            }
            return saved;
        }

        log.error("Falha ao criar tarefa: {}", tarefa.getDescricao());
        return null;
    }

    public boolean distribuirTarefa(Tarefa tarefa, Equipe equipe, Usuario responsavel) {
        if (!responsavel.isGerente() && !responsavel.isAdmin()) {
            log.warn("Usuário {} tentou distribuir tarefa sem permissão", responsavel.getNome());
            return false;
        }

        tarefa.distribuir(equipe, responsavel);
        tarefaRepository.atualizar(tarefa);
        log.info("Tarefa '{}' distribuída para equipe '{}' por {}",
                tarefa.getDescricao(), equipe.getNomeEquipe(), responsavel.getNome());
        return true;
    }

    public boolean atualizarStatusTarefa(Tarefa tarefa, StatusTarefa novoStatus, Usuario responsavel) {
        log.info("Tentativa de atualizar status da tarefa ID: {} para {} por {}",
                tarefa.getId(), novoStatus.getDescricao(), responsavel.getNome());

        // Verificar permissão
        if (!responsavel.equals(tarefa.getResponsavel()) && !responsavel.isGerente() && !responsavel.isAdmin()) {
            log.warn("Usuário {} não tem permissão para atualizar status da tarefa", responsavel.getNome());
            return false;
        }

        tarefa.atualizarStatus(novoStatus, responsavel);
        tarefaRepository.atualizar(tarefa);

        // Atualizar progresso do projeto
        if (tarefa.getProjeto() != null) {
            tarefa.getProjeto().atualizarProgresso();
        }

        log.info("Status da tarefa {} atualizado para {} por {}",
                tarefa.getId(), novoStatus.getDescricao(), responsavel.getNome());
        return true;
    }

    public boolean registrarHorasTarefa(Tarefa tarefa, int horas, Usuario responsavel) {
        log.info("Tentativa de registrar {} horas na tarefa ID: {} por {}", horas, tarefa.getId(), responsavel.getNome());

        if (horas <= 0) {
            log.error("Horas deve ser maior que zero");
            return false;
        }

        // Verificar permissão
        if (!responsavel.equals(tarefa.getResponsavel()) && !responsavel.isGerente() && !responsavel.isAdmin()) {
            log.warn("Usuário {} não tem permissão para registrar horas na tarefa", responsavel.getNome());
            return false;
        }

        tarefa.registrarHoras(horas, responsavel);
        tarefaRepository.atualizar(tarefa);

        // Atualizar progresso do projeto
        if (tarefa.getProjeto() != null) {
            tarefa.getProjeto().atualizarProgresso();
        }

        log.info("{} horas registradas na tarefa {} por {}", horas, tarefa.getId(), responsavel.getNome());
        return true;
    }

    public List<Tarefa> listarMinhasTarefas(Usuario usuario) {
        log.info("Listando tarefas do usuário: {}", usuario.getNome());
        List<Tarefa> tarefas = tarefaRepository.buscarPorResponsavel(usuario.getId());
        log.info("Tarefas encontradas: {}", tarefas.size());
        return tarefas;
    }

    public List<Tarefa> listarTodasTarefas(Usuario usuario) {
        if (usuario.isAdmin() || usuario.isGerente()) {
            return tarefaRepository.buscarTodos();
        }
        return listarMinhasTarefas(usuario);
    }

    public List<Tarefa> listarTarefasPorProjeto(int projetoId, Usuario usuario) {
        if (usuario.isAdmin() || usuario.isGerente()) {
            return tarefaRepository.buscarPorProjeto(projetoId);
        }

        return usuario.getTarefas().stream()
                .filter(tarefa -> tarefa.getProjeto() != null && tarefa.getProjeto().getId() == projetoId)
                .collect(Collectors.toList());
    }

    public boolean excluirTarefa(int id, Usuario usuario) {
        log.info("Tentativa de excluir tarefa ID: {} por {}", id, usuario.getNome());

        Optional<Tarefa> tarefaOpt = buscarPorId(id);

        if (tarefaOpt.isEmpty()) {
            log.error("Tarefa com ID {} não encontrada", id);
            return false;
        }

        Tarefa tarefa = tarefaOpt.get();

        // Verificar permissão
        if (!usuario.equals(tarefa.getResponsavel()) && !usuario.isGerente() && !usuario.isAdmin()) {
            log.warn("Usuário {} sem permissão para excluir tarefa", usuario.getNome());
            return false;
        }

        // Remover a tarefa do projeto antes de excluir
        if (tarefa.getProjeto() != null && tarefa.getProjeto().getTarefas() != null) {
            tarefa.getProjeto().getTarefas().remove(tarefa);
        }

        tarefaRepository.deletar(id);
        log.info("Tarefa {} excluída por {}", id, usuario.getNome());
        return true;
    }

    public boolean atualizarTarefa(Tarefa tarefa, Usuario usuario) {
        log.info("Tentativa de atualizar tarefa ID: {} por {}", tarefa.getId(), usuario.getNome());

        Optional<Tarefa> existente = buscarPorId(tarefa.getId());

        if (existente.isEmpty()) {
            log.error("Tarefa com ID {} não encontrada", tarefa.getId());
            return false;
        }

        Tarefa tarefaExistente = existente.get();

        // Verificar permissão
        if (!usuario.equals(tarefaExistente.getResponsavel()) && !usuario.isGerente() && !usuario.isAdmin()) {
            log.warn("Usuário {} sem permissão para atualizar tarefa", usuario.getNome());
            return false;
        }

        // Atualizar campos permitidos
        if (tarefa.getDescricao() != null && !tarefa.getDescricao().trim().isEmpty()) {
            tarefaExistente.setDescricao(tarefa.getDescricao());
        }
        if (tarefa.getHorasEstimadas() > 0) {
            tarefaExistente.setHorasEstimadas(tarefa.getHorasEstimadas());
        }

        tarefaRepository.atualizar(tarefaExistente);
        log.info("Tarefa {} atualizada por {}", tarefa.getId(), usuario.getNome());
        return true;
    }

    public Optional<Tarefa> buscarPorId(int id) {
        return tarefaRepository.buscarPorId(id);
    }
}