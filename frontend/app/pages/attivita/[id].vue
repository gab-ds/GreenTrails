<script setup lang="ts">
import { nextTick } from 'vue'

const route = useRoute()
const id = Number(route.params.id)
const { activities, camere: camereApi, recensioni: recensioniApi, segnalazioni: segnalazioniApi, upload } = useApi()
const auth = useAuthStore()
const config = useRuntimeConfig()
const BASE = config.public.apiBaseUrl || 'http://localhost:8080/api'

const { data: rawAttivita, error } = await useAsyncData('attivita', () => activities.get(id))
const { data: rawRecensioni, refresh: refreshRecensioni } = await useAsyncData('recensioni', () => recensioniApi.perAttivita(id))

interface AttivitaData {
  id: number
  nome: string
  citta: string
  provincia: string
  prezzo: number
  alloggio: boolean
  coordinate: { x: number; y: number } | null
  descrizioneBreve: string
  descrizioneLunga: string | null
  media: string | null
  valoriEcosostenibilita: Record<string, boolean | number> | null
  categorie: { id: number; nome: string }[] | null
}

interface RecensioneData {
  id: number
  visitatore: { id: number; nome: string } | null
  valutazioneStelleEsperienza: number
  descrizione: string
}

const attivita = computed(() => (rawAttivita.value as { status: string; data: AttivitaData } | undefined)?.data ?? null)
const recensioni = computed(() => (rawRecensioni.value as { status: string; data: RecensioneData[] } | undefined)?.data ?? [])

const camere = ref<Camera[]>([])
const immagini = ref<string[]>([])
const galleryOpen = ref(false)
const galleryIndex = ref(0)
const loadingCamere = ref(false)

interface Camera {
  id: number
  tipoCamera: string
  disponibilita: number
  descrizione: string
  capienza: number
  prezzo: number
}

const isAlloggio = computed(() => attivita.value?.alloggio)
const coordinate = computed(() => attivita.value?.coordinate)
const lat = computed(() => coordinate.value?.y ?? 40.68)
const lng = computed(() => coordinate.value?.x ?? 14.77)
const valori = computed(() => attivita.value?.valoriEcosostenibilita)

const policyLabels: Record<string, string> = {
  politicheAntispreco: 'Antispreco',
  prodottiLocali: 'Prodotti Locali',
  energiaVerde: 'Energia Verde',
  raccoltaDifferenziata: 'Raccolta Differenziata',
  limiteEmissioneCO2: 'Limite Emissione CO₂',
  contattoConNatura: 'Contatto con Natura',
}

onMounted(async () => {
  if (isAlloggio.value) {
    loadingCamere.value = true
    try {
      const res = await camereApi.perAlloggio(id) as { status: string; data: Camera[] }
      if (res.status === 'success') camere.value = res.data
    } catch (err) {
      console.error('Errore caricamento camere:', err)
    }
    loadingCamere.value = false
  }

  const media = attivita.value?.media
  if (media) {
    try {
      const files = await upload.list(media) as string[]
      if (Array.isArray(files)) {
        immagini.value = files.map((f: string) => `${BASE}/file/${media}/${f}`)
      }
    } catch (err) {
      console.error('Errore caricamento media:', err)
    }
  }

  if (coordinate.value && typeof document !== 'undefined') {
    await nextTick()
    const el = document.getElementById('activity-map')
    if (!el) return
    const L = (await import('leaflet')).default
    const map = L.map('activity-map', { zoomControl: true }).setView([lat.value, lng.value], 14)
    L.tileLayer('/api/tiles/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
    }).addTo(map)
    L.marker([lat.value, lng.value]).addTo(map)
    map.invalidateSize()
  }
})

const showReviewForm = ref(false)
const showPrenotazioneDialog = ref(false)
const showSegnalazioneForm = ref(false)
const reviewStars = ref(5)
const reviewDesc = ref('')
const reviewFile = ref<File | null>(null)
const segnalazioneDesc = ref('')
const segnalazioneFile = ref<File | null>(null)
const reviewError = ref('')
const segnalazioneError = ref('')
const reviewSuccess = ref('')
const segnalazioneSuccess = ref('')
const submittingReview = ref(false)
const submittingSegnalazione = ref(false)
const deleteConfirmId = ref<number | null>(null)
const deletingReview = ref(false)

function onReviewFile(e: Event) {
  const t = e.target as HTMLInputElement
  if (t.files?.length) reviewFile.value = t.files[0]
}

function onSegnalazioneFile(e: Event) {
  const t = e.target as HTMLInputElement
  if (t.files?.length) segnalazioneFile.value = t.files[0]
}

async function submitReview() {
  reviewError.value = ''
  reviewSuccess.value = ''
  if (!reviewDesc.value.trim()) {
    reviewError.value = 'Inserisci una descrizione per la recensione.'
    return
  }
  if (reviewStars.value < 1 || reviewStars.value > 5) {
    reviewError.value = 'Numero di stelle non valido.'
    return
  }
  if (reviewDesc.value.length > 255) {
    reviewError.value = 'Valutazione discorsiva troppo lunga.'
    return
  }
  if (!/^[A-Z][A-Za-z0-9À-ú\s'-,.!?]*$/.test(reviewDesc.value)) {
    reviewError.value = 'Formato della valutazione discorsiva non valido.'
    return
  }
  if (reviewFile.value && !reviewFile.value.type.startsWith('image/')) {
    reviewError.value = 'Tipo di file non supportato.'
    return
  }
  if (reviewFile.value && reviewFile.value.size > 100 * 1024 * 1024) {
    reviewError.value = 'File troppo grande.'
    return
  }
  submittingReview.value = true
  try {
    const valoriRes = await useApi().valori.create({
      politicheAntispreco: false, prodottiLocali: false, energiaVerde: false,
      raccoltaDifferenziata: false, limiteEmissioneCO2: false, contattoConNatura: false,
    }) as { status: string; data: { id: number } }
    if (valoriRes.status !== 'success') {
      reviewError.value = 'Errore nella creazione della recensione.'
      return
    }
    const fd = new FormData()
    fd.append('idAttivita', String(id))
    fd.append('valutazioneStelleEsperienza', String(reviewStars.value))
    fd.append('descrizione', reviewDesc.value)
    fd.append('idValori', String(valoriRes.data.id))
    if (reviewFile.value) fd.append('immagine', reviewFile.value)
    const res = await recensioniApi.create(fd) as { status: string }
    if (res.status === 'success') {
      reviewSuccess.value = 'Recensione inviata!'
      reviewDesc.value = ''
      reviewFile.value = null
      refreshRecensioni()
      setTimeout(() => { showReviewForm.value = false; reviewSuccess.value = '' }, 1500)
    } else {
      reviewError.value = 'Errore durante l\'invio.'
    }
    } catch (err) {
      console.error('Errore invio recensione:', err)
      reviewError.value = 'Errore durante l\'invio.'
    }
  submittingReview.value = false
}

async function submitSegnalazione() {
  segnalazioneError.value = ''
  segnalazioneSuccess.value = ''
  if (!segnalazioneDesc.value.trim()) {
    segnalazioneError.value = 'Inserisci una descrizione per la segnalazione.'
    return
  }
  submittingSegnalazione.value = true
  try {
    const fd = new FormData()
    fd.append('descrizione', segnalazioneDesc.value)
    fd.append('idAttivita', String(id))
    if (segnalazioneFile.value) fd.append('immagine', segnalazioneFile.value)
    const res = await segnalazioniApi.create(fd) as { status: string }
    if (res.status === 'success') {
      segnalazioneSuccess.value = 'Segnalazione inviata!'
      segnalazioneDesc.value = ''
      segnalazioneFile.value = null
      setTimeout(() => { showSegnalazioneForm.value = false; segnalazioneSuccess.value = '' }, 1500)
    } else {
      segnalazioneError.value = 'Errore durante l\'invio.'
    }
    } catch (err) {
      console.error('Errore invio segnalazione:', err)
      segnalazioneError.value = 'Errore durante l\'invio.'
    }
  submittingSegnalazione.value = false
}

function canDeleteReview(recensione: RecensioneData): boolean {
  return recensione.visitatore?.id === auth.user?.id || auth.isAdmin
}

async function deleteReview(id: number) {
  deletingReview.value = true
  try {
    const res = await recensioniApi.delete(id) as { status: string }
    if (res.status === 'success') {
      deleteConfirmId.value = null
      refreshRecensioni()
    }
    } catch (err) {
    console.error('Errore eliminazione recensione:', err)
  }
  deletingReview.value = false
}
</script>

<template>
  <div>
    <ConfirmDialog
      v-if="deleteConfirmId"
      title="Elimina recensione"
      message="Eliminare questa recensione? L'operazione e' irreversibile."
      confirm-label="Elimina"
      variant="danger"
      :loading="deletingReview"
      @confirm="deleteReview(deleteConfirmId)"
      @cancel="deleteConfirmId = null"
    />

    <PrenotazioneDialog
      v-if="showPrenotazioneDialog"
      :activity-id="id"
      :is-alloggio="!!isAlloggio"
      :activity-name="attivita?.nome ?? ''"
      :activity-price="attivita?.prezzo ?? 0"
      @close="showPrenotazioneDialog = false"
    />

    <div v-if="error" class="py-16 text-center text-gray-500">
      Attivita' non trovata.
    </div>

    <div v-else-if="attivita" class="py-8">
      <div class="grid grid-cols-1 gap-8 lg:grid-cols-3">
        <div class="space-y-6 lg:col-span-1">
          <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <h1 class="text-2xl font-bold text-gray-900">{{ attivita.nome }}</h1>
            <p class="mt-1 text-gray-500">{{ attivita.citta }}, {{ attivita.provincia }}</p>
            <p class="mt-4 text-3xl font-bold text-green-600">{{ attivita.prezzo }}€</p>
            <p class="mt-1 text-sm text-gray-400">
              {{ isAlloggio ? 'Alloggio' : 'Attività Turistica' }}
            </p>
          </div>

          <div v-if="immagini.length" class="rounded-xl border border-gray-200 shadow-sm">
            <div class="flex flex-wrap gap-2 p-2">
              <img
                v-for="(img, i) in immagini" :key="i"
                :src="img"
                :alt="attivita.nome"
                class="h-32 w-full cursor-pointer rounded-lg object-cover transition hover:opacity-80 sm:w-[calc(50%-0.5rem)] lg:w-[calc(33.333%-0.5rem)]"
                @click="galleryIndex = i; galleryOpen = true"
              >
            </div>
          </div>

          <div v-if="valori" class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <h2 class="mb-4 text-lg font-semibold text-gray-800">Politiche eco-sostenibili</h2>
            <div class="flex flex-wrap gap-2">
              <span
                v-for="(label, key) in policyLabels"
                v-show="valori[key]"
                :key="key"
                class="rounded-full bg-green-100 px-3 py-1 text-xs font-medium text-green-700"
              >
                {{ label }}
              </span>
            </div>
            <p v-if="!Object.keys(valori).some((k: string) => valori[k])" class="text-sm text-gray-400">
              Nessuna politica eco-sostenibile.
            </p>
          </div>

          <div id="activity-map" class="h-64 overflow-hidden rounded-xl border border-gray-200 shadow-sm" />
        </div>

        <div class="space-y-6 lg:col-span-2">
          <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <h2 class="mb-4 text-xl font-semibold text-gray-800">Descrizione</h2>
            <p class="text-gray-600">{{ attivita.descrizioneBreve }}</p>
            <p v-if="attivita.descrizioneLunga" class="mt-4 text-gray-600">{{ attivita.descrizioneLunga }}</p>
          </div>

          <div v-if="isAlloggio" class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <h2 class="mb-4 text-xl font-semibold text-gray-800">Camere</h2>
            <div v-if="loadingCamere" class="text-gray-400">Caricamento...</div>
            <div v-else-if="!camere.length" class="text-gray-400">Nessuna camera disponibile.</div>
            <div v-else class="grid gap-4 sm:grid-cols-2">
              <div v-for="c in camere" :key="c.id" class="rounded-lg border border-gray-100 bg-gray-50 p-4">
                <p class="font-semibold text-gray-800">{{ c.tipoCamera }}</p>
                <p class="text-sm text-gray-500">Capienza: {{ c.capienza }} persone</p>
                <p class="text-sm text-gray-500">Disponibilita': {{ c.disponibilita }}</p>
                <p class="mt-1 text-sm text-gray-600">{{ c.descrizione }}</p>
                <p class="mt-2 text-lg font-bold text-green-600">{{ c.prezzo }}€ / notte</p>
              </div>
            </div>
          </div>

          <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div class="mb-4 flex items-center justify-between">
              <h2 class="text-xl font-semibold text-gray-800">Recensioni</h2>
              <UButton v-if="auth.isVisitatore" color="primary" variant="outline" size="sm" @click="showReviewForm = !showReviewForm">
                {{ showReviewForm ? 'Chiudi' : 'Aggiungi recensione' }}
              </UButton>
            </div>

            <div v-if="showReviewForm && auth.isVisitatore" class="mb-6 rounded-lg border border-gray-100 bg-gray-50 p-4">
              <h3 class="mb-3 font-medium text-gray-800">Nuova recensione</h3>
              <div class="mb-3 flex items-center gap-1">
                <span class="mr-2 text-sm text-gray-600">Valutazione:</span>
                <button
                  v-for="s in 5" :key="s"
                  class="text-xl transition" :class="s <= reviewStars ? 'text-amber-400' : 'text-gray-300'"
                  @click="reviewStars = s"
                >
                  ★
                </button>
              </div>
              <UTextarea v-model="reviewDesc" placeholder="Descrivi la tua esperienza..." class="mb-3" />
              <input type="file" accept="image/*" class="mb-3 block text-sm text-gray-500 file:mr-3 file:rounded file:border-0 file:bg-green-50 file:px-3 file:py-1.5 file:text-sm file:font-medium file:text-green-700 hover:file:bg-green-100" @change="onReviewFile">
              <div v-if="reviewError" class="mb-2 rounded bg-red-50 p-2 text-sm text-red-600">{{ reviewError }}</div>
              <div v-if="reviewSuccess" class="mb-2 rounded bg-green-50 p-2 text-sm text-green-700">{{ reviewSuccess }}</div>
              <UButton color="primary" size="sm" :loading="submittingReview" @click="submitReview">Invia</UButton>
            </div>

            <div v-if="recensioni?.length" class="space-y-4">
              <div v-for="recensione in recensioni" :key="recensione.id" class="border-b border-gray-100 pb-4 last:border-0">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-2">
                    <span class="font-medium text-gray-800">{{ recensione.visitatore?.nome || 'Anonimo' }}</span>
                    <span v-if="recensione.valutazioneStelleEsperienza" class="text-amber-400 text-sm">
                      {{ '★'.repeat(recensione.valutazioneStelleEsperienza) }}{{ '☆'.repeat(5 - recensione.valutazioneStelleEsperienza) }}
                    </span>
                  </div>
                  <div v-if="canDeleteReview(recensione)">
                    <button type="button" class="text-xs text-red-500 hover:text-red-700 transition" @click="deleteConfirmId = recensione.id">
                      Elimina
                    </button>
                  </div>
                </div>
                <p class="mt-1 text-sm text-gray-600">{{ recensione.descrizione }}</p>
              </div>
            </div>
            <p v-else class="text-gray-400">Nessuna recensione.</p>
          </div>

          <div v-if="auth.isVisitatore" class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div class="mb-4 flex items-center justify-between">
              <h2 class="text-xl font-semibold text-gray-800">Prenota</h2>
              <UButton color="primary" @click="showPrenotazioneDialog = true">
                {{ isAlloggio ? 'Prenota alloggio' : 'Prenota attività' }}
              </UButton>
            </div>
            <p class="text-sm text-gray-500">Scegli date e camere per completare la prenotazione.</p>
          </div>

          <div v-if="auth.isLoggedIn && !auth.isAdmin" class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
            <div class="mb-4 flex items-center justify-between">
              <h2 class="text-xl font-semibold text-gray-800">Segnala</h2>
              <UButton color="error" variant="outline" size="sm" @click="showSegnalazioneForm = !showSegnalazioneForm">
                {{ showSegnalazioneForm ? 'Chiudi' : 'Segnala questa attività' }}
              </UButton>
            </div>
            <div v-if="showSegnalazioneForm" class="rounded-lg border border-gray-100 bg-gray-50 p-4">
              <UTextarea v-model="segnalazioneDesc" placeholder="Descrivi il problema..." class="mb-3" />
              <input type="file" accept="image/*" class="mb-3 block text-sm text-gray-500 file:mr-3 file:rounded file:border-0 file:bg-green-50 file:px-3 file:py-1.5 file:text-sm file:font-medium file:text-green-700 hover:file:bg-green-100" @change="onSegnalazioneFile">
              <div v-if="segnalazioneError" class="mb-2 rounded bg-red-50 p-2 text-sm text-red-600">{{ segnalazioneError }}</div>
              <div v-if="segnalazioneSuccess" class="mb-2 rounded bg-green-50 p-2 text-sm text-green-700">{{ segnalazioneSuccess }}</div>
              <UButton color="error" size="sm" :loading="submittingSegnalazione" @click="submitSegnalazione">Invia segnalazione</UButton>
            </div>
          </div>
        </div>
      </div>
    </div>

    <GalleryDialog
      v-if="galleryOpen"
      :images="immagini"
      :initial-index="galleryIndex"
      @close="galleryOpen = false"
    />
  </div>
</template>
