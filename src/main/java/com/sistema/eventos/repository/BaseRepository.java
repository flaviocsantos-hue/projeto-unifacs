package com.sistema.eventos.repository;


import com.sistema.eventos.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;


public abstract class BaseRepository<T, ID> {

    protected static final Logger log = LoggerFactory.getLogger(BaseRepository.class);
    protected Class<T> entityClass;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T salvar(T entity) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.persist(entity);
            transaction.commit();

            log.info("Entidade {} salva com sucesso", entityClass.getSimpleName());
            return entity;
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Erro ao fazer rollback: {}", ex.getMessage());
                }
            }
            log.error("Erro ao salvar entidade {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Optional<T> buscarPorId(ID id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            T entity = session.get(entityClass, (Serializable) id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            log.error("Erro ao buscar entidade {} por ID {}: {}", entityClass.getSimpleName(), id, e.getMessage(), e);
            return Optional.empty();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<T> buscarTodos() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<T> query = session.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
            List<T> result = query.list();
            return result;
        } catch (Exception e) {
            log.error("Erro ao buscar todas as entidades {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            return List.of();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void deletar(ID id) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            T entity = session.get(entityClass, (Serializable) id);
            if (entity != null) {
                session.remove(entity);
                transaction.commit();
                log.info("Entidade {} com ID {} deletada", entityClass.getSimpleName(), id);
            } else {
                log.warn("Entidade {} com ID {} nao encontrada para deletar", entityClass.getSimpleName(), id);
                if (transaction != null) transaction.rollback();
            }
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Erro ao fazer rollback: {}", ex.getMessage());
                }
            }
            log.error("Erro ao deletar entidade {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void atualizar(T entity) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.merge(entity);
            transaction.commit();

            log.info("Entidade {} atualizada (void)", entityClass.getSimpleName());
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Erro ao fazer rollback: {}", ex.getMessage());
                }
            }
            log.error("Erro ao atualizar entidade {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public T atualizarComRetorno(T entity) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            T merged = (T) session.merge(entity);
            transaction.commit();

            log.info("Entidade {} atualizada com retorno", entityClass.getSimpleName());
            return merged;
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Erro ao fazer rollback: {}", ex.getMessage());
                }
            }
            log.error("Erro ao atualizar entidade {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}