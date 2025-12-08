package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.Categoria;

public interface CategoriaService {

  Categoria saveCategoria(Categoria categoria) throws Exception;

  boolean deleteCategoria(Categoria categoria) throws Exception;

  Categoria findById(Long id) throws Exception;
}
