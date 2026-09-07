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
  data: { utente: Utente }
}

interface UserResponse {
  status: string
  data: Utente
}

export const useAuthStore = defineStore('auth', () => {
  const { auth: authApi } = useApi()
  const user = ref<Utente | null>(null)

  const isLoggedIn = computed(() => !!user.value)
  const isVisitatore = computed(() => user.value?.ruolo === 'VISITATORE')
  const isGestore = computed(() => user.value?.ruolo === 'GESTORE_ATTIVITA')
  const isAdmin = computed(() => user.value?.ruolo === 'AMMINISTRATORE')

  async function login(email: string, password: string) {
    try {
      const res = await authApi.login(email, password) as LoginResponse
      user.value = res.data.utente
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

  async function restore() {
    try {
      const res = await authApi.getCurrentUser() as UserResponse
      user.value = res.data
    } catch {
      user.value = null
    }
  }

  async function logout() {
    try {
      await authApi.logout()
    } finally {
      user.value = null
    }
  }

  return {
    user,
    isLoggedIn, isVisitatore, isGestore, isAdmin,
    login, register, logout, restore,
  }
})
