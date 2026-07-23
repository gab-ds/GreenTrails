<script setup lang="ts">
definePageMeta({ middleware: 'auth', role: 'VISITATORE' })

const { prenotazioniAlloggio, prenotazioniAttivita } = useApi()

interface PrenotazioneVisitatore {
  id: number
  tipo: 'alloggio' | 'attivita_turistica'
  nomeAttivita: string
  idAttivita: number
  dataInizio: string
  dataFine: string | null
  numAdulti: number
  numBambini: number
  prezzo: number
  stato: string
  statoPagamento: string
  tipoCamera: string | null
  numCamere: number | null
}

interface PrenotazioneAlloggioRaw {
  id: number
  camera: { alloggio: { nome: string; id: number }; tipoCamera: string } | null
  dataInizio: string
  dataFine: string | null
  numAdulti: number
  numBambini: number
  prezzo: number
  stato: string
  statoPagamento: string
  numCamere: number | null
}

interface PrenotazioneAttivitaRaw {
  id: number
  attivitaTuristica: { nome: string; id: number } | null
  dataInizio: string
  dataFine: string | null
  numAdulti: number
  numBambini: number
  prezzo: number
  stato: string
  statoPagamento: string
}

const prenotazioni = ref<PrenotazioneVisitatore[]>([])
const rawPrenotazioni = ref<Map<string, PrenotazioneAlloggioRaw | PrenotazioneAttivitaRaw>>(new Map())
const loading = ref(true)

const selectedPrenotazione = ref<PrenotazioneAlloggioRaw | PrenotazioneAttivitaRaw | null>(null)
const detailType = ref<'alloggio' | 'attivita'>('alloggio')
const deleteConfirm = ref<PrenotazioneVisitatore | null>(null)
const deleting = ref(false)

const badgeStato: Record<string, string> = {
  CREATA: 'bg-blue-100 text-blue-700',
  IN_CORSO: 'bg-amber-100 text-amber-700',
  COMPLETATA: 'bg-green-100 text-green-700',
  NON_CONFERMATA: 'bg-gray-100 text-gray-500',
}

const badgePagamento: Record<string, string> = {
  IN_CORSO: 'bg-amber-100 text-amber-700',
  COMPLETATO: 'bg-green-100 text-green-700',
  FALLITO: 'bg-red-100 text-red-700',
}

function formattaData(d: string | null) {
  if (!d) return '—'
  return new Date(d).toLocaleDateString('it-IT')
}

onMounted(async () => {
  try {
    const tutte: PrenotazioneVisitatore[] = []
    const resAlloggio = await prenotazioniAlloggio.miePrenotazioni() as { status: string; data: PrenotazioneAlloggioRaw[] }
    if (resAlloggio.status === 'success') {
      for (const p of resAlloggio.data) {
        tutte.push({
          id: p.id,
          tipo: 'alloggio',
          nomeAttivita: p.camera?.alloggio?.nome ?? '—',
          idAttivita: p.camera?.alloggio?.id,
          dataInizio: p.dataInizio,
          dataFine: p.dataFine,
          numAdulti: p.numAdulti,
          numBambini: p.numBambini,
          prezzo: p.prezzo,
          stato: p.stato,
          statoPagamento: p.statoPagamento,
          tipoCamera: p.camera?.tipoCamera ?? null,
          numCamere: p.numCamere,
        })
        rawPrenotazioni.value.set(`alloggio-${p.id}`, p)
      }
    }
    const resAttivita = await prenotazioniAttivita.miePrenotazioni() as { status: string; data: PrenotazioneAttivitaRaw[] }
    if (resAttivita.status === 'success') {
      for (const p of resAttivita.data) {
        tutte.push({
          id: p.id,
          tipo: 'attivita_turistica',
          nomeAttivita: p.attivitaTuristica?.nome ?? '—',
          idAttivita: p.attivitaTuristica?.id,
          dataInizio: p.dataInizio,
          dataFine: p.dataFine ?? null,
          numAdulti: p.numAdulti,
          numBambini: p.numBambini,
          prezzo: p.prezzo,
          stato: p.stato,
          statoPagamento: p.statoPagamento,
          tipoCamera: null,
          numCamere: null,
        })
        rawPrenotazioni.value.set(`attivita_turistica-${p.id}`, p)
      }
    }
    prenotazioni.value = tutte
  } catch (err) {
    console.error('Errore caricamento prenotazioni:', err)
  }
  loading.value = false
})

async function deletePrenotazione(p: PrenotazioneVisitatore) {
  deleting.value = true
  try {
    if (p.tipo === 'alloggio') {
      await prenotazioniAlloggio.delete(p.id)
    } else {
      await prenotazioniAttivita.delete(p.id)
    }
    prenotazioni.value = prenotazioni.value.filter(x => x !== p)
    deleteConfirm.value = null
  } catch (err) {
    console.error('Errore eliminazione prenotazione:', err)
  }
  deleting.value = false
}
</script>

<template>
  <div class="py-8">
    <h1 class="mb-8 text-center text-3xl font-bold text-green-600">
      I miei viaggi
      <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
    </h1>

    <div v-if="loading" class="text-center text-gray-400">Caricamento...</div>

    <div v-else-if="prenotazioni.length === 0" class="text-center text-gray-400">
      Nessuna prenotazione. Inizia a esplorare le attività!
    </div>

    <div v-else class="mx-auto max-w-5xl overflow-x-auto">
      <table class="w-full text-left text-sm">
        <thead>
          <tr class="border-b border-gray-200 text-gray-500">
            <th class="px-3 py-3 font-medium">Attività</th>
            <th class="px-3 py-3 font-medium">Tipo</th>
            <th class="px-3 py-3 font-medium">Dettaglio</th>
            <th class="px-3 py-3 font-medium">Date</th>
            <th class="px-3 py-3 font-medium">Ospiti</th>
            <th class="px-3 py-3 font-medium">Prezzo</th>
            <th class="px-3 py-3 font-medium">Stato</th>
            <th class="px-3 py-3 font-medium">Pagamento</th>
            <th class="px-3 py-3 font-medium" />
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="p in prenotazioni"
            :key="`${p.tipo}-${p.id}`"
            class="border-b border-gray-100 transition-colors hover:bg-gray-50"
          >
            <td class="px-3 py-3">
              <NuxtLink :to="`/attivita/${p.idAttivita}`" class="font-medium text-gray-800 hover:text-green-600">
                {{ p.nomeAttivita }}
              </NuxtLink>
            </td>
            <td class="px-3 py-3">
              <span v-if="p.tipo === 'alloggio'" class="rounded-full bg-blue-100 px-2 py-0.5 text-xs text-blue-700">Alloggio</span>
              <span v-else class="rounded-full bg-amber-100 px-2 py-0.5 text-xs text-amber-700">Attività</span>
            </td>
            <td class="px-3 py-3 text-gray-600">
              <span v-if="p.tipo === 'alloggio' && p.tipoCamera">{{ p.tipoCamera }} × {{ p.numCamere }}</span>
              <span v-else class="text-gray-400">—</span>
            </td>
            <td class="whitespace-nowrap px-3 py-3 text-gray-600">
              {{ formattaData(p.dataInizio) }}<span v-if="p.dataFine"> → {{ formattaData(p.dataFine) }}</span>
            </td>
            <td class="whitespace-nowrap px-3 py-3 text-gray-600">
              {{ p.numAdulti + p.numBambini }}
              <span class="text-xs text-gray-400">({{ p.numAdulti }}A {{ p.numBambini }}B)</span>
            </td>
            <td class="whitespace-nowrap px-3 py-3 font-semibold text-green-600">{{ p.prezzo }}€</td>
            <td class="px-3 py-3">
              <span class="rounded-full px-2 py-0.5 text-xs font-medium" :class="badgeStato[p.stato] || 'bg-gray-100 text-gray-500'">{{ p.stato }}</span>
            </td>
            <td class="px-3 py-3">
              <span class="rounded-full px-2 py-0.5 text-xs font-medium" :class="badgePagamento[p.statoPagamento] || 'bg-gray-100 text-gray-500'">{{ p.statoPagamento }}</span>
            </td>
            <td class="whitespace-nowrap px-3 py-3">
              <UButton color="neutral" variant="ghost" size="sm" icon="i-lucide-info" title="Dettagli" @click="selectedPrenotazione = rawPrenotazioni.get(`${p.tipo}-${p.id}`); detailType = p.tipo === 'alloggio' ? 'alloggio' : 'attivita'" />
              <UButton color="error" variant="ghost" size="sm" icon="i-lucide-trash-2" title="Elimina" @click="deleteConfirm = p" />
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <DettagliPrenotazioneDialog
      v-if="selectedPrenotazione"
      :prenotazione="selectedPrenotazione"
      :type="detailType"
      @close="selectedPrenotazione = null"
    />

    <ConfirmDialog
      v-if="deleteConfirm"
      title="Cancella prenotazione"
      message="Annullare questa prenotazione?"
      confirm-label="Elimina"
      variant="danger"
      :loading="deleting"
      @confirm="deletePrenotazione(deleteConfirm)"
      @cancel="deleteConfirm = null"
    />
  </div>
</template>
