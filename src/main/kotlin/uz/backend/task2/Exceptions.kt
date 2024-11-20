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
class CategoryAllReadyExistException:Task1ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.CATEGORY_ALREADY_EXIST
    }
}
class CategoryNotFoundException:Task1ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.CATEGORY_NOT_FOUNT
    }
}
class ProductAllReadyExistException:Task1ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.PRODUCT_ALREADY_EXIST
    }
}
class ProductNotFoundException:Task1ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.PRODUCT_NOT_FOUNT
    }
}

class ProductLackOfException:Task1ExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.PRODUCT_LACK_OF
    }
}

class InsufficientFundsException: Task1ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.INSUFFICIENT_FUNDS
    }
}