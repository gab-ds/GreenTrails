import { beforeAll, describe, expect, it, vi } from 'vitest'
import { mountSuspended } from '@nuxt/test-utils/runtime'

import AreaRiservata from '~/pages/area-riservata.vue'
import Questionario from '~/pages/questionario.vue'
import ListaSegnalazioni from '~/pages/lista-segnalazioni.vue'
import ModificaValoriAdmin from '~/pages/modifica-valori-admin.vue'
import MieAttivita from '~/pages/mie-attivita.vue'
import InserimentoAttivita from '~/pages/inserimento-attivita.vue'
import AttivitaId from '~/pages/attivita/[id].vue'
import TabellaPrenotazioni from '~/pages/tabella-prenotazioni.vue'
import IMieiViaggi from '~/pages/i-miei-viaggi.vue'
import ItinerarioAutomatico from '~/pages/itinerario-automatico.vue'

vi.mock('~/stores/auth', () => ({
  useAuthStore: vi.fn(() => ({
    user: { id: 1, nome: 'Mario', cognome: 'Rossi', email: 'm.rossi@example.com', ruolo: 'VISITATORE' },
    isLoggedIn: true,
    isVisitatore: true,
    isGestore: false,
    isAdmin: false,
    logout: vi.fn(),
  })),
}))

vi.mock('~/composables/useApi', () => ({
  useApi: () => ({
    auth: {
      invioQuestionario: vi.fn().mockResolvedValue({ status: 'success' }),
      getPreferenze: vi.fn().mockResolvedValue({ status: 'success', data: { viaggioPreferito: 'MARE', alloggioPreferito: 'HOTEL', preferenzaAlimentare: 'NESSUNA_PREFERENZA', attivitaPreferita: 'RELAX', animaleDomestico: false, budgetPreferito: 'MEDIO', souvenir: true, stagioniPreferite: 'PRIMAVERA_ESTATE' } }),
    },
    activities: {
      list: vi.fn().mockResolvedValue({ status: 'success', data: [
        { id: 1, nome: 'Hotel Test', citta: 'Salerno', provincia: 'SA', prezzo: 80, isAlloggio: true, categoriaAlloggio: 'HOTEL', descrizioneBreve: 'Un bell\'hotel', media: 'abc' },
        { id: 2, nome: 'Escursione', citta: 'Amalfi', provincia: 'SA', prezzo: 25, isAlloggio: false, categoriaAttivitaTuristica: 'RELAX', descrizioneBreve: 'Escursione rilassante', media: 'def' },
        { id: 3, nome: 'Museo', citta: 'Napoli', provincia: 'NA', prezzo: 60, isAlloggio: false, categoriaAttivitaTuristica: 'VISITE_CULTURALI_STORICHE', descrizioneBreve: 'Tour guidato', media: 'ghi' },
      ] }),
      myActivities: vi.fn().mockResolvedValue({ status: 'success', data: [
        { id: 1, nome: 'Hotel Test', citta: 'Salerno', provincia: 'SA', prezzo: 80, alloggio: true },
        { id: 2, nome: 'Escursione', citta: 'Amalfi', provincia: 'SA', prezzo: 25, alloggio: false },
      ] }),
      get: vi.fn().mockResolvedValue({ status: 'success', data: { id: 1, nome: 'Hotel Test', indirizzo: 'Via Roma 1', cap: '84100', citta: 'Salerno', provincia: 'SA', descrizioneBreve: 'Un bell\'hotel', descrizioneLunga: 'Descrizione lunga', prezzo: 80, alloggio: true, disponibilita: 10, coordinate: { x: 14.77, y: 40.68 }, valoriEcosostenibilita: { politicheAntispreco: true, prodottiLocali: false, energiaVerde: true, raccoltaDifferenziata: true, limiteEmissioneCO2: false, contattoConNatura: true } } }),
      create: vi.fn().mockResolvedValue({ status: 'success', data: { id: 99 } }),
      delete: vi.fn().mockResolvedValue({}),
    },
    recensioni: {
      perAttivita: vi.fn().mockResolvedValue({ status: 'success', data: [{ id: 1, titolo: 'Bella esperienza', descrizione: 'Bellissimo posto', valutazioneStelleEsperienza: 5, visitatore: { id: 1, nome: 'Mario' } }] }),
      create: vi.fn().mockResolvedValue({ status: 'success' }),
      delete: vi.fn().mockResolvedValue({ status: 'success' }),
    },
    camere: {
      perAlloggio: vi.fn().mockResolvedValue({ status: 'success', data: [{ id: 1, tipoCamera: 'Doppia', disponibilita: 5, descrizione: 'Camera doppia con vista mare', capienza: 2, prezzo: 80 }] }),
    },
    segnalazioni: {
      list: vi.fn().mockResolvedValue({ status: 'success', data: [
        { id: 1, descrizione: 'Test descrizione', utente: { email: 'test@example.com' }, attivita: { id: 5, nome: 'Attività test' } },
      ] }),
      create: vi.fn().mockResolvedValue({ status: 'success' }),
    },
    upload: {
      list: vi.fn().mockResolvedValue(['foto1.jpg']),
    },
    prenotazioniAlloggio: {
      miePrenotazioni: vi.fn().mockResolvedValue({ status: 'success', data: [
        { id: 10, numAdulti: 2, numBambini: 1, dataInizio: '2025-07-01T00:00:00.000+00:00', dataFine: '2025-07-05T00:00:00.000+00:00', numCamere: 1, stato: 'CREATA', statoPagamento: 'IN_CORSO', prezzo: 320, camera: { tipoCamera: 'Doppia', prezzo: 80, alloggio: { id: 1, nome: 'Hotel Test' } }, itinerario: { visitatore: { nome: 'Mario', cognome: 'Rossi', email: 'm.rossi@example.com' } } },
      ] }),
      disponibilita: vi.fn().mockResolvedValue({ status: 'success', data: 5 }),
      create: vi.fn().mockResolvedValue({ status: 'success' }),
      perAttivita: vi.fn().mockResolvedValue({ status: 'success', data: [
        { id: 10, numAdulti: 2, numBambini: 1, dataInizio: '2025-07-01T00:00:00.000+00:00', dataFine: '2025-07-05T00:00:00.000+00:00', numCamere: 1, stato: 'CREATA', statoPagamento: 'IN_CORSO', prezzo: 320, camera: { tipoCamera: 'Doppia', prezzo: 80 }, itinerario: { visitatore: { nome: 'Mario', cognome: 'Rossi', email: 'm.rossi@example.com' } } },
      ] }),
    },
    prenotazioniAttivita: {
      miePrenotazioni: vi.fn().mockResolvedValue({ status: 'success', data: [
        { id: 20, numAdulti: 3, numBambini: 0, dataInizio: '2025-08-10T00:00:00.000+00:00', dataFine: null, stato: 'COMPLETATA', statoPagamento: 'COMPLETATO', prezzo: 75, attivitaTuristica: { id: 2, nome: 'Escursione' } },
      ] }),
      disponibilita: vi.fn().mockResolvedValue({ status: 'success', data: 10 }),
      create: vi.fn().mockResolvedValue({ status: 'success' }),
      perAttivita: vi.fn().mockResolvedValue({ status: 'success', data: [
        { id: 20, numAdulti: 3, numBambini: 0, dataInizio: '2025-08-10T00:00:00.000+00:00', dataFine: null, stato: 'COMPLETATA', statoPagamento: 'COMPLETATO', prezzo: 75, itinerario: { visitatore: { nome: 'Luigi', cognome: 'Verdi', email: 'l.verdi@example.com' } } },
      ] }),
    },
    itinerari: {
      create: vi.fn().mockResolvedValue({ status: 'success', data: { id: 100 } }),
      genera: vi.fn().mockResolvedValue({ status: 'success', data: { id: 100, stato: 'PIANIFICATO', totale: 0 } }),
      get: vi.fn().mockResolvedValue({ status: 'success', data: { itinerario: { id: 100, stato: 'PIANIFICATO', totale: 395 }, prenotazioniAlloggio: [], prenotazioniAttivitaTuristica: [] } }),
    },
    valori: {
      list: vi.fn().mockResolvedValue([
        { id: 1, nome: 'Antispreco', valoreCorrente: true, descrizione: 'Politica antispreco' },
        { id: 2, nome: 'Energia Verde', valoreCorrente: false, descrizione: 'Politica energia verde' },
        { id: 3, nome: 'Raccolta Differenziata', valoreCorrente: true, descrizione: 'Politica raccolta differenziata' },
      ]),
      create: vi.fn().mockResolvedValue({ status: 'success', data: { id: 1 } }),
      update: vi.fn().mockResolvedValue({}),
    },
  }),
}))

describe('AreaRiservata', () => {
  it('mostra il titolo Area Riservata', async () => {
    const wrapper = await mountSuspended(AreaRiservata)
    expect(wrapper.text()).toContain('Area Riservata')
  })

  it('mostra le iniziali del profilo utente', async () => {
    const wrapper = await mountSuspended(AreaRiservata)
    expect(wrapper.text()).toContain('MR')
  })

  it('mostra nome e cognome dell\'utente', async () => {
    const wrapper = await mountSuspended(AreaRiservata)
    expect(wrapper.text()).toContain('Mario')
    expect(wrapper.text()).toContain('Rossi')
  })

  it('mostra le card per visitatore', async () => {
    const wrapper = await mountSuspended(AreaRiservata)
    expect(wrapper.text()).toContain('Compila questionario')
    expect(wrapper.text()).toContain('Suggeriti per te')
    expect(wrapper.text()).toContain('I miei viaggi')
    expect(wrapper.text()).toContain('Genera itinerario')
  })
})

describe('AreaRiservata — ruolo gestore', () => {
  it('mostra le card per gestore', async () => {
    vi.mocked(useAuthStore).mockReturnValue({
      user: { id: 2, nome: 'Luigi', cognome: 'Verdi', email: 'l.verdi@example.com', ruolo: 'GESTORE_ATTIVITA' },
      isLoggedIn: true,
      isVisitatore: false,
      isGestore: true,
      isAdmin: false,
      logout: vi.fn(),
    })
    const wrapper = await mountSuspended(AreaRiservata)
    expect(wrapper.text()).toContain('Le mie attività')
    expect(wrapper.text()).toContain('Prenotazioni')
    expect(wrapper.text()).toContain('Inserisci attività')
    expect(wrapper.text()).not.toContain('Compila questionario')
  })
})

describe('AreaRiservata — ruolo admin', () => {
  it('mostra la card segnalazioni per admin', async () => {
    vi.mocked(useAuthStore).mockReturnValue({
      user: { id: 3, nome: 'Sofia', cognome: 'Neri', email: 's.neri@example.com', ruolo: 'AMMINISTRATORE' },
      isLoggedIn: true,
      isVisitatore: false,
      isGestore: false,
      isAdmin: true,
      logout: vi.fn(),
    })
    const wrapper = await mountSuspended(AreaRiservata)
    expect(wrapper.text()).toContain('Segnalazioni')
    expect(wrapper.text()).not.toContain('Compila questionario')
    expect(wrapper.text()).not.toContain('Le mie attività')
  })
})

describe('Questionario', () => {
  it('mostra il titolo Compilazione Questionario', async () => {
    const wrapper = await mountSuspended(Questionario)
    expect(wrapper.text()).toContain('Compilazione Questionario')
  })

  it('mostra la prima domanda con le opzioni a icone', async () => {
    const wrapper = await mountSuspended(Questionario)
    expect(wrapper.text()).toContain('Dove preferisci viaggiare?')
    expect(wrapper.text()).toContain('Montagna')
    expect(wrapper.text()).toContain('Mare')
    expect(wrapper.text()).toContain('Città')
    expect(wrapper.text()).toContain('Indifferente')
  })

  it('mostra il contatore e la barra di progresso', async () => {
    const wrapper = await mountSuspended(Questionario)
    expect(wrapper.text()).toContain('Domanda 1 di 8')
    expect(wrapper.text()).toContain('13%')
  })

  it('il pulsante Avanti è presente sulla prima domanda', async () => {
    const wrapper = await mountSuspended(Questionario)
    expect(wrapper.text()).toContain('Avanti')
  })

  it('naviga alla domanda successiva dopo aver selezionato', async () => {
    const wrapper = await mountSuspended(Questionario)
    const firstOption = wrapper.findAll('button[type="button"]')[0]
    await firstOption.trigger('click')
    const _nextBtn = wrapper.findAll('span').find(el => el.text() === 'Avanti')
    // click on the parent button
    const buttons = wrapper.findAll('button')
    const avanti = buttons.filter(b => b.text().includes('Avanti'))
    if (avanti.length > 0) {
      await avanti[0].trigger('click')
      await new Promise(r => setTimeout(r, 50))
      expect(wrapper.text()).toContain('Domanda 2 di 8')
    }
  })

  it('mostra Invia sull\'ultima domanda', async () => {
    const wrapper = await mountSuspended(Questionario)
    const buttons = wrapper.findAll('button')
    // Navigate through all 8 questions by selecting + clicking Avanti
    for (let i = 0; i < 7; i++) {
      const opts = wrapper.findAll('button[type="button"]')
      if (opts.length > 0) await opts[0].trigger('click')
      const avanti = buttons.filter(b => b.text().includes('Avanti'))
      if (avanti.length > 0) await avanti[0].trigger('click')
      await new Promise(r => setTimeout(r, 30))
    }
    // Now on last step
    expect(wrapper.text()).toContain('Domanda 8 di 8')
    expect(wrapper.text()).toContain('Invia')
  })
})

describe('ListaSegnalazioni', () => {
  it('mostra il titolo Lista segnalazioni', async () => {
    const wrapper = await mountSuspended(ListaSegnalazioni)
    expect(wrapper.text()).toContain('Lista segnalazioni')
  })

  it('mostra la segnalazione nel corpo della tabella', async () => {
    const wrapper = await mountSuspended(ListaSegnalazioni)
    await new Promise(r => setTimeout(r, 100))
    expect(wrapper.text()).toContain('Test descrizione')
    expect(wrapper.text()).toContain('Attività test')
  })
})

describe('ModificaValoriAdmin', () => {
  it('mostra il titolo corretto', async () => {
    const wrapper = await mountSuspended(ModificaValoriAdmin)
    expect(wrapper.text()).toContain('Modifica valori di ecosostenibilità')
  })

  it('mostra errore se ID non presente', async () => {
    const wrapper = await mountSuspended(ModificaValoriAdmin)
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.text()).toContain('ID attività non valido')
  })
})

describe('MieAttivita', () => {
  beforeAll(() => {
    vi.mocked(useAuthStore).mockReturnValue({
      user: { id: 2, nome: 'Luigi', cognome: 'Verdi', email: 'l.verdi@example.com', ruolo: 'GESTORE_ATTIVITA' },
      isLoggedIn: true,
      isVisitatore: false,
      isGestore: true,
      isAdmin: false,
      logout: vi.fn(),
    })
  })

  it('mostra il titolo Le mie attività', async () => {
    const wrapper = await mountSuspended(MieAttivita)
    expect(wrapper.text()).toContain('Le mie attività')
  })

  it('mostra il pulsante Nuova attività', async () => {
    const wrapper = await mountSuspended(MieAttivita)
    expect(wrapper.text()).toContain('Nuova attività')
  })

  it('mostra la lista delle attività', async () => {
    const wrapper = await mountSuspended(MieAttivita)
    await new Promise(r => setTimeout(r, 100))
    expect(wrapper.text()).toContain('Hotel Test')
    expect(wrapper.text()).toContain('Escursione')
  })

  it('mostra il tipo alloggio e attività turistica', async () => {
    const wrapper = await mountSuspended(MieAttivita)
    await new Promise(r => setTimeout(r, 100))
    expect(wrapper.text()).toContain('Alloggio')
    expect(wrapper.text()).toContain('Attività Turistica')
  })
})

describe('AttivitaId', () => {
  beforeAll(() => {
    vi.mocked(useAuthStore).mockReturnValue({
      user: { id: 1, nome: 'Mario', cognome: 'Rossi', email: 'm.rossi@example.com', ruolo: 'VISITATORE' },
      isLoggedIn: true,
      isVisitatore: true,
      isGestore: false,
      isAdmin: false,
      logout: vi.fn(),
    })
  })

  it('mostra il nome dell\'attività', async () => {
    const wrapper = await mountSuspended(AttivitaId, { route: { params: { id: '1' } } })
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Hotel Test')
  })

  it('mostra la sezione descrizione', async () => {
    const wrapper = await mountSuspended(AttivitaId, { route: { params: { id: '1' } } })
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Descrizione')
  })

  it('mostra le politiche eco-sostenibili attive', async () => {
    const wrapper = await mountSuspended(AttivitaId, { route: { params: { id: '1' } } })
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Antispreco')
    expect(wrapper.text()).toContain('Energia Verde')
    expect(wrapper.text()).toContain('Raccolta Differenziata')
    expect(wrapper.text()).toContain('Contatto con Natura')
  })

  it('mostra la sezione recensioni', async () => {
    const wrapper = await mountSuspended(AttivitaId, { route: { params: { id: '1' } } })
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Recensioni')
    expect(wrapper.text()).toContain('Mario')
    expect(wrapper.text()).toContain('Bellissimo posto')
  })

  it('mostra il pulsante Aggiungi recensione per visitatore', async () => {
    const wrapper = await mountSuspended(AttivitaId, { route: { params: { id: '1' } } })
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Aggiungi recensione')
  })

  it('mostra il pulsante Segnala per utente loggato', async () => {
    const wrapper = await mountSuspended(AttivitaId, { route: { params: { id: '1' } } })
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Segnala questa attività')
  })

  it('mostra la sezione camere per alloggio', async () => {
    const wrapper = await mountSuspended(AttivitaId, { route: { params: { id: '1' } } })
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Camere')
    expect(wrapper.text()).toContain('Doppia')
  })

  it('mostra la sezione prenota', async () => {
    const wrapper = await mountSuspended(AttivitaId, { route: { params: { id: '1' } } })
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Prenota')
  })
})

describe('InserimentoAttivita', () => {
  beforeAll(() => {
    vi.mocked(useAuthStore).mockReturnValue({
      user: { id: 2, nome: 'Luigi', cognome: 'Verdi', email: 'l.verdi@example.com', ruolo: 'GESTORE_ATTIVITA' },
      isLoggedIn: true,
      isVisitatore: false,
      isGestore: true,
      isAdmin: false,
      logout: vi.fn(),
    })
  })

  it('mostra il titolo Nuova attività', async () => {
    const wrapper = await mountSuspended(InserimentoAttivita)
    expect(wrapper.text()).toContain('Nuova attività')
  })

  it('mostra il form con i campi principali', async () => {
    const wrapper = await mountSuspended(InserimentoAttivita)
    expect(wrapper.text()).toContain('Informazioni di base')
    expect(wrapper.text()).toContain('Luogo')
    expect(wrapper.text()).toContain('Politiche eco-sostenibili')
  })

  it('mostra il pulsante Crea attività', async () => {
    const wrapper = await mountSuspended(InserimentoAttivita)
    expect(wrapper.text()).toContain('Crea attività')
  })

  it('mostra il pulsante Annulla', async () => {
    const wrapper = await mountSuspended(InserimentoAttivita)
    expect(wrapper.text()).toContain('Annulla')
  })
})

describe('TabellaPrenotazioni', () => {
  beforeAll(() => {
    vi.mocked(useAuthStore).mockReturnValue({
      user: { id: 2, nome: 'Luigi', cognome: 'Verdi', email: 'l.verdi@example.com', ruolo: 'GESTORE_ATTIVITA' },
      isLoggedIn: true,
      isVisitatore: false,
      isGestore: true,
      isAdmin: false,
      logout: vi.fn(),
    })
  })

  it('mostra il titolo Tabella prenotazioni', async () => {
    const wrapper = await mountSuspended(TabellaPrenotazioni)
    expect(wrapper.text()).toContain('Tabella prenotazioni')
  })

  it('mostra la prenotazione alloggio', async () => {
    const wrapper = await mountSuspended(TabellaPrenotazioni)
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Mario Rossi')
    expect(wrapper.text()).toContain('m.rossi@example.com')
    expect(wrapper.text()).toContain('CREATA')
    expect(wrapper.text()).toContain('IN_CORSO')
    expect(wrapper.text()).toContain('320')
  })

  it('mostra la prenotazione attività turistica', async () => {
    const wrapper = await mountSuspended(TabellaPrenotazioni)
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Luigi Verdi')
    expect(wrapper.text()).toContain('COMPLETATA')
    expect(wrapper.text()).toContain('COMPLETATO')
    expect(wrapper.text()).toContain('75')
  })

  it('mostra i filtri', async () => {
    const wrapper = await mountSuspended(TabellaPrenotazioni)
    expect(wrapper.text()).toContain('Tutte le attività')
    expect(wrapper.text()).toContain('Tutti gli stati')
  })
})

describe('IMieiViaggi', () => {
  beforeAll(() => {
    vi.mocked(useAuthStore).mockReturnValue({
      user: { id: 1, nome: 'Mario', cognome: 'Rossi', email: 'm.rossi@example.com', ruolo: 'VISITATORE' },
      isLoggedIn: true,
      isVisitatore: true,
      isGestore: false,
      isAdmin: false,
      logout: vi.fn(),
    })
  })

  it('mostra il titolo I miei viaggi', async () => {
    const wrapper = await mountSuspended(IMieiViaggi)
    expect(wrapper.text()).toContain('I miei viaggi')
  })

  it('mostra la prenotazione alloggio', async () => {
    const wrapper = await mountSuspended(IMieiViaggi)
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Hotel Test')
    expect(wrapper.text()).toContain('CREATA')
    expect(wrapper.text()).toContain('320')
  })

  it('mostra la prenotazione attività turistica', async () => {
    const wrapper = await mountSuspended(IMieiViaggi)
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Escursione')
    expect(wrapper.text()).toContain('COMPLETATA')
    expect(wrapper.text()).toContain('75')
  })
})

describe('ItinerarioAutomatico', () => {
  beforeAll(() => {
    vi.mocked(useAuthStore).mockReturnValue({
      user: { id: 1, nome: 'Mario', cognome: 'Rossi', email: 'm.rossi@example.com', ruolo: 'VISITATORE' },
      isLoggedIn: true,
      isVisitatore: true,
      isGestore: false,
      isAdmin: false,
      logout: vi.fn(),
    })
  })

  it('mostra il titolo Genera itinerario', async () => {
    const wrapper = await mountSuspended(ItinerarioAutomatico)
    expect(wrapper.text()).toContain('Genera itinerario')
  })

  it('mostra le preferenze dell\'utente', async () => {
    const wrapper = await mountSuspended(ItinerarioAutomatico)
    await new Promise(r => setTimeout(r, 200))
    expect(wrapper.text()).toContain('Mare')
    expect(wrapper.text()).toContain('Hotel')
    expect(wrapper.text()).toContain('Medio')
  })

  it('mostra il pulsante Genera itinerario', async () => {
    const wrapper = await mountSuspended(ItinerarioAutomatico)
    expect(wrapper.text()).toContain('Genera itinerario')
  })
})

describe('1.3 Questionario', () => {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  async function navigateToStep(wrapper: any, targetStep: number) {
    for (let i = 0; i < targetStep; i++) {
      const opts = wrapper.findAll('button[type="button"]')
      if (opts.length > 0) await opts[0].trigger('click')
      const buttons = wrapper.findAll('button')
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const avanti = buttons.filter((b: any) => b.text().includes('Avanti'))
      if (avanti.length > 0) await avanti[0].trigger('click')
      await new Promise(r => setTimeout(r, 30))
    }
  }

  it('TC_1.3.1 — SRPV1: mostra errore se nessuna preferenza viaggio', async () => {
    const wrapper = await mountSuspended(Questionario)
    wrapper.vm.next()
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.text()).toContain('Nessuna preferenza indicata per i viaggi')
  })

  it('TC_1.3.2 — SRPA1: mostra errore se nessuna preferenza alloggio', async () => {
    const wrapper = await mountSuspended(Questionario)
    await navigateToStep(wrapper, 1)
    wrapper.vm.next()
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.text()).toContain('Non risulta alcuna preferenza per l\'alloggio')
  })

  it('TC_1.3.3 — SRPAL1: mostra errore se nessuna preferenza alimentare', async () => {
    const wrapper = await mountSuspended(Questionario)
    await navigateToStep(wrapper, 2)
    wrapper.vm.next()
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.text()).toContain('Non risulta inserita alcuna preferenza alimentare')
  })

  it('TC_1.3.4 — SRPAS1: mostra errore se nessuna preferenza attività', async () => {
    const wrapper = await mountSuspended(Questionario)
    await navigateToStep(wrapper, 3)
    wrapper.vm.next()
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.text()).toContain('Non risulta indicata alcuna preferenza per le attività da svolgere')
  })

  it('TC_1.3.5 — SRCAD1: mostra errore se animale domestico non selezionato', async () => {
    const wrapper = await mountSuspended(Questionario)
    await navigateToStep(wrapper, 4)
    wrapper.vm.next()
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.text()).toContain('Campo "Viaggiare con un animale domestico" non selezionato')
  })

  it('TC_1.3.6 — SRPB1: mostra errore se nessuna preferenza budget', async () => {
    const wrapper = await mountSuspended(Questionario)
    await navigateToStep(wrapper, 5)
    wrapper.vm.next()
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.text()).toContain('Non risulta indicata alcuna preferenza riguardo il budget')
  })

  it('TC_1.3.7 — SRPSA1: mostra errore se nessuna preferenza souvenir', async () => {
    const wrapper = await mountSuspended(Questionario)
    await navigateToStep(wrapper, 6)
    wrapper.vm.next()
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.text()).toContain('Nessuna preferenza indicata riguardo i souvenirs')
  })

  it('TC_1.3.8 — SRPS1: mostra errore se nessuna preferenza stagione', async () => {
    const wrapper = await mountSuspended(Questionario)
    await navigateToStep(wrapper, 7)
    await wrapper.vm.invio()
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.text()).toContain('Nessuna preferenza indicata riguardo la stagione')
  })

  it('TC_1.3.9 — All OK: questionario completato con successo', async () => {
    const wrapper = await mountSuspended(Questionario)
    await navigateToStep(wrapper, 7)
    const opts = wrapper.findAll('button[type="button"]')
    await opts[0].trigger('click')
    await wrapper.vm.invio()
    await new Promise(r => setTimeout(r, 50))
    expect(wrapper.text()).toContain('Questionario completato!')
  })
})
