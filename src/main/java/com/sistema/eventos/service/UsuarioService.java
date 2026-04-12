package com.sistema.eventos.service;

import com.sistema.eventos.model.PerfilAcesso;
import com.sistema.eventos.model.Usuario;
import com.sistema.eventos.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
        inicializarAdminPadrao();
    }

    private void inicializarAdminPadrao() {
        try {
            List<Usuario> usuarios = usuarioRepository.buscarTodos();
            if (usuarios == null || usuarios.isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail("admin@gestaoprojetos.com");
                admin.setPerfilAcesso(PerfilAcesso.ADMIN);
                admin.setSenha("admin123");
                admin.setGrupo("Administracao");

                Usuario saved = usuarioRepository.salvarUsuario(admin);
                if (saved != null) {
                    log.info("Administrador padrao criado: admin@gestaoprojetos.com (ID: {}, Senha: admin123)", saved.getId());
                }
            }
        } catch (Exception e) {
            log.error("Erro ao criar admin padrao: {}", e.getMessage(), e);
        }
    }

    public Usuario cadastrarUsuario(Usuario usuario, Usuario admin) {
        log.info("Tentativa de cadastro de usuario: {} por admin: {}", usuario.getNome(), admin.getNome());

        if (!admin.isAdmin()) {
            log.warn("Usuario {} tentou cadastrar novo usuario sem permissao de ADMIN", admin.getNome());
            return null;
        }

        // Verificar se email já existe
        Optional<Usuario> emailExistente = buscarPorEmail(usuario.getEmail());
        if (emailExistente.isPresent()) {
            log.warn("Email {} já está cadastrado para o usuario ID: {}", usuario.getEmail(), emailExistente.get().getId());
            return null;
        }

        Usuario saved = usuarioRepository.salvarUsuario(usuario);
        if (saved != null && saved.getId() > 0) {
            log.info("ADMIN {} cadastrou novo usuario: {} (ID: {})", admin.getNome(), usuario.getNome(), saved.getId());
            return saved;
        }

        log.error("Falha ao cadastrar usuario: {}", usuario.getNome());
        return null;
    }

    public Usuario editarUsuario(Usuario usuario, Usuario admin) {
        log.info("Tentativa de edicao de usuario ID: {} por admin: {}", usuario.getId(), admin.getNome());

        if (!admin.isAdmin()) {
            log.warn("Usuario {} tentou editar usuario sem permissao de ADMIN", admin.getNome());
            return null;
        }

        // Verificar se usuario existe
        Optional<Usuario> existente = usuarioRepository.buscarPorId(usuario.getId());
        if (existente.isEmpty()) {
            log.warn("Usuario com ID {} nao encontrado para edicao", usuario.getId());
            return null;
        }

        // Verificar se email já existe para outro usuario
        Optional<Usuario> emailExistente = buscarPorEmail(usuario.getEmail());
        if (emailExistente.isPresent() && emailExistente.get().getId() != usuario.getId()) {
            log.warn("Email {} já está cadastrado para outro usuario (ID: {})", usuario.getEmail(), emailExistente.get().getId());
            return null;
        }

        Usuario updated = usuarioRepository.atualizarUsuario(usuario);
        if (updated != null) {
            log.info("ADMIN {} editou usuario: {} (ID: {})", admin.getNome(), usuario.getNome(), usuario.getId());
            return updated;
        }

        log.error("Falha ao editar usuario ID: {}", usuario.getId());
        return null;
    }

    public void excluirUsuario(int id, Usuario admin) {
        log.info("Tentativa de exclusao de usuario ID: {} por admin: {}", id, admin.getNome());

        if (!admin.isAdmin()) {
            log.warn("Usuario {} tentou excluir usuario sem permissao de ADMIN", admin.getNome());
            return;
        }

        // Impedir exclusão do próprio admin
        if (id == admin.getId()) {
            log.warn("Admin {} tentou excluir a si mesmo", admin.getNome());
            return;
        }

        usuarioRepository.deletarUsuario(id);
        log.info("ADMIN {} excluiu usuario com ID: {}", admin.getNome(), id);
    }

    public List<Usuario> listarTodosUsuarios(Usuario usuario) {
        log.info("Listagem de usuarios solicitada por: {} (Perfil: {})", usuario.getNome(), usuario.getPerfilAcesso());

        if (usuario.isAdmin() || usuario.isGerente()) {
            List<Usuario> usuarios = usuarioRepository.buscarTodos();
            log.info("Total de usuarios encontrados: {}", usuarios != null ? usuarios.size() : 0);
            return usuarios != null ? usuarios : List.of();
        }

        log.warn("Usuario {} tentou listar todos usuarios sem permissao", usuario.getNome());
        return List.of();
    }

    public Optional<Usuario> buscarPorId(int id) {
        return usuarioRepository.buscarPorId(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }
}