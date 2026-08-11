# syntax=docker/dockerfile:1
FROM dhi.io/bun:1-alpine-dev@sha256:d364f4eb6d20f8e906bdb9d12726995f8335878f46e0c1c69c910df9d92df5d8 AS builder
WORKDIR /app
COPY package.json bun.lock ./
RUN --mount=type=cache,target=/root/.bun/install/cache bun install --frozen-lockfile
COPY . .
RUN bun run build

FROM dhi.io/node:22-alpine3.23@sha256:f3c24df224fbc6842525b329b4625a2dc6d101b9d9f0fe455aa2ab17b9afec82 AS runtime
WORKDIR /app
COPY --from=builder /app/.output ./.output
ENV HOST=0.0.0.0
ENV PORT=3000
ENV NUXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
EXPOSE 3000
CMD ["node", ".output/server/index.mjs"]
