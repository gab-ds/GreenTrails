import { describe, expect, it, vi, beforeEach } from 'vitest'

const mockNavigateTo = vi.fn()

vi.mock('#app/composables/router', async (importOriginal) => {
  const orig = await importOriginal<typeof import('#app/composables/router')>()
  return {
    ...orig,
    navigateTo: mockNavigateTo,
  }
})

const mockStore: {
  isLoggedIn: boolean
  user: { ruolo: string } | null
} = {
  isLoggedIn: false,
  user: null,
}

vi.mock('~/stores/auth', () => ({
  useAuthStore: () => mockStore,
}))

const { default: authMiddleware } = await import('~/middleware/auth')

function makeRoute(meta: Record<string, unknown> = {}) {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  return { meta, path: '/test', matched: [] } as any
}

describe('auth middleware — logica di navigazione', () => {
  beforeEach(() => {
    mockNavigateTo.mockReset()
    mockStore.isLoggedIn = false
    mockStore.user = null
  })

  it('non autenticato → redirect /login', async () => {
    mockStore.isLoggedIn = false

    await authMiddleware(makeRoute(), makeRoute())

    expect(mockNavigateTo).toHaveBeenCalledWith('/login')
  })

  it('autenticato, nessun ruolo → prosegue', async () => {
    mockStore.isLoggedIn = true
    mockStore.user = { ruolo: 'VISITATORE' }

    await authMiddleware(makeRoute(), makeRoute())

    expect(mockNavigateTo).not.toHaveBeenCalled()
  })

  it('autenticato, ruolo corretto → prosegue', async () => {
    mockStore.isLoggedIn = true
    mockStore.user = { ruolo: 'GESTORE_ATTIVITA' }

    await authMiddleware(makeRoute({ role: 'GESTORE_ATTIVITA' }), makeRoute())

    expect(mockNavigateTo).not.toHaveBeenCalled()
  })

  it('autenticato, ruolo sbagliato → redirect /', async () => {
    mockStore.isLoggedIn = true
    mockStore.user = { ruolo: 'VISITATORE' }

    await authMiddleware(makeRoute({ role: 'AMMINISTRATORE' }), makeRoute())

    expect(mockNavigateTo).toHaveBeenCalledWith('/')
  })

  it('non autenticato con ruolo richiesto → redirect /login', async () => {
    mockStore.isLoggedIn = false

    await authMiddleware(makeRoute({ role: 'VISITATORE' }), makeRoute())

    expect(mockNavigateTo).toHaveBeenCalledWith('/login')
  })

  it('AMMINISTRATORE accede ad area admin', async () => {
    mockStore.isLoggedIn = true
    mockStore.user = { ruolo: 'AMMINISTRATORE' }

    await authMiddleware(makeRoute({ role: 'AMMINISTRATORE' }), makeRoute())

    expect(mockNavigateTo).not.toHaveBeenCalled()
  })

  it('VISITATORE non accede ad area admin', async () => {
    mockStore.isLoggedIn = true
    mockStore.user = { ruolo: 'VISITATORE' }

    await authMiddleware(makeRoute({ role: 'AMMINISTRATORE' }), makeRoute())

    expect(mockNavigateTo).toHaveBeenCalledWith('/')
  })
})
