<script setup lang="ts">
const props = defineProps<{
  images: string[]
  initialIndex?: number
}>()

const emit = defineEmits<{ close: [] }>()

const currentIndex = ref(props.initialIndex ?? 0)

function prev() {
  if (currentIndex.value > 0) currentIndex.value--
}

function next() {
  if (currentIndex.value < props.images.length - 1) currentIndex.value++
}

function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape') emit('close')
  if (e.key === 'ArrowLeft') prev()
  if (e.key === 'ArrowRight') next()
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

    <button
      v-if="images.length > 1 && currentIndex > 0"
      class="absolute left-4 top-1/2 z-10 -translate-y-1/2 text-4xl text-white transition hover:text-gray-300"
      @click="prev"
    >
      &#8249;
    </button>

    <img :src="images[currentIndex]" class="max-h-[85vh] max-w-[90vw] rounded-lg object-contain">

    <button
      v-if="images.length > 1 && currentIndex < images.length - 1"
      class="absolute right-4 top-1/2 z-10 -translate-y-1/2 text-4xl text-white transition hover:text-gray-300"
      @click="next"
    >
      &#8250;
    </button>

    <div v-if="images.length > 1" class="absolute bottom-6 text-sm text-white">
      {{ currentIndex + 1 }} / {{ images.length }}
    </div>
  </div>
</template>
