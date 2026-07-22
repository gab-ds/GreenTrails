import { describe, expect, it, vi, beforeEach } from 'vitest'
import { mountSuspended } from '@nuxt/test-utils/runtime'

vi.mock('~/composables/useApi', () => ({
  useApi: () => ({
    activities: {
      create: vi.fn().mockResolvedValue({ status: 'success', data: { id: 99 } }),
      get: vi.fn().mockResolvedValue({ status: 'success', data: {} }),
    },
    valori: {
      create: vi.fn().mockResolvedValue({ status: 'success', data: { id: 1 } }),
    },
  }),
}))

vi.mock('~/stores/auth', () => ({
  useAuthStore: vi.fn(() => ({
    user: { id: 2, nome: 'Luigi', cognome: 'Verdi', email: 'l.verdi@example.com', ruolo: 'GESTORE_ATTIVITA' },
    isLoggedIn: true,
    isVisitatore: false,
    isGestore: true,
    isAdmin: false,
    logout: vi.fn(),
  })),
}))

import InserimentoAttivita from '~/pages/inserimento-attivita.vue'

function setValidAttivitaTuristica(w: any) {
  w.nome = 'Escursione in montagna'
  w.alloggio = false
  w.indirizzo = 'Via Roma 1'
  w.cap = '84100'
  w.citta = 'Salerno'
  w.provincia = 'SA'
  w.latitudine = '40.68'
  w.longitudine = '14.77'
  w.descrizioneBreve = 'Una splendida escursione in montagna'
  w.descrizioneLunga = 'Una descrizione molto lunga e dettagliata per testare la validazione del modulo'
  w.categoriaAttivitaTuristica = 0
  w.prezzo = '25.00'
  w.disponibilita = '10'
  w.politicheAntispreco = true
  w.prodottiLocali = true
  w.energiaVerde = true
  w.raccoltaDifferenziata = true
  w.limiteEmissioneCO2 = true
  w.contattoConNatura = true
}

function setValidAlloggio(w: any) {
  w.nome = 'Hotel Belvedere'
  w.alloggio = true
  w.indirizzo = 'Via Roma 1'
  w.cap = '84100'
  w.citta = 'Salerno'
  w.provincia = 'SA'
  w.latitudine = '40.68'
  w.longitudine = '14.77'
  w.descrizioneBreve = 'Un bellissimo hotel sul mare'
  w.descrizioneLunga = 'Una descrizione molto lunga e dettagliata per testare la validazione dell alloggio'
  w.categoriaAlloggio = 0
  w.politicheAntispreco = true
  w.prodottiLocali = true
  w.energiaVerde = true
  w.raccoltaDifferenziata = true
  w.limiteEmissioneCO2 = true
  w.contattoConNatura = true
}

describe('Inserimento Attività Turistica — TC_2.1.*', () => {
  let wrapper: Awaited<ReturnType<typeof mountSuspended>>

  beforeEach(async () => {
    wrapper = await mountSuspended(InserimentoAttivita, { route: '/inserimento-attivita' })
  })

  it('TC_2.1.1: LN1 — nome vuoto', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.nome = ''
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('lunghezza del nome errata')
  })

  it('TC_2.1.1: LN1 — nome troppo lungo', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.nome = 'A'.repeat(51)
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('lunghezza del nome errata')
  })

  it('TC_2.1.2: FN1 — formato nome non corretto', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.nome = 'Mario@123'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('formato nome non corretto')
  })

  it('TC_2.1.3: LI1 — indirizzo vuoto', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.indirizzo = ''
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('lunghezza indirizzo non corretta')
  })

  it('TC_2.1.4: FI1 — formato indirizzo non corretto', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.indirizzo = 'Via@!'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('formato indirizzo non corretto')
  })

  it('TC_2.1.5: LCAP1 — CAP diverso da 5', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.cap = '123'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('lunghezza CAP non corretta')
  })

  it('TC_2.1.6: FCAP1 — CAP formato non corretto', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.cap = 'abcde'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('formato CAP non corretto')
  })

  it('TC_2.1.7: LC1 — città vuota', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.citta = ''
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('lunghezza città non corretta')
  })

  it('TC_2.1.8: FC1 — formato città non corretto', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.citta = 'Napoli@!'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('formato città non corretto')
  })

  it('TC_2.1.9: LP1 — provincia diversa da 2', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.provincia = 'S'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('lunghezza provincia non corretta')
  })

  it('TC_2.1.10: FP1 — formato provincia non corretto', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.provincia = 'sa'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('formato provincia non corretto')
  })

  it('TC_2.1.11: LAT1 — latitudine fuori range', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.latitudine = '100'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('latitudine non corretta')
  })

  it('TC_2.1.12: LON1 — longitudine fuori range', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.longitudine = '200'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('longitudine non corretta')
  })

  it('TC_2.1.13: ST1 — tipologia non selezionata', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.alloggio = null
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('tipologia non selezionata')
  })

  it('TC_2.1.14: SC1 — categoria non selezionata', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.categoriaAttivitaTuristica = null
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('categoria non selezionata')
  })

  it('TC_2.1.15: LDB1 — descrizione breve troppo corta', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.descrizioneBreve = 'ab'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('lunghezza descrizione breve non corretta')
  })

  it('TC_2.1.16: FDB1 — descrizione breve non inizia con maiuscola', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.descrizioneBreve = 'abcde'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('formato descrizione breve non corretto')
  })

  it('TC_2.1.17: LDL1 — descrizione lunga troppo corta', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.descrizioneLunga = 'ab'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('lunghezza descrizione lunga non corretta')
  })

  it('TC_2.1.18: FDL1 — descrizione lunga non inizia con maiuscola', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.descrizioneLunga = 'abcde'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('formato descrizione lunga non corretto')
  })

  it('TC_2.1.19: TM1 — tipologia media non corretta (PDF)', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.file = new File([''], 'test.pdf', { type: 'application/pdf' })
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('tipologia media non corretta')
  })

  it('TC_2.1.20: GM1 — grandezza media oltre 100MB', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    const bigFile = new File([''], 'big.mp4', { type: 'video/mp4' })
    Object.defineProperty(bigFile, 'size', { value: 101 * 1024 * 1024 })
    wrapper.vm.file = bigFile
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('grandezza media non corretta')
  })

  it('TC_2.1.21: PAT1 — prezzo inferiore a 0', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.prezzo = '-1'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('prezzo inferiore a 0')
  })

  it('TC_2.1.22: DAT1 — disponibilità inferiore a 1', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.disponibilita = '0'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('disponibilità inferiore a 1')
  })

  it('TC_2.1.23: caso valido senza ecosostenibilità', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    wrapper.vm.politicheAntispreco = false
    wrapper.vm.prodottiLocali = false
    wrapper.vm.energiaVerde = false
    wrapper.vm.raccoltaDifferenziata = false
    wrapper.vm.limiteEmissioneCO2 = false
    wrapper.vm.contattoConNatura = false
    await wrapper.vm.onSubmit()
    expect(wrapper.vm.success).toBe('ok')
  })

  it('TC_2.1.24: caso valido con ecosostenibilità', async () => {
    setValidAttivitaTuristica(wrapper.vm)
    await wrapper.vm.onSubmit()
    expect(wrapper.vm.success).toBe('ok')
  })
})

describe('Inserimento Alloggio — TC_2.2.*', () => {
  let wrapper: Awaited<ReturnType<typeof mountSuspended>>

  beforeEach(async () => {
    wrapper = await mountSuspended(InserimentoAttivita, { route: '/inserimento-attivita' })
  })

  it('TC_2.2.14: SC1 — categoria alloggio non selezionata', async () => {
    setValidAlloggio(wrapper.vm)
    wrapper.vm.categoriaAlloggio = null
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('categoria non selezionata')
  })

  it('TC_2.2.22: caso valido senza ecosostenibilità', async () => {
    setValidAlloggio(wrapper.vm)
    wrapper.vm.politicheAntispreco = false
    wrapper.vm.prodottiLocali = false
    wrapper.vm.energiaVerde = false
    wrapper.vm.raccoltaDifferenziata = false
    wrapper.vm.limiteEmissioneCO2 = false
    wrapper.vm.contattoConNatura = false
    await wrapper.vm.onSubmit()
    expect(wrapper.vm.success).toBe('ok')
  })

  it('TC_2.2.23: caso valido con ecosostenibilità', async () => {
    setValidAlloggio(wrapper.vm)
    await wrapper.vm.onSubmit()
    expect(wrapper.vm.success).toBe('ok')
  })
})
