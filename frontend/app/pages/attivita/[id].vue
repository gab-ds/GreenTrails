<script setup lang="ts">
const route = useRoute()
const id = Number(route.params.id)
const { activities, recensioni: recensioniApi } = useApi()

const { data: attivita, error } = await useAsyncData('attivita', () => activities.get(id))
const { data: recensioni } = await useAsyncData('recensioni', () => recensioniApi.perAttivita(id))

const isAlloggio = computed(() => attivita.value?.alloggio)
</script>

<template>
  <div v-if="error" class="py-16 text-center text-gray-500">
    Attività non trovata.
  </div>

  <div v-else-if="attivita" class="py-8">
    <div class="grid grid-cols-1 gap-8 lg:grid-cols-3">
      <div class="lg:col-span-1 space-y-6">
        <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
          <h1 class="text-2xl font-bold text-gray-900">{{ attivita.nome }}</h1>
          <p class="mt-1 text-gray-500">{{ attivita.citta }}, {{ attivita.provincia }}</p>
          <p class="mt-4 text-3xl font-bold text-green-600">{{ attivita.prezzo }}€</p>
          <p class="mt-1 text-sm text-gray-400">
            {{ isAlloggio ? 'Alloggio' : 'Attività Turistica' }}
          </p>
        </div>
      </div>

      <div class="lg:col-span-2 space-y-6">
        <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
          <h2 class="mb-4 text-xl font-semibold text-gray-800">Descrizione</h2>
          <p class="text-gray-600">{{ attivita.descrizioneBreve }}</p>
          <p v-if="attivita.descrizioneLunga" class="mt-4 text-gray-600">
            {{ attivita.descrizioneLunga }}
          </p>
        </div>

        <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
          <h2 class="mb-4 text-xl font-semibold text-gray-800">Recensioni</h2>
          <div v-if="recensioni?.length" class="space-y-4">
            <div
              v-for="recensione in recensioni"
              :key="recensione.id"
              class="border-b border-gray-100 pb-4 last:border-0"
            >
              <div class="flex items-center gap-2">
                <span class="font-medium text-gray-800">{{ recensione.titolo }}</span>
              </div>
              <p class="mt-1 text-sm text-gray-600">{{ recensione.descrizione }}</p>
            </div>
          </div>
          <p v-else class="text-gray-400">Nessuna recensione.</p>
        </div>

        <div class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
          <h2 class="mb-4 text-xl font-semibold text-gray-800">Prenota</h2>
          <p class="text-gray-500">Funzionalità di prenotazione in fase di sviluppo.</p>
        </div>
      </div>
    </div>
  </div>
</template>
