<script setup lang="ts">
import { nextTick } from "vue";
import "leaflet/dist/leaflet.css";
import type { Map as LMap, Marker as LMarker } from "leaflet";

interface AttivitaResult {
    id: number;
    nome: string;
    citta: string;
    prezzo: number;
    coordinate: { x: number; y: number };
}

const { ricerca } = useApi();

const latitudine = ref(40.68);
const longitudine = ref(14.77);
const raggio = ref(10);
const risultati = ref<AttivitaResult[]>([]);
const loading = ref(false);

let map: LMap | null = null;
let markers: LMarker[] = [];

async function cerca() {
    loading.value = true;
    try {
        const res = await ricerca.perPosizione(
            latitudine.value,
            longitudine.value,
            raggio.value,
        );
        risultati.value = res as AttivitaResult[];
        await aggiornaMarker();
    } catch (err) {
        console.error('Errore ricerca posizione:', err);
        risultati.value = [];
    }
    loading.value = false;
}

async function aggiornaMarker() {
    if (!map) return;
    markers.forEach((m) => map!.removeLayer(m));
    markers = [];
    const L = (await import("leaflet")).default;
    for (const r of risultati.value) {
        const marker = L.marker([r.coordinate.x, r.coordinate.y])
            .addTo(map!)
            .bindPopup(
                `<a href="/attivita/${r.id}">${r.nome}</a><br>${r.prezzo}€`,
            );
        markers.push(marker);
    }
}

onMounted(async () => {
    const L = (await import("leaflet")).default;
    map = L.map("map", { zoomControl: true }).setView(
        [latitudine.value, longitudine.value],
        12,
    );
    L.tileLayer("/api/tiles/{z}/{x}/{y}.png", {
        attribution: "&copy; OpenStreetMap contributors",
    }).addTo(map!);
    await nextTick();
    map.invalidateSize();
});
</script>

<template>
    <div class="py-8">
        <h1 class="mb-8 text-center text-3xl font-bold text-gray-800">
            Ricerca per Posizione
        </h1>

        <div class="mx-auto mb-8 flex max-w-2xl flex-wrap items-end gap-4">
            <UInput
                v-model="latitudine"
                type="number"
                step="0.01"
                placeholder="Latitudine"
                class="flex-1"
            />
            <UInput
                v-model="longitudine"
                type="number"
                step="0.01"
                placeholder="Longitudine"
                class="flex-1"
            />
            <UInput
                v-model="raggio"
                type="number"
                step="1"
                placeholder="Raggio (km)"
                class="w-32"
            />
            <UButton color="primary" :loading="loading" @click="cerca">
                Cerca
            </UButton>
        </div>

        <div class="grid grid-cols-1 gap-8 lg:grid-cols-3">
            <div class="lg:col-span-2">
                <ClientOnly>
                    <div
                        id="map"
                        class="h-96 w-full overflow-hidden rounded-xl border border-gray-200"
                    />
                </ClientOnly>
            </div>

            <div class="space-y-3">
                <h2 class="text-lg font-semibold text-gray-800">Risultati</h2>
                <div
                    v-if="risultati.length === 0"
                    class="text-sm text-gray-400"
                >
                    Nessun risultato. Effettua una ricerca.
                </div>
                <NuxtLink
                    v-for="r in risultati"
                    :key="r.id"
                    :to="`/attivita/${r.id}`"
                    class="block rounded-lg border border-gray-200 p-3 transition hover:shadow-sm"
                >
                    <p class="font-medium text-gray-800">{{ r.nome }}</p>
                    <p class="text-sm text-gray-500">{{ r.citta }}</p>
                    <p class="text-sm font-semibold text-green-600">
                        {{ r.prezzo }}€
                    </p>
                </NuxtLink>
            </div>
        </div>
    </div>
</template>
