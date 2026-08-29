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

interface JwtPayload {
  id: number
  nome: string
  cognome: string
  email: string
  ruolo: string
  exp: number
}

function decodeJwtPayload(token: string): JwtPayload | null {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload) as JwtPayload
  } catch {
    return null
  }
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

  const isLoggedIn = computed(() => !!user.value && !!token.value)
  const isVisitatore = computed(() => user.value?.ruolo === 'VISITATORE')
  const isGestore = computed(() => user.value?.ruolo === 'GESTORE_ATTIVITA')
  const isAdmin = computed(() => user.value?.ruolo === 'AMMINISTRATORE')

  async function login(email: string, password: string) {
    try {
      const res = await authApi.login(email, password) as LoginResponse
      token.value = res.data.token
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

  function restore() {
    if (token.value) {
      const payload = decodeJwtPayload(token.value)
      if (payload && payload.exp * 1000 > Date.now()) {
        user.value = {
          id: payload.id,
          nome: payload.nome,
          cognome: payload.cognome,
          email: payload.email,
          ruolo: payload.ruolo,
        }
        return
      }
    }
    logout()
  }

  function logout() {
    user.value = null
    token.value = null
  }

  restore()

  return {
    user, token,
    isLoggedIn, isVisitatore, isGestore, isAdmin,
    login, register, logout, restore,
  }
})
