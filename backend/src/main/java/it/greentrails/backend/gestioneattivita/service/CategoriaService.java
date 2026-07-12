package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.Categoria;

/*@ nullable_by_default @*/
public interface CategoriaService {

  /*@
    @ ensures \result != null;
    @*/
  Categoria saveCategoria(/*@ nullable @*/ Categoria categoria) throws Exception;

  boolean deleteCategoria(/*@ nullable @*/ Categoria categoria) throws Exception;

  /*@
    @ ensures \result != null;
    @*/
  Categoria findById(/*@ nullable @*/ Long id) throws Exception;
}
