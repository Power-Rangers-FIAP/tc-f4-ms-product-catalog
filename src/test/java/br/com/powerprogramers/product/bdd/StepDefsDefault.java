package br.com.powerprogramers.product.bdd;

import br.com.powerprogramers.product.ProductApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ProductApplication.class)
public class StepDefsDefault {}
