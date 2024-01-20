package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

// JpaRepository가 기본 CRUD 함수 가지고 있음.
// @Repository 어노테이션이 없어도 IoC가능, 상속했기 때문에
public interface UserRepository extends JpaRepository<User, Integer>{

}
