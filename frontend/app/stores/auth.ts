import { defineStore } from 'pinia'

interface Utente {
  id: number
  nome: string
  cognome: string
  email: string
  ruolo: string
}

interface LoginResponse {
  status: string
  data: { token: string; utente: Utente }
}

export const useAuthStore = defineStore('auth', () => {
  const { auth: authApi } = useApi()
  const user = ref<Utente | null>(null)
  const token = useCookie<string | null>('token', {
    default: () => null,
    httpOnly: false,
    secure: true,
    sameSite: 'strict',
    maxAge: 3600,
  })
  const userCookie = useCookie<string | null>('user', { default: () => null })

  const isLoggedIn = computed(() => !!user.value && !!token.value)
  const isVisitatore = computed(() => user.value?.ruolo === 'VISITATORE')
  const isGestore = computed(() => user.value?.ruolo === 'GESTORE_ATTIVITA')
  const isAdmin = computed(() => user.value?.ruolo === 'AMMINISTRATORE')

  async function login(email: string, password: string) {
    try {
      const res = await authApi.login(email, password) as LoginResponse
      user.value = res.data.utente
      token.value = res.data.token
      userCookie.value = JSON.stringify(user.value)
      return true
    } catch (err) {
      console.error('Login error:', err)
      return false
    }
  }

  async function register(data: Record<string, unknown>, isGestore: boolean) {
    try {
      await authApi.register(data, isGestore)
      return await login(data.email as string, data.password as string)
    } catch (err) {
      console.error('Register error:', err)
      return false
    }
  }

  function restore() {
    const stored = userCookie.value
    if (stored && token.value) {
      try {
        user.value = JSON.parse(stored)
      } catch (err) {
        console.error('Restore user error:', err)
        logout()
      }
    } else {
      logout()
    }
  }

  function logout() {
    user.value = null
    token.value = null
    userCookie.value = null
  }

  restore()

  return {
    user, token,
    isLoggedIn, isVisitatore, isGestore, isAdmin,
    login, register, logout, restore,
  }
})
