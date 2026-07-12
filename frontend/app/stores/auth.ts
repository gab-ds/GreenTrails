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
      const res = await authApi.login(email, password) as Utente
      user.value = res
      credenziali.value = creds
      userCookie.value = JSON.stringify(user.value)
      return true
    } catch {
      return false
    }
  }

  async function register(data: Record<string, unknown>, isGestore: boolean) {
    try {
      const res = await authApi.register(data, isGestore) as Utente
      user.value = res
      const creds = btoa(`${data.email}:${data.password}`)
      credenziali.value = creds
      userCookie.value = JSON.stringify(user.value)
      return true
    } catch {
      return false
    }
  }

  function restore() {
    const stored = userCookie.value
    if (stored) {
      try {
        user.value = JSON.parse(stored)
      } catch {
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
