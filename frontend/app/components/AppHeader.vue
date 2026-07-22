<script setup lang="ts">
const auth = useAuthStore()
const router = useRouter()

const links = [
  { label: 'Home', to: '/' },
  { label: 'Chi Siamo', to: '/chi-siamo' },
]

const protectedLinks = computed(() => {
  const items: { label: string; to: string }[] = []
  if (auth.isVisitatore) {
    items.push({ label: 'Area Riservata', to: '/area-riservata' })
  }
  if (auth.isGestore) {
    items.push({ label: 'Le Mie Attività', to: '/mie-attivita' })
  }
  if (auth.isAdmin) {
    items.push({ label: 'Segnalazioni', to: '/lista-segnalazioni' })
  }
  return items
})

function logout() {
  auth.logout()
  router.push('/')
}
</script>

<template>
  <header class="sticky top-0 z-50 border-b border-gray-200 bg-white/80 backdrop-blur-sm">
    <div class="container mx-auto flex items-center justify-between px-4 py-3">
      <NuxtLink to="/" class="text-2xl font-bold text-green-600">
        GreenTrails
      </NuxtLink>
      <SearchBar />
      <nav class="flex items-center gap-3">
        <NuxtLink
          v-for="link in links"
          :key="link.to"
          :to="link.to"
          class="text-sm font-medium text-gray-600 hover:text-green-600 transition-colors"
        >
          {{ link.label }}
        </NuxtLink>
        <NuxtLink
          v-for="link in protectedLinks"
          :key="link.to"
          :to="link.to"
          class="text-sm font-medium text-green-600 hover:text-green-700 transition-colors"
        >
          {{ link.label }}
        </NuxtLink>
        <template v-if="auth.isLoggedIn">
          <span class="text-sm text-gray-500">{{ auth.user?.nome }}</span>
          <UButton color="neutral" variant="ghost" size="sm" @click="logout">
            Esci
          </UButton>
        </template>
        <NuxtLink
          v-else
          to="/login"
          class="rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white hover:bg-green-700 transition-colors"
        >
          Accedi
        </NuxtLink>
      </nav>
    </div>
  </header>
</template>