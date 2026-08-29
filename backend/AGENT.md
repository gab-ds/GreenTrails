# Guida all'Implementazione dei Microbenchmark JMH per GreenTrails

Questo documento descrive il piano d'azione dettagliato e COMPLETO per introdurre microbenchmark JMH (Java Microbenchmark Harness) nel backend Spring Boot di GreenTrails. L'obiettivo è misurare le performance di tutti i componenti critici identificati.

## Obiettivi Generali
Analizzare e misurare i colli di bottiglia causati da operazioni CPU-intensive, filtraggi di grandi collezioni in memoria ed algoritmi non ottimizzati.

---

## Step 1: Configurazione delle Dipendenze

Modificare il file `pom.xml` per includere le librerie JMH.

**Azioni:**
Aggiungere le seguenti dipendenze nel blocco `<dependencies>` (scope `test` per non intaccare la build di produzione):

```xml
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.37</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.37</version>
    <scope>test</scope>
</dependency>
```

---

## Step 2: Benchmark "Puro" - Calcolo Matematico

**Target:** `it.greentrails.backend.utils.DistanceCalculator`
**Tipo:** CPU-Bound / Math

Questa classe esegue calcoli matematici (Haversine).

**File Target:** `src/test/java/it/greentrails/backend/benchmarks/DistanceCalculatorBenchmark.java`
**Strategia:**
1.  **State**: `@State(Scope.Thread)` con due oggetti `Point` pre-generati casualmente.
2.  **Benchmark**: Invocare `DistanceCalculator.distance(p1, p2)`.
3.  **Obiettivo**: Misurare throughput (ops/sec).

---

## Step 3: Benchmark "Service" - Filtraggio Coordinate

**Target:** `it.greentrails.backend.gestionericerca.service.RicercaServiceImpl`
**Metodo Critico:** `findAttivitaByPosizione`
**Problema:** `findAll()` seguito da un filtro spaziale in Java.

**File Target:** `src/test/java/it/greentrails/backend/benchmarks/RicercaServiceBenchmark.java`
**Strategia:**
1.  **State**:
    *   Mock di `AttivitaRepository`.
    *   Setup di una lista di `Attivita` (coordinate random) con dimensione parametrica (`@Param({"100", "1000", "5000"})`).
2.  **Benchmark**: Eseguire il metodo di ricerca.
3.  **Insight**: Mostrerà il degrado lineare (o peggiore) al crescere del DB.

---

## Step 4: Benchmark "Memory Hog" - Filtraggio Recensioni

**Target:** `it.greentrails.backend.gestioneattivita.service.RecensioneServiceImpl`
**Metodo Critico:** `getAllRecensioniByVisitatore`
**Problema:** Carica l'intera tabella recensioni (`findAll().forEach(...)`) per filtrare un singolo utente.

**File Target:** `src/test/java/it/greentrails/backend/benchmarks/RecensioneServiceBenchmark.java`
**Strategia:**
1.  **State**:
    *   Mock di `RecensioneRepository`.
    *   Generare una lista massiva di `Recensione` (es. 10.000, 100.000). Solo una piccola percentuale appartiene all'utente target.
2.  **Benchmark**: Misurare il tempo per estrarre le recensioni dell'utente.
3.  **Obiettivo**: Evidenziare l'inefficienza del filtraggio applicativo vs query DB nativa.

---

## Step 5: Benchmark "Memory Hog" - Filtro Prenotazioni

**Target:** `it.greentrails.backend.gestioneprenotazioni.service.PrenotazioneAlloggioServiceImpl`
**Metodo Critico:** `getPrenotazioniAlloggioByStato`
**Problema:** Pattern `findAll().forEach(...)` identico al caso precedente.

**File Target:** `src/test/java/it/greentrails/backend/benchmarks/PrenotazioneBenchmark.java`
**Strategia:**
1.  **State**:
    *   Mock di `PrenotazioneAlloggioRepository`.
    *   Lista mista di prenotazioni con stati diversi (`CONFERMATA`, `IN_ATTESA`, etc.).
2.  **Benchmark**: Filtrare per uno stato specifico.

---

## Step 6: Benchmark "Algoritmi Randomizzati" - Generazione Itinerari

**Target:** `it.greentrails.backend.gestioneitinerari.adapter.ItinerariStubAdapter`
**Metodo Critico:** `pianificazioneAutomatica`
**Problema:** `Collections.shuffle()` su intere liste scaricate dal DB.

**File Target:** `src/test/java/it/greentrails/backend/benchmarks/ItinerariAdapterBenchmark.java`
**Strategia:**
1.  **State**:
    *   Mock di `AttivitaRepository` e `CameraRepository` che ritornano liste di varie dimensioni.
    *   L'adapter ha bisogno di mock anche per i repository di salvataggio (`save` può essere una no-op).
2.  **Benchmark**: Eseguire la pianificazione.
3.  **Analisi**: Costo dello shuffle e dello stream limit su liste grandi.

---

## Step 7: Runner di Esecuzione

Creare un unico punto di ingresso per lanciare i benchmark dall'IDE.

**File Target:** `src/test/java/it/greentrails/backend/benchmarks/BenchmarkRunner.java`

```java
public static void main(String[] args) throws Exception {
    org.openjdk.jmh.runner.options.Options opt = new org.openjdk.jmh.runner.options.OptionsBuilder()
            .include("it.greentrails.backend.benchmarks.*")
            .forks(1)
            .warmupIterations(3)
            .measurementIterations(5)
            .build();

    new org.openjdk.jmh.runner.Runner(opt).run();
}
```

---

## Istruzioni Operative per l'Agente

1.  **Verifica**: Controllare che le dipendenze in `pom.xml` siano corrette (Step 1 già eseguito, verificare se serve altro).
2.  **Struttura**: Creare il package `it.greentrails.backend.benchmarks` in `src/test/java`.
3.  **Implementazione**:
    *   Implementare `DistanceCalculatorBenchmark` (Step 2).
    *   Implementare `RicercaServiceBenchmark` (Step 3).
    *   Implementare `RecensioneServiceBenchmark` (Step 4).
    *   Implementare `PrenotazioneBenchmark` (Step 5 - opzionale se ridondante rispetto al 4, ma consigliato per completezza).
    *   Implementare `ItinerariAdapterBenchmark` (Step 6).
4.  **Esecuzione**: Implementare ed eseguire `BenchmarkRunner` (Step 7).
5.  **Report**: Riassumere i risultati ottenuti.
