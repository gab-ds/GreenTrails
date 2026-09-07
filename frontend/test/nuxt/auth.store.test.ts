import { describe, expect, it, vi, beforeEach } from 'vitest'

const { mockLogin, mockRegister, mockLogout, mockGetCurrentUser } = vi.hoisted(() => ({
  mockLogin: vi.fn(),
  mockRegister: vi.fn(),
  mockLogout: vi.fn(),
  mockGetCurrentUser: vi.fn(),
}))

vi.mock('~/composables/useApi', () => ({
  useApi: () => ({
    auth: {
      login: mockLogin,
      register: mockRegister,
      logout: mockLogout,
      getCurrentUser: mockGetCurrentUser,
    },
  }),
}))

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
    mockLogout.mockReset()
    mockGetCurrentUser.mockReset()
  })

  it('inizia con utente non loggato', () => {
    const store = freshStore()
    expect(store.isLoggedIn).toBe(false)
    expect(store.user).toBeNull()
  })

  it('login imposta utente', async () => {
    mockLogin.mockResolvedValue({
      status: 'success',
      data: { utente: mario },
    })

    const store = freshStore()
    const ok = await store.login('mario@test.it', 'password')

    expect(ok).toBe(true)
    expect(store.isLoggedIn).toBe(true)
    expect(store.user?.nome).toBe('Mario')
    expect(store.user?.ruolo).toBe('VISITATORE')
  })

  it('login fallito restituisce false e non imposta utente', async () => {
    mockLogin.mockRejectedValue(new Error('Unauthorized'))

    const store = freshStore()
    const ok = await store.login('wrong@test.it', 'badpassword')

    expect(ok).toBe(false)
    expect(store.isLoggedIn).toBe(false)
    expect(store.user).toBeNull()
  })

  it('logout resetta utente e chiama backend', async () => {
    mockLogin.mockResolvedValue({
      status: 'success',
      data: { utente: mario },
    })
    mockLogout.mockResolvedValue({ status: 'success' })

    const store = freshStore()
    await store.login('mario@test.it', 'password')
    expect(store.isLoggedIn).toBe(true)

    mockLogout.mockClear()
    await store.logout()
    expect(store.isLoggedIn).toBe(false)
    expect(store.user).toBeNull()
    expect(mockLogout).toHaveBeenCalledOnce()
  })

  it('register chiama register poi login automaticamente', async () => {
    mockRegister.mockResolvedValue({ status: 'success' })
    mockLogin.mockResolvedValue({
      status: 'success',
      data: { utente: luigi },
    })

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

  it('restore recupera utente dal backend', async () => {
    mockGetCurrentUser.mockResolvedValue({
      status: 'success',
      data: mario,
    })

    const store = freshStore()
    expect(store.user).toBeNull()

    await store.restore()
    expect(store.user?.nome).toBe('Mario')
    expect(store.isLoggedIn).toBe(true)
  })

  it('restore imposta user a null se il backend fallisce', async () => {
    mockGetCurrentUser.mockRejectedValue(new Error('Unauthorized'))

    const store = freshStore()
    await store.restore()
    expect(store.user).toBeNull()
    expect(store.isLoggedIn).toBe(false)
  })

  it('getter ruolo funzionano correttamente', async () => {
    mockLogin.mockResolvedValue({
      status: 'success',
      data: { utente: admin },
    })

    const store = freshStore()
    await store.login('admin@test.it', 'password')

    expect(store.isVisitatore).toBe(false)
    expect(store.isGestore).toBe(false)
    expect(store.isAdmin).toBe(true)
  })

  it('getter isGestore funziona', async () => {
    mockLogin.mockResolvedValue({
      status: 'success',
      data: { utente: luigi },
    })

    const store = freshStore()
    await store.login('luigi@test.it', 'password')

    expect(store.isVisitatore).toBe(false)
    expect(store.isGestore).toBe(true)
    expect(store.isAdmin).toBe(false)
  })

  it('isLoggedIn dipende solo da user', async () => {
    mockLogin.mockResolvedValue({
      status: 'success',
      data: { utente: mario },
    })

    const store = freshStore()
    await store.login('mario@test.it', 'password')
    expect(store.isLoggedIn).toBe(true)

    store.user = null
    expect(store.isLoggedIn).toBe(false)
  })
})
