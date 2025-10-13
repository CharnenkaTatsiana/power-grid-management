package com.powergrid.management.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Базовый интерфейс для всех DAO классов
 * @param <T> тип сущности
 * @param <ID> тип идентификатора (должен быть Serializable)
 */
public interface BaseDAO<T, ID extends Serializable> {

    /**
     * Найти сущность по идентификатору
     */
    Optional<T> findById(ID id);

    /**
     * Найти все сущности
     */
    List<T> findAll();

    /**
     * Найти все сущности с пагинацией
     */
    List<T> findAll(int offset, int limit);

    /**
     * Сохранить новую сущность
     */
    T save(T entity);

    /**
     * Обновить существующую сущность
     */
    T update(T entity);

    /**
     * Сохранить или обновить сущность
     */
    T saveOrUpdate(T entity);

    /**
     * Удалить сущность
     */
    void delete(T entity);

    /**
     * Удалить сущность по идентификатору
     */
    void deleteById(ID id);

    /**
     * Проверить существование сущности по идентификатору
     */
    boolean existsById(ID id);

    /**
     * Получить количество сущностей
     */
    long count();

    /**
     * Найти сущности по примеру (QBE)
     */
    List<T> findByExample(T example);
}
