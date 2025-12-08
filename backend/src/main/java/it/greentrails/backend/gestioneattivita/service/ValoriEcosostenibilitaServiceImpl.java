package it.greentrails.backend.gestioneattivita.service;

import it.greentrails.backend.entities.ValoriEcosostenibilita;
import it.greentrails.backend.gestioneattivita.repository.ValoriEcosostenibilitaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValoriEcosostenibilitaServiceImpl implements ValoriEcosostenibilitaService {

  private final ValoriEcosostenibilitaRepository repository;

  @Override
  public ValoriEcosostenibilita saveValori(ValoriEcosostenibilita valori) throws Exception {
    if (valori == null) {
      throw new Exception("Non è possibile salvare questo valore di ecosostenibilità.");
    }
    return repository.save(valori);
  }


  @Override
  public boolean deleteValori(ValoriEcosostenibilita valori) throws Exception {
    if (valori == null) {
      throw new Exception("Non è possibile cancellare questo valore di ecosostenibilità.");
    }
    repository.delete(valori);
    repository.flush();
    return repository.findById(valori.getId()).isEmpty();
  }

  @Override
  public ValoriEcosostenibilita findById(Long id) throws Exception {
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
