<script setup lang="ts">
definePageMeta({ middleware: 'auth', role: 'GESTORE_ATTIVITA' })

interface Attivita {
  id: number
  nome: string
  citta: string
  provincia: string
  prezzo: number
  alloggio?: boolean
}

const { activities } = useApi()

const lista = ref<Attivita[]>([])
const loading = ref(true)
const deleting = ref<number | null>(null)
const confirmDeleteId = ref<number | null>(null)
const cameraDialogId = ref<number | null>(null)
const categoriaDialogId = ref<number | null>(null)

onMounted(async () => {
  try {
    const res = await activities.myActivities() as { status: string; data: Attivita[] }
    if (res.status === 'success') lista.value = res.data
  } catch (err) {
    console.error('Errore caricamento attività:', err)
  }
  loading.value = false
})

async function elimina(id: number) {
  deleting.value = id
  confirmDeleteId.value = null
  try {
    await activities.delete(id)
    lista.value = lista.value.filter(a => a.id !== id)
  } catch (err) {
    console.error('Errore eliminazione attività:', err)
  }
  deleting.value = null
}
</script>

<template>
  <div class="py-8">
    <div class="mx-auto flex max-w-5xl items-center justify-between">
      <h1 class="text-3xl font-bold text-green-600">
        Le mie attività
        <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
      </h1>
      <UButton color="primary" icon="i-lucide-plus" to="/inserimento-attivita">
        Nuova attività
      </UButton>
    </div>

    <div v-if="loading" class="mt-8 text-center text-gray-400">Caricamento...</div>

    <div v-else-if="lista.length === 0" class="mt-8 text-center text-gray-400">
      Nessuna attività. Creane una nuova!
    </div>

    <div v-else class="mx-auto mt-8 max-w-5xl overflow-x-auto">
      <table class="w-full text-left text-sm">
        <thead>
          <tr class="border-b border-gray-200 text-gray-500">
            <th class="px-4 py-3 font-medium">Nome</th>
            <th class="px-4 py-3 font-medium">Città</th>
            <th class="px-4 py-3 font-medium">Prezzo</th>
            <th class="px-4 py-3 font-medium">Tipo</th>
            <th class="px-4 py-3 font-medium" />
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="a in lista"
            :key="a.id"
            class="border-b border-gray-100 transition-colors hover:bg-gray-50"
          >
            <td class="px-4 py-3">
              <NuxtLink :to="`/attivita/${a.id}`" class="font-medium text-gray-800 hover:text-green-600">
                {{ a.nome }}
              </NuxtLink>
            </td>
            <td class="px-4 py-3 text-gray-600">{{ a.citta }}, {{ a.provincia }}</td>
            <td class="px-4 py-3 font-semibold text-green-600">{{ a.prezzo }}€</td>
            <td class="px-4 py-3">
              <span v-if="a.alloggio" class="rounded-full bg-blue-100 px-2 py-0.5 text-xs text-blue-700">Alloggio</span>
              <span v-else class="rounded-full bg-amber-100 px-2 py-0.5 text-xs text-amber-700">Attività Turistica</span>
            </td>
            <td class="px-4 py-3">
              <div class="flex gap-1">
                <UButton color="primary" variant="ghost" size="sm" icon="i-lucide-pencil" :to="`/inserimento-attivita?id=${a.id}`" title="Modifica" />
                <UButton color="error" variant="ghost" size="sm" icon="i-lucide-trash-2" :loading="deleting === a.id" title="Elimina" @click="confirmDeleteId = a.id" />
                <UButton v-if="a.alloggio" color="neutral" variant="ghost" size="sm" icon="i-lucide-bed-double" title="Camere" @click="cameraDialogId = a.id" />
                <UButton v-else color="neutral" variant="ghost" size="sm" icon="i-lucide-tags" title="Categorie" @click="categoriaDialogId = a.id" />
                <UButton color="neutral" variant="ghost" size="sm" icon="i-lucide-settings" :to="`/modifica-valori/${a.id}`" title="Valori" />
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <ConfirmDialog
      v-if="confirmDeleteId"
      title="Elimina attività"
      message="Eliminare questa attività? L'operazione è irreversibile."
      confirm-label="Elimina"
      variant="danger"
      :loading="deleting === confirmDeleteId"
      @confirm="elimina(confirmDeleteId)"
      @cancel="confirmDeleteId = null"
    />

    <CameraDialog
      v-if="cameraDialogId"
      :activity-id="cameraDialogId"
      @close="cameraDialogId = null"
    />

    <CategoriaDialog
      v-if="categoriaDialogId"
      :activity-id="categoriaDialogId"
      @close="categoriaDialogId = null"
    />
  </div>
</template>
