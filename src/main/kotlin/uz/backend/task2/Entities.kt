package uz.backend.task2

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Column(nullable = false)  val createdAt: LocalDateTime?=null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false
)

@Entity(name = "users")
class User(
    @Column(nullable = false, unique = true) var username: String,
    @Column(nullable = false) var fullName: String,
    @Column var email: String,
    @Column var address: String,
    @Enumerated(EnumType.STRING) var role: UserRole,
):BaseEntity()

@Entity(name = "category")
class Category(
    @Column(nullable = false, unique = true) var name: String,
    @Column(nullable = false) var description: String,
):BaseEntity()

@Entity(name = "product")
class Product(
    @Column(nullable = false, unique = true) var name: String,
    @Column(nullable = false) var description: String,
    @Column(nullable = false) var price:Double,
    @Column(nullable = false) var stockCount: Int,
    @ManyToOne var category: Category,
):BaseEntity()

@Entity(name = "orders")
class Order(
    @ManyToOne var user: User,
    @Column var totalAmount: Double,
    @Enumerated(EnumType.STRING) var status: OrderStatus,
):BaseEntity()

@Entity(name = "order_item")
class OrderItem(
    @ManyToOne var order:Order,
    @ManyToOne var product:Product,
    @Column var quantity:Double,
    @Column var unitPrice:Double,
    @Column var totalPrice:Double
):BaseEntity()

@Entity(name = "payment")
class Payment(
    @OneToOne val order: Order,
    @ManyToOne val user: User,
    @Enumerated(EnumType.STRING) var paymentMethod: PaymentMethod,
    @Column val amount: Double,
    @Enumerated(EnumType.STRING) var paymentStatus: PaymentStatus,
):BaseEntity()