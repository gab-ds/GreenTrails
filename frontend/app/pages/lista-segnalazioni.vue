<script setup lang="ts">
definePageMeta({ middleware: 'auth', role: 'AMMINISTRATORE' })

interface Segnalazione {
  id: number
  descrizione: string
  utente: { email: string }
  attivita: { id: number; nome: string }
}

const { segnalazioni } = useApi()
const router = useRouter()

const lista = ref<Segnalazione[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await segnalazioni.list(false) as { status: string; data: Segnalazione[] }
    if (res.status === 'success') lista.value = res.data
  } catch (err) {
    console.error('Errore caricamento segnalazioni:', err)
  }
  loading.value = false
})

function modifica(idAttivita: number) {
  router.push(`/modifica-valori-admin?id=${idAttivita}`)
}
</script>

<template>
  <div class="py-8">
    <h1 class="mb-8 text-center text-3xl font-bold text-green-600">
      Lista segnalazioni
      <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
    </h1>

    <div v-if="loading" class="text-center text-gray-400">Caricamento...</div>

    <div v-else-if="lista.length === 0" class="text-center text-gray-400">
      Nessuna segnalazione presente.
    </div>

    <div v-else class="mx-auto max-w-4xl overflow-x-auto">
      <table class="w-full text-left text-sm">
        <thead>
          <tr class="border-b border-gray-200 text-gray-500">
            <th class="px-4 py-3 font-medium">N.</th>
            <th class="px-4 py-3 font-medium">Email Utente</th>
            <th class="px-4 py-3 font-medium">Attività</th>
            <th class="px-4 py-3 font-medium">Descrizione</th>
            <th class="px-4 py-3 font-medium" />
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(s, i) in lista"
            :key="s.id"
            class="border-b border-gray-100 hover:bg-gray-50 transition-colors"
          >
            <td class="px-4 py-3 text-gray-600">{{ i + 1 }}</td>
            <td class="px-4 py-3 text-gray-800">{{ s.utente?.email }}</td>
            <td class="px-4 py-3 font-medium text-gray-800">{{ s.attivita?.nome }}</td>
            <td class="px-4 py-3 text-gray-600">{{ s.descrizione }}</td>
            <td class="px-4 py-3">
              <UButton
                color="primary"
                variant="ghost"
                size="sm"
                icon="i-lucide-pencil"
                @click="modifica(s.attivita.id)"
              />
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
