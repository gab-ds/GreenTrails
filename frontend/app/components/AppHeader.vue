<script setup lang="ts">
const links = [
  { label: 'Home', to: '/' },
  { label: 'Chi Siamo', to: '/chi-siamo' },
]
</script>

<template>
  <header class="sticky top-0 z-50 border-b border-gray-200 bg-white/80 backdrop-blur-sm">
    <div class="container mx-auto flex items-center justify-between px-4 py-3">
      <NuxtLink to="/" class="text-2xl font-bold text-green-600">
        GreenTrails
      </NuxtLink>
      <SearchBar />
      <nav class="flex items-center gap-6">
        <NuxtLink
          v-for="link in links"
          :key="link.to"
          :to="link.to"
          class="text-sm font-medium text-gray-600 hover:text-green-600 transition-colors"
        >
          {{ link.label }}
        </NuxtLink>
        <NuxtLink
          to="/login"
          class="rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white hover:bg-green-700 transition-colors"
        >
          Accedi
        </NuxtLink>
      </nav>
    </div>
  </header>
</template>