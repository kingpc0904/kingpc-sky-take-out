package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        //新增套餐数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);

        //获取套餐数据的主键值
        Long setmealId = setmeal.getId();

        //新增套餐菜品表数据
        //需要获取套餐的主键值
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
            setmealDishMapper.insert(setmealDish);
        });

    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {

        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page = setmealMapper.select(setmealPageQueryDTO);

        List<SetmealVO> setmeals = page.getResult();
        setmeals.forEach(setmeal -> {
            //获取套餐对应的分类名称
            String categoryName =  categoryMapper.getNameById(setmeal.getCategoryId());
            //设置套餐的分类名称
            setmeal.setCategoryName(categoryName);
        });

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        //获取Setmeal对应的数据
        Setmeal setmeal = setmealMapper.getById(id);
        //将setmeal的数据拷贝到setmealVO
        BeanUtils.copyProperties(setmeal,setmealVO);

        //获取setmeal_dish对应的数据
        List<SetmealDish> setmealDishes = setmealDishMapper.getById(id);

        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }
}
