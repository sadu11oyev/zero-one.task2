package uz.backend.task2

enum class ErrorCodes(val code: Int){
    USER_NOT_FOUND(100),
    USER_ALREADY_EXIST(101),
    CATEGORY_NOT_FOUNT(200),
    CATEGORY_ALREADY_EXIST(201),
    PRODUCT_NOT_FOUNT(300),
    PRODUCT_ALREADY_EXIST(301),
    PRODUCT_LACK_OF(302),
    NOT_CANCEL_ORDER(303),
    ORDER_NOT_FOUND(304),
    NOT_ADMIN_EXCEPTION(305),
    CANCELLED_ORDER_EXCEPTION(306)

}
enum class UserRole{
    USER, ADMIN
}

enum class OrderStatus{
    PENDING, DELIVERED, FINISHED, CANCELLED
}
enum class PaymentMethod {
    UZCARD, HUMO, PAYME, CASH
}

enum class PaymentStatus{
    SUCCESS, UN_SUCCESS
}