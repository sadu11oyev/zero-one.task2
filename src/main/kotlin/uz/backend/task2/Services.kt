package uz.backend.task2

import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

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
            if (category != null) {
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
                val nameDeletedFalse = repository.findByName(id, it)
                if (nameDeletedFalse != null) {
                    throw CategoryAllReadyExistException()
                }
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
            if (product != null) {
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
