import { describe, expect, it, vi, beforeEach } from 'vitest'
import { mountSuspended } from '@nuxt/test-utils/runtime'
import { CalendarDate } from '@internationalized/date'

import Registrazione from '~/pages/registrazione.vue'
import Login from '~/pages/login.vue'

vi.mock('~/composables/useApi', () => ({
  useApi: () => ({
    auth: {
      login: vi.fn(),
      register: vi.fn(),
    },
    activities: {
      list: vi.fn().mockResolvedValue([]),
    },
  }),
}))

describe('Registrazione — validazione lato client', () => {
  let wrapper: Awaited<ReturnType<typeof mountSuspended>>

  beforeEach(async () => {
    wrapper = await mountSuspended(Registrazione)
  })

  it('TC_1.1.3: nome vuoto mostra errore lunghezza', async () => {
    wrapper.vm.cognome = 'Rossi'
    wrapper.vm.email = 'm.rossi@example.com'
    wrapper.vm.password = 'Password123!'
    wrapper.vm.dataNascita = new CalendarDate(1995, 5, 23)
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('La lunghezza del nome non è corretta.')
  })

  it('TC_1.1.4: nome con numeri mostra errore formato', async () => {
    wrapper.vm.nome = 'Mario123'
    wrapper.vm.cognome = 'Rossi'
    wrapper.vm.email = 'm.rossi@example.com'
    wrapper.vm.password = 'Password123!'
    wrapper.vm.dataNascita = new CalendarDate(1995, 5, 23)
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('Il formato del nome non è valido.')
  })

  it('TC_1.1.5: cognome vuoto mostra errore lunghezza', async () => {
    wrapper.vm.nome = 'Mario'
    wrapper.vm.email = 'm.rossi@example.com'
    wrapper.vm.password = 'Password123!'
    wrapper.vm.dataNascita = new CalendarDate(1995, 5, 23)
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('La lunghezza del cognome non è corretta.')
  })

  it('TC_1.1.6: cognome con numeri mostra errore formato', async () => {
    wrapper.vm.nome = 'Mario'
    wrapper.vm.cognome = 'Rossi123'
    wrapper.vm.email = 'm.rossi@example.com'
    wrapper.vm.password = 'Password123!'
    wrapper.vm.dataNascita = new CalendarDate(1995, 5, 23)
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('Il formato del cognome non è valido.')
  })

  it('TC_1.1.7: email vuota mostra errore lunghezza', async () => {
    wrapper.vm.nome = 'Mario'
    wrapper.vm.cognome = 'Rossi'
    wrapper.vm.email = ''
    wrapper.vm.password = 'Password123!'
    wrapper.vm.dataNascita = new CalendarDate(1995, 5, 23)
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain("La lunghezza dell'email non è corretta.")
  })

  it('TC_1.1.8: email senza @ mostra errore formato', async () => {
    wrapper.vm.nome = 'Mario'
    wrapper.vm.cognome = 'Rossi'
    wrapper.vm.email = 'm.rossiexample.com'
    wrapper.vm.password = 'Password123!'
    wrapper.vm.dataNascita = new CalendarDate(1995, 5, 23)
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain("Il formato dell'email non è valido.")
  })

  it('TC_1.1.9: password corta mostra errore lunghezza', async () => {
    wrapper.vm.nome = 'Mario'
    wrapper.vm.cognome = 'Rossi'
    wrapper.vm.email = 'm.rossi@example.com'
    wrapper.vm.password = 'Ab12!'
    wrapper.vm.dataNascita = new CalendarDate(1995, 5, 23)
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('La lunghezza della password non è corretta.')
  })

  it('TC_1.1.10: password senza cifra mostra errore formato', async () => {
    wrapper.vm.nome = 'Mario'
    wrapper.vm.cognome = 'Rossi'
    wrapper.vm.email = 'm.rossi@example.com'
    wrapper.vm.password = 'Password!'
    wrapper.vm.dataNascita = new CalendarDate(1995, 5, 23)
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('Il formato della password non è valido.')
  })
})

describe('Login — validazione lato client', () => {
  let wrapper: Awaited<ReturnType<typeof mountSuspended>>

  beforeEach(async () => {
    wrapper = await mountSuspended(Login)
  })

  it('TC_1.2.1: email vuota mostra errore lunghezza', async () => {
    wrapper.vm.password = 'Password123!'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain("La lunghezza dell'email non è corretta.")
  })

  it('TC_1.2.2: email malformata mostra errore formato', async () => {
    wrapper.vm.email = '@.i'
    wrapper.vm.password = 'Password123!'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain("Il formato dell'email non è valido.")
  })

  it('TC_1.2.4: password corta mostra errore lunghezza', async () => {
    wrapper.vm.email = 'test@example.com'
    wrapper.vm.password = 'Pas12!'
    await wrapper.vm.onSubmit()
    expect(wrapper.text()).toContain('La lunghezza della password non è corretta.')
  })
})
