package com.generation.blogpessoal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;

@RestController // controladora de rotas
@RequestMapping ("/postagens") //como chegar no "insomnia"
@CrossOrigin  (origins = "*", allowedHeaders = "*") //libera acesso em outras máquinas/allowedHeard - libera a passagem

public class PostagemController {
	
	@Autowired // injeção de dependencias - instanciar a classe Repository
	private PostagemRepository postagemRepository;
	
	@GetMapping // defini o verbo http que atende o metodo
	public ResponseEntity<List<Postagem>> getAll(){
		//ResponseEntity - CLasse
		return ResponseEntity.ok(postagemRepository.findAll());
		//SELECT * FROM tb_postagens
	}

}
