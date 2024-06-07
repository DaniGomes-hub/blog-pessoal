package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new usuario(0L, 
				"Root", "root@root.com", "rootroot", " "));
	}
	
	@Test
	@DisplayName("Deve criar um novo usuario")
	public void deveCriarUmNovoUsuario () {
		HttpEntity<usuario> corpoRequisicao = new HttpEntity<usuario>(
				new usuario(0L, "Dani", "dani.gomes@gmail.com", "25262728", "")
				);
		ResponseEntity<usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, 
				corpoRequisicao, usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {

		usuarioService.cadastrarUsuario(new usuario(0L, 
			"Maria", "maria_gomes@email.com.br", "13465278", "-"));

		HttpEntity<usuario> corpoRequisicao = new HttpEntity<usuario>(new usuario(0L, 
			"Maria", "maria_gomes@email.com.br", "13465278", "-"));

		ResponseEntity<usuario> corpoResposta = testRestTemplate
			.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, usuario.class);

		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());}
	
		@Test
		@DisplayName("Atualizar um Usuário")
		public void deveAtualizarUmUsuario() {
			
			Optional<usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new usuario(0L,
					"Cristina", "cristina.gomes@gmail.com", "20212223", "-"));
			
			usuario usuarioUpdate = new usuario(usuarioCadastrado.get().getId(), 
					"Cristina Gomes", "cristina.gomes@gmail.com", "20212223", "-");
				
				HttpEntity<usuario> corpoRequisicao = new HttpEntity<usuario>(usuarioUpdate);

				ResponseEntity<usuario> corpoResposta = testRestTemplate
					.withBasicAuth("root@root.com", "rootroot")
					.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, usuario.class);

				assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
				
			}
		
		@Test
		@DisplayName("Listar todos os Usuários")
		public void deveMostrarTodosUsuarios() {

			usuarioService.cadastrarUsuario(new usuario(0L, 
				"Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "-"));
			
			usuarioService.cadastrarUsuario(new usuario(0L, 
				"Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "-"));

			ResponseEntity<String> resposta = testRestTemplate
			.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

			assertEquals(HttpStatus.OK, resposta.getStatusCode());

		}
}
