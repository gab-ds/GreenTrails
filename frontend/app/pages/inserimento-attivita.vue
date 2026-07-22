<script setup lang="ts">
definePageMeta({ middleware: 'auth', role: 'GESTORE_ATTIVITA' })

const { activities, valori: valoriApi } = useApi()
const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.query.id)
const idAttivita = computed(() => Number(route.query.id))

const nome = ref('')
const alloggio = ref<boolean | null>(null)
const indirizzo = ref('')
const cap = ref('')
const citta = ref('')
const provincia = ref('')
const latitudine = ref('')
const longitudine = ref('')
const descrizioneBreve = ref('')
const descrizioneLunga = ref('')
const prezzo = ref('')
const disponibilita = ref('')
const categoriaAlloggio = ref<number | null>(null)
const categoriaAttivitaTuristica = ref<number | null>(null)
const file = ref<File | null>(null)

const politicheAntispreco = ref(false)
const prodottiLocali = ref(false)
const energiaVerde = ref(false)
const raccoltaDifferenziata = ref(false)
const limiteEmissioneCO2 = ref(false)
const contattoConNatura = ref(false)

const nomeRegex = /^[0-9A-Za-zÀ-ú '*-]*$/
const indirizzoRegex = /^[0-9A-Za-zÀ-ú '-,.]*$/
const capRegex = /^\d{5}$/
const cittaRegex = /^[0-9A-Za-zÀ-ú '*-]*$/
const provinciaRegex = /^[A-Z]{2}$/
const descrizioneBreveRegex = /^[A-Z]/
const descrizioneLungaRegex = /^[A-Z]/

const error = ref('')
const success = ref('')
const loading = ref(false)
const loadingData = ref(!!isEdit.value)

const categorieAlloggio = [
  { value: 0, label: 'Hotel' },
  { value: 1, label: 'Bed & Breakfast' },
  { value: 2, label: 'Villaggio Turistico' },
  { value: 3, label: 'Ostello' },
]

const categorieAttivitaTuristica = [
  { value: 0, label: "All'aperto" },
  { value: 1, label: 'Visite Culturali e Storiche' },
  { value: 2, label: 'Relax' },
  { value: 3, label: 'Gastronomia' },
]

if (isEdit.value) {
  ;(async () => {
    try {
      const res = await activities.get(idAttivita.value) as { status: string; data: Record<string, unknown> }
      if (res.status === 'success') {
        const a = res.data
        nome.value = a.nome as string
        alloggio.value = a.alloggio as boolean
        indirizzo.value = (a.indirizzo as string) || ''
        cap.value = a.cap as string
        citta.value = a.citta as string
        provincia.value = a.provincia as string
        descrizioneBreve.value = a.descrizioneBreve as string
        descrizioneLunga.value = a.descrizioneLunga as string
        prezzo.value = a.prezzo != null ? String(a.prezzo) : ''
        disponibilita.value = a.disponibilita != null ? String(a.disponibilita) : ''
      } else {
        error.value = 'Attività non trovata.'
      }
    } catch (err) {
      console.error('Errore caricamento attività:', err)
      error.value = 'Errore durante il caricamento.'
    }
    loadingData.value = false
  })()
}

function onFileChange(e: Event) {
  const target = e.target as HTMLInputElement
  if (target.files?.length) {
    const f = target.files[0]
    if (!f.type.startsWith('image/') && !f.type.startsWith('video/')) {
      error.value = 'tipologia media non corretta'
      file.value = null
      target.value = ''
      return
    }
    if (f.size > 100 * 1024 * 1024) {
      error.value = 'grandezza media non corretta'
      file.value = null
      target.value = ''
      return
    }
    file.value = f
  }
}

async function onSubmit() {
  error.value = ''
  success.value = ''

  if (!nome.value || nome.value.length < 1 || nome.value.length > 50) {
    error.value = 'lunghezza del nome errata'
    return
  }
  if (!nomeRegex.test(nome.value)) {
    error.value = 'formato nome non corretto'
    return
  }
  if (!indirizzo.value || indirizzo.value.length < 1 || indirizzo.value.length > 255) {
    error.value = 'lunghezza indirizzo non corretta'
    return
  }
  if (!indirizzoRegex.test(indirizzo.value)) {
    error.value = 'formato indirizzo non corretto'
    return
  }
  if (!cap.value || cap.value.length !== 5) {
    error.value = 'lunghezza CAP non corretta'
    return
  }
  if (!capRegex.test(cap.value)) {
    error.value = 'formato CAP non corretto'
    return
  }
  if (!citta.value || citta.value.length < 1 || citta.value.length > 50) {
    error.value = 'lunghezza città non corretta'
    return
  }
  if (!cittaRegex.test(citta.value)) {
    error.value = 'formato città non corretto'
    return
  }
  if (!provincia.value || provincia.value.length !== 2) {
    error.value = 'lunghezza provincia non corretta'
    return
  }
  if (!provinciaRegex.test(provincia.value)) {
    error.value = 'formato provincia non corretto'
    return
  }
  const lat = parseFloat(latitudine.value)
  const lon = parseFloat(longitudine.value)
  if (isNaN(lat) || lat < -90 || lat > 90) {
    error.value = 'latitudine non corretta'
    return
  }
  if (isNaN(lon) || lon < -180 || lon > 180) {
    error.value = 'longitudine non corretta'
    return
  }
  if (alloggio.value == null) {
    error.value = 'tipologia non selezionata'
    return
  }
  const isAlloggio = alloggio.value
  if (isAlloggio && categoriaAlloggio.value == null) {
    error.value = 'categoria non selezionata'
    return
  }
  if (!isAlloggio && categoriaAttivitaTuristica.value == null) {
    error.value = 'categoria non selezionata'
    return
  }
  if (!descrizioneBreve.value || descrizioneBreve.value.length < 5 || descrizioneBreve.value.length > 140) {
    error.value = 'lunghezza descrizione breve non corretta'
    return
  }
  if (!descrizioneBreveRegex.test(descrizioneBreve.value)) {
    error.value = 'formato descrizione breve non corretto'
    return
  }
  if (!descrizioneLunga.value || descrizioneLunga.value.length < 5 || descrizioneLunga.value.length > 2000) {
    error.value = 'lunghezza descrizione lunga non corretta'
    return
  }
  if (!descrizioneLungaRegex.test(descrizioneLunga.value)) {
    error.value = 'formato descrizione lunga non corretto'
    return
  }
  if (file.value) {
    if (!file.value.type.startsWith('image/') && !file.value.type.startsWith('video/')) {
      error.value = 'tipologia media non corretta'
      return
    }
    if (file.value.size > 100 * 1024 * 1024) {
      error.value = 'grandezza media non corretta'
      return
    }
  }
  if (!isAlloggio) {
    const p = parseFloat(prezzo.value)
    if (isNaN(p) || p < 0) {
      error.value = 'prezzo inferiore a 0'
      return
    }
    const d = parseInt(disponibilita.value, 10)
    if (isNaN(d) || d <= 0) {
      error.value = 'disponibilità inferiore a 1'
      return
    }
  }

  loading.value = true
  try {
    const valoriRes = await valoriApi.create({
      politicheAntispreco: politicheAntispreco.value,
      prodottiLocali: prodottiLocali.value,
      energiaVerde: energiaVerde.value,
      raccoltaDifferenziata: raccoltaDifferenziata.value,
      limiteEmissioneCO2: limiteEmissioneCO2.value,
      contattoConNatura: contattoConNatura.value,
    }) as { status: string; data: { id: number } }

    if (valoriRes.status !== 'success') {
      error.value = 'Errore nella creazione dei valori di ecosostenibilità.'
      loading.value = false
      return
    }

    const fd = new FormData()
    fd.append('alloggio', String(alloggio.value))
    fd.append('nome', nome.value)
    fd.append('indirizzo', indirizzo.value)
    fd.append('cap', cap.value)
    fd.append('citta', citta.value)
    fd.append('provincia', provincia.value)
    fd.append('latitudine', latitudine.value)
    fd.append('longitudine', longitudine.value)
    fd.append('descrizioneBreve', descrizioneBreve.value)
    fd.append('descrizioneLunga', descrizioneLunga.value)
    fd.append('valori', String(valoriRes.data.id))
    fd.append('immagine', file.value || new Blob([], { type: 'application/octet-stream' }))

    if (alloggio.value) {
      fd.append('categoriaAlloggio', String(categoriaAlloggio.value))
    } else {
      fd.append('prezzo', prezzo.value)
      fd.append('disponibilita', disponibilita.value)
      fd.append('categoriaAttivitaTuristica', String(categoriaAttivitaTuristica.value))
    }

    let res: { status: string; data: { id: number } }
    if (isEdit.value) {
      res = await activities.update(idAttivita.value, fd) as { status: string; data: { id: number } }
    } else {
      res = await activities.create(fd) as { status: string; data: { id: number } }
    }

    if (res.status === 'success') {
      success.value = 'ok'
    } else {
      error.value = 'Errore durante il salvataggio.'
    }
  } catch (err) {
    console.error('Errore salvataggio attività:', err)
    error.value = 'Errore durante il salvataggio.'
  }
  loading.value = false
}
</script>

<template>
  <div class="py-8">
    <h1 class="mb-8 text-center text-3xl font-bold text-green-600">
      {{ isEdit ? 'Modifica attività' : 'Nuova attività' }}
      <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
    </h1>

    <div v-if="loadingData" class="text-center text-gray-400">Caricamento...</div>

    <form v-else class="mx-auto max-w-2xl space-y-6" @submit.prevent="onSubmit">
      <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
        <h2 class="mb-4 text-lg font-semibold text-gray-800">Informazioni di base</h2>
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <UInput v-model="nome" placeholder="Nome attività" :maxlength="50" label="Nome *" class="sm:col-span-2" />
          <div class="sm:col-span-2">
            <label class="mb-1 block text-sm font-medium text-gray-600">Tipo *</label>
            <div class="flex gap-4">
              <label class="flex cursor-pointer items-center gap-2 rounded-lg border px-4 py-2 text-sm transition" :class="alloggio === true ? 'border-green-500 bg-green-50 text-green-700' : 'border-gray-200 text-gray-600 hover:border-gray-300'">
                <input v-model="alloggio" type="radio" name="alloggio" :value="true" class="sr-only">
                Alloggio
              </label>
              <label class="flex cursor-pointer items-center gap-2 rounded-lg border px-4 py-2 text-sm transition" :class="alloggio === false ? 'border-green-500 bg-green-50 text-green-700' : 'border-gray-200 text-gray-600 hover:border-gray-300'">
                <input v-model="alloggio" type="radio" name="alloggio" :value="false" class="sr-only">
                Attività Turistica
              </label>
            </div>
          </div>
          <UInput v-model="descrizioneBreve" placeholder="Descrizione breve (max 140 caratteri)" :maxlength="140" label="Descrizione breve *" class="sm:col-span-2" />
          <UTextarea v-model="descrizioneLunga" placeholder="Descrizione lunga (max 2000 caratteri)" :maxlength="2000" label="Descrizione lunga *" class="sm:col-span-2" />
        </div>
      </div>

      <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
        <h2 class="mb-4 text-lg font-semibold text-gray-800">Luogo</h2>
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <UInput v-model="indirizzo" placeholder="Indirizzo" label="Indirizzo *" class="sm:col-span-2" />
          <UInput v-model="cap" placeholder="CAP (5 cifre)" :maxlength="5" label="CAP *" />
          <UInput v-model="citta" placeholder="Città" label="Città *" />
          <UInput v-model="provincia" placeholder="Provincia (es. SA)" :maxlength="2" label="Provincia *" />
          <UInput v-model="latitudine" type="number" step="0.000001" placeholder="es. 40.68" label="Latitudine *" />
          <UInput v-model="longitudine" type="number" step="0.000001" placeholder="es. 14.77" label="Longitudine *" />
        </div>
      </div>

      <div v-if="alloggio != null" class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
        <h2 class="mb-4 text-lg font-semibold text-gray-800">
          {{ alloggio ? 'Categoria alloggio' : 'Categoria e prezzo' }}
        </h2>
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div v-if="alloggio" class="sm:col-span-2">
            <label class="mb-1 block text-sm font-medium text-gray-600">Categoria *</label>
            <div class="flex flex-wrap gap-2">
              <label v-for="cat in categorieAlloggio" :key="cat.value" class="cursor-pointer rounded-lg border px-4 py-2 text-sm transition" :class="categoriaAlloggio === cat.value ? 'border-green-500 bg-green-50 text-green-700' : 'border-gray-200 text-gray-600 hover:border-gray-300'">
                <input v-model="categoriaAlloggio" type="radio" name="categoriaAlloggio" :value="cat.value" class="sr-only">
                {{ cat.label }}
              </label>
            </div>
          </div>
          <div v-else class="sm:col-span-2">
            <label class="mb-1 block text-sm font-medium text-gray-600">Categoria *</label>
            <div class="flex flex-wrap gap-2">
              <label v-for="cat in categorieAttivitaTuristica" :key="cat.value" class="cursor-pointer rounded-lg border px-4 py-2 text-sm transition" :class="categoriaAttivitaTuristica === cat.value ? 'border-green-500 bg-green-50 text-green-700' : 'border-gray-200 text-gray-600 hover:border-gray-300'">
                <input v-model="categoriaAttivitaTuristica" type="radio" name="categoriaAttivitaTuristica" :value="cat.value" class="sr-only">
                {{ cat.label }}
              </label>
            </div>
          </div>
          <UInput v-if="!alloggio" v-model="prezzo" type="number" step="0.01" placeholder="Prezzo" label="Prezzo *" />
          <UInput v-if="!alloggio" v-model="disponibilita" type="number" step="1" placeholder="Disponibilità" label="Disponibilità *" />
        </div>
      </div>

      <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
        <h2 class="mb-4 text-lg font-semibold text-gray-800">Politiche eco-sostenibili</h2>
        <div class="flex flex-wrap gap-2">
          <label v-for="p in [
            { key: 'politicheAntispreco', label: 'Antispreco', val: politicheAntispreco },
            { key: 'prodottiLocali', label: 'Prodotti Locali', val: prodottiLocali },
            { key: 'energiaVerde', label: 'Energia Verde', val: energiaVerde },
            { key: 'raccoltaDifferenziata', label: 'Raccolta Differenziata', val: raccoltaDifferenziata },
            { key: 'limiteEmissioneCO2', label: 'Limite Emissione CO₂', val: limiteEmissioneCO2 },
            { key: 'contattoConNatura', label: 'Contatto con Natura', val: contattoConNatura },
          ]" :key="p.key" class="cursor-pointer rounded-lg border px-4 py-2 text-sm transition" :class="p.val ? 'border-green-500 bg-green-50 text-green-700' : 'border-gray-200 text-gray-600 hover:border-gray-300'">
            <input v-model="p.val" type="checkbox" class="sr-only">
            {{ p.label }}
          </label>
        </div>
      </div>

      <div v-if="!isEdit" class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
        <h2 class="mb-4 text-lg font-semibold text-gray-800">Immagine</h2>
        <input type="file" accept="image/*,video/*" @change="onFileChange" class="text-sm text-gray-500 file:mr-3 file:rounded file:border-0 file:bg-green-50 file:px-3 file:py-1.5 file:text-sm file:font-medium file:text-green-700 hover:file:bg-green-100">
      </div>

      <div v-if="error" class="rounded-lg bg-red-50 p-4 text-sm text-red-600">{{ error }}</div>
      <div class="flex justify-between">
        <UButton color="neutral" variant="outline" @click="router.push('/mie-attivita')">
          Annulla
        </UButton>
        <UButton type="submit" color="primary" :loading="loading">
          {{ isEdit ? 'Salva modifiche' : 'Crea attività' }}
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
        <h2 class="mb-2 text-xl font-bold text-gray-900">Attività salvata con successo!</h2>
        <p class="mb-6 text-sm text-gray-500">
          La tua attività è stata salvata correttamente.
        </p>
        <button
          type="button"
          class="w-full rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-green-700"
          @click="router.push('/mie-attivita')"
        >
          Torna alle mie attività
        </button>
      </div>
    </div>
  </div>
</template>
