<script setup lang="ts">
interface Attivita {
  id: number
  nome: string
  citta?: string
}

const router = useRouter()
const { activities } = useApi()

const filterTerm = ref('')
const showResults = ref(false)
const allActivities = ref<Attivita[]>([])

onMounted(async () => {
  try {
    const data = await activities.list() as Attivita[]
    allActivities.value = data
  } catch {
    // silently fail
  }
})

const filtered = computed(() => {
  if (!filterTerm.value) return []
  return allActivities.value.filter(a =>
    a.nome.toLowerCase().includes(filterTerm.value.toLowerCase())
  )
})

function navigateTo(id: number) {
  filterTerm.value = ''
  showResults.value = false
  router.push(`/attivita/${id}`)
}
</script>

<template>
  <div class="relative">
    <div class="flex items-center gap-1">
      <UInput
        v-model="filterTerm"
        placeholder="Cerca per nome, città, categoria..."
        size="sm"
        class="w-64"
        @focus="showResults = true"
        @blur="setTimeout(() => showResults = false, 200)"
      />
      <UButton
        color="neutral"
        variant="ghost"
        size="sm"
        icon="i-lucide-map-pin"
        :to="'/ricerca/posizione'"
        title="Ricerca per posizione"
      />
    </div>
    <div
      v-if="showResults && filtered.length > 0"
      class="absolute top-full left-0 mt-1 w-full rounded-lg border border-gray-200 bg-white shadow-lg"
    >
      <button
        v-for="a in filtered"
        :key="a.id"
        class="w-full px-4 py-2 text-left text-sm hover:bg-gray-50 transition-colors"
        @mousedown.prevent="navigateTo(a.id)"
      >
        {{ a.nome }}
      </button>
    </div>
  </div>
</template>
