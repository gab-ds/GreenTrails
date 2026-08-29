# Assessment delle Performance dei Microservizi

## 1. Introduzione e Metodologia

Questo documento raccoglie i risultati dei benchmark eseguiti sulle componenti critiche del backend dell'applicazione GreenTrails. L'obiettivo è stabilire una baseline di performance per identificare potenziali colli di bottiglia e guidare future ottimizzazioni.

### Strumenti Utilizzati
- **JMH (Java Microbenchmark Harness)**: Framework standard de facto per il microbenchmarking in ambiente Java/JVM.
- **Modalità di Test**:
    - `AverageTime` (Tempo medio per operazione) per i servizi logici.
    - `Throughput` (Operazioni al secondo) per componenti ad alta frequenza come il calcolo delle distanze.
- **Parametrizzazione**: Test con dataset di dimensioni variabili (10, 100, 1000, 10000, 50000 elementi) per valutare la scalabilità.

### Criteri di Selezione
Le seguenti aree sono state identificate come critiche e sottoposte a benchmark:
1.  **Core Services (`AttivitaService`, `CameraService`, `GestioneUtenze`)**: Servizi fondamentali invocati frequentemente.
2.  **Calcolo e Logica (`DistanceCalculator`)**: Utility potenzialmente usata in loop (es. filtri per vicinanza).
3.  **Sicurezza (`PasswordEncoding`)**: Operazioni crittografiche intenzionalmente lente, il cui impatto deve essere monitorato.
4.  **Adattatori e AI (`ItinerariAdapter`)**: Componenti che simulano interazioni complesse o manipolazione di grandi strutture dati per la pianificazione.
5.  **Filtraggio e Ricerca (`Prenotazione`, `Ricerca`)**: Operazioni su collezioni che possono degradare con l'aumentare dei dati.

---

## 2. Risultati dei Benchmark

Di seguito i risultati significativi ottenuti dall'esecuzione dei test.

### 2.1 Componenti ad Alta Efficienza
*   **DistanceCalculator**:
    *   Throughput: **~4.8 Milioni ops/s**
    *   Basso overhead, adatto all'uso intensivo all'interno di cicli.

### 2.2 Servizi Core (Operazioni DB Mockate)
*   **AttivitaService**:
    *   Tempi di risposta costanti intorno ai **14-16 µs** indipendentemente dalla dimensione simulata (10-100 elementi), indicando che l'overhead del framework è predominante rispetto alla logica di business semplice in questo test.
*   **GestioneUtenze**:
    *   `findById` / `loadUserByUsername`: **~11.5 µs**, confermando un accesso rapido ai dati utente.

### 2.3 Operazioni Dati e Scalabilità (Filtri in memoria)
Questi test evidenziano come i tempi crescano in relazione alla dimensione del dataset trattato.

| Componente | Dimensione Lista | Tempo Medio (µs) | Note |
| :--- | :--- | :--- | :--- |
| **CameraService** (`getCamereByAlloggio`) | 1,000 | ~23 µs | Veloce |
| | 10,000 | ~107 µs | |
| | 50,000 | ~641 µs | Scalabilità lineare |
| **Prenotazione** (`FiltroStato`) | 1,000 | ~20-22 µs | |
| | 10,000 | ~120-130 µs | |
| | 50,000 | ~820-890 µs | Scalabilità lineare, performance accettabili sotto il millisecondo anche per grandi liste. |

### 2.4 Itinerari e Pianificazione (Simulazione Carico)
Test sull'`ItinerariAdapter`, critico per la generazione di piani di viaggio.

*   **100 items**: ~0.10 ms
*   **1,000 items**: ~0.17 ms
*   **5,000 items**: ~0.55 ms
*   **10,000 items**: ~0.98 ms
*   **Analisi**: Il tempo di esecuzione resta contenuto (sotto 1ms) anche con 10.000 elementi processati, suggerendo una buona efficienza dell'algoritmo di adattamento attuale.

### 2.5 Sicurezza (Security Overhead)
*   **Password Encoding (BCrypt)**:
    *   Encoding: **~142 ms**
    *   Matching: **~142 ms**
*   **Analisi**: Come previsto, BCrypt è "costoso". Questo conferma che le operazioni di login e registrazione sono limitate dalla CPU per il fattore di work della crittografia. Va evitato di eseguire queste operazioni nel thread principale se si prevede un alto traffico concorrente, oppure va accettato come limite naturale di throughput per la sicurezza.

---

## 3. Conclusioni e Raccomandazioni

1.  **Prestazioni Generali**: Il sistema mostra buone performance di base. La logica di business pura (esclusa I/O DB) introduce overhead minimi (nell'ordine dei microsecondi).
2.  **Scalabilità**: Le operazioni di filtraggio su liste in memoria scalano linearmente. Fino a 50.000 elementi, i tempi rimangono sotto il millisecondo, il che è eccellente.
3.  **Colli di Bottiglia**:
    *   **Autenticazione**: Il costo di ~140ms per operazione di password check è il singolo fattore più impattante rilevato. È normale per la sicurezza, ma definisce il limite massimo di login/s per singolo thread.
4.  **Prossimi Passi**:
    *   Estendere i benchmark per includere interazioni reali con il Database (usando Testcontainers o un DB di staging) per misurare l'I/O, che rappresenta verosimilmente il vero collo di bottiglia in produzione.
    *   Monitorare i tempi di `ItinerariAdapter` in scenari reali con dati complessi dall'AI.
