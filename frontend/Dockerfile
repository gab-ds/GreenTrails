# syntax=docker/dockerfile:1
FROM dhi.io/bun:1-alpine-dev@sha256:9a5d8af34d97f1a40e8497af1584987ca043093040d820cdae990de54d23cbcf AS builder
WORKDIR /app
COPY package.json bun.lock ./
RUN --mount=type=cache,target=/root/.bun/install/cache bun install --frozen-lockfile
COPY . .
RUN bun run build

FROM dhi.io/node:22-alpine3.23@sha256:f7692e98521c11110c4df18ddda7dba5f9819f676b708a731c7c8b26c6e6954c AS runtime
WORKDIR /app
COPY --from=builder /app/.output ./.output
ENV HOST=0.0.0.0
ENV PORT=3000
ENV NUXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
EXPOSE 3000
CMD ["node", ".output/server/index.mjs"]
