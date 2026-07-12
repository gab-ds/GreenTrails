<script setup lang="ts">
const show = ref(false)

onMounted(() => {
  const accepted = localStorage.getItem('acceptedCookies')
  if (!accepted) {
    setTimeout(() => (show.value = true), 1000)
  }
})

function accept() {
  localStorage.setItem('acceptedCookies', 'true')
  show.value = false
}

function decline() {
  window.location.href = 'https://www.google.com'
}
</script>

<template>
  <div
    v-if="show"
    class="fixed bottom-0 left-0 right-0 z-50 border-t border-gray-200 bg-white p-4 shadow-lg"
  >
    <div class="container mx-auto flex items-center justify-between gap-4">
      <p class="text-sm text-gray-600">
        Questo sito utilizza cookie per garantire una migliore esperienza di navigazione.
      </p>
      <div class="flex gap-2">
        <UButton color="primary" @click="accept">
          Accetta
        </UButton>
        <UButton color="gray" variant="outline" @click="decline">
          Rifiuta
        </UButton>
      </div>
    </div>
  </div>
</template>