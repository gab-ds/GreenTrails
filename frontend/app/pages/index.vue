<script setup lang="ts">
const { activities } = useApi()

const { data: suggeriti } = await useAsyncData('suggeriti', () => activities.list())
const { data: turistiche } = await useAsyncData('turistiche', () => activities.touristActivities(8))
const { data: alloggi } = await useAsyncData('alloggi', () => activities.accommodations(8))
const { data: economici } = await useAsyncData('economici', () => activities.byPrice(300))

const suggeritiList = computed(() => suggeriti.value?.data ?? [])
const turisticheList = computed(() => turistiche.value?.data ?? [])
const alloggiList = computed(() => alloggi.value?.data ?? [])
const economiciList = computed(() => economici.value?.data ?? [])
</script>

<template>
  <div class="space-y-12 py-8">
    <section v-if="suggeritiList.length">
      <h2 class="mb-6 text-2xl font-bold text-gray-800">Suggeriti per te</h2>
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        <NuxtLink
          v-for="a in suggeritiList"
          :key="a.id"
          :to="`/attivita/${a.id}`"
          class="group rounded-xl border border-gray-200 bg-white p-4 shadow-sm transition hover:shadow-md"
        >
          <div class="mb-3 aspect-video w-full rounded-lg bg-gray-100" />
          <h3 class="font-semibold text-gray-800 group-hover:text-green-600">{{ a.nome }}</h3>
          <p class="text-sm text-gray-500">{{ a.citta }}</p>
          <p class="mt-1 text-sm text-gray-400">{{ a.prezzo }}€</p>
        </NuxtLink>
      </div>
    </section>

    <section v-if="turisticheList.length">
      <h2 class="mb-6 text-2xl font-bold text-gray-800">Attività turistiche</h2>
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        <NuxtLink
          v-for="a in turisticheList"
          :key="a.id"
          :to="`/attivita/${a.id}`"
          class="group rounded-xl border border-gray-200 bg-white p-4 shadow-sm transition hover:shadow-md"
        >
          <div class="mb-3 aspect-video w-full rounded-lg bg-gray-100" />
          <h3 class="font-semibold text-gray-800 group-hover:text-green-600">{{ a.nome }}</h3>
          <p class="text-sm text-gray-500">{{ a.citta }}</p>
          <p class="mt-1 text-sm text-gray-400">{{ a.prezzo }}€</p>
        </NuxtLink>
      </div>
    </section>

    <section v-if="alloggiList.length">
      <h2 class="mb-6 text-2xl font-bold text-gray-800">Alloggi</h2>
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        <NuxtLink
          v-for="a in alloggiList"
          :key="a.id"
          :to="`/attivita/${a.id}`"
          class="group rounded-xl border border-gray-200 bg-white p-4 shadow-sm transition hover:shadow-md"
        >
          <div class="mb-3 aspect-video w-full rounded-lg bg-gray-100" />
          <h3 class="font-semibold text-gray-800 group-hover:text-green-600">{{ a.nome }}</h3>
          <p class="text-sm text-gray-500">{{ a.citta }}</p>
          <p class="mt-1 text-sm text-gray-400">{{ a.prezzo }}€</p>
        </NuxtLink>
      </div>
    </section>

    <section v-if="economiciList.length">
      <h2 class="mb-6 text-2xl font-bold text-gray-800">I più economici</h2>
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        <NuxtLink
          v-for="a in economiciList"
          :key="a.id"
          :to="`/attivita/${a.id}`"
          class="group rounded-xl border border-gray-200 bg-white p-4 shadow-sm transition hover:shadow-md"
        >
          <div class="mb-3 aspect-video w-full rounded-lg bg-gray-100" />
          <h3 class="font-semibold text-gray-800 group-hover:text-green-600">{{ a.nome }}</h3>
          <p class="text-sm text-gray-500">{{ a.citta }}</p>
          <p class="mt-1 text-sm text-gray-400">{{ a.prezzo }}€</p>
        </NuxtLink>
      </div>
    </section>
  </div>
</template>
