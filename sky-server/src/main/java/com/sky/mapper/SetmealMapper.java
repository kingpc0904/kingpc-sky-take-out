package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer SetmealByCategoryID(Long categoryId);

    /**
     * 新增套餐
     * @param setmeal
     * @return
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> select(SetmealPageQueryDTO setmealPageQueryDTO);
}
