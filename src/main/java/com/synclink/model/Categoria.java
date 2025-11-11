    package com.synclink.model;
    
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Size;
    import java.util.ArrayList;
    import java.util.List;
    
    @Entity
    @Table(name = "categorias")
    public class Categoria {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        @NotBlank
        @Size(max = 100)
        @Column(nullable = false)
        private String nome;
    
        @Size(max = 255)
        private String descricao;
    
        @Column(nullable = false)
        private Boolean ativo = true;
    
        @OneToMany(mappedBy = "categoria")
        private List<Produto> produtos = new ArrayList<>();
    
        // Construtores
        public Categoria() {}
    
        public Categoria(String nome, String descricao) {
            this.nome = nome;
            this.descricao = descricao;
        }
    
        // Getters e Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
    
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    
        public Boolean getAtivo() { return ativo; }
        public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    
        public List<Produto> getProdutos() { return produtos; }
        public void setProdutos(List<Produto> produtos) { this.produtos = produtos; }
    
    }