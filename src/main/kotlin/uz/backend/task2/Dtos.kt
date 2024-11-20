package uz.backend.task2

import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class BaseMessage(val code: Int, val message: String?)

// U S E R
data class UserCreateRequest(
    @field:Size(min = 0, max = 10) val username: String,
    val fullName: String,
    val email:String,
    val address: String,
    val role: UserRole,
) {
    fun toEntity(): User {
        return User(username, fullName, email, address, role)
    }
}

data class UserResponse(
    val id: Long,
    val username: String,
    val fullName: String,
    val email:String,
    val address: String,
    val role: UserRole,
    val createdAt: LocalDateTime?,
) {
    companion object {
        fun toResponse(user: User): UserResponse {
            user.run {
                return UserResponse(id!!, username, fullName, email, address, role, createdAt)
            }
        }
    }
}

data class UserUpdateRequest(
    val username: String?,
    val fullName: String?,
    val email:String?,
    val address: String?,
    val role: UserRole?,
)

// C A T E G O R Y
data class CategoryCreateRequest(
    @field:Size(min = 0, max = 10) val name: String,
    val description: String
) {
    fun toEntity(): Category {
        return Category(name, description)
    }
}

data class CategoryResponse(
    val id: Long,
    val name: String,
    val description: String,
    val createdAt:LocalDateTime?
) {
    companion object {
        fun toResponse(category: Category): CategoryResponse {
            category.run {
                return CategoryResponse(id!!, name, description, createdAt)
            }
        }
    }
}

data class CategoryUpdateRequest(
    val name: String?,
    val description: String?
)

// P R O D U C T
data class ProductCreateRequest(
    @field:Size(min = 0, max = 10) val name: String,
    val description: String,
    var price:Double,
    var stockCount: Int,
    val categoryId: Long
) {
    fun toEntity(category: Category): Product {
        return Product(name, description,price, stockCount, category)
    }
}

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    var price:Double,
    var stockCount: Int,
    val categoryName: String
) {
    companion object {
        fun toResponse(product: Product): ProductResponse {
            product.run {
                return ProductResponse(id!!, name, description,price,stockCount, category.name)
            }
        }
    }
}

data class ProductUpdateRequest(
    val name: String?,
    val description: String?,
    var price:Double?,
    var stockCount: Int?,
    val categoryId: Long?
)