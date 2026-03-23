FROM jenkins/jenkins:lts-jdk17

USER root
# Устанавливаем Docker CLI, добавляем пользователя jenkins в группу docker
RUN apt-get update && apt-get install -y docker.io \
    && groupadd -for -g 999 docker \
    && usermod -aG docker jenkins
USER jenkins

# Предустановим Gradle (не обязательно, можно использовать Gradle Wrapper или контейнер)
RUN mkdir -p /home/jenkins/tools && cd /home/jenkins/tools \
    && wget -qO- https://services.gradle.org/distributions/gradle-8.6-bin.zip > gradle-8.6-bin.zip \
    && unzip gradle-8.6-bin.zip \
    && rm gradle-8.6-bin.zip
ENV PATH="/home/jenkins/tools/gradle-8.6/bin:${PATH}"