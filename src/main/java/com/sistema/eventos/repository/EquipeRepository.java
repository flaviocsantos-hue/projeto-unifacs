package com.sistema.eventos.repository;

import com.sistema.eventos.model.Equipe;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class EquipeRepository extends BaseRepository<Equipe, Integer> {

    private static final Logger log = LoggerFactory.getLogger(EquipeRepository.class);

    public EquipeRepository() {
        super(Equipe.class);
    }

    public Optional<Equipe> buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Equipe equipe = session.get(Equipe.class, id);
            if (equipe != null) {
                Hibernate.initialize(equipe.getMembros());
            }
            return Optional.ofNullable(equipe);
        } catch (Exception e) {
            log.error("Erro ao buscar equipe por ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public List<Usuario> buscarMembrosDaEquipe(int equipeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Equipe equipe = session.get(Equipe.class, equipeId);
            if (equipe != null) {
                Hibernate.initialize(equipe.getMembros());
                return equipe.getMembros() != null ? equipe.getMembros() : new ArrayList<>();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Erro ao buscar membros da equipe: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Equipe> buscarPorProjeto(int projetoId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Equipe> equipes = session.createQuery(
                            "SELECT e FROM Equipe e WHERE e.projeto.id = :projetoId", Equipe.class)
                    .setParameter("projetoId", projetoId)
                    .list();

            for (Equipe e : equipes) {
                Hibernate.initialize(e.getMembros());
            }
            return equipes;
        } catch (Exception e) {
            log.error("Erro ao buscar equipes por projeto: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Equipe> buscarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Equipe> equipes = session.createQuery("SELECT e FROM Equipe e", Equipe.class).list();
            for (Equipe e : equipes) {
                Hibernate.initialize(e.getMembros());
            }
            return equipes;
        } catch (Exception e) {
            log.error("Erro ao buscar todas as equipes: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // Usa o método salvar do base repository
    public Equipe salvarEquipe(Equipe equipe) {
        return salvar(equipe);
    }

    // Usa o método atualizarComRetorno (retorna a entidade)
    public Equipe atualizarEquipe(Equipe equipe) {
        return atualizarComRetorno(equipe);
    }

    // Usa o método atualizar void
    public void atualizarEquipeVoid(Equipe equipe) {
        atualizar(equipe);
    }

    // Metodo especifico para adicionar membro via SQL nativo
    public boolean adicionarMembro(int equipeId, int usuarioId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            String sql = "INSERT INTO equipe_usuarios (equipe_id, usuario_id) VALUES (:equipeId, :usuarioId)";
            int result = session.createNativeQuery(sql)
                    .setParameter("equipeId", equipeId)
                    .setParameter("usuarioId", usuarioId)
                    .executeUpdate();

            transaction.commit();
            log.info("Membro ID: {} adicionado a equipe ID: {}", usuarioId, equipeId);
            return result > 0;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error("Erro ao adicionar membro: {}", e.getMessage(), e);
            return false;
        }
    }

    // Metodo especifico para remover membro via SQL nativo
    public boolean removerMembro(int equipeId, int usuarioId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            String sql = "DELETE FROM equipe_usuarios WHERE equipe_id = :equipeId AND usuario_id = :usuarioId";
            int result = session.createNativeQuery(sql)
                    .setParameter("equipeId", equipeId)
                    .setParameter("usuarioId", usuarioId)
                    .executeUpdate();

            transaction.commit();
            log.info("Membro ID: {} removido da equipe ID: {}", usuarioId, equipeId);
            return result > 0;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error("Erro ao remover membro: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean deletarEquipeComCascata(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            String sqlDeleteMembros = "DELETE FROM equipe_usuarios WHERE equipe_id = :equipeId";
            session.createNativeQuery(sqlDeleteMembros)
                    .setParameter("equipeId", id)
                    .executeUpdate();

            String sqlUpdateTarefas = "UPDATE tarefas SET equipe_id = NULL WHERE equipe_id = :equipeId";
            session.createNativeQuery(sqlUpdateTarefas)
                    .setParameter("equipeId", id)
                    .executeUpdate();

            String sqlDeleteEquipe = "DELETE FROM equipes WHERE id = :equipeId";
            int deleted = session.createNativeQuery(sqlDeleteEquipe)
                    .setParameter("equipeId", id)
                    .executeUpdate();

            transaction.commit();
            log.info("Equipe com ID {} deletada com sucesso", id);
            return deleted > 0;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error("Erro ao deletar equipe: {}", e.getMessage(), e);
            return false;
        }
    }
}