package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 新增分类
     * @param category
     */
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "VALUES (#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);

    /**
     * 分类的分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageFind(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 修改分类
     * @param category
     */
    void updateCategory(Category category);

    /**
     * 启用禁用分类
     * @param status
     * @return
     */
    List<Category> getByType(Integer status);

    /**
     * 删除分类
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void delete(Long id);
}
