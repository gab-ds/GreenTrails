<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface Camera {
  id: number
  tipoCamera: string
  disponibilita: number
  descrizione: string
  capienza: number
  prezzo: number
}

const props = defineProps<{
  activityId: number
}>()

const emit = defineEmits<{
  close: []
}>()

const { camere: camereApi } = useApi()

const camere = ref<Camera[]>([])
const loadingCamere = ref(true)
const showForm = ref(false)
const saving = ref(false)
const error = ref('')
const confirmDeleteId = ref<number | null>(null)
const deleting = ref(false)

const tipoCamera = ref('')
const capienza = ref(1)
const prezzo = ref(0)
const disponibilita = ref(1)
const descrizione = ref('')

onMounted(loadCamere)

async function loadCamere() {
  loadingCamere.value = true
  try {
    const res = await camereApi.perAlloggio(props.activityId) as { status: string; data: Camera[] }
    if (res.status === 'success') camere.value = res.data
  } catch (err) {
    console.error('Errore caricamento camere:', err)
    error.value = 'Errore nel caricamento delle camere.'
  }
  loadingCamere.value = false
}

async function aggiungiCamera() {
  if (!tipoCamera.value.trim()) {
    error.value = 'Inserisci il tipo di camera.'
    return
  }
  saving.value = true
  error.value = ''
  try {
    const res = await camereApi.create({
      idAlloggio: props.activityId,
      tipoCamera: tipoCamera.value,
      disponibilita: disponibilita.value,
      descrizione: descrizione.value,
      capienza: capienza.value,
      prezzo: prezzo.value,
    }) as { status: string; data: Camera }
    if (res.status === 'success') {
      camere.value.push(res.data)
      tipoCamera.value = ''
      capienza.value = 1
      prezzo.value = 0
      disponibilita.value = 1
      descrizione.value = ''
      showForm.value = false
    } else {
      error.value = 'Errore durante il salvataggio.'
    }
  } catch (err) {
    console.error('Errore salvataggio camera:', err)
    error.value = 'Errore durante il salvataggio.'
  }
  saving.value = false
}

async function eliminaCamera(id: number) {
  deleting.value = true
  confirmDeleteId.value = null
  try {
    const res = await camereApi.delete(id) as { status: string }
    if (res.status === 'success') {
      camere.value = camere.value.filter(c => c.id !== id)
    }
  } catch (err) {
    console.error('Errore eliminazione camera:', err)
  }
  deleting.value = false
}
</script>

<template>
  <div class="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto bg-black/40 pt-12" @click.self="emit('close')">
    <div class="mx-4 mb-12 w-full max-w-lg rounded-xl bg-white p-6 shadow-xl">
      <div class="mb-4 flex items-center justify-between">
        <h2 class="text-lg font-bold text-gray-900">Gestione Camere</h2>
        <button type="button" class="text-gray-400 hover:text-gray-600 transition" @click="emit('close')">
          <UIcon name="i-lucide-x" class="size-5" />
        </button>
      </div>

      <div v-if="loadingCamere" class="py-8 text-center text-sm text-gray-400">Caricamento...</div>

      <template v-else>
        <div v-if="camere.length" class="mb-4 space-y-3">
          <div v-for="c in camere" :key="c.id" class="flex items-start justify-between rounded-lg border border-gray-100 bg-gray-50 p-3">
            <div class="flex-1">
              <p class="font-medium text-gray-800">{{ c.tipoCamera }}</p>
              <p class="text-xs text-gray-500">Capienza: {{ c.capienza }} | Disponibilità: {{ c.disponibilita }} | {{ c.prezzo }}€/notte</p>
              <p v-if="c.descrizione" class="mt-1 text-xs text-gray-500">{{ c.descrizione }}</p>
            </div>
            <button type="button" class="ml-2 text-red-400 hover:text-red-600 transition" title="Elimina camera" @click="confirmDeleteId = c.id">
              <UIcon name="i-lucide-trash-2" class="size-4" />
            </button>
          </div>
        </div>
        <p v-else class="mb-4 text-sm text-gray-400">Nessuna camera. Aggiungine una.</p>

        <div v-if="error" class="mb-3 rounded bg-red-50 p-2 text-sm text-red-600">{{ error }}</div>

        <button
          v-if="!showForm"
          type="button"
          class="mb-4 flex items-center gap-2 rounded-lg border border-dashed border-gray-300 px-4 py-2 text-sm text-gray-600 transition hover:border-green-400 hover:text-green-600"
          @click="showForm = true"
        >
          <UIcon name="i-lucide-plus" class="size-4" />
          Aggiungi camera
        </button>

        <div v-if="showForm" class="space-y-3 rounded-lg border border-gray-200 bg-white p-4">
          <UInput v-model="tipoCamera" placeholder="Tipo camera (es. Doppia)" label="Tipo *" size="sm" />
          <div class="grid grid-cols-3 gap-3">
            <UInput v-model.number="capienza" type="number" min="1" label="Capienza *" size="sm" />
            <UInput v-model.number="prezzo" type="number" step="0.01" min="0" label="Prezzo *" size="sm" />
            <UInput v-model.number="disponibilita" type="number" min="1" label="Disponibilità *" size="sm" />
          </div>
          <UTextarea v-model="descrizione" placeholder="Descrizione (opzionale)" size="sm" />
          <div class="flex justify-end gap-2">
            <UButton color="neutral" variant="outline" size="sm" @click="showForm = false">Annulla</UButton>
            <UButton color="primary" size="sm" :loading="saving" @click="aggiungiCamera">Salva</UButton>
          </div>
        </div>
      </template>

      <ConfirmDialog
        v-if="confirmDeleteId"
        title="Elimina camera"
        message="Eliminare questa camera? L'operazione è irreversibile."
        confirm-label="Elimina"
        variant="danger"
        :loading="deleting"
        @confirm="eliminaCamera(confirmDeleteId)"
        @cancel="confirmDeleteId = null"
      />
    </div>
  </div>
</template>
