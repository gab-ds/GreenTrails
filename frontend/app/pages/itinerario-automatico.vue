<script setup lang="ts">
definePageMeta({ middleware: 'auth', role: 'VISITATORE' })

const { auth: authApi, itinerari: itinerariApi } = useApi()
const router = useRouter()

interface Preferenze {
  viaggioPreferito?: string
  alloggioPreferito?: string
  preferenzaAlimentare?: string
  attivitaPreferita?: string
  animaleDomestico?: boolean
  budgetPreferito?: string
  souvenir?: boolean
  stagioniPreferite?: string
}

interface PrenotazioneAlloggioLight {
  id: number
  camera: { alloggio: { nome: string }; tipoCamera: string } | null
  numCamere: number
  prezzo: number
  dataInizio: string
  dataFine: string
}

interface PrenotazioneAttivitaLight {
  id: number
  attivitaTuristica: { nome: string } | null
  numAdulti: number
  numBambini: number
  prezzo: number
  dataInizio: string
}

interface ItinerarioCompleto {
  itinerario: { id: number; stato: string; totale: number }
  prenotazioniAlloggio: PrenotazioneAlloggioLight[]
  prenotazioniAttivitaTuristica: PrenotazioneAttivitaLight[]
}

const preferenze = ref<Preferenze | null>(null)
const loadingPref = ref(true)
const generating = ref(false)
const itinerario = ref<ItinerarioCompleto | null>(null)
const error = ref('')
const showNoQuestionario = ref(false)

const labelMap: Record<string, string> = {
  MONTAGNA: 'Montagna', MARE: 'Mare', CITTA: 'Città', NESSUNA_PREFERENZA: 'Nessuna preferenza',
  HOTEL: 'Hotel', BED_AND_BREAKFAST: 'Bed & Breakfast', VILLAGGIO_TURISTICO: 'Villaggio Turistico', OSTELLO: 'Ostello',
  VEGAN: 'Vegan', VEGETARIAN: 'Vegetariano', GLUTEN_FREE: 'Gluten Free',
  ALL_APERTO: "All'aperto", VISITE_CULTURALI_STORICHE: 'Visite Culturali', RELAX: 'Relax', GASTRONOMIA: 'Gastronomia',
  BASSO: 'Basso', MEDIO: 'Medio', ALTO: 'Alto', FLESSIBILE: 'Flessibile',
  AUTUNNO_INVERNO: 'Autunno/Inverno', PRIMAVERA_ESTATE: 'Primavera/Estate',
}

function label(val: string | undefined | null): string {
  return val ? labelMap[val] || val : '—'
}

onMounted(async () => {
  try {
    const res = await authApi.getPreferenze() as { status: string; data: Preferenze | null }
    if (res.status === 'success' && res.data) {
      preferenze.value = res.data
    }
  } catch (err) {
    console.error('Errore caricamento preferenze:', err)
  }
  loadingPref.value = false
})

async function genera() {
  error.value = ''
  generating.value = true
  try {
    const res = await itinerariApi.genera() as { status: string; data: { id: number } }
    if (res.status === 'success') {
      const dettagli = await itinerariApi.get(res.data.id) as { status: string; data: ItinerarioCompleto }
      if (dettagli.status === 'success') {
        itinerario.value = dettagli.data
      } else {
        error.value = 'Errore nel caricamento dell\'itinerario.'
      }
    } else {
      showNoQuestionario.value = true
    }
  } catch (err) {
    console.error('Errore generazione itinerario:', err)
    error.value = 'Errore durante la generazione.'
  }
  generating.value = false
}
</script>

<template>
  <div class="py-8">
    <h1 class="mb-8 text-center text-3xl font-bold text-green-600">
      Genera itinerario
      <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
    </h1>

    <div v-if="loadingPref" class="text-center text-gray-400">Caricamento...</div>

    <div v-else-if="!preferenze" class="mx-auto max-w-md text-center">
      <p class="mb-4 text-gray-500">Non hai ancora compilato il questionario. Compilalo per ricevere suggerimenti personalizzati.</p>
      <UButton color="primary" @click="router.push('/questionario')">Vai al questionario</UButton>
    </div>

    <div v-else class="mx-auto max-w-4xl space-y-8">
      <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
        <h2 class="mb-4 text-lg font-semibold text-gray-800">Le tue preferenze</h2>
        <dl class="grid grid-cols-1 gap-3 sm:grid-cols-2">
          <div>
            <dt class="text-xs font-medium uppercase tracking-wide text-gray-400">Viaggio preferito</dt>
            <dd class="text-gray-800">{{ label(preferenze.viaggioPreferito) }}</dd>
          </div>
          <div>
            <dt class="text-xs font-medium uppercase tracking-wide text-gray-400">Alloggio preferito</dt>
            <dd class="text-gray-800">{{ label(preferenze.alloggioPreferito) }}</dd>
          </div>
          <div>
            <dt class="text-xs font-medium uppercase tracking-wide text-gray-400">Preferenza alimentare</dt>
            <dd class="text-gray-800">{{ label(preferenze.preferenzaAlimentare) }}</dd>
          </div>
          <div>
            <dt class="text-xs font-medium uppercase tracking-wide text-gray-400">Attività preferita</dt>
            <dd class="text-gray-800">{{ label(preferenze.attivitaPreferita) }}</dd>
          </div>
          <div>
            <dt class="text-xs font-medium uppercase tracking-wide text-gray-400">Animale domestico</dt>
            <dd class="text-gray-800">{{ preferenze.animaleDomestico ? 'Sì' : 'No' }}</dd>
          </div>
          <div>
            <dt class="text-xs font-medium uppercase tracking-wide text-gray-400">Budget</dt>
            <dd class="text-gray-800">{{ label(preferenze.budgetPreferito) }}</dd>
          </div>
          <div>
            <dt class="text-xs font-medium uppercase tracking-wide text-gray-400">Souvenir</dt>
            <dd class="text-gray-800">{{ preferenze.souvenir ? 'Sì' : 'No' }}</dd>
          </div>
          <div>
            <dt class="text-xs font-medium uppercase tracking-wide text-gray-400">Stagioni</dt>
            <dd class="text-gray-800">{{ label(preferenze.stagioniPreferite) }}</dd>
          </div>
        </dl>
      </div>

      <div class="text-center">
        <UButton color="primary" size="lg" :loading="generating" @click="genera">
          Genera itinerario
        </UButton>
      </div>

      <div v-if="error" class="rounded-lg bg-red-50 p-4 text-sm text-red-600">{{ error }}</div>

      <div v-if="itinerario" class="rounded-xl border border-green-200 bg-green-50 p-6 shadow-sm">
        <h2 class="mb-4 text-xl font-semibold text-green-700">Itinerario generato</h2>
        <p class="mb-2 text-gray-700">Stato: <span class="font-medium">{{ itinerario.itinerario.stato }}</span></p>
        <p class="mb-4 text-gray-700">Totale: <span class="text-xl font-bold text-green-600">{{ itinerario.itinerario.totale }}€</span></p>

        <div v-if="itinerario.prenotazioniAlloggio.length" class="mb-4">
          <h3 class="mb-2 font-medium text-gray-800">Alloggi prenotati</h3>
          <div v-for="p in itinerario.prenotazioniAlloggio" :key="p.id" class="mb-2 rounded-lg border border-green-100 bg-white p-3 text-sm">
            <p class="font-medium">{{ p.camera?.alloggio?.nome }}</p>
            <p class="text-gray-500">{{ p.camera?.tipoCamera }} — {{ p.numCamere }} camera/e — {{ p.prezzo }}€</p>
            <p class="text-gray-500">{{ new Date(p.dataInizio).toLocaleDateString('it-IT') }} → {{ new Date(p.dataFine).toLocaleDateString('it-IT') }}</p>
          </div>
        </div>

        <div v-if="itinerario.prenotazioniAttivitaTuristica.length">
          <h3 class="mb-2 font-medium text-gray-800">Attività turistiche</h3>
          <div v-for="p in itinerario.prenotazioniAttivitaTuristica" :key="p.id" class="mb-2 rounded-lg border border-green-100 bg-white p-3 text-sm">
            <p class="font-medium">{{ p.attivitaTuristica?.nome }}</p>
            <p class="text-gray-500">{{ p.numAdulti + p.numBambini }} posti — {{ p.prezzo }}€</p>
            <p class="text-gray-500">{{ new Date(p.dataInizio).toLocaleDateString('it-IT') }}</p>
          </div>
        </div>

        <div v-if="!itinerario.prenotazioniAlloggio.length && !itinerario.prenotazioniAttivitaTuristica.length" class="text-sm text-gray-500">
          Nessuna prenotazione inclusa nell'itinerario.
        </div>
      </div>
    </div>

    <div
      v-if="showNoQuestionario"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
    >
      <div class="mx-4 w-full max-w-sm rounded-xl bg-white p-8 text-center shadow-xl">
        <div class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-red-100">
          <UIcon name="i-lucide-alert-circle" class="size-8 text-red-600" />
        </div>
        <h2 class="mb-2 text-xl font-bold text-gray-900">Questionario non compilato</h2>
        <p class="mb-6 text-sm text-gray-500">
          Compila prima il questionario per generare un itinerario.
        </p>
        <button
          type="button"
          class="mb-3 w-full rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-green-700"
          @click="router.push('/questionario')"
        >
          Vai al questionario
        </button>
        <button
          type="button"
          class="w-full rounded-lg bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-200"
          @click="showNoQuestionario = false"
        >
          Annulla
        </button>
      </div>
    </div>
  </div>
</template>
