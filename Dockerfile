# ═══════════════════════════════════════════════════════════════
# RoomRent — Multi-stage Docker build
#
# Stage 1: Build React public portal (Vite + Node 24)
# Stage 2: Build Spring Boot + Angular (Maven prod profile + JDK 21)
# Stage 3: Minimal JRE 21 runtime
#
# Produces a single self-contained JAR that serves:
#   /portal/**    → React public portal (served from classpath:/static/portal/)
#   /api/**       → Spring Boot REST API
#   /admin/**     → JHipster Angular administration
# ═══════════════════════════════════════════════════════════════

# ── Stage 1: React portal ──────────────────────────────────────
FROM node:24-alpine AS react-build

WORKDIR /app

# Dependency layer: copy lockfiles before source for better cache reuse
COPY frontRoomRent/package.json frontRoomRent/package-lock.json ./
RUN npm ci

COPY frontRoomRent/ ./

# VITE_API_BASE defaults to '' (same-origin), which is correct when
# Nginx terminates TLS and proxies all traffic to this container.
# Override at build time with: --build-arg VITE_API_BASE=https://api.example.com
ARG VITE_API_BASE=
ENV VITE_API_BASE=${VITE_API_BASE}

RUN npm run build
# Output in /app/dist/ (base path: /portal/)

# ── Stage 2: Spring Boot + Angular ────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS maven-build

WORKDIR /build

# Maven wrapper (must be executable)
COPY mvnw .
COPY .mvn/ .mvn/
COPY pom.xml .
RUN chmod +x mvnw

# Application source
COPY src/ src/

# Inject React portal dist into Spring Boot's static classpath.
# Spring Boot maps classpath:/static/portal/ → URL /portal/
COPY --from=react-build /app/dist/ src/main/resources/static/portal/

# Build production JAR.
# The Maven prod profile also runs Angular build via frontend-maven-plugin
# (downloads Node v24.16.0 and npm 11.15.0 automatically).
RUN ./mvnw -Pprod package -DskipTests -B

# ── Stage 3: Runtime ──────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as non-root
RUN addgroup -S roomrent && adduser -S roomrent -G roomrent

COPY --from=maven-build /build/target/room-0.0.1-SNAPSHOT.jar app.jar
RUN chown roomrent:roomrent app.jar

USER roomrent
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

# JAVA_OPTS can be overridden at runtime via docker-compose environment section
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS:--Xmx512m -Xms256m} -jar app.jar"]
