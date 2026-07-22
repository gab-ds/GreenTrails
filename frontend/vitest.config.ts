import { defineVitestConfig } from '@nuxt/test-utils/config'

export default defineVitestConfig({
  test: {
    environment: 'nuxt',
    testTimeout: 15000,
    hookTimeout: 30000,
  },
})
