package org.matveyvs.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    E save(E e);

    List<E> findAll();

    Optional<E> findById(K id);

    boolean update(E e);

    boolean delete(K id);

}
