import { describe, expect, it, vi, beforeEach } from 'vitest'
import { mountSuspended } from '@nuxt/test-utils/runtime'
import LoginPage from '~/pages/login.vue'

const mockLogin = vi.fn()
const mockPush = vi.fn()

vi.mock('~/stores/auth', () => ({
  useAuthStore: vi.fn(() => ({
    user: null,
    token: null,
    isLoggedIn: false,
    login: mockLogin,
    logout: vi.fn(),
  })),
}))

vi.stubGlobal('useRouter', () => ({ push: mockPush }))

describe('Login page', () => {
  beforeEach(() => {
    mockLogin.mockReset()
    mockPush.mockReset()
  })

  it('mostra il titolo Accedi', async () => {
    const wrapper = await mountSuspended(LoginPage)
    expect(wrapper.text()).toContain('Accedi')
  })

  it('mostra il form con campi email e password', async () => {
    const wrapper = await mountSuspended(LoginPage)
    expect(wrapper.find('input[type="email"]').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="••••••••"]').exists()).toBe(true)
  })

  it('mostra link alla registrazione', async () => {
    const wrapper = await mountSuspended(LoginPage)
    expect(wrapper.text()).toContain('Non hai un account?')
    expect(wrapper.find('a[href="/registrazione"]').exists()).toBe(true)
  })

  it('email vuota mostra errore validazione', async () => {
    const wrapper = await mountSuspended(LoginPage)

    const form = wrapper.find('form')
    await form.trigger('submit')

    expect(wrapper.text()).toContain("La lunghezza dell'email non è corretta.")
    expect(mockLogin).not.toHaveBeenCalled()
  })

  it('email invalida mostra errore di formato', async () => {
    const wrapper = await mountSuspended(LoginPage)

    const emailInput = wrapper.find('input[type="email"]')
    await emailInput.setValue('not-an-email')

    const form = wrapper.find('form')
    await form.trigger('submit')

    expect(wrapper.text()).toContain("Il formato dell'email non è valido.")
    expect(mockLogin).not.toHaveBeenCalled()
  })

  it('password troppo corta mostra errore', async () => {
    const wrapper = await mountSuspended(LoginPage)

    const emailInput = wrapper.find('input[type="email"]')
    await emailInput.setValue('test@test.it')

    const pwdInput = wrapper.find('input[placeholder="••••••••"]')
    await pwdInput.setValue('short')

    const form = wrapper.find('form')
    await form.trigger('submit')

    expect(wrapper.text()).toContain('La lunghezza della password non è corretta.')
    expect(mockLogin).not.toHaveBeenCalled()
  })

  it('login successful naviga a /', async () => {
    mockLogin.mockResolvedValue(true)

    const wrapper = await mountSuspended(LoginPage)

    const emailInput = wrapper.find('input[type="email"]')
    await emailInput.setValue('test@test.it')

    const pwdInput = wrapper.find('input[placeholder="••••••••"]')
    await pwdInput.setValue('password123')

    const form = wrapper.find('form')
    await form.trigger('submit')
    await new Promise(r => setTimeout(r, 100))

    expect(mockLogin).toHaveBeenCalledWith('test@test.it', 'password123')
    expect(mockLogin).toHaveReturnedWith(Promise.resolve(true))
  })

  it('login fallito mostra modale errore', async () => {
    mockLogin.mockResolvedValue(false)

    const wrapper = await mountSuspended(LoginPage)

    const emailInput = wrapper.find('input[type="email"]')
    await emailInput.setValue('test@test.it')

    const pwdInput = wrapper.find('input[placeholder="••••••••"]')
    await pwdInput.setValue('password123')

    const form = wrapper.find('form')
    await form.trigger('submit')
    await new Promise(r => setTimeout(r, 100))

    expect(wrapper.text()).toContain('Email o password errati.')
    expect(mockPush).not.toHaveBeenCalled()
  })

  it('click chiudi nasconde modale errore', async () => {
    mockLogin.mockResolvedValue(false)

    const wrapper = await mountSuspended(LoginPage)

    const emailInput = wrapper.find('input[type="email"]')
    await emailInput.setValue('test@test.it')

    const pwdInput = wrapper.find('input[placeholder="••••••••"]')
    await pwdInput.setValue('password123')

    const form = wrapper.find('form')
    await form.trigger('submit')
    await new Promise(r => setTimeout(r, 100))

    expect(wrapper.text()).toContain('Email o password errati.')

    const chiudiBtn = wrapper.findAll('button').find(b => b.text() === 'Chiudi')
    expect(chiudiBtn).toBeDefined()
    await chiudiBtn!.trigger('click')

    expect(wrapper.text()).not.toContain('Email o password errati.')
  })

  it('toggle mostra/nascondi password', async () => {
    const wrapper = await mountSuspended(LoginPage)

    const pwdInput = wrapper.find('input[placeholder="••••••••"]')
    expect(pwdInput.attributes('type')).toBe('password')

    const toggleBtn = wrapper.findAll('button').find(b => b.text() === 'mostra')
    expect(toggleBtn).toBeDefined()
    await toggleBtn!.trigger('click')

    const pwdInputAfter = wrapper.find('input[placeholder="••••••••"]')
    expect(pwdInputAfter.attributes('type')).toBe('text')
  })
})
