# Usa a imagem do Gradle com JDK 17
FROM gradle:8.5-jdk17 AS dev

# Define o diretório de trabalho
WORKDIR /app

# Copia apenas os arquivos de configuração do Gradle primeiro (para cache)
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle gradle

# Baixa as dependências antes de copiar o código-fonte (para otimizar cache)
RUN gradle dependencies --no-daemon

# Agora copia o restante do código
COPY . .

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação no modo desenvolvimento
CMD ["gradle", "run", "--no-daemon"]
