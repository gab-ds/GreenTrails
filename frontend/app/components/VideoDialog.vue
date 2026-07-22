<script setup lang="ts">
const props = defineProps<{
  src: string
  title?: string
}>()

const emit = defineEmits<{ close: [] }>()

function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape') emit('close')
}

onMounted(() => window.addEventListener('keydown', onKeydown))
onUnmounted(() => window.removeEventListener('keydown', onKeydown))
</script>

<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/80" @click.self="emit('close')">
    <button
      class="absolute right-4 top-4 z-10 text-3xl text-white transition hover:text-gray-300"
      @click="emit('close')"
    >
      &times;
    </button>

    <div class="flex flex-col items-center gap-4">
      <p v-if="title" class="text-lg font-medium text-white">{{ title }}</p>
      <video :src="src" controls autoplay class="max-h-[85vh] max-w-[90vw] rounded-lg" />
    </div>
  </div>
</template>
