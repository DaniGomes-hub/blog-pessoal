package com.generation.blogpessoal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.model.usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;

@Service //Estamos tratando regras de negocios
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	/* autenticar o usuario
	*classe do security que tem gestão de autenticação
	*permite acessar metodos que podem entregar ao objeto as suas autoridade concedidas
	*/
	
	public Optional<usuario> cadastrarUsuario(usuario usuario){
		// nome | usuario (email) | senha | foto		
		if(usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty(); // se o objeto estiver vazio 
		
		usuario.setSenha(criptografarSenha(usuario.getSenha()));
		return Optional.of(usuarioRepository.save(usuario));
	}
	
	// vai tratar para a senha ser criptografada antes de ser persistida no banco 
	
	private String criptografarSenha(String senha) {
		// Classe que trata a criptografia
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(senha); // encoder sendo aplicado na senha
	}
	
	// segundo problema
	
	public Optional<usuario> atualizarUsuario (usuario usuario) {
		if(usuarioRepository.findById(usuario.getId()).isPresent()){
			
			Optional<usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());
			
			if(buscaUsuario.isPresent() && (buscaUsuario.get().getId()) != usuario.getId())
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe", null);
				
			usuario.setSenha(criptografarSenha(usuario.getSenha()));	

			return Optional.ofNullable(usuarioRepository.save(usuario));
		}
		return Optional.empty();
	}
	public Optional <UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin){
		
		var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getUsuario(),
			usuarioLogin.get().getSenha());	
		
		//tiver esse usuario e senha
				Authentication authentication = authenticationManager.authenticate(credenciais);
				
		if(authentication.isAuthenticated()){
					
					Optional<usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());		
		
		if (usuario.isPresent()) {
			
			usuarioLogin.get().setId(usuario.get().getId());
			usuarioLogin.get().setNome(usuario.get().getNome());
			usuarioLogin.get().setFoto(usuario.get().getFoto());
			usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario()));
			usuarioLogin.get().setSenha("");
			
		return usuarioLogin;
	}
		}
	
	return Optional.empty();
}
	/*
	 * metodo que usa o jwt para gerar o token do usuario
	 * 
	 */
	
		private String gerarToken(String usuario) {
			return "Bearer"+jwtService.generateToken(usuario);
		}
}
