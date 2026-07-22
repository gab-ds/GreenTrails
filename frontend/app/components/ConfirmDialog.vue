<script setup lang="ts">
defineProps<{
  title?: string
  message: string
  confirmLabel?: string
  cancelLabel?: string
  loading?: boolean
  variant?: 'danger' | 'primary'
}>()

const emit = defineEmits<{
  confirm: []
  cancel: []
}>()
</script>

<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/40" @click.self="emit('cancel')">
    <div class="mx-4 w-full max-w-sm rounded-xl bg-white p-6 shadow-xl">
      <h3 v-if="title" class="mb-2 text-lg font-bold text-gray-900">{{ title }}</h3>
      <p class="mb-6 text-sm text-gray-600">{{ message }}</p>
      <div class="flex justify-end gap-3">
        <button
          type="button"
          class="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-50"
          @click="emit('cancel')"
        >
          {{ cancelLabel || 'Annulla' }}
        </button>
        <button
          type="button"
          :class="variant === 'danger'
            ? 'bg-red-600 text-white hover:bg-red-700'
            : 'bg-green-600 text-white hover:bg-green-700'"
          class="rounded-lg px-4 py-2 text-sm font-medium transition disabled:opacity-50"
          :disabled="loading"
          @click="emit('confirm')"
        >
          {{ loading ? '...' : (confirmLabel || 'Conferma') }}
        </button>
      </div>
    </div>
  </div>
</template>
