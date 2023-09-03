package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

public interface CategoryService {

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageFind(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 启用和禁用分类
     * @param status
     * @param id
     * @return
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> getByType(Integer type);

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    void updateCategory(CategoryDTO categoryDTO);

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    void deleteCategory(Long id);
}
