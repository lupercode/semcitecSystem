<h1 align="center">
    <img src="./src/main/resources/static/img/SemcitecSystemLogo.svg" alt="Semcitec Logo">
</h1>

<p align="center">
    Sistema integrado desenvolvido inicialmente para a gestão escolar e administrativa da Secretaria de Ciência e Tecnologia de Coroatá.
</p>

![Semcitec System devices](./src/main/resources/static/img/digital_devices.png)

## Instalação via Docker

1. Faça o download dos arquivos ou clone o repositório `git clone https://github.com/lupercode/semcitecSystem.git`.
2. Instale o [Docker](https://docs.docker.com/install/), [Docker Compose](https://docs.docker.com/compose/install/) e o [npm](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm).
3. No terminal acesse o diretório baixado `cd ./semcitecSystem`
    - Vá até o diretório static `cd ./src/main/resources/static/` e execute o comando `npm install`.
    - Retorne para a raiz do projeto `cd -` e execute o comando `./mvnw spring-boot:build-image` para criar a imagem docker da aplicação.
    - Ainda no teminal execute o comando `docker compose up -d` para criar o container da aplicação e do bando de dados.
        > Edite as credenciais no arquivo .env como preferir
4. Acesse a URL `http://localhost/` ou o ip do servidor.
    > `usuário = admin: senha = admin`

    > ⚠️ **Importante:** Altere a senha padrão do usuário admin.


## Bibliotecas / Frameworks

### FrontEnd
- [twbs/bootstrap](https://github.com/twbs/bootstra)
- [FortAwesome/Font-Awesome](https://github.com/FortAwesome/Font-Awesome)
- [chartjs/Chart.js](https://github.com/chartjs/Chart.js)
- [datatables](https://datatables.net/)
- [tinymce/tinymce](https://github.com/tinymce/tinymce)
- [startbootstrap-sb-admin-2](https://github.com/StartBootstrap/startbootstrap-sb-admin-2)

### BackEnd
- [spring-projects](https://github.com/spring-projects)
- [projectlombok/lombok](https://github.com/projectlombok/lombok)
- [thymeleaf/thymeleaf](https://github.com/thymeleaf/thymeleaf)
- [itext/itextpdf](https://github.com/itext/itextpdf)
- [flyway/flyway](https://github.com/flyway/flyway)

## Autor

| ![](https://avatars.githubusercontent.com/lupercode?s=120) | [Luan Pereira](https://github.com/lupercode)<br>Desenvolvedor Full Stack<br>[![Static Badge](https://img.shields.io/badge/Instagram-black?style=for-the-badge&logo=instagram)](https://www.instagram.com/lupercode/)|
|:-----------------------------------------------------------:| ------------------------------------------------------------------------ |
