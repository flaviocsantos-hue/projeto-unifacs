package com.sistema.eventos.repository;

import com.sistema.eventos.model.Projeto;
import com.sistema.eventos.model.StatusProjeto;
import com.sistema.eventos.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class ProjetoRepository extends BaseRepository<Projeto, Integer> {

    public ProjetoRepository() {
        super(Projeto.class);
    }

    public List<Projeto> buscarPorStatus(StatusProjeto status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Projeto WHERE status = :status", Projeto.class)
                    .setParameter("status", status)
                    .list();
        }
    }

    public List<Projeto> buscarPorGerente(int gerenteId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Projeto WHERE gerente.id = :gerenteId", Projeto.class)
                    .setParameter("gerenteId", gerenteId)
                    .list();
        }
    }
}
