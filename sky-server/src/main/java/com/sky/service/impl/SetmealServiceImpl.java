package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.aspectj.apache.bcel.ExceptionConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.openmbean.OpenDataException;
import java.util.List;
import java.util.Set;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

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
        //设置套餐初始为停售状态
        setmeal.setStatus(StatusConstant.DISABLE);
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
            //如果套餐中有菜品处于停售状态，则设置套餐也处于停售状态
            if (setmeal.getStatus() == StatusConstant.ENABLE){
                //根据套餐id获取菜品id
                List<Long> dishesId = setmealDishMapper.getDishIdBySetmealId(setmeal.getId());
                //遍历菜品id判断是否都处于启售状态
                for (Long dishId : dishesId) {
                    Dish dish = dishMapper.getById(dishId);
                    if (dish.getStatus() == StatusConstant.DISABLE){
                        setmeal.setStatus(StatusConstant.DISABLE);
                    }
                }
            }
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

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        //从setmealDTO获取setmeal对象
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);

        //根据套餐id在套餐菜品表中删除对应的菜品关联信息
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());

        //将修改后的套餐菜品数据插入表中
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
            setmealDishMapper.insert(setmealDish);
        });
    }

    /**
     * 启售or禁售套餐
     * @param status
     * @return
     */
    @Override
    public void startOrStop(Integer status,Long id) {
        //如果要启售套餐，判断套餐里的菜品是否都处于启售状态
        //菜品都处于启售状态下，才可以启售套餐
        if (status == StatusConstant.ENABLE){
            //根据套餐id获取菜品id
            List<Long> dishesId = setmealDishMapper.getDishIdBySetmealId(id);
            //遍历菜品id判断是否都处于启售状态
            for (Long dishId : dishesId) {
                Dish dish = dishMapper.getById(dishId);
                if (dish.getStatus() == StatusConstant.DISABLE){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Setmeal setmeal = Setmeal.builder()
                                .id(id)
                                .status(status)
                                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @Transactional
    public void delete(List<Long> ids) {
        //获取要删除的套餐是否处于停售状态
        for (Long id : ids) {
            if (setmealMapper.getById(id).getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //删除套餐数据
        for (Long id : ids) {
            setmealMapper.delete(id);
            //删除套餐菜品表的对应数据
            setmealDishMapper.deleteBySetmealId(id);
        }

    }
}
