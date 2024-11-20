package uz.backend.task2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["uz.backend.task2"],
    repositoryBaseClass = BaseRepositoryImpl::class
)
class Task2Application

fun main(args: Array<String>) {
    runApplication<Task2Application>(*args)
}
