<script setup lang="ts">
definePageMeta({ middleware: 'auth', role: 'AMMINISTRATORE' })

interface ValoriRaw {
  id: number
  politicheAntispreco: boolean
  prodottiLocali: boolean
  energiaVerde: boolean
  raccoltaDifferenziata: boolean
  limiteEmissioneCO2: boolean
  contattoConNatura: boolean
}

const { activities, valori } = useApi()
const route = useRoute()
const router = useRouter()
const { $toast } = useNuxtApp() || {}

const idAttivita = computed(() => Number(route.query.id))
const nome = ref('')
const valoriRaw = ref<ValoriRaw | null>(null)
const items = ref<{ key: string; label: string; value: boolean | null }[]>([])
const loading = ref(true)
const error = ref('')
const success = ref(false)

const labelMap: Record<string, string> = {
  politicheAntispreco: 'Politiche Antispreco',
  prodottiLocali: 'Prodotti Locali',
  energiaVerde: 'Energia Verde',
  raccoltaDifferenziata: 'Raccolta Differenziata',
  limiteEmissioneCO2: 'Limite Emissione CO2',
  contattoConNatura: 'Contatto Con Natura',
}

onMounted(async () => {
  if (!idAttivita.value) {
    error.value = 'ID attività non valido.'
    loading.value = false
    return
  }
  try {
    const res = await activities.get(idAttivita.value) as {
      status: string; data: { nome: string; valoriEcosostenibilita: ValoriRaw }
    }
    if (res.status !== 'success') {
      error.value = 'Attività non trovata.'
      loading.value = false
      return
    }
    const a = res.data
    nome.value = a.nome
    valoriRaw.value = a.valoriEcosostenibilita
    items.value = Object.entries(labelMap)
      .filter(([key]) => a.valoriEcosostenibilita[key as keyof ValoriRaw] === true)
      .map(([key, label]) => ({ key, label, value: true }))
  } catch (err) {
    console.error('Errore durante il caricamento:', err)
    error.value = 'Errore durante il caricamento.'
  }
  loading.value = false
})

function setValue(item: { key: string; value: boolean | null }, v: boolean) {
  item.value = v
}

async function submitForm() {
  if (!valoriRaw.value) return
  const params: Record<string, boolean> = {}
  for (const item of items.value) {
    params[item.key] = item.value ?? true
  }
  try {
    const res = await valori.update(valoriRaw.value.id, params) as { status: string }
    if (res.status === 'success') {
      success.value = true
    } else {
      error.value = 'Impossibile effettuare la modifica.'
    }
  } catch (err) {
    console.error('Errore durante la modifica:', err)
    error.value = 'Errore durante la modifica.'
  }
}
</script>

<template>
  <div class="py-8">
    <h1 class="mb-2 text-center text-3xl font-bold text-green-600">
      Modifica valori di ecosostenibilità
      <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
    </h1>
    <h2 class="mb-8 text-center text-xl text-gray-600">{{ nome }}</h2>

    <div v-if="loading" class="text-center text-gray-400">Caricamento...</div>

    <form v-else class="mx-auto max-w-lg space-y-6" @submit.prevent="submitForm">
      <div v-for="item in items" :key="item.key" class="flex items-center justify-between rounded-lg border border-gray-200 p-4">
        <span class="text-sm font-medium text-gray-800">{{ item.label }}</span>
        <div class="flex gap-3">
          <label class="flex cursor-pointer items-center gap-1 text-sm text-gray-600">
            <input type="radio" :name="item.key" :checked="item.value === true" @change="setValue(item, true)">
            Sì
          </label>
          <label class="flex cursor-pointer items-center gap-1 text-sm text-gray-600">
            <input type="radio" :name="item.key" :checked="item.value === false" @change="setValue(item, false)">
            No
          </label>
        </div>
      </div>

      <div class="flex justify-between">
        <UButton color="neutral" variant="outline" @click="router.back()">
          Annulla modifica
        </UButton>
        <UButton type="submit" color="primary">
          Procedi
        </UButton>
      </div>
    </form>

    <div
      v-if="success"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
    >
      <div class="mx-4 w-full max-w-sm rounded-xl bg-white p-8 text-center shadow-xl">
        <div class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-green-100">
          <UIcon name="i-lucide-check" class="size-8 text-green-600" />
        </div>
        <h2 class="mb-2 text-xl font-bold text-gray-900">Valori aggiornati</h2>
        <p class="mb-6 text-sm text-gray-500">
          I valori ecosostenibili sono stati aggiornati con successo.
        </p>
        <button
          type="button"
          class="w-full rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-green-700"
          @click="router.push('/area-riservata')"
        >
          Torna alla dashboard
        </button>
      </div>
    </div>

    <div
      v-if="error"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
    >
      <div class="mx-4 w-full max-w-sm rounded-xl bg-white p-8 text-center shadow-xl">
        <div class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-red-100">
          <UIcon name="i-lucide-alert-circle" class="size-8 text-red-600" />
        </div>
        <h2 class="mb-2 text-xl font-bold text-gray-900">Errore</h2>
        <p class="mb-6 text-sm text-gray-500">{{ error }}</p>
        <button
          type="button"
          class="w-full rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-green-700"
          @click="error = ''"
        >
          Chiudi
        </button>
      </div>
    </div>
  </div>
</template>
