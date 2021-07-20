package com.example.algaworks.algamoney.api.resource;


import com.example.algaworks.algamoney.api.event.RecursoCriadoEvent;
import com.example.algaworks.algamoney.api.model.Pessoa;
import com.example.algaworks.algamoney.api.repository.PessoaRepository;
import com.example.algaworks.algamoney.api.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/pessoas")
public class PessoaResource {
        @Autowired
        private PessoaRepository pessoaRepository;
        @Autowired
        private PessoaService pessoaService;
        @Autowired
        private ApplicationEventPublisher publisher;

        @GetMapping
        public List<Pessoa> listar(){
            return pessoaRepository.findAll();
        }

        @PostMapping
        public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
            Pessoa pessoaSalva = pessoaRepository.save(pessoa);
            publisher.publishEvent(new RecursoCriadoEvent(this, response,pessoaSalva.getCodigo() ));
            return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
        }
    @GetMapping("/{codigo}")
    public ResponseEntity<Pessoa> buscarPeloCodigo(@PathVariable Long codigo) {
            return  this.pessoaRepository.findById(codigo).map(pessoa ->
                    ResponseEntity.ok(pessoa)).
                    orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long codigo) {
        this.pessoaRepository.deleteById(codigo);
    }

     @PutMapping("/{codigo}")
    public ResponseEntity<Pessoa> atualizar (@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa){
            Pessoa pessoaSalva = pessoaService.atualizar(codigo, pessoa);
            return ResponseEntity.ok(pessoaSalva);
     }
     @PutMapping("/{codigo}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarPropriedadeAtivo(@PathVariable long codigo, @RequestBody Boolean ativo){
            pessoaService.atualizarPropriedadeAtivo(codigo, ativo);
     }
}


