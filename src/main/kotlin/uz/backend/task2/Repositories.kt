package uz.backend.task2

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@NoRepositoryBean
interface BaseRepository<T: BaseEntity>: JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids: List<Long>): List<T?>
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeleted(pageable: Pageable): List<T>
    fun findAllNotDeletedForPageable(pageable: Pageable): Page<T>
    fun saveAndRefresh(t: T): T
}

class BaseRepositoryImpl<T: BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>,
    private val entityManager: EntityManager
): SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T>{
    val isNotDeletedSpecification = Specification<T>{ root, _, cb -> cb.equal(root.get<Boolean>("deleted"),false)}
    override fun findByIdAndDeletedFalse(id: Long) = findByIdOrNull(id)?.run {if (deleted) null else this}
    @Transactional
    override fun trash(id: Long): T? = findByIdOrNull(id)?.run {
        deleted = true
        save(this)
    }
    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)
    override fun findAllNotDeleted(pageable: Pageable): List<T> = findAll(isNotDeletedSpecification,pageable).content
    override fun findAllNotDeletedForPageable(pageable: Pageable): Page<T> = findAll(isNotDeletedSpecification,pageable)


    override fun trashList(ids: List<Long>): List<T?>  = ids.map { (trash(it)) }
    @Transactional
    override fun saveAndRefresh(t: T): T {
        return save(t).apply { entityManager.refresh(this) }
    }
}

@Repository
interface UserRepository: BaseRepository<User>{
    fun findByUsernameAndDeletedFalse(userName: String): User?

    @Query("""
        select u from users u
            where u.id!= :id 
            and u.username= :username
            and u.deleted=false
    """)
    fun findByUsername(id: Long, username: String): User?
}
@Repository
interface CategoryRepository: BaseRepository<Category>{
    fun findByNameAndDeletedFalse(name: String)
    @Query("""
        select c from category c 
        where c.id!= :id
        and c.name= :name
        and c.deleted=false
    """)
    fun findByName(id: Long, name: String): Category

    @Modifying
    @Query("""
    UPDATE product p
    SET p.deleted = false
    WHERE p.category.id = :categoryId
    """)
    fun updateProductDeleted(categoryId: Long)
}

@Repository
interface ProductRepository: BaseRepository<Product>{
    fun findByNameAndDeletedFalse(name: String)
    @Query("""
        select p from product p 
        where p.id!= :id
        and p.name= :name
        and p.deleted=false
    """)
    fun findByName(id: Long, name: String): Category

    @Query("""
        select p.count - COALESCE(sum(tri.count), 0) from product p 
         left join transaction_item tri on p.id=tri.product.id 
         where p.id= :id
         GROUP BY p.id, p.count
    """)
    fun getAllCount(id: Long): Long
}