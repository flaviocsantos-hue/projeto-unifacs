package com.sistema.eventos.repository;


import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class UsuarioRepository extends BaseRepository<Usuario, Integer> {

    private static final Logger log = LoggerFactory.getLogger(UsuarioRepository.class);

    public UsuarioRepository() {
        super(Usuario.class);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("FROM Usuario WHERE email = :email", Usuario.class)
                    .setParameter("email", email)
                    .uniqueResultOptional();
        } catch (Exception e) {
            log.error("Erro ao buscar usuario por email {}: {}", email, e.getMessage(), e);
            return Optional.empty();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Usuario salvarUsuario(Usuario usuario) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Garantir que a senha não seja nula
            if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
                usuario.setSenha("senha123");
            }

            if (usuario.getGrupo() == null) {
                usuario.setGrupo("");
            }

            // Verificar se o usuario ja existe pelo ID
            if (usuario.getId() > 0) {
                Usuario existing = session.get(Usuario.class, usuario.getId());
                if (existing != null) {
                    existing.setNome(usuario.getNome());
                    existing.setEmail(usuario.getEmail());
                    existing.setPerfilAcesso(usuario.getPerfilAcesso());
                    existing.setSenha(usuario.getSenha());
                    existing.setGrupo(usuario.getGrupo());
                    session.merge(existing);
                    transaction.commit();
                    log.info("Usuario atualizado com sucesso: {} (ID: {})", usuario.getNome(), usuario.getId());
                    return existing;
                }
            }

            // Novo usuario
            session.persist(usuario);
            transaction.commit();
            log.info("Usuario salvo com sucesso: {} (ID: {})", usuario.getNome(), usuario.getId());
            return usuario;
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Erro ao fazer rollback: {}", ex.getMessage());
                }
            }
            log.error("Erro ao salvar usuario: {}", e.getMessage(), e);
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Usuario existing = session.get(Usuario.class, usuario.getId());
            if (existing != null) {
                existing.setNome(usuario.getNome());
                existing.setEmail(usuario.getEmail());
                existing.setSenha(usuario.getSenha());
                existing.setPerfilAcesso(usuario.getPerfilAcesso());
                Usuario merged = (Usuario) session.merge(existing);
                transaction.commit();
                log.info("Usuario atualizado com sucesso: {} (ID: {})", merged.getNome(), merged.getId());
                return merged;
            }

            transaction.commit();
            log.warn("Usuario com ID {} nao encontrado para atualizar", usuario.getId());
            return null;
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    log.error("Erro ao fazer rollback: {}", ex.getMessage());
                }
            }
            log.error("Erro ao atualizar usuario: {}", e.getMessage(), e);
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void deletarUsuario(int id) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Usuario usuario = session.get(Usuario.class, id);
            if (usuario != null) {
                session.remove(usuario);
                transaction.commit();
                log.info("Usuario com ID {} deletado com sucesso", id);
            } else {
                log.warn("Usuario com ID {} nao encontrado para deletar", id);
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
            log.error("Erro ao deletar usuario: {}", e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}