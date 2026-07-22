import { describe, expect, it, vi, beforeEach } from 'vitest'

const { mockLogin, mockRegister } = vi.hoisted(() => ({
  mockLogin: vi.fn().mockResolvedValue(null),
  mockRegister: vi.fn().mockResolvedValue(null),
}))

vi.mock('~/composables/useApi', () => ({
  useApi: () => ({
    auth: {
      login: mockLogin,
      register: mockRegister,
    },
  }),
}))

describe('useAuthStore', () => {
  beforeEach(() => {
    mockLogin.mockClear()
    mockRegister.mockClear()
    mockLogin.mockResolvedValue(null)
    mockRegister.mockResolvedValue(null)
  })

  it('inizia con utente non loggato', () => {
    const store = useAuthStore()
    expect(store.isLoggedIn).toBe(false)
    expect(store.user).toBeNull()
  })

  it('login imposta utente e cookie', async () => {
    mockLogin.mockResolvedValue({
      status: 'success',
      data: {
        id: 1,
        nome: 'Mario',
        cognome: 'Rossi',
        email: 'mario@test.it',
        ruolo: 'VISITATORE',
      },
    })

    const store = useAuthStore()
    const ok = await store.login('mario@test.it', 'password')

    expect(ok).toBe(true)
    expect(store.isLoggedIn).toBe(true)
    expect(store.user?.nome).toBe('Mario')
    expect(store.user?.ruolo).toBe('VISITATORE')
  })

  it('logout resetta utente e cookie', async () => {
    mockLogin.mockResolvedValue({
      status: 'success',
      data: {
        id: 1,
        nome: 'Mario',
        cognome: 'Rossi',
        email: 'mario@test.it',
        ruolo: 'VISITATORE',
      },
    })

    const store = useAuthStore()
    await store.login('mario@test.it', 'password')
    expect(store.isLoggedIn).toBe(true)

    store.logout()
    expect(store.isLoggedIn).toBe(false)
    expect(store.user).toBeNull()
  })

  it('register imposta utente e cookie', async () => {
    mockRegister.mockResolvedValue({
      status: 'success',
      data: {
        id: 2,
        nome: 'Luigi',
        cognome: 'Verdi',
        email: 'luigi@test.it',
        ruolo: 'GESTORE_ATTIVITA',
      },
    })

    const store = useAuthStore()
    const ok = await store.register(
      { email: 'luigi@test.it', password: 'password' },
      true
    )

    expect(ok).toBe(true)
    expect(store.isLoggedIn).toBe(true)
    expect(store.user?.ruolo).toBe('GESTORE_ATTIVITA')
  })

  it('getter ruolo funzionano correttamente', async () => {
    mockLogin.mockResolvedValue({
      status: 'success',
      data: {
        id: 1,
        nome: 'Admin',
        cognome: 'User',
        email: 'admin@test.it',
        ruolo: 'AMMINISTRATORE',
      },
    })

    const store = useAuthStore()
    await store.login('admin@test.it', 'password')

    expect(store.isVisitatore).toBe(false)
    expect(store.isGestore).toBe(false)
    expect(store.isAdmin).toBe(true)
  })
})
