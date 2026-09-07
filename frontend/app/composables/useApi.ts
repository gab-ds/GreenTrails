export function useApi() {
  const config = useRuntimeConfig()
  const BASE = import.meta.server
    ? (config.apiBaseUrl || 'http://localhost:8080/api')
    : (config.public.apiBaseUrl || 'http://localhost:8080/api')

  return {
    activities: {
      list: () => $fetch(`${BASE}/attivita/all`),
      get: (id: number) => $fetch(`${BASE}/attivita/${id}`),
      byPrice: (limite: number) => $fetch(`${BASE}/attivita/perPrezzo`, { params: { limite } }),
      accommodations: (limite: number) => $fetch(`${BASE}/attivita/alloggi`, { params: { limite } }),
      touristActivities: (limite: number) => $fetch(`${BASE}/attivita/attivitaTuristiche`, { params: { limite } }),
      myActivities: () => $fetch(`${BASE}/attivita/perGestore`, { credentials: 'include' }),
      create: (data: FormData) => $fetch(`${BASE}/attivita`, { method: 'POST', body: data, credentials: 'include' }),
      update: (id: number, data: FormData) => $fetch(`${BASE}/attivita/${id}`, { method: 'POST', body: data, credentials: 'include' }),
      delete: (id: number) => $fetch(`${BASE}/attivita/${id}`, { method: 'DELETE', credentials: 'include' }),
    },
    camere: {
      perAlloggio: (idAlloggio: number) => $fetch(`${BASE}/camere/perAlloggio/${idAlloggio}`),
      get: (id: number) => $fetch(`${BASE}/camere/${id}`),
      create: (data: Record<string, unknown>) => $fetch(`${BASE}/camere`, { method: 'POST', body: data, credentials: 'include' }),
      delete: (id: number) => $fetch(`${BASE}/camere/${id}`, { method: 'DELETE', credentials: 'include' }),
    },
    categorie: {
      get: (id: number) => $fetch(`${BASE}/categorie/${id}`, { credentials: 'include' }),
      add: (idAttivita: number, id: number) =>
        $fetch(`${BASE}/categorie/${id}`, { method: 'POST', params: { idAttivita }, credentials: 'include' }),
      remove: (id: number, idAttivita: number) =>
        $fetch(`${BASE}/categorie/${id}`, { method: 'DELETE', params: { idAttivita }, credentials: 'include' }),
    },
    auth: {
      login: (email: string, password: string) =>
        $fetch(`${BASE}/auth/login`, {
          method: 'POST',
          body: { email, password },
          credentials: 'include',
        }),
      logout: () =>
        $fetch(`${BASE}/auth/logout`, {
          method: 'POST',
          credentials: 'include',
        }),
      getCurrentUser: () =>
        $fetch(`${BASE}/utenti`, {
          credentials: 'include',
        }),
      register: (data: Record<string, unknown>, isGestore: boolean) =>
        $fetch(`${BASE}/utenti`, { method: 'PUT', params: { isGestore }, body: data, credentials: 'include' }),
      invioQuestionario: (params: Record<string, unknown>) =>
        $fetch(`${BASE}/utenti/questionario`, { method: 'POST', params, credentials: 'include' }),
      getPreferenze: () => $fetch(`${BASE}/utenti/preferenze`, { credentials: 'include' }),
    },
    itinerari: {
      list: () => $fetch(`${BASE}/itinerari`, { credentials: 'include' }),
      get: (id: number) => $fetch(`${BASE}/itinerari/${id}`, { credentials: 'include' }),
      create: () => $fetch(`${BASE}/itinerari`, { method: 'POST', credentials: 'include' }),
      genera: () => $fetch(`${BASE}/itinerari/genera`, { method: 'POST', credentials: 'include' }),
      delete: (id: number) => $fetch(`${BASE}/itinerari/${id}`, { method: 'DELETE', credentials: 'include' }),
    },
    prenotazioniAlloggio: {
      miePrenotazioni: () => $fetch(`${BASE}/prenotazioni-alloggio`, { credentials: 'include' }),
      perAttivita: (idAttivita: number) => $fetch(`${BASE}/prenotazioni-alloggio/perAttivita/${idAttivita}`, { credentials: 'include' }),
      create: (data: Record<string, unknown>) => $fetch(`${BASE}/prenotazioni-alloggio`, { method: 'POST', body: data, credentials: 'include' }),
      confirm: (id: number, data: Record<string, unknown>) =>
        $fetch(`${BASE}/prenotazioni-alloggio/${id}`, { method: 'POST', body: data, credentials: 'include' }),
      delete: (id: number) => $fetch(`${BASE}/prenotazioni-alloggio/${id}`, { method: 'DELETE', credentials: 'include' }),
      disponibilita: (idCamera: number, dataInizio: string, dataFine: string) =>
        $fetch(`${BASE}/prenotazioni-alloggio/perCamera/${idCamera}/disponibilita`, {
          params: { idCamera, dataInizio, dataFine }, credentials: 'include' }),
    },
    prenotazioniAttivita: {
      miePrenotazioni: () => $fetch(`${BASE}/prenotazioni-attivita-turistica`, { credentials: 'include' }),
      perAttivita: (idAttivita: number) => $fetch(`${BASE}/prenotazioni-attivita-turistica/perAttivita/${idAttivita}`, { credentials: 'include' }),
      create: (data: Record<string, unknown>) =>
        $fetch(`${BASE}/prenotazioni-attivita-turistica`, { method: 'POST', body: data, credentials: 'include' }),
      confirm: (id: number, data: Record<string, unknown>) =>
        $fetch(`${BASE}/prenotazioni-attivita-turistica/${id}`, { method: 'POST', body: data, credentials: 'include' }),
      delete: (id: number) =>
        $fetch(`${BASE}/prenotazioni-attivita-turistica/${id}`, { method: 'DELETE', credentials: 'include' }),
      disponibilita: (idAttivita: number, dataInizio: string) =>
        $fetch(`${BASE}/prenotazioni-attivita-turistica/perAttivita/${idAttivita}/disponibilita`, {
          params: { idAttivita, dataInizio }, credentials: 'include' }),
    },
    recensioni: {
      perAttivita: (idAttivita: number) => $fetch(`${BASE}/recensioni/perAttivita/${idAttivita}`),
      get: (id: number) => $fetch(`${BASE}/recensioni/${id}`),
      create: (data: FormData) => $fetch(`${BASE}/recensioni`, { method: 'POST', body: data, credentials: 'include' }),
      delete: (id: number) => $fetch(`${BASE}/recensioni/${id}`, { method: 'DELETE', credentials: 'include' }),
    },
    segnalazioni: {
      list: (isForRecensione: boolean) =>
        $fetch(`${BASE}/segnalazioni`, { params: { isForRecensione }, credentials: 'include' }),
      create: (data: FormData) => $fetch(`${BASE}/segnalazioni`, { method: 'POST', body: data, credentials: 'include' }),
    },
    ricerca: {
      perPosizione: (latitudine: number, longitudine: number, raggio: number) =>
        $fetch(`${BASE}/ricerca/perPosizione`, { method: 'POST', params: { latitudine, longitudine, raggio } }),
    },
    upload: {
      list: (media: string) => $fetch(`${BASE}/file/${media}`),
      get: (media: string, filename: string) => $fetch(`${BASE}/file/${media}/${filename}`, { responseType: 'blob' }),
    },
    valori: {
      create: (params: Record<string, unknown>) =>
        $fetch(`${BASE}/valori`, { method: 'POST', params, credentials: 'include' }),
      update: (id: number, params: Record<string, unknown>) =>
        $fetch(`${BASE}/valori/${id}`, { method: 'POST', params, credentials: 'include' }),
    },
  }
}
