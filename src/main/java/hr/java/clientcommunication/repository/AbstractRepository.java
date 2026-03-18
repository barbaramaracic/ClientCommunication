package hr.java.clientcommunication.repository;

import hr.java.clientcommunication.entity.Entity;
import hr.java.clientcommunication.exception.RepositoryAccessException;

import java.util.List;

public abstract class AbstractRepository<T extends Entity> {
    public abstract T findById(Long id) throws RepositoryAccessException;
    public abstract List<T> findAll() throws RepositoryAccessException;
    public abstract void save(T entity) throws RepositoryAccessException;
}
