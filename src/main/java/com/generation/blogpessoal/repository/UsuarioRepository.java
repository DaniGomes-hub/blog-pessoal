package com.generation.blogpessoal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.blogpessoal.model.usuario;

public interface UsuarioRepository extends JpaRepository<usuario, Long> {

	public Optional<usuario> findByUsuario(String usuario);
	
}
