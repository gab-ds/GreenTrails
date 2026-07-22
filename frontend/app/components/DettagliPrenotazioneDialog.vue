<script setup lang="ts">
interface PrenotazioneAlloggioFull {
  id: number
  camera: { alloggio: { nome: string }; tipoCamera: string } | null
  attivita?: { nome: string }
  numAdulti: number
  numBambini: number
  dataInizio: string
  dataFine: string
  stato: string
  statoPagamento: string
  prezzo: number
}

interface PrenotazioneAttivitaFull {
  id: number
  attivitaTuristica: { nome: string } | null
  numAdulti: number
  numBambini: number
  dataInizio: string
  stato: string
  statoPagamento: string
  prezzo: number
}

const props = defineProps<{
  prenotazione: PrenotazioneAlloggioFull | PrenotazioneAttivitaFull
  type: 'alloggio' | 'attivita'
}>()
const emit = defineEmits<{ close: [] }>()

function formatDate(d: string) {
  if (!d) return '—'
  const dt = new Date(d)
  const day = String(dt.getDate()).padStart(2, '0')
  const month = String(dt.getMonth() + 1).padStart(2, '0')
  const year = dt.getFullYear()
  return `${day}/${month}/${year}`
}
</script>

<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/40" @click.self="emit('close')">
    <div class="mx-4 w-full max-w-lg rounded-xl bg-white p-6 shadow-xl">
      <h2 class="mb-4 text-xl font-bold text-gray-900">
        {{ type === 'alloggio' ? 'Dettagli Prenotazione Alloggio' : 'Dettagli Prenotazione Attività Turistica' }}
      </h2>

      <dl class="space-y-3 text-sm">
        <div class="flex justify-between">
          <dt class="text-gray-500">ID</dt>
          <dd class="font-medium text-gray-900">{{ prenotazione.id }}</dd>
        </div>

        <div v-if="type === 'alloggio'" class="flex justify-between">
          <dt class="text-gray-500">Attività</dt>
          <dd class="font-medium text-gray-900">{{ prenotazione.camera?.alloggio?.nome || prenotazione.attivita?.nome || '-' }}</dd>
        </div>
        <div v-else class="flex justify-between">
          <dt class="text-gray-500">Attività</dt>
          <dd class="font-medium text-gray-900">{{ prenotazione.attivitaTuristica?.nome || '-' }}</dd>
        </div>

        <div class="flex justify-between">
          <dt class="text-gray-500">Ospiti</dt>
          <dd class="font-medium text-gray-900">{{ prenotazione.numAdulti }} adulti, {{ prenotazione.numBambini }} bambini</dd>
        </div>

        <div v-if="type === 'alloggio'" class="flex justify-between">
          <dt class="text-gray-500">Date</dt>
          <dd class="font-medium text-gray-900">{{ formatDate(prenotazione.dataInizio) }} → {{ formatDate(prenotazione.dataFine) }}</dd>
        </div>
        <div v-else class="flex justify-between">
          <dt class="text-gray-500">Data</dt>
          <dd class="font-medium text-gray-900">{{ formatDate(prenotazione.dataInizio) }}</dd>
        </div>

        <div v-if="type === 'alloggio'" class="flex justify-between">
          <dt class="text-gray-500">Camera</dt>
          <dd class="font-medium text-gray-900">{{ prenotazione.camera?.tipoCamera || '-' }}</dd>
        </div>

        <div class="flex justify-between">
          <dt class="text-gray-500">Stato</dt>
          <dd class="font-medium text-gray-900">{{ prenotazione.stato }}</dd>
        </div>

        <div class="flex justify-between">
          <dt class="text-gray-500">Pagamento</dt>
          <dd class="font-medium text-gray-900">{{ prenotazione.statoPagamento }}</dd>
        </div>

        <div class="flex justify-between border-t border-gray-100 pt-3">
          <dt class="text-gray-500">Prezzo</dt>
          <dd class="text-lg font-bold text-green-600">{{ prenotazione.prezzo }}€</dd>
        </div>
      </dl>

      <div class="mt-6 flex justify-end">
        <button
          type="button"
          class="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-50"
          @click="emit('close')"
        >
          Chiudi
        </button>
      </div>
    </div>
  </div>
</template>
