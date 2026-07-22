export default defineEventHandler(async (event) => {
  const path = getRouterParam(event, 'path')
  if (!path) {
    throw createError({ statusCode: 400, message: 'Missing tile path' })
  }
  const url = `https://tile.openstreetmap.org/${path}`
  const res = await $fetch.raw(url, {
    headers: {
      'User-Agent': 'GreenTrails/1.0',
      'Referer': 'https://greentrails.app/',
    },
    responseType: 'arrayBuffer',
  })
  setHeader(event, 'Content-Type', 'image/png')
  setHeader(event, 'Cache-Control', 'public, max-age=86400')
  return new Response(res._data)
})
