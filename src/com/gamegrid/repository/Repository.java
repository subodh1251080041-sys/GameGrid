package com.gamegrid.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for persistent data access.
 * Demonstrates Generics, Abstraction, and the Repository Pattern in OOP.
 *
 * @param <T>  The entity type
 * @param <ID> The entity identifier type
 */
public interface Repository<T, ID> {
    void save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id);
    boolean existsById(ID id);
    void flush();
    void reload();
}
