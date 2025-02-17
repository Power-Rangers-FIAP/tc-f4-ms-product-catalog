# Tech Challenger FIAP F4

Welcome to the "Power Programmers Product Batch" project repository. This project aims to implement a batch processing system for product data.

## 🎓 Academic Project

Developed as part of the **Java Architecture and Development** postgraduate course at FIAP.

## 👨‍💻 Developers

- Edson Antonio da Silva Junior
- Gabriel Ricardo dos Santos
- Luiz Henrique Romão de Carvalho
- Marcelo de Souza

## 💡 Technologies

![Java](https://img.shields.io/badge/Java-17-blue?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-3.9.9-C71A36?style=for-the-badge&logo=apachemaven)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-27.4.0-2496ED?style=for-the-badge&logo=docker)
![Swagger](https://img.shields.io/badge/Swagger-3.0-85EA2D?style=for-the-badge&logo=swagger)

## 📦 Project Structure

```markdown
📦 br.com.powerprogramers.product
├── configuration          // Application Settings
├── 🎯 domain
│   ├── batch              // Batch processing logic
│   │   ├── impl           // Batch processing logic implementation
│   │   └── job            // Job configurations
│   │
│   ├── consumer           // Consumer logic
│   ├── controller         // REST controllers
│   ├── entity             // Database models
│   ├── exceptions         // Custom exceptions
│   ├── mappers            // Object mapping
│   ├── model              // Models
│   ├── repository         // Data access layer
│   └── service            // Business logic
│
└── 🚀 ProductBatchApplication.java
```

## Introducing microservice

Link: [Tech Challenge F4 - MS Produto - Introducing video](https://www.youtube.com/watch?v=kGPE7fFuiC8)

## ▶️ Como Executar o Projeto

To run the project locally, follow the steps below:

1. **Clone the repository:**
    
    ```bash
    git clone <https://github.com/Power-Rangers-FIAP/tech-challenge-f4-ms-produto.git>
    
    ```
    
2. **Browse to the project directory:**
    
    ```bash
    cd tech-challenge-f4-ms-produto
    
    ```
    
3. **Build the project with Maven:**
    
    ```bash
    mvn clean install -U
    
    ``` 

4. **Start the application locally:**
    
    ```bash
    mvn spring-boot:run
    
    ```

## 🧪 How to do the tests

- **To perform the unit tests:**
    
    ```bash
    mvn test
    
    ```
    
- **To perform the integrated tests:**
    
    ```bash
    mvn test -P integration-text
    
    ``` 

- **To perform the performance tests:**

    >  With the docker running, run the command below: 

    ```bash
    mvn gatling:test -P performance-test
    
    ``` 

- **To perfom the system text:**
    
    ```bash
    mvn test -P system-text
    
    ```

## 📄 Relatório de Performance

After performing the performance test, you can see an execution report opening the index.html file within `target/gatling/performancesimulation-\<dataexecução>/index.html \`
example of the path: `target/gatling/performancesimulation-20241209162646899/index.html`

## 🧪 API Endpoint

The API can be explored and tested using Swagger. The documentation is available at:
[`Swagger`](http://localhost:8081/swagger-ui/index.html)

## Load data

You can configure the path where the loaded files are stored`application.yaml` on the property `load.input-path`:

```yaml
load:
  input-path: ${USERPROFILE}\Downloads\loadProducts
```

## Csv file data

The csv file must have the following columns without a header:

```csv  
name,description,amount,price,active
```

## 👥 contribute

Contributions are welcome! To contribute to the project, please follow these steps:

1. Make a fork of the repository.
2. Create a branch for your feature or correction (`git checkout -b feature/nova-feature`).
3. Make Commit of your changes (`git commit -am 'Add new feature'`).
4. Send your changes to the repository (`git push origin feature/nova-feature`).
5. Open a request pull.

## License

This project is licensed under the [MIT License](https://www.notion.so/LICENSE).
