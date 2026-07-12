package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.Categoria;
import it.greentrails.backend.gestioneattivita.repository.CategoriaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/*@ nullable_by_default @*/
public class CategoriaServiceImpl implements CategoriaService {

  /*@ spec_public @*/
  private final CategoriaRepository repository;

  //@ public invariant repository != null;

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public Categoria saveCategoria(/*@ nullable @*/ Categoria categoria) throws Exception {
    if (categoria == null) {
      throw new Exception("Non è possibile salvare questa categoria.");
    }
    return repository.save(categoria);
  }

  @Override
  public boolean deleteCategoria(/*@ nullable @*/ Categoria categoria) throws Exception {
    if (categoria == null) {
      throw new Exception("Non è possibile cancellare questa categoria.");
    }
    repository.delete(categoria);
    repository.flush();
    return repository.findById(categoria.getId()).isEmpty();

  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public Categoria findById(/*@ nullable @*/ Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<Categoria> categoria = repository.findById(id);
    if (categoria.isEmpty()) {
      throw new Exception("La categoria non è stata trovata.");
    }
    return categoria.get();
  }

}
