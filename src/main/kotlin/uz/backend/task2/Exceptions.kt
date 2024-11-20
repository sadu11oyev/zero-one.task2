package uz.backend.task2

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource

sealed class Task1ExceptionHandler: RuntimeException(){
    abstract fun errorCode(): ErrorCodes
    open  fun getAllArguments(): Array<Any>? = null

    fun getErrorMessage( resourceBundle: ResourceBundleMessageSource):BaseMessage{
        val message = try {
            resourceBundle.getMessage(
                errorCode().name, getAllArguments(), LocaleContextHolder.getLocale()
            )
        }catch (e: Exception){
            e.message
        }
        return BaseMessage(errorCode().code,message)
    }
}

class UserAllReadyExistException:Task1ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.USER_ALREADY_EXIST
    }
}
class UserNotFoundException:Task1ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.USER_NOT_FOUND
    }
}