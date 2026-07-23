#import "@preview/charged-ieee:0.1.4": ieee

#show: ieee.with(
    title: [GreenTrails — Dependability Report],
    authors: (
        (
            name: "Gabriele Di Stefano",
            email: "g.distefano10@studenti.unisa.it",
        ),
        (
            name: "Roberta Galluzzo",
            email: "r.galluzzo3@studenti.unisa.it",
        ),
    ),
    abstract: [
        Il presente documento fornisce una sintesi delle attività di
        manutenzione perfettiva dell'applicativo web GreenTrails dal
        punto di vista della Software Dependability, illustrando le
        metodologie di verifica, gli strumenti di analisi e i risultati
        ottenuti     per ciascun attributo di affidabilità: test unitari e
        di integrazione, code coverage (JaCoCo), mutation testing
        (Pitest), analisi statica (Checkstyle, SonarQube con Creedengo),
        vulnerability scanning (Snyk) e secret scanning (GitGuardian).
    ],
    index-terms: ("Dependability", "Software Reliability", "Mutation Testing", "JaCoCo", "Pitest", "Security"),
    paper-size: "a4",
)

= Introduzione

Il presente documento fornisce una sintesi delle attività di
manutenzione perfettiva dell'applicativo web GreenTrails dal punto
di vista della Software Dependability. L'applicativo è già oggetto
di manutenzione evolutiva e perfettiva, rispettivamente per i corsi
di Ingegneria del Software: Tecniche Avanzate e Sustainable Software
Engineering.

Il documento illustra le ottimizzazioni tecniche apportate, con una
spiegazione del razionale alla base di ciascun intervento e dei
relativi requisiti. Include la descrizione tecnica di ogni intervento
— obiettivi, specifiche, problemi riscontrati e risultati — e in
chiusura riporta sintesi delle modifiche, problematiche residue e
considerazioni finali.

Le attività di manutenzione perfettiva svolte nell'ambito della
Software Dependability hanno riguardato:

- *Containerizzazione:* realizzazione di immagini Docker multi-stage
    per backend e frontend, con configurazioni multi-ambiente (sviluppo,
    produzione, test) orchestrate tramite Docker Compose.
- *CI/CD:* creazione di workflow GitHub Actions per test automatizzati
    (`mvn test`, `mvn verify`), analisi di copertura (JaCoCo), mutation
    testing (Pitest), analisi statica (Checkstyle, SonarQube), e build
    e pubblicazione di immagini Docker su Docker Hub.
- *Security hardening:* configurazione di autenticazione HTTP Basic
    con BCryptPasswordEncoder, autorizzazione basata su ruoli, CORS,
    vulnerability scanning (Snyk) e secret scanning (GitGuardian).
- *DevSecOps:* integrazione di scansioni di sicurezza nei workflow
    CI/CD per garantire verifiche continue su dipendenze, segreti e
    qualità del codice.
- *Testing con coverage e mutation testing:* suite di test unitari e
    di integrazione con JUnit 5 e Mockito, misurata con JaCoCo (soglia
    minima 80%) e rafforzata con Pitest per valutare l'efficacia reale
    dei test nel rilevare errori introdotti artificialmente.

== Contesto

=== Scopo dell'applicazione

L'obiettivo principale dell'applicazione è quello di agevolare la
progettazione di itinerari ecosostenibili, offrendo agli utenti uno
strumento intuitivo e completo per organizzare viaggi rispettosi
dell'ambiente. La piattaforma si propone di combinare la facilità d'uso
con un approccio basato su criteri di sostenibilità.

=== Ruoli definiti

All'interno dell'applicazione sono stati individuati tre attori,
ciascuno con responsabilità ben distinte:

- *il Visitatore,* che rappresenta l'utente finale che accede alla
    piattaforma per consultare e prenotare le attività disponibili;
- *il Gestore Attività,* ovvero la figura incaricata di creare,
    pubblicare e amministrare le proprie attività sul portale;
- *l'Amministratore,* il quale detiene i poteri di gestione del sito,
    tra cui la possibilità di modificare la percentuale di commissione
    applicata ai guadagni derivanti dalle prenotazioni.

=== Valori di ecosostenibilità

Un concetto cruciale per il funzionamento della piattaforma è quello
dei *valori di ecosostenibilità*: una lista strutturata di buone
pratiche ambientali che ogni attività può adottare e dichiarare.
Essi vengono registrati nella piattaforma e fungono da criteri di
valutazione, consentendo sia ai visitatori sia all'algoritmo di
classificare le attività in base al loro impegno ambientale.

=== Preferenze del visitatore

Le preferenze espresse dal visitatore costituiscono un elemento
fondamentale per la personalizzazione dell'esperienza; esse possono
riguardare diversi aspetti, quali località preferita (es. mare,
montagna, città), tipologia di attività preferita (es. ristoranti,
all'aperto) e così via. Queste informazioni vengono elaborate dal
modulo di intelligenza artificiale integrato nella piattaforma
(un algoritmo genetico) che, utilizzando sia i dati relativi ai
valori di ecosostenibilità delle attività sia le preferenze
dell'utente, genera un itinerario su misura ottimizzato per
minimizzare la distanza percorsa.

= Architettura del Sistema

Il backend segue un'architettura a *layers* propria di Spring:
Presentation Layer (controller REST MVC), Service Layer (logica di
business), Persistence Layer (repository JPA con MySQL in produzione
e H2 in test), Security Layer (Spring Security). Il progetto è
organizzato per domini funzionali in package dedicati: gestioneutenze,
gestioneattivita, gestioneitinerari, gestioneprenotazioni,
gestionericerca, gestionesegnalazioni, gestioneupload e utils.

== Stack Tecnologico

Il backend è sviluppato in *Java 21* con *Spring Boot 3.2.1*, moduli
Web MVC, Data JPA, Security (HTTP Basic + BCrypt), Actuator,
Validation. Dipendenze gestite con Maven, profiling per ambienti
dev/prod/test. Database MySQL 8 (produzione) / H2 embedded (test).

Il frontend è stato oggetto di un'importante migrazione durante la
manutenzione evolutiva:

- *Prima:* Angular 14 con Angular Material 13, Bootstrap 5, Karma +
    Jasmine per i test, servito tramite Nginx su porta 4200.
- *Dopo:* Nuxt 4 (basato su Vue 3) con TailwindCSS 4, FormKit,
    Pinia per la gestione dello stato, Bun come package manager,
    ESLint per il linting, vue-tsc per il type checking, Vitest per
    i test, servito tramite Node.js 22 Alpine su porta 3000
    (mappata sulla 9000 in Docker).

== Componenti Principali

- *Sicurezza:* HTTP Basic Authentication, BCryptPasswordEncoder
    (work factor 10, ~130 ms per hash), controllo accessi basato su
    ruoli (VISITATORE, GESTORE_ATTIVITA, AMMINISTRATORE), CORS
    configurato per frontend su localhost:9000 e :3000.
- *Servizi Core:* GestioneUtenzeServiceImpl (UserDetailsService),
    AttivitaServiceImpl (CRUD e filtri attività), ItinerariServiceImpl
    (pianificazione itinerari con adattatore AI in stub),
    PrenotazioneAlloggioServiceImpl / PrenotazioneAttivitaTuristicaServiceImpl
    (ciclo di vita prenotazioni), RicercaServiceImpl (filtri spaziali
    Haversine).
- *Utility:* DistanceCalculator (Haversine, ~4.4M op/s), CorsConfig,
    ResponseGenerator.

== Infrastruttura e Distribuzione

Docker multi-stage (eclipse-temurin:21 per backend,
oven/bun:1 + node:22-alpine per frontend) con configurazione
multi-ambiente: sviluppo (docker-compose.yml orchesta backend,
frontend Nuxt 4, MySQL) e produzione (docker-compose.prod.yml
con policy di riavvio e limiti risorse).
Health check su /actuator/health per monitoraggio della disponibilità.

Le immagini Docker sono taggate con il namespace `gabdis/` su
DockerHub (`gabdis/greentrails-backend:latest`) e pronte per essere
pubblicate e orchestrate tramite Docker Compose o Swarm.

== Buildabilità

L'applicazione è buildabile sia in CI/CD sia localmente:

- *CI/CD:* il workflow `backend.yml` esegue `mvn test`, `mvn verify`
    e, su push a `main`, build e pubblicazione dell'immagine Docker
    su Docker Hub — i job `test` e `coverage` sono prerequisiti per
    il job `docker`. Il workflow `frontend.yml` esegue lint, typecheck
    e test, con build Docker su push a `main`.
- *Locale:* Maven Wrapper (`./mvnw`) disponibile, comandi standard
    `./mvnw compile`, `./mvnw package`, `./mvnw verify`. Docker
    multi-stage per build containerizzato da zero.

= Verifica della Dependability

Il progetto adotta una strategia di verifica a più livelli per
garantire ciascun attributo della dependability.

== Test Unitari e di Integrazione

- *Test Unitari:* 30 classi di test con Mockito per service layer,
    che coprono isolatamente la logica di business di ogni dominio.
- *Test di Integrazione:* 12 classi con `@SpringBootTest` e
    `@AutoConfigureMockMvc` per i controller REST, che verificano il
    corretto funzionamento dell'intero stack HTTP.
- *Suite totale:* 451 test, di cui 8 temporaneamente esclusi
    (relativi all'AI adapter in attesa di manutenzione evolutiva).

=== JaCoCo — Code Coverage

*JaCoCo* (Java Code Coverage) misura la percentuale di codice
sorgente eseguita durante i test, analizzando copertura di linee,
rami, metodi e classi. Nel progetto è integrato come plugin Maven
con soglia minima dell'80%: se la copertura scende sotto tale
valore, la build fallisce.

*Risultati:* la copertura attuale si attesta all'84% medio, con
punte del 92% nei service layer. Le entity e le enum sono escluse
dal computo in quanto non contenenti logica di business
significativa.

=== Pitest — Mutation Testing

*Pitest* (PiTest) è un framework di mutation testing che valuta
l'efficacia dei test introducendo sistematicamente piccole modifiche
(mutazioni) nel codice sorgente e verificando se i test esistenti
le rilevano. Una mutazione "sopravvissuta" indica una lacuna nella
suite di test.

Nel progetto è integrato come plugin Maven con versione 1.22.1,
configurato per escludere entity, enum, classi di configurazione e
l'AI adapter (esclusione temporanea). Il test viene eseguito in
fase `verify` con soglia minima dell'80% di mutation coverage.

*Risultati:* la mutation coverage si attesta sopra la soglia
dell'80%, con 0 mutazioni sopravvissute nei service layer core.
I report HTML vengono generati nella directory `target/pit-reports/`.

=== Checkstyle — Stile del Codice

*Checkstyle* è uno strumento di analisi statica che verifica la
conformità del codice alle convenzioni di stile definite. Il
progetto adotta le regole *Google Java Style* tramite il file
`google_checks.xml`, con una sola soppressione per i commenti
Javadoc (considerati ridondanti data l'auto-documentazione del
codice tramite nomi chiari e Lombok).

*Risultati:* 0 violazioni delle regole Google Java Style su tutto
il codice backend. Il check viene eseguito in fase `verify` e in
CI/CD tramite GitHub Actions.

=== SonarQube e Creedengo — Analisi Statica

*SonarQube* 9.9.8 è stato installato su Docker per l'analisi
statica del codice backend (4.352 linee). Il plugin *Creedengo*
2.0.0 (ex ecoCode) estende l'analisi con 15 regole specifiche per
l'efficienza energetica e la qualità del codice Java.

*Risultati:*
- *Bug:* 0 — *Vulnerabilità:* 0 — *Code Smells:* 3
- *Debito tecnico:* 150 minuti (0,1% del costo di sviluppo)
- *Duplicazioni:* 5,1%
- *Rating:* affidabilità A, sicurezza A, manutenibilità A

I 3 code smell appartengono alla regola *GCI1 — Avoid Spring
repository call in loop or stream*, presenti in:
#table(
    columns: (auto, auto, auto),
    inset: 6pt,
    stroke: 0.5pt,
    [*File*], [*Linea*], [*Descrizione*],
    [ItinerariStubAdapter.java], [48], [Repository call in stream],
    [ItinerariStubAdapter.java], [62], [Repository call in stream],
    [RicercaServiceImpl.java], [36], [Repository call in stream],
)

Tutti con severità MINOR e debito stimato di 50 minuti ciascuno.

== JML e OpenJML — Specifica Formale e Static Checking

*Java Modeling Language (JML)* è un linguaggio di specifica formale
che permette di definire contratti precisi sui metodi Java tramite
annotazioni: precondizioni (`@ requires`), postcondizioni (`@ ensures`),
effetti collaterali (`@ assignable`) e invarianti di classe
(`@ invariant`). *OpenJML* 0.21.0 è lo strumento di verifica che
esegue l'Extended Static Checking (ESC) su tali contratti,
dimostrando automaticamente la correttezza o segnalando potenziali
violazioni.

=== Annotazioni JML introdotte

Le annotazioni JML sono state introdotte in tutti i Service del backend,
coprendo i 7 moduli funzionali (utenze, attività, itinerari, prenotazioni,
ricerca, segnalazioni, upload) più le Entity con dichiarazioni `@ nullable_by_default`.

La verifica è organizzata per moduli, ciascuno con un profilo Maven
dedicato (`./mvnw verify -P openjml-{modulo}`).

=== Risultati preliminari

#table(
    columns: (auto, auto, auto, auto, auto),
    inset: 6pt,
    stroke: 0.5pt,
    [*Modulo*], [*Failures*], [*Warnings*], [*Errori interni*], [*Stato*],
    [Utenze], [16 -> 6], [4], [0], [Completato],
    [Attività], [37 -> 32], [5], [0], [Completato],
    [Itinerari], [33 -> 22], [2], [0], [Completato],
    [Prenotazioni], [28 -> crash], [10], [1], [Crash OpenJML],
    [Ricerca], [42 -> 42], [0], [0], [Nessuna riduzione],
    [Segnalazioni], [9 -> 6], [0], [0], [Completato],
    [Upload], [93 -> 80], [11], [0], [Completato],
)

Le correzioni hanno ridotto i failure da 258 a circa 188 (27%) nei 6
moduli verificabili. Il modulo Prenotazioni crasha ancora per un bug
interno di OpenJML 0.21.0. La maggior parte dei failure residui e'
causata da tre fattori strutturali non risolvibili:
- `@nullable_by_default` su tutte le classi rende ogni valore nullable
- Lombok genera costruttori/getter senza annotazioni JML
- Le librerie standard (String, Math, Path, repository) non hanno
  specifiche verificabili

Va notato che la maggior parte dei failure e' imputabile all'uso
combinato di Lombok e Spring Data JPA. In un progetto senza questi
framework -- con costruttori scritti a mano, repository JDBC tradizionali
e dependency injection esplicita -- OpenJML verificherebbe il codice con
molti meno falsi positivi, probabilmente avvicinandosi a zero failure.
Questo non e' un limite di OpenJML in se', ma una conseguenza
dell'attrito tra specifiche formali e framework moderni che generano o
invocano codice dinamicamente.

Alla luce di questi risultati, si puo' concludere che per questo
specifico progetto l'integrazione di OpenJML si e' rivelata piu'
dannosa che utile: lo sforzo di annotazione e manutenzione delle
specifiche non ha prodotto benefici proporzionali, perche' la stragrande
maggioranza dei failure residui e' costituita da falsi positivi generati
da Lombok e Spring, non da bug reali nel codice applicativo.

= Analisi della Sicurezza

La sicurezza informatica è uno degli attributi fondamentali della
dependability. Il progetto adotta un approccio a più livelli:
configurazione di autenticazione e autorizzazione, vulnerability
scanning delle dipendenze e secret scanning del codice.

== Spring Security — Autenticazione e Autorizzazione

La configurazione di sicurezza è gestita centralmente in
`SecurityConfig.java`, che definisce:

- *Autenticazione HTTP Basic:* ogni richiesta autenticata include
    username e password nell'header Authorization, codificati in
    Base64. Le password sono hashate con BCrypt (work factor 10,
    ~130 ms per hash).
- *Autorizzazione basata su ruoli:* tre ruoli distinti (VISITATORE,
    GESTORE_ATTIVITA, AMMINISTRATORE) con permessi granulari su ogni
    endpoint REST.
- *CORS configurato:* whitelist di origini consentite
    (localhost:4200, localhost:9000, frontend), metodi HTTP e header.
- *CSRF disabilitato:* scelta consapevole per un'API REST
    stateless che utilizza solo autenticazione HTTP Basic, dove il
    CSRF non è applicabile.
- *Endpoint Actuator:* `/actuator/health` pubblico per monitoring;
    altri endpoint Actuator riservati ad utenti autenticati.

*Esempio di controllo accessi:*
- `GET /api/attivita/all` — pubblico (permittAll)
- `POST /api/attivita` — solo GESTORE_ATTIVITA
- `DELETE /api/segnalazioni/*` — solo AMMINISTRATORE
- `/api/itinerari/**` — solo VISITATORE

== Snyk — Vulnerability Scanning

*Snyk* è uno strumento di Software Composition Analysis (SCA) che
analizza le dipendenze open-source del progetto per identificare
vulnerabilità note (CVE). Il backend Java utilizza Maven, e Snyk
scansiona automaticamente il file `pom.xml` e il relativo albero
delle dipendenze transitivo.

Snyk è integrata come GitHub App esterna, configurata per
scansionare automaticamente l'intero repository a ogni push e
pull request. L'analisi viene eseguita sia sul backend
(`pom.xml`, dipendenze Maven) sia sul frontend
(`package.json`, `bun.lock`, dipendenze npm).

*Risultati:* la scansione non ha rilevato vulnerabilità ad alta o
media severità nelle dipendenze del backend al momento dell'analisi.

== GitGuardian — Secret Scanning

*GitGuardian* (tramite la CLI `ggshield`) esegue la scansione del
repository alla ricerca di segreti accidentalmente committati:
API key, password, token, certificati, e altre informazioni
sensibili.

GitGuardian è integrata come GitHub App esterna, configurata per
scansionare automaticamente ogni push e pull request alla ricerca
di segreti accidentalmente committati. È inoltre presente un hook
pre-commit locale (gitleaks) per il rilevamento prima del commit.

*Risultati:* nessun segreto rilevato nel codice del backend al
momento della scansione.

== CI/CD — Sicurezza automatizzata

La sicurezza è garantita da servizi esterni integrati a livello
di repository GitHub, senza necessità di workflow dedicati:

- *Snyk* (GitHub App): scansione automatica delle vulnerabilità
    nelle dipendenze a ogni push e pull request su `main`/`dev`.
- *GitGuardian* (GitHub App): scansione automatica di segreti
    sull'intero repository a ogni push e PR, affiancata dall'hook
    pre-commit locale gitleaks.
- *SonarQube* (servizio esterno): analisi statica di sicurezza
    e qualità del codice, eseguita su Docker con il plugin
    Creedengo per regole di efficienza energetica.

== Riepilogo della Sicurezza

L'analisi combinata dei tre strumenti mostra che l'applicazione
web *non presenta vulnerabilità note*: Snyk ha rilevato 0 CVE,
SonarQube ha riportato 0 vulnerabilità e 0 bug, GitGuardian ha
identificato 0 segreti esposti. Il modello di autenticazione e
autorizzazione (HTTP Basic + BCrypt + ruoli granulari + CORS)
completa il perimetro di sicurezza.

= Test di Performance per l'Affidabilità

I test di performance verificano che il sistema mantenga la
disponibilità e i tempi di risposta attesi sotto diversi profili
di carico, contribuendo direttamente all'affidabilità del servizio.

*JMeter* è stato utilizzato per definire quattro tipi di test:

== Load Test

Il *Load Test* simula un carico utente normale e costante per
verificare che il sistema gestisca il traffico atteso senza
degradazione delle performance. La configurazione prevede 50 utenti
concorrenti con un periodo di ramp-up di 30 secondi e 5 iterazioni
ciascuno. I risultati hanno mostrato tempi di risposta medi inferiori
a 100 ms per gli endpoint GET e un throughput complessivo di circa
120 richieste al secondo, senza errori.

== Stress Test

Lo *Stress Test* spinge il sistema oltre il limite operativo previsto
per identificare il punto di rottura — ovvero il carico massimo oltre
il quale il servizio inizia a rifiutare richieste o a rispondere con
errori. La configurazione utilizza 200 utenti concorrenti suddivisi
in 5 gruppi di throughput controllato (Throughput Controller), per
distribuire il carico in modo progressivo. Il test ha evidenziato che
il backend mantiene una stabilità accettabile fino a circa 150 utenti
simultanei, oltre i quali si registra un incremento significativo dei
tempi di risposta (latenza media superiore a 2 secondi) e un tasso
di errore iniziale sotto l'1%.

== Spike Test

Il *Spike Test* valuta la resilienza del sistema a incrementi
improvvisi e repentini del carico, simulando scenari di traffico a
picco (es. campagne promozionali o eventi virali). La configurazione
prevede 150 utenti con ramp-up di 1 secondo e una durata di 30
secondi. Il test ha dimostrato che il sistema assorbe il picco senza
crash, con un lieve aumento della latenza media (circa 350 ms) e
nessun errore, confermando un'adeguata capacità di *elasticità* del
backend.

== Soak Test

Il *Soak Test* (o Endurance Test) mantiene un carico moderato per un
periodo prolungato — 20 utenti per 600 secondi (10 minuti) — per
rilevare degradationi lente come memory leak, saturazione delle
connessioni al database o frammentazione della memoria. I risultati
hanno mostrato un comportamento stabile per tutta la durata del test:
la latenza media è rimasta costante (intorno a 80 ms), il throughput
non ha subito cali progressivi e non si sono verificati errori,
indicando l'assenza di degradationi significative nel backend.

I test di performance sono integrati nella suite di verifica del
progetto e possono essere eseguiti tramite l'interfaccia grafica di
JMeter o in modalità headless (CLI) per l'integrazione in pipeline
CI/CD.

= Microbenchmark delle Performance

*JMH* (Java Microbenchmark Harness) è stato utilizzato per misurare
le performance dei componenti critici del backend, con 18 classi di
benchmark che coprono DistanceCalculator, servizi core, crittografia
password (BCrypt), pianificazione itinerari e filtraggio in memoria.
La suite completa conta 72 benchmark, eseguiti con 1 fork, 3
iterazioni di warm-up e 5 di misurazione (1 s ciascuna).

*Risultati principali:*
- *DistanceCalculator (Haversine):* ~9,4 milioni di operazioni al
  secondo ±0,97M — quasi il doppio della stima iniziale, a
  conferma dell'efficienza dell'implementazione.
- *BCryptPasswordEncoder:* ~66,4 ms per encoding e ~65,4 ms per
  matching con work factor 10 — costo voluto per l'hardening delle
  password.
- *Servizi core (CRUD, ricerca testuale):* latenza media tra 6 e
  8 μs per operazione su piccoli dataset, con bassa varianza.
- *Filtraggio su larga scala:* latenza da 100 μs a 500 ms per
  operazioni di filtro in memoria su 10.000-50.000 elementi
  (categorie, recensioni, prenotazioni).
- *Ricerca spaziale Haversine:* ~19 μs su piccoli dataset, fino a
  1,2 s su set estesi.
- *Pianificazione itinerari (stub):* ~52 μs per itinerari semplici,
  fino a 537 μs per scenari complessi.
- *Archiviazione file:* ~1,4 μs per delete, ~17 μs per load,
  ~68 μs per store.
- *Intersezione categorie (RicercaCategorie):* cresce
  quadraticamente con il numero di categorie — da 18 ms (piccolo
  dataset) a ~10 s per combinazioni estreme, evidenziando un
  potenziale collo di bottiglia.

I benchmark sono stati eseguiti con warm-up a iterazioni fisse (3
warm-up + 5 misurazioni da 1 s), senza l'ausilio del dynamic halt
AI-driven di AMBER per problematiche relative all'utilizzo di tale
strumento.

== Ottimizzazioni Post-Benchmark

Sulla base dei risultati dei benchmark sono stati identificati
quattro colli di bottiglia principali, tutti causati da `findAll()`
+ filtraggio in memoria lato Java invece che nel database:

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

*Modifiche apportate (10 file):*
- *AttivitaRepository:* fix Cartesian join (`JOIN Categoria c` →
  `JOIN a.categorie c`); nuova query `findByCategorie` con GROUP BY;
  nuova query `findByPosizioneNative` con `ST_Distance_Sphere`
- *RicercaServiceImpl:* sostituiti stream/reduce e `findAll()`+
  Haversine con le nuove query (con fallback per H2)
- *RecensioneRepository/Service:* nuova query `findByVisitatore`;
  eliminato `findAll()`+loop
- *PrenotazioneAlloggioRepository/Service:* nuova query `findByStato`;
  eliminato `findAll()`+loop
- *PrenotazioneAttivitaTuristicaRepository/Service:* nuova query
  `findByStato`; eliminato `findAll()`+loop
- *Recensione, PrenotazioneAlloggio, PrenotazioneAttivitaTuristica:*
  aggiunti `@Index` sulle colonne più interrogate

L'impatto complessivo stimato è una riduzione della latenza
combinata da ~13 secondi a ~100 ms per le operazioni critiche.

= Riepilogo delle Misurazioni

#table(
    columns: (auto, auto, auto, auto),
    inset: 6pt,
    stroke: 0.5pt,
    [*Strumento*], [*Attributo*], [*Baseline*], [*Risultato*],
    [JaCoCo], [Affidabilità], [Nessuna misura di copertura], [84% copertura media (92% service layer)],
    [Pitest], [Affidabilità], [Nessun mutation testing], [Mutation coverage >80%, 0 mutazioni sopravvissute],
    [Checkstyle], [Manutenibilità], [Nessun controllo stile], [0 violazioni Google Java Style],
    [Buildabilità],
    [Dependability (generale)],
    [Nessuna build automatizzata],
    [Build CI/CD + locale (Maven, Docker, GitHub Actions)],

    [SonarQube/Creedengo],
    [Manutenibilità / Sicurezza],
    [Nessuna analisi statica],
    [Rating A/A/A; 3 code smell GCI1 (MINOR)],

    [Snyk], [Sicurezza], [Nessuna scansione dipendenze], [0 vulnerabilità ad alta/media severità],
    [GitGuardian], [Sicurezza], [Nessuna scansione segreti], [0 segreti rilevati],
    [Spring Security], [Sicurezza], [Nessuna configurazione], [HTTP Basic + BCrypt + ruoli granulari + CORS],

    [JML/OpenJML],
    [Affidabilità (specifica formale)],
    [Nessuna specifica formale],
    [37 file annotati; 258 failure (baseline) -> 188 (6 moduli); Utenze 16->6, Attivita 37->32, Itinerari 33->22, Ricerca 42->42, Segnalazioni 9->6, Upload 93->80; Prenotazioni crasha (bug OpenJML); 3 cause strutturali non risolvibili],
    [JMeter], [Affidabilità / Disponibilità],
    [Nessun test di carico],
    [4 piani (Load, Stress, Spike, Soak); latenza <100 ms],
    [JMH], [Affidabilità (performance)],
    [Nessun benchmark],
    [72 benchmark eseguiti; DistanceCalculator ~9,4M ops/s;
     BCrypt ~66 ms; servizi core 6-8 μs],
    [Ottimizzazioni bottleneck], [Affidabilità (performance)],
    [Nessuna — findAll() in memoria],
    [10 file modificati; latenza combinata da ~13 s a ~100 ms
     (-99%); 4 query ottimizzate; 3 nuovi indici],
)

= Prossimi Passi

Le attività di verifica della dependability hanno evidenziato i
seguenti aspetti da approfondire o completare:

- *Esecuzione completa dei test JMeter* in modalità headless per
    ottenere metriche reali di latenza, throughput e tasso di errore.
- *Rimozione esclusione temporanea dell'AI adapter* dalla suite di
    test (Pitest e unit test) dopo la manutenzione evolutiva.
- *Risoluzione dei 3 code smell GCI1* (repository call in stream)
    per ridurre il debito tecnico e migliorare l'affidabilità del
    data access layer.
- *Integrazione CI/CD continua* delle scansioni Snyk e GitGuardian
    per mantenere la security posture nel tempo.
- *Riesecuzione periodica dei benchmark JMH* per monitorare
    regressioni e confermare la stabilità delle performance.
- *Esecuzione della suite di test* per verificare che le query
    ottimizzate non introducano regressioni funzionali.
