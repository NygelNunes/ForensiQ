#!/bin/bash

# ==============================================================================
# WORKFLOW ALTERNATIVO GITHUB: NygelNunes
# Objetivo: Enviar uma pasta LOCAL JÁ EXISTENTE para um repositório NOVO e VAZIO 
# no GitHub, SEM usar 'git clone' e SEM criar pastas duplicadas.
# ==============================================================================

# ------------------------------------------------------------------------------
# ETAPA 1: Entrar na pasta onde o seu código já está
# Você não vai baixar nada. Apenas navegue até a pasta original do seu projeto.
# Exemplo: cd AulaEncapsulamentoBCC
# ------------------------------------------------------------------------------
cd Caminho/Para/Sua/PastaOriginal

# ------------------------------------------------------------------------------
# ETAPA 2: Inicializar o Git localmente
# Este comando transforma a sua pasta comum em um repositório Git oficial. 
# Ele cria uma pasta oculta chamada '.git' para monitorar os arquivos.
# ------------------------------------------------------------------------------
git init

# ------------------------------------------------------------------------------
# ETAPA 3: Conectar a sua pasta local ao GitHub (Remote)
# Como você não clonou, o Git não sabe para onde enviar os códigos. Este 
# comando cria a ponte entre o seu PC e o repositório vazio no GitHub.
# DICA: Substitua a URL pela URL real do seu repositório recém-criado.
# ------------------------------------------------------------------------------
git remote add origin https://github.com/NygelNunes/NomeDoRepositorioVazio.git

# ------------------------------------------------------------------------------
# ETAPA 4: Mapear todos os arquivos da pasta (Staging)
# O ponto (.) avisa o Git para preparar todos os seus códigos (.java, .php, etc.)
# para o primeiro envio.
# ------------------------------------------------------------------------------
git add .

# ------------------------------------------------------------------------------
# ETAPA 5: Registrar o estado inicial do projeto (Commit)
# Cria o pacote com o seu código pronto para subir.
# ------------------------------------------------------------------------------
git commit -m "feat: commit inicial do projeto sem duplicar pastas"

# ------------------------------------------------------------------------------
# ETAPA 6: Renomear a branch principal para 'main'
# Como você iniciou o Git do zero na Etapa 2, ele pode chamar a branch padrão 
# de 'master'. Este comando garante o padrão moderno e profissional ('main').
# ------------------------------------------------------------------------------
git branch -M main

# ------------------------------------------------------------------------------
# ETAPA 7: Enviar para o GitHub vinculando as branches (Push)
# Envia os códigos para o GitHub e usa a flag "-u" para fixar a conexão.
# Nas próximas vezes, você usará apenas 'git push origin main'.
# ------------------------------------------------------------------------------
git push -u origin main

# ==============================================================================
# STATUS: Código enviado! Repositório populado e workspace limpo (sem duplicatas).
# ==============================================================================