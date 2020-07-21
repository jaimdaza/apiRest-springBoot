package com.bolsadeideas.springboot.backend.apirest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.services.IClienteService;

@CrossOrigin(origins = {"http://localhost:4200"}) // para indicar quien puede tener acceso al recurso 
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService iClienteService;
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return iClienteService.findAll();
	}
	
//	@GetMapping("/clientes/{id}")
//	public Cliente show(@PathVariable Long id) {
//		return iClienteService.findById(id);
//	}
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		try {
		   cliente = iClienteService.findById(id);
		}catch (DataAccessException e) {
			response.put("mensaje", "Error a la realizar la consulta de base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(cliente == null) {
			response.put("mensaje", "Cliente con id: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	
	}
	
	@PostMapping("/clientes")
	// @Valid para validar segun entidad
	//@ResponseStatus(HttpStatus.CREATED) // si sale bien todo
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		Cliente clienteAux = null;
		Map<String, Object> response = new HashMap<>();
		if(result.hasErrors()) {
			/*List<String> erros = new ArrayList<>();
			result.getFieldErrors().forEach(error -> erros.add("El campo '"+error.getField()+"' " +error.getDefaultMessage()));
			*/
			List<String> errores = result.getFieldErrors().stream()
					.map(error -> "El campo '"+error.getField()+"' " +error.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("mensaje", "Error al crear Cliente");
			response.put("errors",errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);	
		}
		try {
			clienteAux = iClienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al crear Cliente");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		
		}
		response.put("mensaje", "Cliente creado con exito");
		response.put("cliente", clienteAux);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping("/clientes")
	//@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result/*, @PathVariable Long id*/) {
		Cliente clienteAux = null;
		Map<String, Object> response = new HashMap<>();
		if(result.hasErrors()) {
			List<String> errores = result.getFieldErrors().stream()
					.map(error -> "El campo '"+error.getField()+"' " +error.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("mensaje", "Error al crear Cliente");
			response.put("errors",errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);	
		}
		try {			
			Cliente clienteBD  =  iClienteService.findById(cliente.getId());
			if(clienteBD == null) {
				response.put("mensaje", "Error al actualizar Cliente, no existe cliente con id: ".concat(cliente.getId().toString()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			clienteAux = iClienteService.save(cliente);
		}  catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar Cliente");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Cliente actualizado con exito");
		response.put("cliente", clienteAux);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			iClienteService.delete(id);	
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar Cliente");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Cliente eliminado con exito");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
