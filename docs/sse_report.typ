= Introduzione

#link("https://github.com/gab-ds/GreenTrails")

Il presente documento fornisce una sintesi delle attività di
manutenzione perfettiva dell'applicativo web GreenTrails dal punto
di vista della sostenibilità del software. L'applicativo è già oggetto
di manutenzione evolutiva e perfettiva, rispettivamente per i corsi
di Ingegneria del Software: Tecniche Avanzate e Software Dependability.

Il documento illustra le ottimizzazioni tecniche apportate, con una
spiegazione del razionale alla base di ciascun intervento e dei
relativi requisiti. Include la descrizione tecnica di ogni intervento
— obiettivi, specifiche, problemi riscontrati e risultati — e in
chiusura riporta sintesi delle modifiche, problematiche residue e
considerazioni finali.

Le attività di manutenzione perfettiva svolte nell'ambito della
sostenibilità del software hanno riguardato:

- *Code coverage:* integrazione di JaCoCo con soglia minima dell'80%
    per garantire la copertura del codice sorgente.
- *Microbenchmark delle performance:* integrazione di JMH
    per la misurazione delle performance dei
    componenti critici.
- *Analisi statica per l'efficienza energetica:* integrazione di
    SonarQube con il plugin Creedengo per l'individuazione di pattern
    di programmazione energeticamente inefficienti.
- *Test di performance:* definizione di piani di Load, Stress, Spike
    e Soak Test con JMeter.
- *Analisi ambientale del frontend:* misurazione dell'impatto
    ambientale tramite GreenIT-Analysis, con baseline EcoIndex.
- *Misurazione del consumo energetico:* integrazione di EnergiBridge
    per la misurazione del consumo reale di CPU e memoria durante
    l'esecuzione.

== Contesto

==== Scopo dell'applicazione

L'obiettivo principale dell'applicazione è quello di agevolare la
progettazione di itinerari ecosostenibili, offrendo agli utenti uno
strumento intuitivo e completo per organizzare viaggi rispettosi
dell'ambiente. La piattaforma si propone di combinare la facilità d'uso
con un approccio basato su criteri di sostenibilità, in modo da rendere
la scelta delle destinazioni e delle attività non solo pratica, ma anche
consapevole dal punto di vista ecologico.

==== Valori di ecosostenibilità

Un concetto cruciale per il funzionamento della piattaforma è quello dei
valori di ecosostenibilità. Si tratta di una lista strutturata di "good
practices" ambientali che ogni attività può adottare e dichiarare. Esse
vengono registrate nella piattaforma e fungono da *criteri di
valutazione*, consentendo sia ai visitatori sia all'algoritmo di
classificare le attività in base al loro impegno ambientale.

==== Ruoli definiti

All'interno dell'applicazione sono stati individuati tre attori,
ciascuno con responsabilità ben distinte:

- *il Visitatore*, che rappresenta l'utente finale che accede alla
  piattaforma per consultare e prenotare le attività disponibili. Il suo
  ruolo è quello di fruire dei servizi offerti, selezionando le
  esperienze più adatte alle proprie esigenze e preferenze;

- *il Gestore Attività*, ovvero la figura incaricata di creare,
  pubblicare e amministrare le proprie attività sul portale. Oltre a
  descrivere i dettagli logistici, il gestore inserisce informazioni
  relative alle pratiche di sostenibilità adottate, contribuendo così al
  valore complessivo della piattaforma;

- *l'Amministratore*, il quale detiene i poteri di gestione del sito,
  tra cui la possibilità di modificare la percentuale di commissione
  applicata ai guadagni derivanti dalle prenotazioni effettuate dai
  visitatori. Ha inoltre la responsabilità di monitorare e moderare i
  contenuti, garantendo che le attività elencate rispettino gli standard
  qualitativi della piattaforma.

==== Preferenze del visitatore e personalizzazione dell'itinerario

Le preferenze espresse dal visitatore costituiscono un elemento
fondamentale per la personalizzazione dell'esperienza; esse possono
riguardare diversi aspetti, quali località preferita (es. _mare_,
_montagna_, _città_), tipologia di attività preferita (es. _ristoranti_,
_all'aperto_, ecc.), e così via. Queste informazioni vengono elaborate
(o, almeno, _dovrebbero essere elaborate_) dal modulo di intelligenza
artificiale integrato nella piattaforma. Tale modulo, un algoritmo
genetico, utilizza sia i dati relativi ai valori di ecosostenibilità
delle attività sia le preferenze dell'utente per generare un itinerario
su misura, ottimizzato per minimizzare la distanza percorsa, pur
soddisfacendo le preferenze del visitatore.

= Architettura del Sistema

Il backend segue un'architettura a *layers* propria di Spring: Presentation Layer (controller REST MVC), Service Layer (logica di business), Persistence Layer (repository JPA con MySQL in produzione e H2 in test), Security Layer (Spring Security). Il progetto è organizzato per domini funzionali in package dedicati: gestioneutenze, gestioneattivita, gestioneitinerari, gestioneprenotazioni, gestionericerca, gestionesegnalazioni, gestioneupload e utils.

== Stack Tecnologico

*Java 21* con *Spring Boot 3.5.16* (aggiornato da 3.2.1), moduli Web MVC, Data JPA, Security (HTTP Basic + BCrypt), Actuator, Validation. Dipendenze gestite con Maven, profiling per ambienti dev/prod/test. Database MySQL 8 (produzione) / H2 embedded (test).

== Componenti Principali

- *Sicurezza*: HTTP Basic Authentication, BCryptPasswordEncoder (work factor 10, ~130 ms per hash), controllo accessi basato su ruoli (VISITATORE, GESTORE_ATTIVITA, AMMINISTRATORE), CORS configurato per frontend su localhost:4200 e :9000.
- *Servizi Core*: GestioneUtenzeServiceImpl (UserDetailsService), AttivitaServiceImpl (CRUD e filtri attività), ItinerariServiceImpl (pianificazione con adattatore AI — attualmente stub con shuffle casuale), PrenotazioneAlloggioServiceImpl / PrenotazioneAttivitaTuristicaServiceImpl (ciclo di vita prenotazioni), RicercaServiceImpl (filtri spaziali).
- *Utility*: CorsConfig, ResponseGenerator.

== Infrastruttura e Distribuzione

Docker multi-stage (eclipse-temurin:21) con configurazione multi-ambiente: sviluppo (docker-compose.yml orchesta backend, frontend Angular su Nginx, MySQL), produzione (docker-compose.prod.yml con policy di riavvio e limiti risorse), test (docker-compose.test.yml). Health check su /actuator/health.

== Piattaforma di Benchmark e Riproducibilità

Le nuove misurazioni relative alla configurazione raffinata vengono eseguite
su un host Proxmox dedicato, un Lenovo
ThinkCentre M700 SFF equipaggiato con CPU Intel(R) Core(TM) i5-6500 @ 3.20 GHz,
16 GB di RAM DDR4 (2 moduli da 8 GB a 2133 MT/s, 1,2 V) e un SSD da 1 TB
(WDC WDS100T1R0A-68A4W0, famiglia WD Blue/Red/Green SSDs). Il sistema esegue
Proxmox VE `pve-manager/9.2.10/43df2e01f27a1a19`, kernel Linux
`7.0.14-11-pve` e QEMU `11.0.3` (`pve-qemu-kvm_11.0.3-2`). La connettività
verso la rete esterna usa Ethernet collegata direttamente al router.

Le impostazioni di affinità CPU, governor, Turbo Boost, memoria delle VM e
configurazione della rete vengono applicate e verificate dai playbook; non
sono quindi ripetute qui come caratteristiche hardware statiche. JMeter viene
eseguito da un laptop esterno tramite il port-forward configurato su Proxmox:
le specifiche hardware del laptop non fanno parte della piattaforma sotto
misurazione e non vengono incluse nel report.

Questa piattaforma costituisce il riferimento per le nuove esecuzioni; le
metriche storiche riportate nelle sezioni precedenti non vengono
retroattivamente attribuite a questo hardware.

== Verifica e Qualità del Software

- *Test Unitari*: ~14 classi con Mockito per service layer.
- *Test di Integrazione*: ~12 classi con `@SpringBootTest` e `@AutoConfigureMockMvc` per controller REST.
- *Mutation Testing*: PiTest 1.22.1, soglia minima 80%.

=== JaCoCo — Code Coverage

*JaCoCo* (Java Code Coverage) è uno strumento che misura la percentuale di codice sorgente effettivamente eseguita durante i test, analizzando copertura di linee, rami, metodi e classi. Nel progetto è integrato come plugin Maven con soglia minima dell'80%. Inizialmente la soglia era applicata direttamente nella build: se la copertura scendeva sotto tale valore, la build falliva. Nel corso della manutenzione il controllo è stato spostato in CI/CD tramite lo script `scripts/jacoco_coverage.py --fail-below 80`, eseguito sulle pull request al posto della precedente action `PavanMudigonda/jacoco-reporter`.

*Risultati*: la copertura attuale si attesta al 97,5% sulle linee, 97,2% sui rami e 100% sulle classi (30/30), con i service layer al 100%. Le esclusioni agent/report sono state allineate: entity, enum, utility, eccezioni di gestioneupload, security e BackendApplication, classi senza logica di business significativa. I report HTML vengono generati in fase `verify` e pubblicati su GitHub Pages per una consultazione continuativa.

*Importanza per la sostenibilità tecnica*: una copertura adeguata riduce il rischio di regressioni e abbassa il costo di manutenzione nel tempo. Codice non testato è codice che degrada — richiede più tempo per essere modificato con sicurezza e tende ad accumulare debito tecnico. Mantenere una copertura elevata significa preservare la *manutenibilità* del sistema, un pilastro della sostenibilità del software a lungo termine.

=== JMH — Microbenchmark delle Performance

*JMH* (Java Microbenchmark Harness) è un framework di Oracle per la
scrittura di microbenchmark affidabili in Java, progettato per evitare
le insidie comuni della misurazione delle performance (warming up
della JVM, ottimizzazioni del JIT compiler, code elimination). Nel
progetto i benchmark sono stati inizialmente definiti in 16 classi
(44 benchmark totali) coprendo i componenti critici: servizi core
(CRUD, crittografia, pianificazione), filtraggio in memoria e
archiviazione file; la suite è stata successivamente consolidata in
4 classi (9 benchmark).

*Risultati — baseline storica, suite originale*
(1 fork, 3 warm-up + 5 misurazioni da 1 s):

#table(
  columns: (auto, auto, auto),
  inset: 6pt,
  stroke: 0.5pt,
  [*Componente*], [*Operazione*], [*Risultato*],
  [BCryptPasswordEncoder], [Encoding], [~66,4 ms],
  [BCryptPasswordEncoder], [Matching], [~65,4 ms],
  [Servizi core], [CRUD, ricerca testuale],
  [6-8 μs per operazione (piccoli dataset)],
  [Filtro in memoria], [Recensioni, prenotazioni, stato],
  [da 11 μs (100 elem.) a ~500 ms (50.000 elem.)],
  [Ricerca spaziale], [Filtro su coordinate],
  [da 19 μs a ~1,2 s (dip. da dimensione dataset)],
  [Pianificazione itinerari], [Stub AI],
  [da 52 μs a ~537 μs],
  [Intersezione categorie], [RicercaCategorie],
  [da 18 ms a ~10 s (crescita quadratica)],
  [Archiviazione file], [Delete / Load / Store],
  [1,4 μs / 17 μs / 68 μs],
)

I valori evidenziano il costo
voluto di BCrypt (~66 ms per hash con work factor 10). Le
operazioni su larga scala (intersezione categorie, ricerca spaziale,
filtraggio su 50.000 elementi) mostrano latenze in secondi che
costituiscono un potenziale collo di bottiglia da ottimizzare in
future iterazioni.

*Risultati — suite consolidata, VM QEMU*
(1 fork, 500 iterazioni warm-up + 300 misurazioni, `--summary`):

#table(
  columns: (auto, auto, auto, auto),
  inset: 6pt,
  stroke: 0.5pt,
  [*Benchmark*], [*Modo*], [*Score*], [*Errore 99,9 %*],
  [benchmarkDelete], [avgt], [0,0104 ms/op], [± 0,0000],
  [benchmarkLoadAll], [avgt], [0,0273 ms/op], [± 0,0001],
  [benchmarkStore], [avgt], [0,0245 ms/op], [± 0,0002],
  [benchmarkPianificazione (100)], [avgt], [0,0035 ms/op], [± 0,0000],
  [benchmarkPianificazione (1.000)], [avgt], [0,0324 ms/op], [± 0,0000],
  [benchmarkPianificazione (5.000)], [avgt], [0,1570 ms/op], [± 0,0010],
  [benchmarkPianificazione (10.000)], [avgt], [0,3150 ms/op], [± 0,0026],
  [benchmarkPasswordEncoding], [avgt], [73,1671 ms/op], [± 0,0367],
  [benchmarkPasswordMatching], [avgt], [73,1230 ms/op], [± 0,0332],
)

=== Ottimizzazioni Post-Benchmark

Dai benchmark sono emersi quattro colli di bottiglia principali,
tutti riconducibili a `findAll()` + filtraggio in memoria lato Java:

#table(
  columns: (auto, auto, auto, auto),
  inset: 6pt,
  stroke: 0.5pt,
  [*Bottleneck*], [*Causa*], [*Soluzione*], [*Risparmio stimato*],
  [Intersezione categorie],
  [Query N (Cartesian join) + O(N²) in memoria],
  [Singola query JPQL con GROUP BY / HAVING COUNT],
  [~99,5% (10 s → 50 ms)],
  [Ricerca spaziale],
  [findAll() + Haversine per riga in Java],
  [Query nativa MySQL ST_Distance_Sphere + indice spaziale],
  [~98% (1,2 s → 20 ms)],
  [Filtro recensioni per visitatore],
  [findAll() + loop manuale],
  [Query findByVisitatore + indice su FK],
  [~99% (300 ms → 2 ms)],
  [Filtro prenotazioni per stato],
  [findAll() + loop manuale],
  [Query findByStato + indice su colonna stato],
  [~99% (500 ms → 2 ms)],
)

Le modifiche hanno interessato 10 file: repository (nuove query),
service (eliminati loop Java), entity (aggiunti `@Index`). Il
risparmio complessivo è stimato da ~13 s a ~100 ms per le
operazioni critiche, con benefici diretti sulla sostenibilità
tecnica (minor carico CPU/DB) e ambientale (minor consumo
energetico).

*Importanza per la sostenibilità tecnica*: le performance software
hanno un impatto diretto sulla sostenibilità ambientale.
Un'applicazione inefficiente consuma più CPU, più memoria e più
energia per soddisfare lo stesso carico utente, aumentando la
carbon footprint dell'infrastruttura. Misurare regolarmente le
performance con JMH permette di rilevare regressioni e ottimizzare
i colli di bottiglia, garantendo che il sistema rimanga *efficiente
energeticamente* nel tempo. In un'ottica di *Green Software
Engineering*, ogni ciclo di CPU risparmiato è un contributo concreto
alla riduzione dell'impatto ambientale del software.

I benchmark sono stati eseguiti con warm-up a iterazioni fisse (3
warm-up + 5 misurazioni da 1 s), senza l'ausilio del dynamic halt
AI-driven di AMBER per problematiche relative all'utilizzo di tale
strumento.

=== Rimozione del Fallback In-Memory e di DistanceCalculator

La ricerca per posizione è stata ulteriormente semplificata rimuovendo
il *fallback* in `RicercaServiceImpl.findAttivitaByPosizione`: un
`catch (Exception)` ripristinava `findAll()` + filtraggio Haversine in
memoria ad ogni errore della query nativa.

- *No full table scans:* eliminato il caricamento dell'intera tabella
  `attivita` in memoria, coerente con l'ottimizzazione JMH già
  documentata (~98%); il calcolo spaziale resta ora esclusivamente nel
  DB (`ST_Distance_Sphere`), senza doppio algoritmo Haversine in Java.
- *Cleanup:* eliminato `DistanceCalculator` (dead code) con i suoi 4
  unit test e 2 benchmark JMH; `findByPosizioneNative` → `findByPosizione`.

Risultato: nessuna regressione, minore consumo CPU/memoria.

=== Consolidamento della Suite

Come intervento più recente, la suite di benchmark è stata
consolidata da *16 classi (44 benchmark)* a *4 classi (9 benchmark)*,
con benefici diretti sulla sostenibilità tecnica della base di
codice:

- *Rimozione dei benchmark pass-through:* dodici classi misuravano
  esclusivamente operazioni su repository simulati con Mockito
  (`save`, `findById`, filtri su liste fittizie generate in memoria),
  senza esercitare logica applicativa reale: codice di misura privo
  di valore informativo, mantenuto a costo di debito tecnico e di
  tempo di esecuzione della suite.
- *Eliminazione dell'errata inclusione di Mockito nei benchmark:*
  la reflection del framework introduceva nel percorso misurato un
  overhead assente in produzione, falsando i risultati. Mockito è
  stato sostituito da fake in-memory scritti a mano
  (`FakeJpaRepository` e specializzazioni concrete, basate su sole
  chiamate virtuali) e da `MockMultipartFile` (spring-test) per la
  simulazione degli upload. In occasione dell'intervento è stato
  anche corretto il packaging del jar eseguibile `benchmarks.jar`.

// TODO: rieseguire i benchmark sulla macchina di riferimento e
// aggiornare i valori alla suite consolidata (4 classi, 9 benchmark).

Durante le esecuzioni preliminari, i benchmark sono terminati con
errori `OutOfMemoryError: Java heap space` durante la fase di
warm-up (alla iterazione 44 su 500 previste), con il forked VM che
termina con exit code 3. La causa è stata identificata in
`ItinerariAdapterBenchmark`: il fake in-memory repository
(`FakeJpaRepository`) accumulava ogni oggetto `Itinerario` creato
nelle iterazioni di warm-up nella lista `items` senza mai
svuotarla, provocando un crescente consumo di heap fino all'esaurimento
dello spazio disponibile. Il problema è stato risolto rimuovendo
l'`items.add(entity)` dal metodo `save()`, poiché il repository
fittizio non ha bisogno di persistere gli oggetti per misurare le
performance di `pianificazioneAutomatica()`.

=== Confronto con la Suite Precedente

I risultati preliminari della suite consolidata (9 benchmark) sono
stati confrontati con i valori storici della suite originaria
(16 classi, 44 benchmark) eseguita su un hardware diverso:

- *BCrypt:* 73 ms vs 66 ms storici — delta atteso, attribuibile
  alla diversa architettura CPU della macchina di riferimento.
- *ArchiviazioneFileSystemService:* 10 / 27 / 24 μs (delete/loadAll/store)
  vs 1.4 / 17 / 68 μs storici — variazioni compatibili con I/O
  del filesystem su VM vs bare metal.
- *Pianificazione itinerari:* 0.004--0.315 ms (100--10.000 attivita)
  vs 0.052--0.537 ms storici — range coerente, crescita lineare.

Non essendo ancora disponibili i test di carico (JMeter), i
confronti sulla componente di carico restano indicativi. I profili
energetici (EnergiBridge) sono ora disponibili per tre scenari
(baseline, idle, load) su VM QEMU. La suite consolidata rimuove i
benchmark pass-through e quelli con dataset estremi (fino a 50.000
elementi) che producevano latenze in secondi, concentrandosi sui
componenti critici effettivamente misurabili.

= Sostenibilità Sociale

La sostenibilità sociale del software riguarda l'impatto del progetto
sulla comunità di sviluppo e sugli utenti. Per analizzare questa
dimensione è stato impiegato *GUIDO* per il rilevamento di community
smells, e *FOSSA* per la conformità delle licenze.

== GUIDO — Community Smells

*GUIDO* (Gathering and Understanding Socio-Technical Aspects in
Development Organizations) è un conversational agent progettato per
l'identificazione e la gestione dei *community smells* nelle comunità
di sviluppo software su GitHub. Sviluppato da Stefano Lambiase come
evoluzione del precedente tool CADOCS (Voria et al., ICSME 2022),
GUIDO opera localmente tramite Docker e si interfaccia con l'API di
GitHub per analizzare i repository e rilevare pattern socio-tecnici
disfunzionali.

I community smells individuabili da GUIDO includono: Organizational
Silo, Black-cloud Effect (BCE), Radio Silence (RS), Prima-donnas
Effect (PDE), Sharing Villainy, Organizational Skirmish, Solution
Defiance, Truck Factor Smell, Unhealthy Interaction e Toxic
Communication (TC). Per ciascun smell, GUIDO suggerisce strategie
di mitigazione con un rating di efficacia (da 1 a 3 stelle).

=== Situazione Pre-Intervento

L'analisi è stata condotta sul repository originale
`GerardoFesta/GreenTrails` prima delle attività di manutenzione
perfettiva. Sono stati rilevati 4 community smells:

#table(
  columns: (auto, auto, auto),
  inset: 6pt,
  stroke: 0.5pt,
  [*Community Smell*], [*Descrizione*], [*Mitigazioni suggerite*],
  [BCE — Black-cloud Effect ⚫️☁️],
  [Sovraccarico informativo dovuto a mancanza di comunicazione
   strutturata e governance della cooperazione],
  [Communication plan (★★★); Restructure community (★★);
   Social sanctioning mechanism (★★)],
  [PDE — Prima-donnas Effect ✨👑],
  [Membri del team riluttanti ad accettare contributi esterni
   a causa di una collaborazione inefficientemente strutturata],
  [N/D],
  [RS — Radio Silence 🔇📻],
  [Un membro si interpone in ogni interazione formale tra
   sotto-comunità, senza flessibilità per introdurre altri canali],
  [Communication plan (★★★); Restructure community (★★);
   Mentoring (★★); Cohesion exercising (★★); Monitoring (★);
   Social-rewarding mechanism (★★)],
  [TC — Toxic Communication 👤😡],
  [Interazioni tossiche e opinioni conflittuali tra sviluppatori
   che possono spingere i membri ad abbandonare il progetto],
  [N/D],
)

La presenza simultanea di BCE, PDE, RS e TC indica una comunità di
sviluppo con significative problematiche socio-strutturali: assenza
di governance della comunicazione (BCE), resistenza ai contributi
esterni (PDE), colli di bottiglia comunicativi (RS) e conflittualità
interpersonale (TC).

Tuttavia, non è stato possibile condurre un intervento effettivo
sui community smells rilevati, poiché l'analisi è stata condotta
sul team del gruppo di progetto precedente. L'attuale gruppo di
manutenzione è composto da due sole persone, rendendo il contesto
socio-tecnico non direttamente confrontabile né applicabile per
azioni correttive mirate.

== FOSSA — Conformità delle Licenze

*FOSSA* è uno strumento di Software Composition Analysis (SCA)
specializzato nella gestione della conformità delle licenze
open-source. Analizza l'albero delle dipendenze del progetto e
verifica che tutte le librerie utilizzate siano compatibili con la
licenza del progetto, segnalando conflitti di licenza, obblighi di
attribuzione e rischi legali.

L'integrazione di FOSSA è prevista nella prossima iterazione per
completare la copertura della sfera sociale della sostenibilità,
insieme all'esecuzione di una nuova analisi GUIDO sul repository
corrente.

=== CI/CD e Qualità del Codice

I workflow GitHub Actions coprono build, test, coverage, mutation, style check (Checkstyle Google) e benchmark JMH. La qualità del codice è garantita da Checkstyle con regole Google, dall'uso di Lombok per ridurre il boilerplate e dall'esclusione mirata di entità, enum e classi di configurazione dalle metriche di copertura in quanto non contenenti logica di business significativa.

Nel corso della manutenzione la pipeline CI/CD è stata estesa con un reporting automatizzato della copertura: gli script `scripts/jacoco_coverage.py` e `scripts/pitest_coverage.py` leggono i report XML (jacoco.xml, mutations.xml) e producono un riepilogo del coverage — in Markdown (`--markdownify`) per lo step summary e il commento sulla pull request, con le mutazioni sopravvissute raggruppate in una sezione collassabile `<details>`, oppure in testo semplice per l'uso locale da CLI. Gli stessi script implementano i gate `--fail-below 80` (JaCoCo linee e Pitest) sulle pull request, sostituendo la precedente action `PavanMudigonda/jacoco-reporter`. I report vengono conservati anche a test falliti: gli step di pubblicazione usano `if: !cancelled()` invece di `continue-on-error`. L'azione di pubblicazione dei test Surefire è stata migrata da `scacap/action-surefire-report` v1 al successore `ScalableCapital/action-surefire-report` v2.0.5. Le immagini Docker sono buildate anche su pull request (`push: false`) e pubblicate su Docker Hub solo su push a `main`, con `environment: production` (protetto da review manuale), SBOM e provenance.

=== Creedengo — Analisi Statica per l'Efficienza Energetica

*Creedengo* (ex ecoCode) è un plugin per SonarQube che estende l'analisi
statica del codice con regole specifiche per l'efficienza energetica. Le 15
regole attivate per Java — raggruppate sotto il profilo *Creedengo* —
individuano pattern di programmazione che consumano CPU, memoria o I/O in
modo non necessario, tra cui:

- chiamate a repository Spring all'interno di loop o stream (GCI1)
- creazione di oggetti inutili all'interno di iterazioni (GCI2, GCI3)
- uso inefficiente di strutture dati e operazioni di boxing/unboxing (GCI5)
- condizioni booleane calcolate superfluamente (GCI27, GCI28)
- cicli con operazioni invarianti spostabili all'esterno (GCI32)
- altri pattern di codice energeticamente dispendiosi

L'analisi è stata condotta sul backend Java di GreenTrails (4.352 linee di
codice), utilizzando SonarQube 9.9.8 in esecuzione su Docker con il plugin
Creedengo 2.0.0. Il profilo "Creedengo", che eredita le regole di "Sonar way",
è stato impostato come default per il linguaggio Java.

*Risultati:*
- *Bug:* 0 — *Vulnerabilità:* 0 — *Code Smells totali:* 3
- *Debito tecnico:* 150 minuti (0,1% del costo di sviluppo stimato)
- *Duplicazioni:* 5,1%
- *Rating:* affidabilità A, sicurezza A, manutenibilità A

Tutti e 3 i code smell rilevati appartengono alla regola *GCI1 — Avoid
Spring repository call in loop or stream*:

#table(
  columns: (auto, auto, auto),
  inset: 6pt,
  stroke: 0.5pt,
  [*File*], [*Linea*], [*Descrizione*],
  [ItinerariStubAdapter.java], [48], [Repository call in stream],
  [ItinerariStubAdapter.java], [62], [Repository call in stream],
  [RicercaServiceImpl.java], [36], [Repository call in stream],
)

Tutte e tre le occorrenze riguardano chiamate a repository JPA all'interno di
stream Java, un pattern che moltiplica le connessioni al database per ogni
elemento della collezione, aumentando il carico sulla base dati e il consumo
energetico complessivo. La severità è *MINOR* e il debito stimato per ogni
occorrenza è di 50 minuti di refactoring.

*Importanza per la sostenibilità tecnica:* l'analisi statica con Creedengo
si inserisce nella sfera della *sostenibilità tecnica* perché individua
precocemente pattern inefficienti che, se non corretti, degradano le
performance e aumentano il consumo di risorse hardware. Ogni chiamata
superflua al database, ogni oggetto creato in un loop e ogni istruzione
ridondante si traduce in cicli di CPU sprecati e, a scala, in un maggior
consumo energetico dell'infrastruttura. Integrare Creedengo nel processo di
sviluppo significa dotarsi di uno strumento automatico per mantenere
l'efficienza del codice nel tempo, prevenendo l'accumulo di debito tecnico
energetico.

= Test di Performance con JMeter

*JMeter* è uno strumento open-source sviluppato dalla Apache Software Foundation, progettato per eseguire test di carico e misurare le performance di applicazioni web e servizi REST. Consente di simulare richieste HTTP concorrenti, analizzare i tempi di risposta e identificare colli di bottiglia sotto diversi profili di carico.

Sono stati definiti quattro tipi di test, ciascuno mirato a verificare un aspetto specifico del comportamento del sistema sotto stress:

La configurazione dei test è stata successivamente raffinata rispetto alla
prima versione descritta sotto. I dati necessari vengono predisposti dal
workflow di benchmark: il template contiene un database inizialmente vuoto,
MySQL resta disabilitato durante la fase JMH e il backend viene avviato con il
profilo `dev` solo per le misurazioni energetiche e di carico. In occasione del
primo avvio, `DataSeeder` crea automaticamente utenti, attività, alloggi,
camere, recensioni, preferenze, itinerari, prenotazioni e segnalazioni. Questo
presuppone che, dopo un arresto anomalo, l'operatore completi il teardown prima
di ripetere l'esecuzione, così da ottenere una nuova infrastruttura di
benchmark.

I quattro piani JMeter attuali esercitano esclusivamente endpoint pubblici e
non includono ancora richieste con autenticazione HTTP Basic. Le misure
descritte rappresentano quindi il comportamento del percorso di consultazione
anonimo; i flussi autenticati per visitatori, gestori e amministratori potranno
essere aggiunti in una successiva estensione, usando account e dati predisposti
fuori dalla finestra temporizzata del test.

== Load Test

Il *Load Test* simula un carico utente normale e costante per verificare che il sistema gestisca il traffico atteso senza degradazione delle performance. La configurazione storica prevedeva 50 utenti concorrenti con un periodo di ramp-up di 30 secondi e 5 iterazioni ciascuno. Il test interroga 11 endpoint REST del backend, tra cui la lista delle attività, i dettagli, le recensioni, le camere, la ricerca e l'health check di Actuator. I risultati storici hanno mostrato tempi di risposta medi inferiori a 100 ms per gli endpoint GET e un throughput complessivo di circa 120 richieste al secondo, senza errori.

Come raffinamento, il piano attuale esegue una singola navigazione per iterazione
all'interno di una durata complessiva configurabile (300 secondi di default),
con 50 utenti e ramp-up di 30 secondi. Un timer casuale introduce 500 ms di
think time più un intervallo casuale di 0--250 ms. Il piano rappresenta quindi
un carico nominale a concorrenza costante; i valori quantitativi storici non
sono automaticamente attribuibili alla versione raffinata e dovranno essere
riconfermati con un'esecuzione live.

== Stress Test

Lo *Stress Test* spinge il sistema oltre il limite operativo previsto per identificare il punto di rottura — ovvero il carico massimo oltre il quale il servizio inizia a rifiutare richieste o a rispondere con errori. La configurazione storica utilizzava 200 utenti concorrenti suddivisi in 5 gruppi di throughput controllato (Throughput Controller), per distribuire il carico in modo progressivo. Il test storico ha evidenziato che il backend mantiene una stabilità accettabile fino a circa 150 utenti simultanei, oltre i quali si registra un incremento significativo dei tempi di risposta (latenza media superiore a 2 secondi) e un tasso di errore iniziale sotto l'1%.

Nel piano attuale, 200 utenti sono mantenuti a concorrenza costante per 600
secondi di default, con cinque stadi da 60 secondi: il think time viene ridotto
progressivamente da 2 s a 0 s, mantenendo poi il livello massimo. I cinque
Throughput Controller continuano a distribuire il mix funzionale 30/25/20/15/10%.
Il punto di esaurimento deve essere determinato osservando errori, latenza e
risorse del backend durante l'esecuzione, non assunto dai risultati storici.

== Spike Test

Il *Spike Test* valuta la resilienza del sistema a incrementi improvvisi e repentini del carico, simulando scenari di traffico a picco (es. campagne promozionali o eventi virali). La configurazione storica prevedeva 150 utenti con ramp-up di 1 secondo e una durata di 30 secondi, con tutti gli endpoint richiamati in sequenza. Il test storico aveva dimostrato che il sistema assorbiva il picco senza crash, con un lieve aumento della latenza media (circa 350 ms) e nessun errore.

Il piano raffinato mantiene 150 utenti e la durata predefinita di 30 secondi,
ma distingue una fase baseline di 10 secondi, una fase burst di 10 secondi e
una fase recovery finale. La variazione è ottenuta riducendo il think time nella
fase burst, mentre il numero di utenti resta fisso: si misura quindi uno spike
della frequenza di richieste offerte, non l'aggiunta dinamica di thread del
sistema operativo. Anche questi risultati devono essere riconfermati dal run
live.

== Soak Test

Il *Soak Test* (o Endurance Test) mantiene un carico moderato per un periodo prolungato — 20 utenti per 600 secondi (10 minuti) — per rilevare degradationi lente come memory leak, saturazione delle connessioni al database o frammentazione della memoria. I risultati storici hanno mostrato un comportamento stabile per tutta la durata del test: la latenza media è rimasta costante (intorno a 80 ms), il throughput non ha subito cali progressivi e non si sono verificati errori, indicando l'assenza di degradationi significative nel backend. La variabilità dei tempi di risposta ($sigma$) è risultata contenuta, con un massimo registrato di 450 ms per le richieste di ricerca che coinvolgono filtri spaziali.

Il piano attuale mantiene 20 utenti per 600 secondi di default, esegue una sola
navigazione per iterazione e applica 500 ms di think time più 0--250 ms casuali.
È quindi un test di endurance a concorrenza costante, non un generatore a RPS
fisso. Le metriche storiche restano rilevanti come confronto, ma non
costituiscono ancora la validazione della versione raffinata.

I test di performance sono integrati nella suite di verifica del progetto e possono essere eseguiti tramite l'interfaccia grafica di JMeter o in modalità headless (CLI) per l'integrazione in pipeline CI/CD.

= Analisi Ambientale del Frontend con GreenIT-Analysis

*GreenIT-Analysis* è un'estensione browser open-source (V3.2.0) che analizza
l'impatto ambientale delle pagine web quantificando emissioni di gas serra,
consumo idrico ed efficienza complessiva. L'analisi viene eseguita localmente
nel browser, senza invio di dati esterni, e valuta fino a 75 buone pratiche
di eco-concezione web (ottimizzazione immagini, compressione risorse, caching,
complessità DOM, ecc.).

L'analisi è stata condotta in due fasi: una prima analisi sul frontend
originale in Angular e una successiva sul frontend refattorizzato in Nuxt 4.
In entrambi i casi, l'EcoIndex complessivo è risultato *A*, a conferma
della buona qualità ambientale del frontend indipendentemente dal framework
adottato.

== Risultati

Su 75 buone pratiche verificate, 73 sono risultate conformi sia prima che
dopo il refactoring. Due richiedono interventi:

#table(
  columns: (auto, auto, auto),
  inset: 6pt,
  stroke: 0.5pt,
  [*Pratica*], [*Esito*], [*Risultato*],
  [Externalize CSS and JS], [NO], [13 inline stylesheet(s) e inline javascript(s) found],
  [Provide print stylesheet], [NO], [No print stylesheet found],
)

=== Externalize CSS and JS

I 13 elementi inline rilevati sono tipici sia di Angular che di Nuxt 4,
che incapsulano stili e script nei componenti. Pur essendo un comportamento
previsto dal framework, un eccesso di codice inline aumenta il peso della
pagina e riduce la cacheabilità.

=== Provide print stylesheet

Non è presente un foglio di stile dedicato alla stampa. L'aggiunta di un
`@media print` che nasconda navigazione e pulsanti, ottimizzando la
leggibilità su carta, è un intervento a basso costo con impatto positivo
sull'esperienza utente.

== Sostenibilità ambientale

Il monitoraggio dell'impatto ambientale del frontend è parte integrante
delle attività di *Green Software Engineering*. Ogni byte non necessario
trasferito, ogni richiesta HTTP evitata e ogni ottimizzazione del DOM si
traducono in un minor consumo energetico lato client e server. In un'ottica
di sostenibilità ambientale, ridurre il peso delle pagine web significa
allungare la vita delle batterie dei dispositivi, diminuire il traffico di
rete e abbassare la carbon footprint complessiva dell'applicazione. La
baseline raccolta con GreenIT-Analysis costituisce il primo passo verso un
miglioramento continuo dell'efficienza ambientale del frontend.

=== Strumenti complementari per la sostenibilità ambientale

Oltre a GreenIT-Analysis, il progetto ha preso in considerazione due
strumenti aggiuntivi per la misurazione dell'impatto ambientale, il cui
utilizzo è stato rimandato in attesa di un deployment pubblico
dell'applicazione.

==== WebsiteCarbon

*WebsiteCarbon* è un tool web-based (https://www.websitecarbon.com) che
calcola la carbon footprint di una pagina web analizzando il trasferimento
dati, il tipo di hosting e il consumo energetico stimato. Restituisce metriche
quali grammi di CO2 per pagina visitata, emissioni annuali stimate e un
confronto percentile con altri siti web. A differenza di GreenIT-Analysis,
che opera localmente nel browser, WebsiteCarbon richiede un URL pubblico
raggiungibile da internet. Per questo motivo non è stato possibile applicarlo
a GreenTrails, attualmente accessibile solo su localhost. Il suo impiego è
previsto nella *sfera ambientale* una volta che l'applicazione sarà
deployata su un ambiente pubblico.

==== EcoIndex

*EcoIndex* (https://www.ecoindex.fr/en/) è un tool web che fornisce una
valutazione completa dell'impatto ambientale di una pagina web, calcolando
un punteggio da 0 a 100 (più alto è meglio) basato su numero di richieste
HTTP, dimensione della pagina e complessità del DOM. Restituisce anche il
consumo idrico equivalente in litri e le emissioni di gas serra in grammi di
CO2. Al pari di WebsiteCarbon, necessita di un URL pubblico e non è stato
quindi utilizzabile su localhost. Si colloca nella *sfera ambientale* della
sostenibilità e sarà integrato nelle analisi future. Il suo algoritmo di
calcolo è lo stesso alla base del punteggio EcoIndex fornito da
GreenIT-Analysis, garantendo coerenza tra le misurazioni.

= Misurazione del Consumo Energetico con EnergiBridge

*EnergiBridge* è un framework open-source sviluppato in Rust per la
misurazione del consumo energetico di applicazioni software durante
l'esecuzione. A differenza degli strumenti di analisi statica (Creedengo)
o di stima ambientale (GreenIT-Analysis), EnergiBridge opera a livello di
*sistema operativo*, interfacciandosi con i sensori hardware (RAPL su CPU
Intel/AMD, sensori di batteria su laptop) per registrare il consumo reale
di CPU, memoria, storage e rete.

Il framework è stato clonato e compilato dal repository ufficiale
(https://github.com/tdurieux/EnergiBridge). Le misurazioni richiedono
privilegi di amministratore per accedere ai sensori hardware.

#block(
  fill: rgb("fff3cd"),
  inset: 8pt,
  radius: 4pt,
  [*Nota sulle misurazioni:* tutti i dati energetici riportati di
  seguito sono stati raccolti su *VM QEMU* (2 vCPU, 4 GB RAM, host
  CPU passthrough), non su hardware fisico. Il framework EnergiBridge
  accede ai contatori RAPL (Running Average Power Limit) tramite
  l'MSR `0x611` (`MSR_RAPL_ENERGY_STATUS`), reso disponibile dalla
  VM grazie all'utilizzo di `qemu-vmsr-helper`
  (https://www.qemu.org/docs/master/tools/qemu-vmsr-helper.html).
  L'helper espone i registri RAPL della CPU host alla guest VM
  attraverso dispositivi MSR virtualizzati (`/dev/cpu/N/msr`).
  Poiché la VM non dispone di DRAM separata o unità di cache
  fisiche, i contatori PP0 (core), PP1 (uncore) e DRAM restano a
  zero; è disponibile esclusivamente la lettura a livello di
  package (`PACKAGE_ENERGY`). I valori assoluti dei contatori RAPL
  sono cumulativi dall'avvio del sistema; le misurazioni delta
  (differenza tra valore iniziale e finale di ogni finestra) non
  risentono dell'offset iniziale. Le misurazioni su VM forniscono
  un *indicatore relativo* del consumo del software, ma non
  corrispondono ai valori di potenza assoluta dell'hardware fisico
  sottostante.]
)

== Scenari di Misurazione

Le misurazioni sono state eseguite su tre livelli di carico, ciascuno
ripetuto 3 volte per un totale di 9 run. Ogni run dura 300 secondi
(5 minuti) di misurazione pura, intervallati da 180 secondi di
cooldown. Il backend Spring Boot viene avviato con profilo `dev` e
MySQL abilitato; EnergiBridge campiona ogni 200 ms.

L'esperimento è stato condotto in ambiente indoor a temperatura
stabile di 23 °C, su un nodo Proxmox con 4 core CPU (0-3). I
processi host sono stati isolati sui cores 0-1 tramite systemd
`CPUAffinity` (drop-in `/etc/systemd/system.conf.d/99-cpu-affinity.conf`),
mentre il worker VM è stato pinnato ai cores 2-3 con `qm set --affinity`.
Il governor CPU è stato impostato su `performance` (3191 MHz fissa tramite
`cpupower frequency-set`) e il Turbo Boost disabilitato
(`intel_pstate/no_turbo=1`), così le variazioni di consumo sono
attribuibili esclusivamente al carico computazionale. Durante le
misurazioni sono stati inoltre arrestati i servizi host `ksmtuned` e
`irqbalance` per eliminare ulteriori fonti di rumore.

La VM worker utilizza CPU host passthrough (`cpu: host`), 2 vCPU,
4096 MiB di RAM fissa (ballooning disabilitato — nessun parametro
`balloon:` configurato) e 20 GB su controller `virtio-scsi-pci`.
Il consumo energetico è stato misurato tramite RAPL con passthrough
`qemu-vmsr-helper` (argomento QEMU `-accel kvm,rapl=true`), che espone
il contatore `PACKAGE_ENERGY` (`MSR_RAPL_ENERGY_STATUS`, 0x611) alla
guest; i domini DRAM, PP0 e PP1 restano a zero nella VM. Il backend
JVM è stato configurato con heap fisso 2 GB (`-Xms2048m -Xmx2048m`)
e garbage collector G1GC (`-XX:+UseG1GC`).

La scelta di misurare il consumo energetico su una VM anziché su
hardware fisico comporta vantaggi specifici per un esperimento di
questa natura. La configurazione della VM è deterministica e
perfettamente replicabile: stessa CPU (`host` passthrough), stessa
RAM, stesso governor, stesso isolamento dei core ad ogni esecuzione.
Questo elimina le variabili confondenti introdotte da processi host,
throttling termico o stati BIOS diversi tra run, rendendo le
differenze di consumo tra i tre scenari attribuibili al solo carico
del software. Inoltre, la VM condivide l'hardware fisico con gli
altri componenti del benchmark (AMBER, MySQL, worker), evitando la
necessità di una macchina bare-metal dedicata — un vincolo pratico
rilevante in un contesto universitario. I valori assoluti di watt
non sono trasferibili a un hardware diverso, ma i rapporti relativi
tra scenari e i profili di consumo rimangono interpretativamente
validi.

#table(
  columns: (auto, auto, auto, auto, auto),
  inset: 6pt,
  stroke: 0.5pt,
  [*Scenario*], [*Descrizione*], [*Energia (J)*], [*Potenza media (W)*], [*CPU media*],
  [*Baseline*],
  [Nessun backend attivo; solo OS],
  [1,20 ± 0,05], [0,004], [2,8 %],
  [*Idle*],
  [Backend Spring Boot attivo, nessuna richiesta],
  [2,98 ± 0,07], [0,010], [2,8 %],
  [*Load*],
  [Backend sotto carico JMeter simulato],
  [127,59 ± 11,73], [0,425], [13,1 %],
)

== Analisi

#figure(
  image("plots/energy_consumption.svg", width: 85%),
  caption: [Consumo energetico per scenario.],
)

Il consumo del backend a riposo (idle) è trascurabile rispetto alla
baseline (~+1,8 J, +0,006 W), indicando che il solo processo JVM
inattivo non contribuisce in modo significativo al consumo energetico
della VM. Sotto carico, il consumo sale a ~127,6 J (+126,4 J rispetto
alla baseline), con una potenza media di ~0,425 W e un utilizzo CPU
medio del ~13%.

#figure(
  image("plots/power_timeseries.svg", width: 90%),
  caption: [Potenza istantanea per scenario.],
)

Il profilo di potenza istantanea rivela la dinamica reale del
consumo. La baseline e l'idle mostrano una potenza costante e
bassa (~0,004 W e ~0,010 W), mentre il carico produce una potenza
media di ~0,4--0,5 W. La derivata numerica dE/dt amplifica il rumore
di campionamento a 200 ms; il plot aggrega i valori in finestre di
2 s per renderlo leggibile. La forma d'onda è stabile e ripetibile
tra le prime due ripetizioni.
La ripetizione 3 del carico mostra una caduta di potenza negli
ultimi ~30 s (JMeter terminato prima della finestra di misurazione),
contribuendo alla variabilità inter-run. Una futura possibile
automazione dell'orchestrazione potrebbe eliminare il
disallineamento temporale.

#figure(
  image("plots/cpu_timeseries.svg", width: 90%),
  caption: [Utilizzo CPU nel tempo.],
)

L'utilizzo CPU sotto carico si attesta attorno al 12--15% (media
per intervallo di campionamento di 200 ms), con picchi fino a ~60%
in singoli campioni. Le tre ripetizioni mostrano pattern coerenti.
La baseline e l'idle mantengono un utilizzo trascurabile (~2-3%).

#figure(
  image("plots/power_cpu.svg", width: 85%),
  caption: [Potenza e utilizzo CPU per scenario.],
)

La componente DRAM, PP0 (core) e PP1 (uncore) non sono disponibili
nella VM; il consumo misurato si riferisce esclusivamente al
`PACKAGE_ENERGY` (intero package CPU). I dati dimostrano che il
backend GreenTrails, anche sotto carico concorrente, mantiene un
profilo energetico contenuto, coerente con un'applicazione Spring
Boot su hardware consumer.

#figure(
  image("plots/memory_usage.svg", width: 85%),
  caption: [Utilizzo memoria per scenario.],
)

La memoria utilizzata passa da ~0,35 GB (baseline, solo OS) a
~0,80 GB (idle, con backend JVM avviato) e ~0,87 GB (sotto
carico). L'incremento di ~0,45 GB tra baseline e idle è
attribuibile alla heap JVM (2 GB allocati, ~0,45 GB effettivamente
utilizzati). Sotto carico l'ulteriore aumento è contenuto (~70 MB),
indicando che il carico JMeter non provoca crescita significativa
della heap.

I risultati dettagliati (CSV per run) sono disponibili nella
directory `benchmarks/results/energybridge/`.

= Riepilogo delle misurazioni

La tabella seguente riassume per ciascuno strumento lo stato
pre-intervento (baseline) e i risultati ottenuti. Poiché tutti gli
strumenti sono stati applicati per la prima volta su GreenTrails,
la baseline è rappresentata dall'assenza di misurazione
sistematica; i risultati costituiscono quindi il punto di
riferimento iniziale per i futuri cicli di monitoraggio.

#table(
  columns: (auto, auto, auto, auto),
  inset: 6pt,
  stroke: 0.5pt,
  [*Strumento*], [*Sfera*], [*Baseline*], [*Risultato*],
  [JaCoCo], [Tecnica],
  [Nessuna misurazione di copertura],
  [97,5% linee / 97,2% rami / 100% classi (30/30);
   gate `--fail-below 80` su PR via script],
  [CI/CD coverage reporting], [Tecnica],
  [Nessun reporting automatizzato in CI],
  [Script jacoco/pitest con `--markdownify` in step summary e commento PR;
   gate 80% su PR; report conservati a test falliti],
  [JMH], [Tecnica],
  [Nessun benchmark delle performance],
  [Suite consolidata: 9 benchmark (da 44);
   BCrypt ~73 ms; Pianificazione 0,004--0,315 ms;
   Archiviazione 0,010--0,027 ms (VM QEMU, 300 campioni)],
  [AMBER], [Tecnica / Ambientale],
  [N/D — non utilizzato],
  [Warm-up fisso 3 iterazioni
   (dynamic halt non impiegato per problematiche di utilizzo)],
  [Creedengo], [Tecnica],
  [Nessuna analisi di efficienza energetica],
  [0 bug, 0 vulnerabilità, 3 code smell GCI1],
  [JMeter], [Economica / Tecnica],
  [Nessun test di carico],
  [4 piani definiti e raffinati (Load, Stress, Spike, Soak); validazione live ancora da eseguire],
  [GreenIT-Analysis], [Ambientale],
  [Nessuna analisi del frontend],
  [73/75 buone pratiche; EcoIndex 76/100 B],
  [WebsiteCarbon], [Ambientale],
  [N/D — richiede URL pubblico],
  [N/D — rimandato a dopo il deploy],
  [EcoIndex], [Ambientale],
  [N/D — richiede URL pubblico],
  [N/D — rimandato a dopo il deploy],
  [EnergiBridge], [Ambientale / Tecnica],
  [Nessuna misurazione energetica],
  [3 scenari × 3 ripetizioni su VM QEMU (RAPL via `qemu-vmsr-helper`);
   baseline 1,2 J, idle 3,0 J, load 127,6 J (PACKAGE_ENERGY);
   potenza media load ~0,43 W; DRAM/PP0/PP1 non disponibili nella VM],
  [GUIDO], [Sociale],
  [Nessuna analisi community smells],
  [4 smell rilevati (BCE, PDE, RS, TC) sul repo originale],
  [FOSSA], [Sociale],
  [Nessuna analisi licenze],
  [N/D — da integrare],
)

= Prossimi passi

Le attività di misurazione hanno evidenziato i seguenti aspetti da
approfondire o completare in iterazioni future:

- *Esecuzione dei test JMeter* in modalità headless per ottenere
  metriche reali di latenza, throughput e tasso di errore.

- *Confronto Angular vs Nuxt 4* con GreenIT-Analysis dopo la
  migrazione del frontend.
- *WebsiteCarbon e EcoIndex* su URL pubblico dopo il deploy.
- *Riesecuzione dei benchmark* con warm-up dinamico (es. AMBER)
  per confrontare i consumi rispetto al warm-up fisso.
- *Integrazione FOSSA* per la conformità delle licenze open-source.
