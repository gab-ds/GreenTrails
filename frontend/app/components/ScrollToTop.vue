<script setup lang="ts">
const show = ref(false)

function onScroll() {
  show.value = window.scrollY > 20
}

onMounted(() => window.addEventListener('scroll', onScroll))
onUnmounted(() => window.removeEventListener('scroll', onScroll))

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<template>
  <UButton
    v-if="show"
    :icon="'i-heroicons-arrow-up-20-solid'"
    color="primary"
    size="lg"
    class="fixed bottom-20 right-6 z-50 rounded-full shadow-lg"
    @click="scrollToTop"
  />
</template>