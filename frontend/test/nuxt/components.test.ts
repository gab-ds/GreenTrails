import { describe, expect, it, vi } from 'vitest'
import { mountSuspended } from '@nuxt/test-utils/runtime'
import AppHeader from '~/components/AppHeader.vue'
import AppFooter from '~/components/AppFooter.vue'
import ChiSiamo from '~/pages/chi-siamo.vue'
import SearchBar from '~/components/SearchBar.vue'

describe('AppHeader', () => {
  it('mostra il titolo GreenTrails', async () => {
    const wrapper = await mountSuspended(AppHeader)
    expect(wrapper.text()).toContain('GreenTrails')
  })

  it('mostra i link di navigazione', async () => {
    const wrapper = await mountSuspended(AppHeader)
    expect(wrapper.text()).toContain('Home')
    expect(wrapper.text()).toContain('Chi Siamo')
  })

  it('mostra il pulsante Accedi', async () => {
    const wrapper = await mountSuspended(AppHeader)
    expect(wrapper.text()).toContain('Accedi')
  })
})

describe('AppFooter', () => {
  it('mostra il copyright', async () => {
    const wrapper = await mountSuspended(AppFooter)
    expect(wrapper.text()).toContain('GreenTrails')
    expect(wrapper.text()).toContain('Tutti i diritti riservati')
  })

  it('mostra il link Chi Siamo', async () => {
    const wrapper = await mountSuspended(AppFooter)
    expect(wrapper.text()).toContain('Chi Siamo')
  })
})

describe('ChiSiamo', () => {
  it('mostra il titolo', async () => {
    const wrapper = await mountSuspended(ChiSiamo)
    expect(wrapper.text()).toContain('Chi Siamo')
  })

  it('mostra le politiche eco-sostenibili', async () => {
    const wrapper = await mountSuspended(ChiSiamo)
    expect(wrapper.text()).toContain('Antispreco')
    expect(wrapper.text()).toContain('Energia Verde')
    expect(wrapper.text()).toContain('Raccolta Differenziata')
  })

  it('mostra il messaggio finale', async () => {
    const wrapper = await mountSuspended(ChiSiamo)
    expect(wrapper.text()).toContain('Buon viaggio')
  })
})

describe('SearchBar', () => {
  it('mostra l\'input di ricerca con placeholder', async () => {
    const wrapper = await mountSuspended(SearchBar)
    const input = wrapper.find('input')
    expect(input.exists()).toBe(true)
    expect(input.attributes('placeholder')).toContain('Cerca')
  })

  it('mostra il pulsante per la ricerca geografica', async () => {
    const wrapper = await mountSuspended(SearchBar)
    const btn = wrapper.find('[title="Ricerca per posizione"]')
    expect(btn.exists()).toBe(true)
  })

  it('pulsante geo ricerca punta a /ricerca/posizione', async () => {
    const wrapper = await mountSuspended(SearchBar)
    const btn = wrapper.find('[title="Ricerca per posizione"]')
    expect(btn.attributes('href')).toBe('/ricerca/posizione')
  })
})
