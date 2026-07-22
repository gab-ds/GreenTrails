<script setup lang="ts">
import { ref, onMounted } from 'vue'

const props = defineProps<{
  activityId: number
}>()

const emit = defineEmits<{
  close: []
}>()

const { activities, categorie: categorieApi } = useApi()

const allCategorie = [
  { id: 1, nome: 'Cultura e Storia' },
  { id: 2, nome: 'Enogastronomia' },
  { id: 3, nome: 'Natura e Riserve naturali' },
  { id: 4, nome: 'Sport in spazio aperto' },
  { id: 5, nome: 'Relax e Benessere' },
  { id: 6, nome: 'Eventi locali' },
  { id: 7, nome: 'Escursione' },
  { id: 8, nome: 'Negozio locale' },
  { id: 9, nome: 'Spazio aperto' },
  { id: 10, nome: 'Vicino al mare' },
]

const assignedCategorie = ref<{ id: number; nome: string }[]>([])
const loading = ref(true)
const saving = ref(false)
const error = ref('')
const selectedId = ref<number | null>(null)
const confirmDeleteId = ref<number | null>(null)
const deleting = ref(false)

const availableCategorie = computed(() =>
  allCategorie.filter(c => !assignedCategorie.value.some(a => a.id === c.id))
)

onMounted(loadCategorie)

async function loadCategorie() {
  loading.value = true
  try {
    const res = await activities.get(props.activityId) as { status: string; data: { categorie: { id: number; nome: string }[] | null } }
    if (res.status === 'success') {
      assignedCategorie.value = (res.data.categorie || []).map(c => ({ id: c.id, nome: c.nome }))
    }
  } catch (err) {
    console.error('Errore caricamento categorie:', err)
    error.value = 'Errore nel caricamento delle categorie.'
  }
  loading.value = false
}

async function aggiungiCategoria() {
  if (selectedId.value == null) return
  saving.value = true
  error.value = ''
  try {
    const res = await categorieApi.add(props.activityId, selectedId.value) as { status: string }
    if (res.status === 'success') {
      const cat = allCategorie.find(c => c.id === selectedId.value)
      if (cat) assignedCategorie.value.push({ ...cat })
      selectedId.value = null
    } else {
      error.value = 'Errore nell\'assegnazione della categoria.'
    }
  } catch (err) {
    console.error('Errore assegnazione categoria:', err)
    error.value = 'Errore nell\'assegnazione della categoria.'
  }
  saving.value = false
}

async function rimuoviCategoria(id: number) {
  deleting.value = true
  confirmDeleteId.value = null
  try {
    const res = await categorieApi.remove(id, props.activityId) as { status: string }
    if (res.status === 'success') {
      assignedCategorie.value = assignedCategorie.value.filter(c => c.id !== id)
    }
  } catch (err) {
    console.error('Errore rimozione categoria:', err)
  }
  deleting.value = false
}
</script>

<template>
  <div class="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto bg-black/40 pt-16" @click.self="emit('close')">
    <div class="mx-4 mb-12 w-full max-w-lg rounded-xl bg-white p-6 shadow-xl">
      <div class="mb-4 flex items-center justify-between">
        <h2 class="text-lg font-bold text-gray-900">Gestione Categorie</h2>
        <button type="button" class="text-gray-400 hover:text-gray-600 transition" @click="emit('close')">
          <UIcon name="i-lucide-x" class="size-5" />
        </button>
      </div>

      <div v-if="loading" class="py-8 text-center text-sm text-gray-400">Caricamento...</div>

      <template v-else>
        <div v-if="assignedCategorie.length" class="mb-4 space-y-2">
          <h3 class="text-sm font-medium text-gray-600">Categorie assegnate</h3>
          <div v-for="c in assignedCategorie" :key="c.id" class="flex items-center justify-between rounded-lg border border-green-100 bg-green-50 px-3 py-2">
            <span class="text-sm text-gray-800">{{ c.nome }}</span>
            <button type="button" class="text-red-400 hover:text-red-600 transition" @click="confirmDeleteId = c.id" title="Rimuovi">
              <UIcon name="i-lucide-x" class="size-4" />
            </button>
          </div>
        </div>
        <p v-else class="mb-4 text-sm text-gray-400">Nessuna categoria assegnata.</p>

        <div v-if="error" class="mb-3 rounded bg-red-50 p-2 text-sm text-red-600">{{ error }}</div>

        <div v-if="availableCategorie.length" class="flex gap-2">
          <USelect
            v-model="selectedId"
            placeholder="Seleziona categoria"
            :items="availableCategorie.map(c => ({ label: c.nome, value: c.id }))"
            class="flex-1"
            size="sm"
          />
          <UButton color="primary" size="sm" :disabled="selectedId == null" :loading="saving" @click="aggiungiCategoria">
            Aggiungi
          </UButton>
        </div>
        <p v-else class="text-sm text-green-600">Tutte le categorie sono state assegnate.</p>
      </template>

      <ConfirmDialog
        v-if="confirmDeleteId"
        title="Rimuovi categoria"
        message="Rimuovere questa categoria dall'attività?"
        confirm-label="Rimuovi"
        variant="danger"
        :loading="deleting"
        @confirm="rimuoviCategoria(confirmDeleteId)"
        @cancel="confirmDeleteId = null"
      />
    </div>
  </div>
</template>
