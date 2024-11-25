package uz.backend.task2

import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

interface UserService {
    fun create(request: UserCreateRequest)
    fun getAll(page: Pageable): Page<UserResponse>
    fun getOne(id: Long): UserResponse
    fun update(id: Long, request: UserUpdateRequest)
    fun delete(id: Long)
}
interface CategoryService {
    fun create(request: CategoryCreateRequest)
    fun getAll(pageable: Pageable): Page<CategoryResponse>
    fun getOne(id: Long): CategoryResponse
    fun update(id: Long, request: CategoryUpdateRequest)
}
interface ProductService {
    fun create(request: ProductCreateRequest)
    fun getAll(pageable: Pageable): Page<ProductResponse>
    fun getOne(id: Long): ProductResponse
    fun update(id: Long, request: ProductUpdateRequest)
    fun delete(id: Long)
}
interface OrderItemService{
    fun create(userId: Long,request: OrderItemCreateReq)
    fun getOrderItem(orderId: Long, pageable: Pageable): Page<OrderItemResponse>
    fun getUserOrders(userId: Long, pageable: Pageable):Page<OrderItemResponse>
    fun getProductStatistics(productName:String ): ProductStatistics
    fun getUserOrderStatistics4(userId: Long,startDate: String,endDate: String):List<UserOrderStatisticsRes>
}
interface OrderService {
    fun getAll(userId: Long): List<OrderResponse>
    fun getOne(userId:Long, id:Long): OrderResponse
    fun cancelOrder(userId:Long, id:Long)
    fun getUserOrderStatistics(userId:Long,date:String): OrderStatisticsRes

}
interface PaymentService{
    fun create(request: PaymentCreateReq)
    fun getAll(userId: Long): List<PaymentResponse>
}
interface AdminService {
    fun updateOrder(adminId: Long, orderId: Long): String
}


@Service
class UserServiceImpl(
    private val repository: UserRepository
) : UserService {
    override fun create(request: UserCreateRequest) {
        request.run {
            val user = repository.findByUsernameAndDeletedFalse(username)
            if (user != null) {
                throw UserAllReadyExistException()
            }
            repository.save(this.toEntity())
        }
    }

    override fun getAll(page: Pageable): Page<UserResponse> {
        return repository.findAllNotDeletedForPageable(page).map {
            UserResponse.toResponse(it)
        }
    }

    override fun getOne(id: Long): UserResponse {
        return repository.findByIdAndDeletedFalse(id)?.let {
            UserResponse.toResponse(it)
        } ?: throw UserNotFoundException()
    }

    override fun update(id: Long, request: UserUpdateRequest) {

        val user = repository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException()

        request.run {
            username?.let {
                val usernameDeletedFalse = repository.findByUsername(id, it)
                if (usernameDeletedFalse != null) {
                    throw UserAllReadyExistException()
                }
                user.username = it
            }
            fullName?.let { user.fullName = it }
            email?.let { user. email = it}
            address?.let { user.address = it }
            role?.let { user.role = it }

        }
        repository.save(user)
    }

    @Transactional
    override fun delete(id: Long) {
        repository.trash(id) ?: throw UserNotFoundException()
    }
}

@Service
class CategoryServiceImpl(
    private val repository: CategoryRepository
) : CategoryService {
    override fun create(request: CategoryCreateRequest) {
        request.run {
            val category = repository.findByNameAndDeletedFalse(name)
            if (category!=null){
                throw CategoryAllReadyExistException()
            }
            repository.save(this.toEntity())
        }
    }

    override fun getAll(pageable: Pageable): Page<CategoryResponse> {
        return repository.findAllNotDeletedForPageable(pageable).map {
            CategoryResponse.toResponse(it)
        }
    }

    override fun getOne(id: Long): CategoryResponse {
        return repository.findByIdAndDeletedFalse(id)?.let {
            CategoryResponse.toResponse(it)
        } ?: throw CategoryNotFoundException()
    }

    override fun update(id: Long, request: CategoryUpdateRequest) {
        val category = repository.findByIdAndDeletedFalse(id) ?: throw CategoryNotFoundException()

        request.run {
            name?.let {
                repository.findByName(id, it)?:throw throw CategoryAllReadyExistException()
                category.name = it
            }
            name?.let { category.name = it }
            description?.let { category.description = it }
        }
        repository.save(category)
    }
}

@Service
class ProductServiceImpl(
    private val repository: ProductRepository,
    private val categoryRepository: CategoryRepository,
) : ProductService {
    override fun create(request: ProductCreateRequest) {
        request.run {
            val product = repository.findByNameAndDeletedFalse(name)
            if (product!=null){
                throw ProductAllReadyExistException()
            }
            val category = categoryRepository.findByIdAndDeletedFalse(categoryId)?:throw CategoryNotFoundException()
            repository.save(this.toEntity(category))
        }
    }

    override fun getAll(pageable: Pageable): Page<ProductResponse> {
        return repository.findAllNotDeletedForPageable(pageable).map {
            ProductResponse.toResponse(it)
        }
    }

    override fun getOne(id: Long): ProductResponse {
        return repository.findByIdAndDeletedFalse(id)?.let {
            ProductResponse.toResponse(it)
        } ?: throw CategoryNotFoundException()
    }

    override fun update(id: Long, request: ProductUpdateRequest) {
        val product = repository.findByIdAndDeletedFalse(id) ?: throw ProductNotFoundException()

        request.run {
            name?.let {
                val nameDeletedFalse = repository.findByName(id, it)
                if (nameDeletedFalse != null) {
                    throw ProductAllReadyExistException()
                }
                product.name = it
            }
            categoryId?.let {
                val ct = categoryRepository.findByIdAndDeletedFalse(it) ?: CategoryNotFoundException()
                product.category = ct as Category
            }
            description?.let { product.description = it }
            price?.let { product.price = it }
            stockCount?.let { product.stockCount = it }

        }
        repository.save(product)
    }

    @Transactional
    override fun delete(id: Long) {
        repository.trash(id) ?: throw ProductNotFoundException()
    }
}
@Service
class OrderItemServiceImpl(
    private val repository: OrderItemRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : OrderItemService{
    override fun create(userId: Long, request: OrderItemCreateReq) {
        val user = userRepository.findByIdAndDeletedFalse(userId)?: throw UserNotFoundException()
        var totalAmount = 0.0

        for(item in request.items){
            val product = productRepository.findByIdAndDeletedFalse(item.productId)?:throw ProductNotFoundException()
            val stockCount = productRepository.getAllCount(item.productId)
            if (stockCount < item.quantity) {
                throw ProductLackOfException()
            }
            val prTotalAmount = (item.quantity) * product.price
            totalAmount  += prTotalAmount
        }


        val order = Order(user,totalAmount,OrderStatus.PENDING )
        orderRepository.save(order)

        val orderItemList = mutableListOf<OrderItem>()
        for(item in request.items){
            val product = productRepository.findByIdAndDeletedFalse(item.productId)?:throw ProductNotFoundException()
            val prTotalAmount = item.quantity * product.price
            val orderItem=OrderItem(order,product,item.quantity,product.price,prTotalAmount)
            orderItemList.add(orderItem)
        }
        repository.saveAll(orderItemList)
    }

    override fun getOrderItem(orderId: Long, pageable: Pageable): Page<OrderItemResponse> {
        orderRepository.findById(orderId)
            .orElseThrow { OrderNotFoundException() }
        return repository.findAllByOrderId(orderId,pageable).map {
            OrderItemResponse.toResponse(it)
        }
    }

    override fun getUserOrders(userId: Long, pageable: Pageable): Page<OrderItemResponse> {
        userRepository.findByIdAndDeletedFalse(userId)?: throw UserNotFoundException()
        return repository.findAllByUserId(userId,pageable).map {  OrderItemResponse.toResponse(it) }
    }

    override fun getProductStatistics(productName: String): ProductStatistics {
        val product = productRepository.findByName(productName)?:throw ProductNotFoundException()
        return repository.getProductStatistics(product.id!!)
    }

    override fun getUserOrderStatistics4(userId: Long, startDate: String, endDate: String): List<UserOrderStatisticsRes> {
        userRepository.findByIdAndDeletedFalse(userId)?: throw UserNotFoundException()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val startLocalDate = LocalDate.parse(startDate, formatter)
        val endLocalDate = LocalDate.parse(endDate, formatter)
        return repository.findUserOrderStatistics(userId,startLocalDate,endLocalDate)

    }
}

@Service
class OrderServiceImpl(
    private val repository: OrderRepository,
    private val userRepository: UserRepository,
    private val orderItemRepository: OrderItemRepository
):OrderService{
    override fun getAll(userId: Long): List<OrderResponse> {
        userRepository.findByIdAndDeletedFalse(userId)?: throw UserNotFoundException()
        return repository.findAllByUserId(userId).map { order->OrderResponse.toResponse(order) }
    }

    override fun getOne(userId: Long, id: Long): OrderResponse {
        userRepository.findByIdAndDeletedFalse(userId)?: throw UserNotFoundException()
        val opt = repository.findById(id)
        if(opt.isEmpty){ throw OrderNotFoundException()}
        return OrderResponse.toResponse(opt.get())
    }

    @Transactional
    override fun cancelOrder(userId: Long, id: Long) {
        userRepository.findByIdAndDeletedFalse(userId)?: throw UserNotFoundException()
        val opt = repository.findById(id)
        if(opt.isEmpty){ throw OrderNotFoundException()}

        if (opt.get().status == OrderStatus.PENDING){
            orderItemRepository.deleteByOrderId(id)
            opt.get().status = OrderStatus.CANCELLED
            repository.save(opt.get())
        }else{
            throw  NotCancelOrderException()
        }
    }

    override fun getUserOrderStatistics(userId: Long, date: String): OrderStatisticsRes {
        userRepository.findByIdAndDeletedFalse(userId)?: throw UserNotFoundException()
        val firstDate = getFirstDate(date)
        val lastDate = getLastDate(date)
        return repository.getStatistics(userId,firstDate,lastDate)
    }
}

@Service
class PaymentServiceImpl(
    private val repository: PaymentRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
): PaymentService {
    override fun create(request: PaymentCreateReq) {
        request.run {
            val user = userRepository.findByIdAndDeletedFalse(userId)?:throw UserNotFoundException()
            val opt = orderRepository.findById(orderId)
            if(opt.isEmpty){ throw OrderNotFoundException()}
            repository.save(this.toEntity(user,opt.get()))
        }
    }

    override fun getAll(userId: Long): List<PaymentResponse> {
        userRepository.findByIdAndDeletedFalse(userId)?:throw UserNotFoundException()
        return repository.findAllByUserId(userId).map { payment ->PaymentResponse.toResponse(payment) }
    }
}

@Service
class AdminServiceImpl(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
):AdminService {

    override fun updateOrder(adminId: Long, orderId: Long):String {
        val user = userRepository.findByIdAndDeletedFalse(adminId)?:throw UserNotFoundException()
        if (user.role != UserRole.ADMIN){
            throw NotAdminException()
        }
        val order = orderRepository.findById(orderId).get()
        var boolean = false
        when (order.status) {
            OrderStatus.PENDING -> {
                order.status=OrderStatus.DELIVERED
                boolean=true
            }
            OrderStatus.DELIVERED -> {
                order.status=OrderStatus.FINISHED
                boolean=true
            }
            OrderStatus.FINISHED -> println("Order finished")
            OrderStatus.CANCELLED -> throw CancelledOrderException()
        }
        if(boolean){ orderRepository.save(order)}
        return "Order status: "+ order.status
    }
}

private fun getFirstDate(date: String): LocalDate {
    try {
        val yearMonth = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy.MM"))
        return yearMonth.atDay(1)
    } catch (e: Exception) {
        throw IllegalArgumentException(e)
    }
}

private fun getLastDate(date: String): LocalDate {
    try {
        val yearMonth = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy.MM"))
        return yearMonth.atEndOfMonth()
    } catch (e: Exception) {
        throw IllegalArgumentException(e)
    }
}


