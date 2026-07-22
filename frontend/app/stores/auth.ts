import { defineStore } from 'pinia'

interface Utente {
  id: number
  nome: string
  cognome: string
  email: string
  ruolo: string
}

export const useAuthStore = defineStore('auth', () => {
  const { auth: authApi } = useApi()
  const user = ref<Utente | null>(null)
  const credenziali = useCookie<string | null>('credenziali', { default: () => null })
  const userCookie = useCookie<string | null>('user', { default: () => null })

  const isLoggedIn = computed(() => !!user.value)
  const isVisitatore = computed(() => user.value?.ruolo === 'VISITATORE')
  const isGestore = computed(() => user.value?.ruolo === 'GESTORE_ATTIVITA')
  const isAdmin = computed(() => user.value?.ruolo === 'AMMINISTRATORE')

  async function login(email: string, password: string) {
    try {
      const creds = btoa(`${email}:${password}`)
      const res = await authApi.login(email, password) as { status: string; data: Utente }
      user.value = res.data
      credenziali.value = creds
      userCookie.value = JSON.stringify(user.value)
      return true
    } catch (err) {
      console.error('Login error:', err)
      return false
    }
  }

  async function register(data: Record<string, unknown>, isGestore: boolean) {
    try {
      const res = await authApi.register(data, isGestore) as { status: string; data: Utente }
      user.value = res.data
      const creds = btoa(`${data.email}:${data.password}`)
      credenziali.value = creds
      userCookie.value = JSON.stringify(user.value)
      return true
    } catch (err) {
      console.error('Register error:', err)
      return false
    }
  }

  function restore() {
    const stored = userCookie.value
    if (stored) {
      try {
        user.value = JSON.parse(stored)
      } catch (err) {
        console.error('Restore user error:', err)
        logout()
      }
    }
  }

  function logout() {
    user.value = null
    credenziali.value = null
    userCookie.value = null
  }

  restore()

  return {
    user, credenziali,
    isLoggedIn, isVisitatore, isGestore, isAdmin,
    login, register, logout, restore,
  }
})
