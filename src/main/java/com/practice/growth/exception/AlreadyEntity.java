package com.practice.growth.exception;

/**
 * @author prographer
 * @date: 2019-04-09
 */
public class AlreadyEntity extends Exception {
    public AlreadyEntity(String entityName) {
        super(entityName);
    }
}
