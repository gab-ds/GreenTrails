<script setup lang="ts">
import { CalendarDate } from '@internationalized/date'

const props = defineProps<{
  activityId: number
  isAlloggio: boolean
  activityName: string
  activityPrice?: number
}>()

const emit = defineEmits<{ close: [] }>()

interface Camera {
  id: number
  tipoCamera: string
  capienza: number
  prezzo: number
  disponibilita: number
}

const { camere: camereApi, prenotazioniAlloggio, prenotazioniAttivita, itinerari } = useApi()

const step = ref(1)
const loading = ref(false)
const disponibile = ref<boolean | null>(null)
const messaggioDisponibilita = ref('')
const error = ref('')
const itinerarioId = ref<number | null>(null)
const azioneEseguita = ref(false)
const itinerarioCreato = ref(false)
const prenotazioneOk = ref(false)
const prenotazioneError = ref('')

const today = new CalendarDate(new Date().getFullYear(), new Date().getMonth() + 1, new Date().getDate())

const cameraOptions = ref<Camera[]>([])
const cameraId = ref<number | null>(null)
const dataInizio = ref<CalendarDate>(today)
const dataFine = ref<CalendarDate>(today.add({ days: 1 }))
const numAdulti = ref(1)
const numBambini = ref(0)
const numCamere = ref(1)

const numAdultiAtt = ref(1)
const numBambiniAtt = ref(0)

const fieldErrors = reactive<Record<string, string>>({})

function formattaCalendarDate(d: CalendarDate): string {
  return `${d.year}-${String(d.month).padStart(2, '0')}-${String(d.day).padStart(2, '0')}`
}

onMounted(async () => {
  if (props.isAlloggio) {
    try {
      const res = await camereApi.perAlloggio(props.activityId) as { status: string; data: Camera[] }
      if (res.status === 'success') cameraOptions.value = res.data
    } catch (err) {
      console.error('Errore caricamento camere:', err)
    }
  }
  try {
    const stored = localStorage.getItem('idItinerario')
    if (stored) itinerarioId.value = Number(stored)
  } catch (err) {
    console.error('Errore lettura localStorage:', err)
  }
})

function valida(): boolean {
  fieldErrors.error = ''
  let ok = true
  if (props.isAlloggio) {
    if (!cameraId.value) { fieldErrors.camera = 'Seleziona un tipo camera.'; ok = false } else fieldErrors.camera = ''
    if (numCamere.value < 1) { fieldErrors.camere = 'Almeno 1 camera.'; ok = false } else fieldErrors.camere = ''
  }
  if (numAdulti.value < 1 && props.isAlloggio) { fieldErrors.adulti = 'Almeno 1 adulto.'; ok = false } else if (props.isAlloggio) fieldErrors.adulti = ''
  if (numAdultiAtt.value < 1 && !props.isAlloggio) { fieldErrors.adultiAtt = 'Almeno 1 adulto.'; ok = false } else if (!props.isAlloggio) fieldErrors.adultiAtt = ''
  if (dataFine.value <= dataInizio.value) { fieldErrors.date = 'La data fine deve essere dopo la data inizio.'; ok = false } else fieldErrors.date = ''
  return ok
}

async function verificaDisponibilita() {
  error.value = ''
  disponibile.value = null
  messaggioDisponibilita.value = ''
  if (!valida()) return
  loading.value = true
  try {
    if (props.isAlloggio) {
      const res = await prenotazioniAlloggio.disponibilita(
        cameraId.value!,
        formattaCalendarDate(dataInizio.value),
        formattaCalendarDate(dataFine.value),
      ) as { status: string; data: number }
      if (res.status === 'success') {
        const disp = res.data
        if (numCamere.value <= disp) {
          const camera = cameraOptions.value.find(c => c.id === cameraId.value)
          const capienzaMax = (camera?.capienza ?? 1) * numCamere.value
          if (numAdulti.value + numBambini.value <= capienzaMax) {
            disponibile.value = true
            messaggioDisponibilita.value = 'Disponibile nelle date selezionate.'
          } else {
            disponibile.value = false
            messaggioDisponibilita.value = 'Capienza delle camere superata, aumenta il numero di camere.'
          }
        } else {
          disponibile.value = false
          messaggioDisponibilita.value = 'Non disponibile, cambia date.'
        }
      }
    } else {
      const res = await prenotazioniAttivita.disponibilita(
        props.activityId,
        formattaCalendarDate(dataInizio.value),
      ) as { status: string; data: number }
      if (res.status === 'success') {
        if (numAdultiAtt.value + numBambiniAtt.value <= res.data) {
          disponibile.value = true
          messaggioDisponibilita.value = 'Disponibile nelle date selezionate.'
        } else {
          disponibile.value = false
          messaggioDisponibilita.value = 'Non disponibile, cambia date.'
        }
      }
    }
    step.value = 2
  } catch (err) {
    console.error('Errore verifica disponibilità:', err)
    error.value = 'Errore durante la verifica.'
  }
  loading.value = false
}

async function creaItinerario() {
  loading.value = true
  try {
    const res = await itinerari.create() as { status: string; data: { id: number } }
    if (res.status === 'success') {
      itinerarioId.value = res.data.id
      try { localStorage.setItem('idItinerario', String(res.data.id)) } catch (e) { console.error('Errore salvataggio localStorage:', e) }
      azioneEseguita.value = true
      itinerarioCreato.value = true
    } else {
      error.value = 'Impossibile creare l\'itinerario.'
    }
  } catch (err) {
    console.error('Errore creazione itinerario:', err)
    error.value = 'Errore durante la creazione dell\'itinerario.'
  }
  loading.value = false
}

function usaItinerarioEsistente() {
  if (itinerarioId.value) {
    azioneEseguita.value = true
    itinerarioCreato.value = false
  }
}

function vaiARiepilogo() {
  step.value = 3
}

async function submit() {
  if (!itinerarioId.value) return
  error.value = ''
  prenotazioneError.value = ''
  loading.value = true
  try {
    if (props.isAlloggio) {
      const res = await prenotazioniAlloggio.create({
        idItinerario: itinerarioId.value,
        idCamera: cameraId.value,
        numAdulti: numAdulti.value,
        numBambini: numBambini.value,
        numCamere: numCamere.value,
        dataInizio: formattaCalendarDate(dataInizio.value),
        dataFine: formattaCalendarDate(dataFine.value),
      }) as { status: string }
      if (res.status !== 'success') throw new Error()
    } else {
      const body: Record<string, unknown> = {
        idItinerario: itinerarioId.value,
        idAttivita: props.activityId,
        numAdulti: numAdultiAtt.value,
        numBambini: numBambiniAtt.value,
        dataInizio: formattaCalendarDate(dataInizio.value),
      }
      if (dataFine.value > dataInizio.value) {
        body.dataFine = formattaCalendarDate(dataFine.value)
      }
      const res = await prenotazioniAttivita.create(body) as { status: string }
      if (res.status !== 'success') throw new Error()
    }
    prenotazioneOk.value = true
  } catch (err) {
    console.error('Errore prenotazione:', err)
    prenotazioneError.value = 'Impossibile effettuare la prenotazione.'
  }
  loading.value = false
}

function reset() {
  step.value = 1
  disponibile.value = null
  azioneEseguita.value = false
  error.value = ''
  prenotazioneError.value = ''
  prenotazioneOk.value = false
}

// computed helpers for summary
const cameraSelezionata = computed(() => cameraOptions.value.find(c => c.id === cameraId.value))
const numeroNotti = computed(() => {
  const diff = dataFine.value.compare(dataInizio.value)
  return Math.max(0, diff)
})
const prezzoTotale = computed(() => {
  if (props.isAlloggio && cameraSelezionata.value) {
    return cameraSelezionata.value.prezzo * numCamere.value * numeroNotti.value
  }
  if (!props.isAlloggio) {
    return props.activityPrice ?? 0
  }
  return 0
})
</script>

<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/40" @click.self="emit('close')">
    <div class="mx-4 w-full max-w-lg rounded-xl bg-white p-6 shadow-xl">
      <div v-if="!prenotazioneOk">
        <div class="mb-6 flex items-center justify-between">
          <h2 class="text-xl font-bold text-gray-800">
            {{ isAlloggio ? 'Prenota Alloggio' : 'Prenota Attività' }}
          </h2>
          <button class="text-gray-400 hover:text-gray-600" @click="emit('close')">&times;</button>
        </div>

        <div class="mb-6 flex gap-1">
          <div v-for="(s, idx) in ['Dati', 'Verifica', 'Riepilogo']" :key="s"
            class="flex-1 rounded-full py-1 text-center text-xs font-medium transition"
            :class="step > idx + 1 ? 'bg-green-600 text-white' : step === idx + 1 ? 'bg-green-500 text-white' : 'bg-gray-100 text-gray-400'"
          >
            {{ s }}
          </div>
        </div>

        <div v-if="error" class="mb-4 rounded bg-red-50 p-2 text-sm text-red-600">{{ error }}</div>

        <!-- Step 1: Dati -->
        <div v-if="step === 1" class="space-y-4">
          <div v-if="isAlloggio">
            <label class="mb-1 block text-sm font-medium text-gray-600">Tipo camera</label>
            <div class="grid gap-2 sm:grid-cols-2">
              <button
                v-for="c in cameraOptions" :key="c.id"
                type="button"
                class="rounded-xl border-2 p-3 text-left transition"
                :class="cameraId === c.id ? 'border-green-500 bg-green-50' : 'border-gray-200 hover:border-green-300'"
                @click="cameraId = c.id"
              >
                <p class="font-medium text-gray-800">{{ c.tipoCamera }}</p>
                <p class="text-xs text-gray-500">{{ c.capienza }} posti · {{ c.prezzo }}€/notte</p>
              </button>
            </div>
            <p v-if="!cameraOptions.length" class="mt-1 text-xs text-gray-400">Nessuna camera disponibile.</p>
            <p v-if="fieldErrors.camera" class="mt-1 text-xs text-red-500">{{ fieldErrors.camera }}</p>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="mb-1 block text-sm font-medium text-gray-600">Data inizio</label>
              <UInputDate v-model="dataInizio" :min-value="today" locale="it" />
            </div>
            <div>
              <label class="mb-1 block text-sm font-medium text-gray-600">Data fine</label>
              <UInputDate v-model="dataFine" :min-value="dataInizio.add({ days: 1 })" locale="it" />
            </div>
          </div>
          <p v-if="fieldErrors.date" class="text-xs text-red-500">{{ fieldErrors.date }}</p>

          <div class="grid grid-cols-2 gap-4">
            <div v-if="isAlloggio">
              <label class="mb-1 block text-sm font-medium text-gray-600">Adulti</label>
              <UInput v-model="numAdulti" type="number" min="1" inputmode="numeric" />
              <p v-if="fieldErrors.adulti" class="mt-1 text-xs text-red-500">{{ fieldErrors.adulti }}</p>
            </div>
            <div v-if="isAlloggio">
              <label class="mb-1 block text-sm font-medium text-gray-600">Bambini</label>
              <UInput v-model="numBambini" type="number" min="0" inputmode="numeric" />
            </div>
            <div v-if="!isAlloggio">
              <label class="mb-1 block text-sm font-medium text-gray-600">Adulti</label>
              <UInput v-model="numAdultiAtt" type="number" min="1" inputmode="numeric" />
              <p v-if="fieldErrors.adultiAtt" class="mt-1 text-xs text-red-500">{{ fieldErrors.adultiAtt }}</p>
            </div>
            <div v-if="!isAlloggio">
              <label class="mb-1 block text-sm font-medium text-gray-600">Bambini</label>
              <UInput v-model="numBambiniAtt" type="number" min="0" inputmode="numeric" />
            </div>
          </div>

          <div v-if="isAlloggio">
            <label class="mb-1 block text-sm font-medium text-gray-600">Numero camere</label>
            <UInput v-model="numCamere" type="number" min="1" inputmode="numeric" />
            <p v-if="fieldErrors.camere" class="mt-1 text-xs text-red-500">{{ fieldErrors.camere }}</p>
          </div>

          <UButton color="primary" block :loading="loading" @click="verificaDisponibilita">
            Continua
          </UButton>
        </div>

        <!-- Step 2: Verifica disponibilità + Itinerario -->
        <div v-if="step === 2" class="space-y-4">
          <div v-if="disponibile === true" class="rounded-lg bg-green-50 p-4 text-center text-sm font-medium text-green-700">
            {{ messaggioDisponibilita }}
          </div>
          <div v-else-if="disponibile === false" class="rounded-lg bg-red-50 p-4 text-center text-sm font-medium text-red-600">
            {{ messaggioDisponibilita }}
          </div>

          <div v-if="prenotazioneError" class="rounded bg-red-50 p-2 text-sm text-red-600">{{ prenotazioneError }}</div>

          <div v-if="disponibile === true" class="space-y-3">
            <div class="rounded-lg border border-gray-200 bg-gray-50 p-4 text-sm text-gray-600">
              <p v-if="itinerarioId">Itinerario attivo: <strong>ID {{ itinerarioId }}</strong></p>
              <p v-else>Nessun itinerario trovato. Creane uno nuovo.</p>
            </div>
            <div class="flex gap-2">
              <UButton v-if="itinerarioId" color="primary" variant="outline" :disabled="azioneEseguita" @click="usaItinerarioEsistente">
                Usa itinerario esistente
              </UButton>
              <UButton color="primary" :disabled="azioneEseguita" :loading="loading" @click="creaItinerario">
                Crea nuovo itinerario
              </UButton>
            </div>
            <div v-if="azioneEseguita" class="rounded bg-green-50 p-2 text-center text-sm text-green-700">
              Itinerario {{ itinerarioCreato ? 'creato' : 'selezionato' }} con successo.
            </div>
            <UButton color="primary" block :disabled="!azioneEseguita" @click="vaiARiepilogo">
              Continua con il riepilogo
            </UButton>
          </div>

          <div class="flex gap-2">
            <UButton color="neutral" variant="outline" block @click="reset">
              Indietro
            </UButton>
            <UButton v-if="disponibile === false" color="neutral" variant="outline" block @click="reset">
              Modifica dati
            </UButton>
          </div>
        </div>

        <!-- Step 3: Riepilogo -->
        <div v-if="step === 3" class="space-y-4">
          <h3 class="text-lg font-semibold text-gray-800">Riepilogo prenotazione</h3>

          <div class="rounded-lg border border-gray-200 bg-gray-50 p-4 text-sm space-y-2">
            <div class="flex justify-between">
              <span class="text-gray-500">Attività</span>
              <span class="font-medium text-gray-800">{{ activityName }}</span>
            </div>
            <div v-if="isAlloggio && cameraSelezionata" class="flex justify-between">
              <span class="text-gray-500">Camera</span>
              <span class="font-medium text-gray-800">{{ cameraSelezionata.tipoCamera }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-500">Check-in</span>
              <span class="font-medium text-gray-800">{{ formattaCalendarDate(dataInizio) }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-500">Check-out</span>
              <span class="font-medium text-gray-800">{{ formattaCalendarDate(dataFine) }}</span>
            </div>
            <div v-if="isAlloggio" class="flex justify-between">
              <span class="text-gray-500">Notti</span>
              <span class="font-medium text-gray-800">{{ numeroNotti }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-500">Ospiti</span>
              <span class="font-medium text-gray-800">{{ isAlloggio ? numAdulti + numBambini : numAdultiAtt + numBambiniAtt }} ({{ isAlloggio ? numAdulti : numAdultiAtt }} adulti{{ (isAlloggio ? numBambini : numBambiniAtt) > 0 ? `, ${isAlloggio ? numBambini : numBambiniAtt} bambini` : '' }})</span>
            </div>
            <div v-if="isAlloggio" class="flex justify-between">
              <span class="text-gray-500">Camere</span>
              <span class="font-medium text-gray-800">{{ numCamere }}</span>
            </div>
            <hr class="border-gray-200">
            <div class="flex justify-between text-base">
              <span class="font-semibold text-gray-700">Totale</span>
              <span class="font-bold text-green-600">{{ prezzoTotale.toFixed(2) }} €</span>
            </div>
          </div>

          <div v-if="prenotazioneError" class="rounded bg-red-50 p-2 text-sm text-red-600">{{ prenotazioneError }}</div>

          <div class="flex gap-2">
            <UButton color="neutral" variant="outline" block @click="reset">
              Modifica
            </UButton>
            <UButton color="primary" block :loading="loading" @click="submit">
              Conferma prenotazione
            </UButton>
          </div>
        </div>

        <UButton color="neutral" variant="ghost" block class="mt-4" @click="emit('close')">
          Annulla
        </UButton>
      </div>

      <!-- Success overlay -->
      <div v-else class="py-8 text-center">
        <div class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-green-100">
          <UIcon name="i-lucide-check" class="size-8 text-green-600" />
        </div>
        <h2 class="mb-2 text-xl font-bold text-gray-900">Prenotazione effettuata!</h2>
        <p class="mb-6 text-sm text-gray-500">
          {{ isAlloggio ? 'Alloggio' : 'Attività' }} prenotato{{ isAlloggio ? 'a' : '' }} con successo.
        </p>
        <div class="mx-auto mb-6 max-w-sm rounded-lg border border-gray-200 bg-gray-50 p-4 text-left text-sm text-gray-600 space-y-1">
          <p><strong>{{ activityName }}</strong></p>
          <p v-if="isAlloggio && cameraSelezionata">Camera: {{ cameraSelezionata.tipoCamera }}</p>
          <p>Check-in: {{ formattaCalendarDate(dataInizio) }}</p>
          <p>Check-out: {{ formattaCalendarDate(dataFine) }}</p>
          <hr class="border-gray-200">
          <p class="text-base font-bold text-green-600">{{ prezzoTotale.toFixed(2) }} €</p>
        </div>
        <button
          type="button"
          class="w-full rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-green-700"
          @click="emit('close')"
        >
          Chiudi
        </button>
      </div>
    </div>
  </div>
</template>