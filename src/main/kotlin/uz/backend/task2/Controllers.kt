package uz.backend.task2

import jakarta.validation.Valid
import org.hibernate.annotations.Parameter
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler(private val errorMessageSource: ResourceBundleMessageSource) {

    @ExceptionHandler(Task2ExceptionHandler::class)
    fun handleAccountException(exception: Task2ExceptionHandler): ResponseEntity<BaseMessage> {
        return  ResponseEntity.badRequest().body(exception.getErrorMessage(errorMessageSource))
    }

    @RestController
    @RequestMapping("/api/v1/user")
    class UserController(val service: UserService) {
        @PostMapping("create")
        fun create(@RequestBody @Valid request: UserCreateRequest) = service.create(request)

        @GetMapping
        fun getAll(pageable: Pageable) = service.getAll(pageable)


        @GetMapping("{id}")
        fun getOne(@PathVariable id: Long) = service.getOne(id)

        @PostMapping("{id}")
        fun update(@PathVariable id: Long, @RequestBody request: UserUpdateRequest) = service.update(id, request)

        @DeleteMapping("{id}")
        fun delete(@PathVariable id: Long) = service.delete(id)
    }

    @RestController
    @RequestMapping("/api/v1/category")
    class CategoryController(val service: CategoryService) {
        @PostMapping("create")
        fun create(@RequestBody @Valid request: CategoryCreateRequest) = service.create(request)

        @GetMapping
        fun getAll(pageable: Pageable) = service.getAll(pageable)

        @GetMapping("{id}")
        fun getOne(@PathVariable id: Long) = service.getOne(id)

        @PostMapping("{id}")
        fun update(@PathVariable id: Long, @RequestBody request: CategoryUpdateRequest) = service.update(id, request)

    }

    @RestController
    @RequestMapping("/api/v1/product")
    class ProductController(val service: ProductService) {
        @PostMapping("create")
        fun create(@RequestBody @Valid request: ProductCreateRequest) = service.create(request)

        @GetMapping
        fun getAll(pageable: Pageable) = service.getAll(pageable)

        @GetMapping("{id}")
        fun getOne(@PathVariable id: Long) = service.getOne(id)

        @PostMapping("{id}")
        fun update(@PathVariable id: Long, @RequestBody request: ProductUpdateRequest) = service.update(id, request)

        @DeleteMapping("{id}")
        fun delete(@PathVariable id: Long) = service.delete(id)
    }

    @RestController
    @RequestMapping("/api/v1/order")
    class OrderController(val service: OrderService){
        @GetMapping("{userId}")
        fun getAll(@PathVariable userId: Long) = service.getAll(userId)

        @GetMapping("{userId}/{id}")
        fun getOne(@PathVariable userId: Long, @PathVariable id:Long) = service.getOne(userId,id)

        @DeleteMapping("cancel/{userId}/{id}")
        fun cancelOrder(@PathVariable userId: Long, @PathVariable id:Long) = service.cancelOrder(userId,id)

    }
    @RestController
    @RequestMapping("/api/v1/orderItem")
    class OrderItemController(val service: OrderItemService){
        @PostMapping("{userId}")
        fun getAll(@PathVariable userId: Long, @RequestBody request: OrderItemCreateReq) = service.create(userId,request)

        @GetMapping("{orderId}")
        fun getOne(@PathVariable orderId: Long,pageable: Pageable) = service.getOrderItem(orderId,pageable)

    }
    @RestController
    @RequestMapping("/api/v1/payment")
    class PaymentController(val service: PaymentService){
        @GetMapping("{userId}")
        fun getAll(@PathVariable userId: Long) = service.getAll(userId)

        @PostMapping
        fun create(@RequestBody request: PaymentCreateReq) = service.create(request)
    }

    @RestController
    @RequestMapping("/api/v1/admin")
    class AdminController(val service: AdminService){

        @PostMapping("{adminId}/{orderId}")
        fun updateOrder(@PathVariable adminId:Long, @PathVariable orderId:Long) = service.updateOrder(adminId,orderId)
    }


    @RestController
    @RequestMapping("/api/v1/statistics")
    class StatisticsController(val orderItemService: OrderItemService,
                               val orderService: OrderService){

        @GetMapping("1/{userId}")
        fun getUserOrderItems(@PathVariable userId: Long,pageable: Pageable) = orderItemService.getUserOrders(userId,pageable)

        @GetMapping("3/{userId}/{date}")
        fun getUserOrderStatistics(@PathVariable userId: Long,
                                   @PathVariable date: String ): OrderStatisticsRes {
            match(date)
            return orderService.getUserOrderStatistics(userId,date)
        }

        @GetMapping("4/{userId}/{startDate}/{endDate}")
        fun getUserOrderStatistics4(@PathVariable userId: Long,
                                    @PathVariable startDate: String,
                                    @PathVariable endDate: String ){
            match(startDate)
            match(endDate)
            return orderItemService.getUserOrderStatistics4(userId,startDate,endDate)
        }

        @GetMapping("5")
        fun productStatistics(@RequestBody productName: String) = orderItemService.getProductStatistics(productName)

        private fun match(date: String) {
            if (!date.matches(Regex("\\d{4}\\.\\d{2}"))) {
                throw IllegalArgumentException("Date must be in format YYYY.MM (e.g., 2024.01)")
            }
        }

    }
}
