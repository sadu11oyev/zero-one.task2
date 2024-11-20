package uz.backend.task2

enum class ErrorCodes(val code: Int){
    USER_NOT_FOUND(100),
    USER_ALREADY_EXIST(101),

}
enum class UserRole{
    USER, ADMIN
}

enum class OrderStatus{
    PENDING, DELIVERED, FINISHED, CANCELLED
}
enum class PaymentMethod {
    CASH, CARD, BANK
}

enum class PaymentStatus{
    
}