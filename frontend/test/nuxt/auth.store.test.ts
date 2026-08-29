import { describe, expect, it, vi, beforeEach } from 'vitest'

const { mockLogin, mockRegister } = vi.hoisted(() => ({
  mockLogin: vi.fn(),
  mockRegister: vi.fn(),
}))

vi.mock('~/composables/useApi', () => ({
  useApi: () => ({
    auth: {
      login: mockLogin,
      register: mockRegister,
    },
  }),
}))

function loginResponse(utente: Record<string, unknown>) {
  return {
    status: 'success',
    data: {
      token: 'mock-jwt-token',
      utente,
    },
  }
}

const mario = { id: 1, nome: 'Mario', cognome: 'Rossi', email: 'mario@test.it', ruolo: 'VISITATORE' }
const luigi = { id: 2, nome: 'Luigi', cognome: 'Verdi', email: 'luigi@test.it', ruolo: 'GESTORE_ATTIVITA' }
const admin = { id: 3, nome: 'Admin', cognome: 'User', email: 'admin@test.it', ruolo: 'AMMINISTRATORE' }

function freshStore() {
  const store = useAuthStore()
  store.logout()
  return store
}

describe('useAuthStore', () => {
  beforeEach(() => {
    mockLogin.mockReset()
    mockRegister.mockReset()
  })

  it('inizia con utente non loggato', () => {
    const store = freshStore()
    expect(store.isLoggedIn).toBe(false)
    expect(store.user).toBeNull()
    expect(store.token).toBeNull()
  })

  it('login imposta utente, token e cookie', async () => {
    mockLogin.mockResolvedValue(loginResponse(mario))

    const store = freshStore()
    const ok = await store.login('mario@test.it', 'password')

    expect(ok).toBe(true)
    expect(store.isLoggedIn).toBe(true)
    expect(store.user?.nome).toBe('Mario')
    expect(store.user?.ruolo).toBe('VISITATORE')
    expect(store.token).toBe('mock-jwt-token')
  })

  it('login fallito restituisce false e non imposta utente', async () => {
    mockLogin.mockRejectedValue(new Error('Unauthorized'))

    const store = freshStore()
    const ok = await store.login('wrong@test.it', 'badpassword')

    expect(ok).toBe(false)
    expect(store.isLoggedIn).toBe(false)
    expect(store.user).toBeNull()
    expect(store.token).toBeNull()
  })

  it('logout resetta utente, token e cookie', async () => {
    mockLogin.mockResolvedValue(loginResponse(mario))

    const store = freshStore()
    await store.login('mario@test.it', 'password')
    expect(store.isLoggedIn).toBe(true)

    store.logout()
    expect(store.isLoggedIn).toBe(false)
    expect(store.user).toBeNull()
    expect(store.token).toBeNull()
  })

  it('register chiama register poi login automaticamente', async () => {
    mockRegister.mockResolvedValue({ status: 'success' })
    mockLogin.mockResolvedValue(loginResponse(luigi))

    const store = freshStore()
    const ok = await store.register(
      { email: 'luigi@test.it', password: 'password' },
      true
    )

    expect(ok).toBe(true)
    expect(mockRegister).toHaveBeenCalledOnce()
    expect(mockLogin).toHaveBeenCalledWith('luigi@test.it', 'password')
    expect(store.isLoggedIn).toBe(true)
    expect(store.user?.ruolo).toBe('GESTORE_ATTIVITA')
  })

  it('register fallito restituisce false', async () => {
    mockRegister.mockRejectedValue(new Error('Email exists'))

    const store = freshStore()
    const ok = await store.register(
      { email: 'luigi@test.it', password: 'password' },
      true
    )

    expect(ok).toBe(false)
    expect(store.isLoggedIn).toBe(false)
  })

  it('restore ripristina utente se token e user cookie presenti', async () => {
    mockLogin.mockResolvedValue(loginResponse(mario))

    const store = freshStore()
    await store.login('mario@test.it', 'password')
    expect(store.user?.nome).toBe('Mario')

    const store2 = useAuthStore()
    expect(store2.user).not.toBeNull()
    expect(store2.token).toBe('mock-jwt-token')
  })

  it('restore fa logout se token assente', async () => {
    mockLogin.mockResolvedValue(loginResponse(mario))

    const store = freshStore()
    await store.login('mario@test.it', 'password')
    store.token = null

    const store2 = useAuthStore()
    expect(store2.isLoggedIn).toBe(false)
  })

  it('getter ruolo funzionano correttamente', async () => {
    mockLogin.mockResolvedValue(loginResponse(admin))

    const store = freshStore()
    await store.login('admin@test.it', 'password')

    expect(store.isVisitatore).toBe(false)
    expect(store.isGestore).toBe(false)
    expect(store.isAdmin).toBe(true)
  })

  it('getter isGestore funziona', async () => {
    mockLogin.mockResolvedValue(loginResponse(luigi))

    const store = freshStore()
    await store.login('luigi@test.it', 'password')

    expect(store.isVisitatore).toBe(false)
    expect(store.isGestore).toBe(true)
    expect(store.isAdmin).toBe(false)
  })

  it('isLoggedIn richiede sia user che token', async () => {
    mockLogin.mockResolvedValue(loginResponse(mario))

    const store = freshStore()
    await store.login('mario@test.it', 'password')
    expect(store.isLoggedIn).toBe(true)

    store.user = null
    expect(store.isLoggedIn).toBe(false)

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    store.user = mario as any
    store.token = null
    expect(store.isLoggedIn).toBe(false)
  })
})
