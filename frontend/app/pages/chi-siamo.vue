<template>
  <div class="py-8">
    <h1 class="mb-8 text-center text-3xl font-bold text-green-600">
      Chi Siamo
      <span class="mx-auto mt-2 block h-1 w-16 rounded-full bg-green-500" />
    </h1>

    <div class="mb-10 rounded-2xl bg-green-50 p-6 md:p-8">
      <div class="grid grid-cols-1 items-center gap-8 md:grid-cols-2">
        <img
          src="/assets/chisiamo/travel.png"
          alt="GreenTrails travel"
          class="mx-auto w-full max-w-sm rounded-2xl"
        >
        <div class="space-y-4 text-base leading-relaxed text-gray-700">
          <p>
            <strong class="text-green-700">Benvenuti su GreenTrails</strong>, luogo dove il rispetto per la natura
            si fonde con la passione per esperienze di viaggio
            straordinarie. Siamo entusiasti di condividere con voi la nostra storia e la
            <strong class="text-green-700">visione</strong> che guida ogni passo della nostra piattaforma.
          </p>
          <p>
            Il nostro obiettivo è un impegno verso la creazione di un
            <strong class="text-green-700">impatto positivo sull'ambiente</strong>,
            offrendo esperienze
            uniche che rispettano la bellezza della natura.
          </p>
          <p>
            Ci impegniamo a selezionare attentamente le strutture disponibili sul nostro
            sito, garantendo che rispettino <strong class="text-green-700">rigorosi standard di sostenibilità</strong>.
            Attraverso la promozione di pratiche ecologiche, vogliamo offrirvi non solo
            un luogo dove soggiornare, ma un luogo in cui potete immergervi in
            esperienze che abbracciano l'<strong class="text-green-700">armonia tra l'uomo e l'ambiente</strong>.
          </p>
        </div>
      </div>
    </div>

    <div class="mb-10 border-b border-gray-200" />

    <h2 class="mb-6 text-center text-2xl font-bold text-gray-800">
      Politiche Eco-Sostenibili
      <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
    </h2>
    <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
      <div
        v-for="politica in politiche"
        :key="politica.titolo"
        class="group rounded-xl border border-gray-200 bg-white p-5 shadow-sm transition hover:border-green-300 hover:shadow-md"
      >
        <div class="mb-4 inline-flex h-14 w-14 items-center justify-center rounded-full bg-green-50 transition group-hover:bg-green-100">
          <img
            :src="politica.icona"
            :alt="politica.titolo"
            class="h-8 w-8 object-contain"
          >
        </div>
        <h3 class="mb-2 text-lg font-semibold text-green-700">{{ politica.titolo }}</h3>
        <p class="text-sm leading-relaxed text-gray-600">{{ politica.descrizione }}</p>
      </div>
    </div>

    <div class="my-10 border-b border-gray-200" />

    <h2 class="mb-6 text-center text-2xl font-bold text-gray-800">
      I nostri obiettivi
      <span class="mx-auto mt-2 block h-1 w-12 rounded-full bg-green-500" />
    </h2>
    <div class="mx-auto max-w-3xl space-y-4 text-base leading-relaxed text-gray-700">
      <p>
        Attraverso la promozione di queste pratiche e l'offerta di esperienze di
        viaggio ecologiche, intendiamo non solo soddisfare le vostre aspettative
        di comfort e bellezza, ma anche contribuire al
        <strong class="text-green-700">benessere del nostro pianeta</strong>.
        Siamo certi che la vostra scelta di prenotare attraverso la
        nostra piattaforma rappresenterà un passo verso uno
        <strong class="text-green-700">stile di vita più sostenibile</strong>.
      </p>
      <p>
        Vi ringraziamo per la vostra attenzione e per essere parte di questa
        importante iniziativa. Insieme, possiamo creare un
        <strong class="text-green-700">impatto positivo</strong> e
        costruire un <strong class="text-green-700">futuro più verde e sostenibile</strong> per tutti.
      </p>
      <div class="mt-6 rounded-xl border-l-4 border-green-500 bg-green-50 p-5">
        <p class="text-center text-xl italic font-semibold text-green-700">
          Buon viaggio e buon soggiorno!
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const politiche = [
  {
    titolo: 'Antispreco',
    icona: '/assets/chisiamo/logo1.png',
    descrizione: 'Le strutture che trovate sul nostro sito si impegnano attivamente a ridurre gli sprechi adottando politiche antispreco. Ciò include l\'uso responsabile delle risorse e l\'implementazione di pratiche che mirano a minimizzare l\'impatto ambientale.',
  },
  {
    titolo: 'Prodotti Locali',
    icona: '/assets/chisiamo/logo2.png',
    descrizione: 'Siamo orgogliosi di promuovere l\'adozione di prodotti locali all\'interno delle strutture presenti sulla nostra piattaforma. Preferiamo partner che privilegiano l\'acquisto di ingredienti provenienti dalla comunità locale.',
  },
  {
    titolo: 'Energia Verde',
    icona: '/assets/chisiamo/logo3.png',
    descrizione: 'Le strutture partner sono fortemente impegnate nell\'utilizzo di energia proveniente da fonti rinnovabili. Questo impegno contribuisce a ridurre l\'uso di combustibili fossili e a promuovere un approccio sostenibile alla produzione di energia.',
  },
  {
    titolo: 'Raccolta Differenziata',
    icona: '/assets/chisiamo/logo4.png',
    descrizione: 'Abbiamo fatto della raccolta differenziata una priorità nelle strutture presenti sulla nostra piattaforma. I nostri partner implementano sistemi di raccolta differenziata dei rifiuti.',
  },
  {
    titolo: 'Ridotte Emissioni CO2',
    icona: '/assets/chisiamo/logo5.png',
    descrizione: 'La nostra piattaforma si impegna a lavorare con strutture che adottano politiche mirate a limitare le emissioni di CO2.',
  },
  {
    titolo: 'Contatto con la Natura',
    icona: '/assets/chisiamo/logo6.png',
    descrizione: 'Valorizziamo le strutture che offrono un\'esperienza autentica e un contatto diretto con la natura circostante. Promuoviamo luoghi che incoraggiano gli ospiti a connettersi con l\'ambiente, apprezzando la bellezza della natura e partecipando a attività che promuovono la conservazione.',
  },
]
</script>
