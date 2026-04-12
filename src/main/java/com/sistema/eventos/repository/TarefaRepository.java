package com.sistema.eventos.repository;

import com.sistema.eventos.model.StatusTarefa;
import com.sistema.eventos.model.Tarefa;
import com.sistema.eventos.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class TarefaRepository extends BaseRepository<Tarefa, Integer> {

    public TarefaRepository() {
        super(Tarefa.class);
    }

    public List<Tarefa> buscarPorProjeto(int projetoId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Tarefa WHERE projeto.id = :projetoId", Tarefa.class)
                    .setParameter("projetoId", projetoId)
                    .list();
        }
    }

    public List<Tarefa> buscarPorEquipe(int equipeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Tarefa WHERE equipe.id = :equipeId", Tarefa.class)
                    .setParameter("equipeId", equipeId)
                    .list();
        }
    }

    public List<Tarefa> buscarPorStatus(StatusTarefa status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Tarefa WHERE status = :status", Tarefa.class)
                    .setParameter("status", status)
                    .list();
        }
    }

    public List<Tarefa> buscarPorResponsavel(int responsavelId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Tarefa WHERE responsavel.id = :responsavelId", Tarefa.class)
                    .setParameter("responsavelId", responsavelId)
                    .list();
        }
    }
}
