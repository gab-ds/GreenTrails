<script setup lang="ts">
import { CalendarDate } from '@internationalized/date'

const auth = useAuthStore()
const router = useRouter()

const nome = ref('')
const cognome = ref('')
const email = ref('')
const password = ref('')
const dataNascita = shallowRef<CalendarDate | null>(null)
const ruolo = ref(false)
const error = ref('')
const loading = ref(false)

const nomeRegex = /^[A-Za-zÀ-ÿ\s']+$/
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/

function validateDateOfBirth(dob: CalendarDate): string | null {
  if (!dob) return 'La data di nascita non è valida.'
  const oggi = new Date()
  let eta = oggi.getFullYear() - dob.year
  const m = oggi.getMonth() - (dob.month - 1)
  if (m < 0 || (m === 0 && oggi.getDate() < dob.day)) eta--
  if (eta < 18) return 'Devi essere maggiorenne per registrarti.'
  return null
}

function onSubmit() {
  error.value = ''

  if (!nome.value) {
    error.value = 'La lunghezza del nome non è corretta.'
    return
  }
  if (!nomeRegex.test(nome.value)) {
    error.value = 'Il formato del nome non è valido.'
    return
  }
  if (!cognome.value) {
    error.value = 'La lunghezza del cognome non è corretta.'
    return
  }
  if (!nomeRegex.test(cognome.value)) {
    error.value = 'Il formato del cognome non è valido.'
    return
  }
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
  if (!passwordRegex.test(password.value)) {
    error.value = 'Il formato della password non è valido.'
    return
  }
  if (!dataNascita.value) {
    error.value = 'La data di nascita non è valida.'
    return
  }
  const dateErr = validateDateOfBirth(dataNascita.value)
  if (dateErr) {
    error.value = dateErr
    return
  }

  loading.value = true
  const data = {
    nome: nome.value,
    cognome: cognome.value,
    email: email.value,
    password: password.value,
    dataNascita: dataNascita.value.toString(),
  }
  auth.register(data, ruolo.value).then((ok) => {
    loading.value = false
    if (ok) {
      router.push('/')
    } else {
      error.value = 'Errore durante la registrazione.'
    }
  })
}
</script>

<template>
  <div class="mx-auto mt-12 max-w-md">
    <div class="rounded-xl border border-gray-200 bg-white p-8 shadow-sm">
      <h1 class="mb-8 text-center text-3xl font-bold text-green-600">
        Registrati
        <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
      </h1>
      <form class="space-y-4" @submit.prevent="onSubmit">
        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="mb-1 block text-sm font-medium text-gray-700">Nome</label>
            <UInput v-model="nome" placeholder="Mario" size="lg" class="w-full" />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-gray-700">Cognome</label>
            <UInput v-model="cognome" placeholder="Rossi" size="lg" class="w-full" />
          </div>
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">Email</label>
          <UInput v-model="email" type="email" placeholder="nome@esempio.it" size="lg" class="w-full" />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">Password</label>
          <UInput v-model="password" type="password" placeholder="Minimo 8 caratteri" size="lg" class="w-full" />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">Data di nascita</label>
          <UInputDate v-model="dataNascita" locale="it" size="lg" class="w-full">
            <template #trailing>
              <UPopover>
                <UButton
                  color="neutral"
                  variant="link"
                  size="sm"
                  icon="i-lucide-calendar"
                  aria-label="Seleziona data"
                  class="px-0"
                />
                <template #content>
                  <UCalendar v-model="dataNascita" locale="it" class="p-2" />
                </template>
              </UPopover>
            </template>
          </UInputDate>
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium text-gray-700">Ruolo</label>
          <USelect
            v-model="ruolo"
            :items="[
              { label: 'Visitatore', value: false, icon: 'i-lucide-user' },
              { label: 'Gestore Attività', value: true, icon: 'i-lucide-store' },
            ]"
            size="lg"
            class="w-full"
          />
        </div>
        <div v-if="error" class="rounded-lg bg-red-50 p-3 text-sm text-red-600">
          {{ error }}
        </div>
        <UButton
          type="submit"
          color="primary"
          size="lg"
          block
          :loading="loading"
          class="font-semibold"
        >
          Registrati
        </UButton>
        <p class="text-center text-sm text-gray-500">
          Hai già un account?
          <NuxtLink to="/login" class="font-medium text-green-600 hover:underline">
            Accedi
          </NuxtLink>
        </p>
      </form>
    </div>
  </div>
</template>
