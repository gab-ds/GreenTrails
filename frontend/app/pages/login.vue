<script setup lang="ts">
const auth = useAuthStore()
const router = useRouter()

const email = ref('')
const password = ref('')
const showPwd = ref(false)
const error = ref('')
const loading = ref(false)

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

async function onSubmit() {
  error.value = ''

  if (!email.value) {
    error.value = "La lunghezza dell'email non è corretta."
    return
  }
  if (!emailRegex.test(email.value)) {
    error.value = "Il formato dell'email non è valido."
    return
  }
  if (password.value.length < 8) {
    error.value = 'La lunghezza della password non è corretta.'
    return
  }

  loading.value = true
  const ok = await auth.login(email.value, password.value)
  loading.value = false
  if (ok) {
    router.push('/')
  } else {
    error.value = 'Email o password errati.'
  }
}
</script>

<template>
  <div class="mx-auto mt-12 max-w-md">
    <div class="rounded-xl border border-gray-200 bg-white p-8 shadow-sm">
      <h1 class="mb-8 text-center text-3xl font-bold text-green-600">
        Accedi
        <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
      </h1>
      <form class="space-y-5" @submit.prevent="onSubmit">
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">Email</label>
          <UInput
            v-model="email"
            type="email"
            placeholder="nome@esempio.it"
            size="lg"
            class="w-full"
          />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">Password</label>
          <div class="relative">
            <UInput
              v-model="password"
              :type="showPwd ? 'text' : 'password'"
              placeholder="••••••••"
              size="lg"
              class="w-full"
            />
            <button
              type="button"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-400 hover:text-gray-600"
              @click="showPwd = !showPwd"
            >
              {{ showPwd ? 'nascondi' : 'mostra' }}
            </button>
          </div>
        </div>
        <div
          v-if="error"
          class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
        >
          <div class="mx-4 w-full max-w-sm rounded-xl bg-white p-8 text-center shadow-xl">
            <div class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-red-100">
              <UIcon name="i-lucide-alert-circle" class="size-8 text-red-600" />
            </div>
            <h2 class="mb-2 text-xl font-bold text-gray-900">Errore di accesso</h2>
            <p class="mb-6 text-sm text-gray-500">{{ error }}</p>
            <button
              type="button"
              class="w-full rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-green-700"
              @click="error = ''"
            >
              Chiudi
            </button>
          </div>
        </div>
        <UButton
          type="submit"
          color="primary"
          size="lg"
          block
          :loading="loading"
          class="font-semibold"
        >
          Accedi
        </UButton>
        <p class="text-center text-sm text-gray-500">
          Non hai un account?
          <NuxtLink to="/registrazione" class="font-medium text-green-600 hover:underline">
            Registrati
          </NuxtLink>
        </p>
      </form>
    </div>
  </div>
</template>
