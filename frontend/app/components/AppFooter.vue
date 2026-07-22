<template>
  <footer class="border-t border-gray-200 bg-gray-50">
    <div class="container mx-auto px-4 py-8">
      <div class="flex flex-col items-center justify-between gap-4 sm:flex-row">
        <p class="text-sm text-gray-500">
          &copy; {{ new Date().getFullYear() }} GreenTrails. Tutti i diritti riservati.
        </p>
        <div class="flex gap-4 text-sm text-gray-500">
          <NuxtLink to="/chi-siamo" class="hover:text-green-600 transition-colors">
            Chi Siamo
          </NuxtLink>
        </div>
      </div>
    </div>
  </footer>
</template>