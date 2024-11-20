package uz.backend.task2

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler(private val errorMessageSource: ResourceBundleMessageSource) {

    @ExceptionHandler(Task1ExceptionHandler::class)
    fun handleAccountException(exception: Task1ExceptionHandler): ResponseEntity<BaseMessage> {
        return  ResponseEntity.badRequest().body(exception.getErrorMessage(errorMessageSource))
    }

    @RestController
    @RequestMapping("/api/v1/user")
    class UserController() {

    }
}
