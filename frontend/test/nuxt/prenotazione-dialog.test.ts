import { describe, expect, it, vi } from 'vitest'
import { mountSuspended } from '@nuxt/test-utils/runtime'

import PrenotazioneDialog from '~/components/PrenotazioneDialog.vue'

vi.mock('~/composables/useApi', () => ({
  useApi: () => ({
    camere: {
      perAlloggio: vi.fn().mockResolvedValue({ status: 'success', data: [{ id: 1, tipoCamera: 'Doppia', capienza: 2, prezzo: 80, disponibilita: 5 }, { id: 2, tipoCamera: 'Suite', capienza: 4, prezzo: 150, disponibilita: 2 }] }),
    },
    prenotazioniAlloggio: {
      disponibilita: vi.fn().mockResolvedValue({ status: 'success', data: 5 }),
      create: vi.fn().mockResolvedValue({ status: 'success' }),
    },
    prenotazioniAttivita: {
      disponibilita: vi.fn().mockResolvedValue({ status: 'success', data: 10 }),
      create: vi.fn().mockResolvedValue({ status: 'success' }),
    },
    itinerari: {
      create: vi.fn().mockResolvedValue({ status: 'success', data: { id: 100 } }),
    },
  }),
}))

describe('PrenotazioneDialog — alloggio', () => {
  it('mostra il titolo Prenota Alloggio', async () => {
    const wrapper = await mountSuspended(PrenotazioneDialog, {
      props: { activityId: 1, isAlloggio: true, activityName: 'Hotel Test' },
    })
    expect(wrapper.text()).toContain('Prenota Alloggio')
  })

  it('mostra lo step 1 con i campi input', async () => {
    const wrapper = await mountSuspended(PrenotazioneDialog, {
      props: { activityId: 1, isAlloggio: true, activityName: 'Hotel Test' },
    })
    expect(wrapper.text()).toContain('Dati')
    expect(wrapper.text()).toContain('Tipo camera')
    expect(wrapper.text()).toContain('Data inizio')
    expect(wrapper.text()).toContain('Adulti')
    expect(wrapper.text()).toContain('Numero camere')
  })

  it('mostra il pulsante Annulla', async () => {
    const wrapper = await mountSuspended(PrenotazioneDialog, {
      props: { activityId: 1, isAlloggio: true, activityName: 'Hotel Test' },
    })
    expect(wrapper.text()).toContain('Annulla')
  })
})

describe('PrenotazioneDialog — attività turistica', () => {
  it('mostra il titolo Prenota Attività', async () => {
    const wrapper = await mountSuspended(PrenotazioneDialog, {
      props: { activityId: 2, isAlloggio: false, activityName: 'Escursione' },
    })
    expect(wrapper.text()).toContain('Prenota Attività')
  })

  it('mostra i campi senza tipo camera', async () => {
    const wrapper = await mountSuspended(PrenotazioneDialog, {
      props: { activityId: 2, isAlloggio: false, activityName: 'Escursione' },
    })
    expect(wrapper.text()).toContain('Data inizio')
    expect(wrapper.text()).toContain('Data fine')
    expect(wrapper.text()).toContain('Adulti')
    expect(wrapper.text()).not.toContain('Tipo camera')
    expect(wrapper.text()).not.toContain('Numero camere')
  })
})
