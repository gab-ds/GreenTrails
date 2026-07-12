export function useApi() {
  const config = useRuntimeConfig()
  const BASE = config.public.apiBaseUrl || 'http://localhost:8080/api'

  function authHeaders(): Record<string, string> {
    const creds = useCookie('credenziali').value
    return creds ? { Authorization: `Basic ${creds}` } : {}
  }

  return {
    activities: {
      list: () => $fetch(`${BASE}/attivita/all`),
      get: (id: number) => $fetch(`${BASE}/attivita/${id}`),
      byPrice: (limite: number) => $fetch(`${BASE}/attivita/perPrezzo`, { params: { limite } }),
      accommodations: (limite: number) => $fetch(`${BASE}/attivita/alloggi`, { params: { limite } }),
      touristActivities: (limite: number) => $fetch(`${BASE}/attivita/attivitaTuristiche`, { params: { limite } }),
      myActivities: () => $fetch(`${BASE}/attivita/perGestore`, { headers: authHeaders() }),
      create: (data: FormData) => $fetch(`${BASE}/attivita`, { method: 'POST', body: data, headers: authHeaders() }),
      update: (id: number, data: Record<string, unknown>) => $fetch(`${BASE}/attivita/${id}`, { method: 'POST', body: data, headers: authHeaders() }),
      delete: (id: number) => $fetch(`${BASE}/attivita/${id}`, { method: 'DELETE', headers: authHeaders() }),
    },
    camere: {
      perAlloggio: (idAlloggio: number) => $fetch(`${BASE}/camere/perAlloggio/${idAlloggio}`),
      get: (id: number) => $fetch(`${BASE}/camere/${id}`),
      create: (data: Record<string, unknown>) => $fetch(`${BASE}/camere`, { method: 'POST', body: data, headers: authHeaders() }),
      delete: (id: number) => $fetch(`${BASE}/camere/${id}`, { method: 'DELETE', headers: authHeaders() }),
    },
    categorie: {
      get: (id: number) => $fetch(`${BASE}/categorie/${id}`, { headers: authHeaders() }),
      add: (idAttivita: number, id: number) =>
        $fetch(`${BASE}/categorie/${id}`, { method: 'POST', params: { idAttivita }, headers: authHeaders() }),
      remove: (id: number, idAttivita: number) =>
        $fetch(`${BASE}/categorie/${id}`, { method: 'DELETE', params: { idAttivita }, headers: authHeaders() }),
    },
    auth: {
      login: (email: string, password: string) =>
        $fetch(`${BASE}/utenti`, {
          headers: { Authorization: `Basic ${btoa(`${email}:${password}`)}` },
        }),
      register: (data: Record<string, unknown>, isGestore: boolean) =>
        $fetch(`${BASE}/utenti`, { method: 'PUT', params: { isGestore }, body: data }),
      invioQuestionario: (params: Record<string, unknown>) =>
        $fetch(`${BASE}/utenti/questionario`, { method: 'POST', params, headers: authHeaders() }),
      getPreferenze: () => $fetch(`${BASE}/utenti/preferenze`, { headers: authHeaders() }),
    },
    itinerari: {
      list: () => $fetch(`${BASE}/itinerari`, { headers: authHeaders() }),
      get: (id: number) => $fetch(`${BASE}/itinerari/${id}`, { headers: authHeaders() }),
      create: () => $fetch(`${BASE}/itinerari`, { method: 'POST', headers: authHeaders() }),
      genera: (userId: number) =>
        $fetch(`${BASE}/itinerari/genera`, { method: 'POST', body: { param: userId }, headers: authHeaders() }),
      delete: (id: number) => $fetch(`${BASE}/itinerari/${id}`, { method: 'DELETE', headers: authHeaders() }),
    },
    prenotazioniAlloggio: {
      create: (data: Record<string, unknown>) => $fetch(`${BASE}/prenotazioni-alloggio`, { method: 'POST', body: data, headers: authHeaders() }),
      confirm: (id: number, data: Record<string, unknown>) =>
        $fetch(`${BASE}/prenotazioni-alloggio/${id}`, { method: 'POST', body: data, headers: authHeaders() }),
      delete: (id: number) => $fetch(`${BASE}/prenotazioni-alloggio/${id}`, { method: 'DELETE', headers: authHeaders() }),
      disponibilita: (idCamera: number, dataInizio: string, dataFine: string) =>
        $fetch(`${BASE}/prenotazioni-alloggio/perCamera/${idCamera}/disponibilita`, {
          params: { idCamera, dataInizio, dataFine }, headers: authHeaders() }),
    },
    prenotazioniAttivita: {
      create: (data: Record<string, unknown>) =>
        $fetch(`${BASE}/prenotazioni-attivita-turistica`, { method: 'POST', body: data, headers: authHeaders() }),
      confirm: (id: number, data: Record<string, unknown>) =>
        $fetch(`${BASE}/prenotazioni-attivita-turistica/${id}`, { method: 'POST', body: data, headers: authHeaders() }),
      delete: (id: number) =>
        $fetch(`${BASE}/prenotazioni-attivita-turistica/${id}`, { method: 'DELETE', headers: authHeaders() }),
      disponibilita: (idAttivita: number, dataInizio: string) =>
        $fetch(`${BASE}/prenotazioni-attivita-turistica/perAttivita/${idAttivita}/disponibilita`, {
          params: { idAttivita, dataInizio }, headers: authHeaders() }),
    },
    recensioni: {
      perAttivita: (idAttivita: number) => $fetch(`${BASE}/recensioni/perAttivita/${idAttivita}`),
      get: (id: number) => $fetch(`${BASE}/recensioni/${id}`),
      create: (data: FormData) => $fetch(`${BASE}/recensioni`, { method: 'POST', body: data, headers: authHeaders() }),
    },
    segnalazioni: {
      list: (isForRecensione: boolean) =>
        $fetch(`${BASE}/segnalazioni`, { params: { isForRecensione }, headers: authHeaders() }),
      create: (data: FormData) => $fetch(`${BASE}/segnalazioni`, { method: 'POST', body: data, headers: authHeaders() }),
    },
    ricerca: {
      perPosizione: (latitudine: number, longitudine: number, raggio: number) =>
        $fetch(`${BASE}/ricerca/perPosizione`, { method: 'POST', params: { latitudine, longitudine, raggio } }),
    },
    upload: {
      list: (media: string) => $fetch(`${BASE}/file/${media}`),
      get: (media: string, filename: string) => $fetch(`${BASE}/file/${media}/${filename}`, { responseType: 'blob' }),
    },
    registrazione: {
      register: (userData: Record<string, unknown>) => $fetch(`${BASE}/v1/greentrails`, { method: 'POST', body: userData }),
    },
    valori: {
      create: (params: Record<string, unknown>) =>
        $fetch(`${BASE}/valori`, { method: 'POST', params, headers: authHeaders() }),
      update: (id: number, params: Record<string, unknown>) =>
        $fetch(`${BASE}/valori/${id}`, { method: 'POST', params, headers: authHeaders() }),
    },
  }
}
