package com.example.n0roo.springnetty.repository;

import com.example.n0roo.springnetty.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TestRepository {
	List<Users> getUsers();
}
