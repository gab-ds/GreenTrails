// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: "2025-07-15",
  devtools: { enabled: true },

  modules: [
    "@nuxt/eslint",
    "@nuxt/fonts",
    "@nuxt/image",
    "@nuxt/ui",
    "@pinia/nuxt",
    "@formkit/nuxt",
    "@formkit/auto-animate",
    "@nuxt/test-utils/module",
  ],

  /*$development: {
    pinia: {
      storesDirs: ['./app/stores'],
      },
  },*/

  colorMode: {
    preference: "light",
    fallback: "light",
  },

  ui: {},

  fonts: {
    families: [{ name: "Inter", provider: "google" }],
  },

  css: ["~/assets/css/main.css", "leaflet/dist/leaflet.css"],

  runtimeConfig: {
    public: {
      apiBaseUrl: "http://localhost:8080/api",
    },
  },

  app: {
    head: {
      title: "GreenTrails",
      meta: [
        {
          name: "description",
          content: "Eco-sustainable itinerary booking platform",
        },
      ],
    },
  },
});
