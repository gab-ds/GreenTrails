<script setup lang="ts">
definePageMeta({ middleware: 'auth' })

interface Attivita {
  id: number
  nome: string
  citta: string
  prezzo: number | null
  isAlloggio: boolean
  categoriaAlloggio?: string
  categoriaAttivitaTuristica?: string
  descrizioneBreve: string
  media: string
}

interface Preferenze {
  viaggioPreferito: string
  alloggioPreferito: string
  attivitaPreferita: string
  budgetPreferito: string
  animaleDomestico: boolean
  souvenir: boolean
}

const auth = useAuthStore()
const router = useRouter()
const api = useApi()
const { data: activitiesRes } = await useAsyncData('activities', () => api.activities.list())
const allAttivita = ref<Attivita[]>([])
const preferenze = ref<Preferenze | null>(null)
const suggeriti = ref<Attivita[]>([])
const loading = ref(true)
const failedImages = ref<Record<string, boolean>>({})

function markFailedImage(media: string) {
  failedImages.value[media] = true
}

function scoreAttivita(a: Attivita, p: Preferenze): number {
  let score = 0
  if (!a.isAlloggio && a.categoriaAttivitaTuristica) {
    if (p.attivitaPreferita !== 'NESSUNA_PREFERENZA' && a.categoriaAttivitaTuristica === p.attivitaPreferita) {
      score += 2
    }
  }
  if (a.isAlloggio && a.categoriaAlloggio) {
    if (p.alloggioPreferito !== 'NESSUNA_PREFERENZA' && a.categoriaAlloggio === p.alloggioPreferito) {
      score += 2
    }
  }
  if (a.prezzo != null && p.budgetPreferito !== 'FLESSIBILE') {
    if (p.budgetPreferito === 'BASSO' && a.prezzo < 50) score += 1
    else if (p.budgetPreferito === 'MEDIO' && a.prezzo >= 50 && a.prezzo <= 100) score += 1
    else if (p.budgetPreferito === 'ALTO' && a.prezzo > 100) score += 1
  } else if (a.prezzo != null || p.budgetPreferito === 'FLESSIBILE') {
    score += 1
  }
  return score
}

async function loadPreferenze() {
  try {
    const res = await api.auth.getPreferenze() as { status: string; data: Preferenze }
    if (res?.status === 'success' && res?.data) {
      preferenze.value = res.data
    }
  } catch (err) {
    console.error('Errore caricamento preferenze:', err)
  }
}

async function computeSuggeriti() {
  loading.value = true
  await loadPreferenze()
  if (activitiesRes.value) {
    const data = activitiesRes.value as { status: string; data: Attivita[] }
    allAttivita.value = data?.data || []
  }
  if (preferenze.value) {
    const scored = allAttivita.value
      .map(a => ({ attivita: a, score: scoreAttivita(a, preferenze.value!) }))
      .filter(x => x.score > 0)
      .sort((a, b) => b.score - a.score)
      .slice(0, 4)
      .map(x => x.attivita)
    suggeriti.value = scored
  }
  loading.value = false
}

await computeSuggeriti()

function mediaUrl(media: string): string {
  const config = useRuntimeConfig()
  const base = config.public.apiBaseUrl || 'http://localhost:8080/api'
  return `${base}/file/${media}/download`
}
</script>

<template>
  <div class="py-8">
    <h1 class="mb-8 text-center text-3xl font-bold text-green-600">
      Area Riservata
      <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
    </h1>

    <div class="mx-auto grid max-w-5xl gap-8 lg:grid-cols-3">
      <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
        <div class="mb-4 flex justify-center">
          <div class="flex h-24 w-24 items-center justify-center rounded-full bg-green-100 text-4xl text-green-600">
            {{ auth.user?.nome?.charAt(0) }}{{ auth.user?.cognome?.charAt(0) }}
          </div>
        </div>
        <dl class="space-y-2 text-sm">
          <div class="flex justify-between">
            <dt class="text-gray-500">Nome</dt>
            <dd class="font-medium text-gray-800">{{ auth.user?.nome }}</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-gray-500">Cognome</dt>
            <dd class="font-medium text-gray-800">{{ auth.user?.cognome }}</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-gray-500">Email</dt>
            <dd class="font-medium text-gray-800">{{ auth.user?.email }}</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-gray-500">Ruolo</dt>
            <dd class="font-medium text-gray-800">
              <span v-if="auth.isVisitatore" class="rounded-full bg-blue-100 px-2 py-0.5 text-xs text-blue-700">Visitatore</span>
              <span v-else-if="auth.isGestore" class="rounded-full bg-amber-100 px-2 py-0.5 text-xs text-amber-700">Gestore Attività</span>
              <span v-else class="rounded-full bg-purple-100 px-2 py-0.5 text-xs text-purple-700">Amministratore</span>
            </dd>
          </div>
        </dl>
        <UButton color="neutral" variant="ghost" block class="mt-4" @click="auth.logout(); router.push('/')">
          Esci
        </UButton>
      </div>

      <div class="lg:col-span-2">
        <div v-if="auth.isVisitatore" class="grid gap-4 sm:grid-cols-2">
          <NuxtLink to="/questionario" class="block rounded-xl border border-gray-200 bg-white p-5 shadow-sm transition hover:shadow-md">
            <div class="mb-2 flex items-center gap-2">
              <span class="rounded-full bg-green-100 p-2 text-green-600"><UIcon name="i-lucide-circle-help" class="size-5" /></span>
              <h3 class="font-semibold text-gray-800">Compila questionario</h3>
            </div>
            <p class="text-sm text-gray-500">Permettici di suggerirti il viaggio perfetto per te.</p>
          </NuxtLink>

          <div class="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div class="mb-2 flex items-center gap-2">
              <span class="rounded-full bg-blue-100 p-2 text-blue-600"><UIcon name="i-lucide-lightbulb" class="size-5" /></span>
              <h3 class="font-semibold text-gray-800">Suggeriti per te</h3>
            </div>
            <p v-if="loading" class="text-sm text-gray-500">Caricamento suggerimenti...</p>
            <p v-else-if="!preferenze" class="text-sm text-gray-500">
              Compila prima il
              <NuxtLink to="/questionario" class="font-medium text-green-600 underline">questionario</NuxtLink>
              per ricevere suggerimenti.
            </p>
            <p v-else-if="suggeriti.length === 0" class="text-sm text-gray-500">
              Nessuna attività corrisponde alle tue preferenze.
            </p>
            <NuxtLink
              v-for="a in suggeriti"
              :key="a.id"
              :to="`/attivita/${a.id}`"
              class="mt-3 flex items-center gap-3 rounded-lg border border-gray-100 p-3 transition hover:bg-gray-50"
            >
              <img
                v-if="!failedImages[a.media]"
                :src="mediaUrl(a.media)"
                :alt="a.nome"
                class="h-14 w-14 flex-shrink-0 rounded-lg object-cover"
                @error="markFailedImage(a.media)"
              >
              <div class="min-w-0">
                <p class="truncate font-medium text-gray-800">{{ a.nome }}</p>
                <p class="truncate text-xs text-gray-500">{{ a.citta }}{{ a.prezzo != null ? ` — ${a.prezzo.toFixed(2)} €` : '' }}</p>
              </div>
            </NuxtLink>
          </div>

          <NuxtLink to="/i-miei-viaggi" class="block rounded-xl border border-gray-200 bg-white p-5 shadow-sm transition hover:shadow-md">
            <div class="mb-2 flex items-center gap-2">
              <span class="rounded-full bg-amber-100 p-2 text-amber-600"><UIcon name="i-lucide-plane" class="size-5" /></span>
              <h3 class="font-semibold text-gray-800">I miei viaggi</h3>
            </div>
            <p class="text-sm text-gray-500">Consulta tutte le tue prenotazioni.</p>
          </NuxtLink>
          <NuxtLink to="/itinerario-automatico" class="block rounded-xl border border-gray-200 bg-white p-5 shadow-sm transition hover:shadow-md">
            <div class="mb-2 flex items-center gap-2">
              <span class="rounded-full bg-purple-100 p-2 text-purple-600"><UIcon name="i-lucide-wand" class="size-5" /></span>
              <h3 class="font-semibold text-gray-800">Genera itinerario</h3>
            </div>
            <p class="text-sm text-gray-500">Lascia che l'AI generi l'itinerario perfetto.</p>
          </NuxtLink>
        </div>

        <div v-else-if="auth.isGestore" class="grid gap-4 sm:grid-cols-2">
          <NuxtLink to="/mie-attivita" class="block rounded-xl border border-gray-200 bg-white p-5 shadow-sm transition hover:shadow-md">
            <div class="mb-2 flex items-center gap-2">
              <span class="rounded-full bg-amber-100 p-2 text-amber-600"><UIcon name="i-lucide-building" class="size-5" /></span>
              <h3 class="font-semibold text-gray-800">Le mie attività</h3>
            </div>
            <p class="text-sm text-gray-500">Visualizza e modifica le tue attività.</p>
          </NuxtLink>
          <NuxtLink to="/tabella-prenotazioni" class="block rounded-xl border border-gray-200 bg-white p-5 shadow-sm transition hover:shadow-md">
            <div class="mb-2 flex items-center gap-2">
              <span class="rounded-full bg-blue-100 p-2 text-blue-600"><UIcon name="i-lucide-calendar-check" class="size-5" /></span>
              <h3 class="font-semibold text-gray-800">Prenotazioni</h3>
            </div>
            <p class="text-sm text-gray-500">Visualizza tutte le prenotazioni.</p>
          </NuxtLink>
          <NuxtLink to="/inserimento-attivita" class="block rounded-xl border border-gray-200 bg-white p-5 shadow-sm transition hover:shadow-md">
            <div class="mb-2 flex items-center gap-2">
              <span class="rounded-full bg-green-100 p-2 text-green-600"><UIcon name="i-lucide-plus-circle" class="size-5" /></span>
              <h3 class="font-semibold text-gray-800">Inserisci attività</h3>
            </div>
            <p class="text-sm text-gray-500">Crea una nuova attività ricettiva.</p>
          </NuxtLink>
        </div>

        <div v-else-if="auth.isAdmin" class="grid gap-4 sm:grid-cols-2">
          <NuxtLink to="/lista-segnalazioni" class="block rounded-xl border border-gray-200 bg-white p-5 shadow-sm transition hover:shadow-md">
            <div class="mb-2 flex items-center gap-2">
              <span class="rounded-full bg-purple-100 p-2 text-purple-600"><UIcon name="i-lucide-shield-alert" class="size-5" /></span>
              <h3 class="font-semibold text-gray-800">Segnalazioni</h3>
            </div>
            <p class="text-sm text-gray-500">Visualizza la lista delle segnalazioni.</p>
          </NuxtLink>
        </div>
      </div>
    </div>
  </div>
</template>
