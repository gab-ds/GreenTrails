package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.gestioneattivita.repository.ValoriEcosostenibilitaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/*@ nullable_by_default @*/
public class ValoriEcosostenibilitaServiceImpl implements ValoriEcosostenibilitaService {

  /*@ spec_public non_null @*/
  private final ValoriEcosostenibilitaRepository repository;

  // repository is guaranteed non-null by Spring constructor injection

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public ValoriEcosostenibilita saveValori(/*@ nullable @*/ ValoriEcosostenibilita valori) throws Exception {
    if (valori == null) {
      throw new Exception("Non è possibile salvare questo valore di ecosostenibilità.");
    }
    return repository.save(valori);
  }


  /*@
    @ requires valori != null;
    @*/
  @Override
  public boolean deleteValori(ValoriEcosostenibilita valori) throws Exception {
    if (valori == null) {
      throw new Exception("Non è possibile cancellare questo valore di ecosostenibilità.");
    }
    repository.delete(valori);
    repository.flush();
    return repository.findById(valori.getId()).isEmpty();
  }

  /*@
    @ also
    @ ensures \result != null;
    @*/
  @Override
  public ValoriEcosostenibilita findById(/*@ nullable @*/ Long id) throws Exception {
    if (id == null || id < 0) {
      throw new Exception("L'id non è valido.");
    }
    Optional<ValoriEcosostenibilita> valori = repository.findById(id);
    if (valori.isEmpty()) {
      throw new Exception("I valori non sono stati trovati.");
    }
    return valori.get();
  }


}
