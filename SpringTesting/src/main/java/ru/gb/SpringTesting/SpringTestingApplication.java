package ru.gb.SpringTesting;

/*
10 семинар дз:
 В проекте библиотека написать полноценные API-тесты (с поднятием БД в h2 и WebTestClient) на все ресурсы проекта, т.е.
 Получение книги, читателя, выдачи, создание книги, читателя, ресурса, ...

 Чтобы не париться с безопасностью, рекомендую ее выключить в тестах.
*/

 import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringTestingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringTestingApplication.class, args);
	}

}
