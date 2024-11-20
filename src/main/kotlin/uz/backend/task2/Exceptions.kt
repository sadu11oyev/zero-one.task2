package uz.backend.task2

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource

sealed class Task2ExceptionHandler: RuntimeException(){
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

class UserAllReadyExistException:Task2ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.USER_ALREADY_EXIST
    }
}
class UserNotFoundException:Task2ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.USER_NOT_FOUND
    }
}
class CategoryAllReadyExistException:Task2ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.CATEGORY_ALREADY_EXIST
    }
}
class CategoryNotFoundException:Task2ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.CATEGORY_NOT_FOUNT
    }
}
class ProductAllReadyExistException:Task2ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.PRODUCT_ALREADY_EXIST
    }
}
class ProductNotFoundException:Task2ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.PRODUCT_NOT_FOUNT
    }
}

class ProductLackOfException:Task2ExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.PRODUCT_LACK_OF
    }
}

class OrderNotFoundException():Task2ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.ORDER_NOT_FOUND
    }
}

class NotCancelOrderException():Task2ExceptionHandler(){
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.NOT_CANCEL_ORDER
    }
}