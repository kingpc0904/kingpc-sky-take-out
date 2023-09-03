package com.sky.service;

import com.sky.dto.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public interface CategoryService {


    void save(CategoryDTO categoryDTO);
}
