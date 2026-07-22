<script setup lang="ts">
definePageMeta({ middleware: 'auth' })

const { auth: authApi } = useApi()
const router = useRouter()

const questions = [
  {
    key: 'viaggioPreferito',
    label: 'Dove preferisci viaggiare?',
    options: [
      { value: 'MONTAGNA', label: 'Montagna', icon: 'i-lucide-mountain' },
      { value: 'MARE', label: 'Mare', icon: 'i-lucide-waves' },
      { value: 'CITTA', label: 'Città', icon: 'i-lucide-building-2' },
      { value: 'NESSUNA_PREFERENZA', label: 'Indifferente', icon: 'i-lucide-help-circle' },
    ],
  },
  {
    key: 'alloggioPreferito',
    label: 'Dove preferisci alloggiare?',
    options: [
      { value: 'HOTEL', label: 'Hotel', icon: 'i-lucide-building' },
      { value: 'BED_AND_BREAKFAST', label: 'B&B', icon: 'i-lucide-home' },
      { value: 'VILLAGGIO_TURISTICO', label: 'Villaggio turistico', icon: 'i-lucide-umbrella' },
      { value: 'OSTELLO', label: 'Ostello', icon: 'i-lucide-bed-double' },
      { value: 'NESSUNA_PREFERENZA', label: 'Indifferente', icon: 'i-lucide-help-circle' },
    ],
  },
  {
    key: 'preferenzaAlimentare',
    label: 'Hai qualche preferenza alimentare?',
    options: [
      { value: 'VEGAN', label: 'Vegan', icon: 'i-lucide-leaf' },
      { value: 'VEGETARIAN', label: 'Vegetariano', icon: 'i-lucide-sprout' },
      { value: 'GLUTEN_FREE', label: 'Gluten free', icon: 'i-lucide-croissant' },
      { value: 'NESSUNA_PREFERENZA', label: 'Nessuna', icon: 'i-lucide-help-circle' },
    ],
  },
  {
    key: 'attivitaPreferita',
    label: 'Che attività vorresti svolgere?',
    options: [
      { value: 'ALL_APERTO', label: 'All\'aperto', icon: 'i-lucide-tree-pine' },
      { value: 'VISITE_CULTURALI_STORICHE', label: 'Visite culturali', icon: 'i-lucide-landmark' },
      { value: 'RELAX', label: 'Relax', icon: 'i-lucide-heart' },
      { value: 'GASTRONOMIA', label: 'Gastronomia', icon: 'i-lucide-utensils-crossed' },
      { value: 'NESSUNA_PREFERENZA', label: 'Indifferente', icon: 'i-lucide-help-circle' },
    ],
  },
  {
    key: 'animaleDomestico',
    label: 'Viaggerai con il tuo animale domestico?',
    options: [
      { value: 'true', label: 'Sì', icon: 'i-lucide-paw-print' },
      { value: 'false', label: 'No', icon: 'i-lucide-x' },
    ],
  },
  {
    key: 'budgetPreferito',
    label: 'Che budget preferisci?',
    options: [
      { value: 'BASSO', label: 'Basso', icon: 'i-lucide-chevron-down' },
      { value: 'MEDIO', label: 'Medio', icon: 'i-lucide-minus' },
      { value: 'ALTO', label: 'Alto', icon: 'i-lucide-chevron-up' },
      { value: 'FLESSIBILE', label: 'Flessibile', icon: 'i-lucide-sliders-horizontal' },
    ],
  },
  {
    key: 'souvenir',
    label: 'Sei interessato a souvenir locali?',
    options: [
      { value: 'true', label: 'Sì', icon: 'i-lucide-gift' },
      { value: 'false', label: 'No', icon: 'i-lucide-x' },
    ],
  },
  {
    key: 'stagioniPreferite',
    label: 'Preferisci una stagione in particolare?',
    options: [
      { value: 'AUTUNNO_INVERNO', label: 'Autunno / Inverno', icon: 'i-lucide-snowflake' },
      { value: 'PRIMAVERA_ESTATE', label: 'Primavera / Estate', icon: 'i-lucide-sun' },
      { value: 'NESSUNA_PREFERENZA', label: 'Indifferente', icon: 'i-lucide-help-circle' },
    ],
  },
]

const errorMessages: Record<string, string> = {
  viaggioPreferito: 'Nessuna preferenza indicata per i viaggi',
  alloggioPreferito: 'Non risulta alcuna preferenza per l\'alloggio',
  preferenzaAlimentare: 'Non risulta inserita alcuna preferenza alimentare',
  attivitaPreferita: 'Non risulta indicata alcuna preferenza per le attività da svolgere',
  animaleDomestico: 'Campo "Viaggiare con un animale domestico" non selezionato',
  budgetPreferito: 'Non risulta indicata alcuna preferenza riguardo il budget',
  souvenir: 'Nessuna preferenza indicata riguardo i souvenirs',
  stagioniPreferite: 'Nessuna preferenza indicata riguardo la stagione',
}

const answers = ref<Record<string, string>>({})
const step = ref(0)
const error = ref('')
const success = ref('')
const loading = ref(false)

const currentQuestion = computed(() => questions[step.value])
const totalSteps = computed(() => questions.length)
const isFirstStep = computed(() => step.value === 0)
const isLastStep = computed(() => step.value === totalSteps.value - 1)
const progressPercent = computed(() => ((step.value + 1) / totalSteps.value) * 100)

function selectOption(value: string) {
  answers.value[currentQuestion.value.key] = value
}

function next() {
  error.value = ''
  if (!answers.value[currentQuestion.value.key]) {
    error.value = errorMessages[currentQuestion.value.key] || 'Seleziona un\'opzione per proseguire.'
    return
  }
  if (!isLastStep.value) step.value++
}

function prev() {
  step.value--
}

async function invio() {
  error.value = ''
  success.value = ''
  if (!answers.value[currentQuestion.value.key]) {
    error.value = errorMessages[currentQuestion.value.key] || 'Seleziona un\'opzione per proseguire.'
    return
  }
  loading.value = true
  try {
    const res = await authApi.invioQuestionario(answers.value) as { status: string }
    if (res.status === 'success') {
      success.value = 'ok'
    } else {
      error.value = 'Preferenze non inviate. Riprova.'
    }
  } catch (err) {
    console.error('Errore invio questionario:', err)
    error.value = 'Errore durante l\'invio.'
  }
  loading.value = false
}
</script>

<template>
  <div class="py-8">
    <h1 class="mb-8 text-center text-3xl font-bold text-green-600">
      Compilazione Questionario
      <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
    </h1>

    <form class="mx-auto max-w-xl" @submit.prevent="invio">
      <div class="mb-2 flex items-center justify-between text-sm text-gray-500">
        <span>Domanda {{ step + 1 }} di {{ totalSteps }}</span>
        <span>{{ Math.round(progressPercent) }}%</span>
      </div>
      <div class="mb-8 h-2 overflow-hidden rounded-full bg-gray-100">
        <div
          class="h-full rounded-full bg-green-500 transition-all duration-500 ease-out"
          :style="{ width: progressPercent + '%' }"
        />
      </div>

      <div
        :key="questions[step].key"
        class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm"
      >
        <h2 class="mb-6 text-xl font-semibold text-gray-800">
          {{ questions[step].label }}
        </h2>
        <div class="grid grid-cols-2 gap-3 sm:grid-cols-3">
          <button
            v-for="opt in questions[step].options"
            :key="opt.value"
            type="button"
            class="flex flex-col items-center gap-2 rounded-xl border-2 p-4 text-center transition"
            :class="answers[questions[step].key] === opt.value
              ? 'border-green-500 bg-green-50 text-green-700'
              : 'border-gray-200 bg-white text-gray-600 hover:border-green-300 hover:bg-green-50/50'"
            @click="selectOption(opt.value)"
          >
            <UIcon :name="opt.icon" class="size-8" />
            <span class="text-sm font-medium leading-tight">{{ opt.label }}</span>
          </button>
        </div>
      </div>

      <div v-if="error && !success" class="mt-4 rounded-lg bg-red-50 p-3 text-sm text-red-600">{{ error }}</div>

      <div v-if="!success" class="mt-6 flex justify-between">
        <button
          type="button"
          class="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-50"
          @click="isFirstStep ? router.push('/area-riservata') : prev()"
        >
          {{ isFirstStep ? 'Esci' : 'Indietro' }}
        </button>
        <button
          v-if="!isLastStep"
          type="button"
          class="rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="!answers[currentQuestion.key]"
          @click="next"
        >
          Avanti
        </button>
        <button
          v-else
          type="submit"
          class="rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="loading"
        >
          {{ loading ? 'Invio...' : 'Invia' }}
        </button>
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
        <h2 class="mb-2 text-xl font-bold text-gray-900">Questionario completato!</h2>
        <p class="mb-6 text-sm text-gray-500">
          Le tue preferenze sono state salvate con successo.
        </p>
        <button
          type="button"
          class="w-full rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-green-700"
          @click="router.push('/area-riservata')"
        >
          Torna all'area riservata
        </button>
      </div>
    </div>
  </div>
</template>