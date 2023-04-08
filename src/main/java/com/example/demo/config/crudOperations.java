package com.example.demo.config;

import java.util.Optional;

public interface crudOperations<T, ID> {

    int count();

    Optional<T> findById(ID id);

    Iterable<T> findAll(Class T);
    //TODO: Generate Rest Of Crud Operations
    /* getAll, save, saveAll, deleteById, update*/

}
