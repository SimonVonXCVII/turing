package com.shiminfxcvii.turing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.entity.Menu;
import com.shiminfxcvii.turing.entity.Permission;
import com.shiminfxcvii.turing.mapper.MenuMapper;
import com.shiminfxcvii.turing.mapper.PermissionMapper;
import com.shiminfxcvii.turing.model.cmd.MenuCmd;
import com.shiminfxcvii.turing.model.dto.MenuDTO;
import com.shiminfxcvii.turing.model.query.MenuQuery;
import com.shiminfxcvii.turing.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-26 18:25:51
 */
@RequiredArgsConstructor
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    private final MenuMapper menuMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public List<MenuDTO> selectList(MenuQuery query) {
        // 将两次查询改为提前查询所有数据，减少查询次数，减轻数据库压力
        List<Menu> menuList = lambdaQuery().orderByAsc(Menu::getSort).list();
        List<Menu> menuList1 = menuList.stream()
                .filter(menu -> !StringUtils.hasText(query.getName()) ||
                        (menu.getName().contains(query.getName()) || query.getName().contains(menu.getName())))
                .filter(menu -> !StringUtils.hasText(query.getType()) ||
                        (menu.getType().contains(query.getType()) || query.getType().contains(menu.getType())))
                .filter(menu -> !StringUtils.hasText(query.getPath()) ||
                        (menu.getPath().contains(query.getPath()) || query.getPath().contains(menu.getPath())))
                .filter(menu -> !StringUtils.hasText(query.getComponent()) ||
                        (menu.getComponent().contains(query.getComponent()) || query.getComponent().contains(menu.getComponent())))
                .filter(menu -> query.getSort() == null || Objects.equals(menu.getSort(), query.getSort()))
                .filter(menu -> query.getShow() == null || Objects.equals(menu.getShow(), query.getShow()))
                .filter(menu -> query.getCache() == null || Objects.equals(menu.getCache(), query.getCache()))
                .filter(menu -> query.getExternal() == null || Objects.equals(menu.getExternal(), query.getExternal()))
                .toList();
        if (menuList1.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> parentIdList = menuList1.stream().filter(menu -> menu.getPid() == null).map(Menu::getId).toList();
        List<String> childIdList = menuList1.stream().filter(menu -> menu.getPid() != null).map(Menu::getId).toList();
        List<String> childPidList = menuList1.stream().map(Menu::getPid).filter(Objects::nonNull).toList();
        menuList1 = menuList.stream()
                .filter(menu -> {
                    if (!parentIdList.isEmpty() && parentIdList.contains(menu.getId())) {
                        return true;
                    } else if (!parentIdList.isEmpty() && parentIdList.contains(menu.getPid())) {
                        return true;
                    } else if (!childIdList.isEmpty() && childIdList.contains(menu.getId())) {
                        return true;
                    } else return !childPidList.isEmpty() && childPidList.contains(menu.getId());
                })
                .toList();
        List<Menu> parentList = menuList1.stream().filter(menu -> menu.getPid() == null).toList();
        List<Menu> childList = menuList1.stream().filter(menu -> menu.getPid() != null).toList();
        return parentList.stream()
                .map(parent -> {
                    MenuDTO parentDTO = new MenuDTO();
                    BeanUtils.copyProperties(parent, parentDTO);
                    Permission permission = permissionMapper.selectById(parent.getPermissionId());
                    if (permission != null) {
                        parentDTO.setPermission(permission.getName());
                    }
                    childList.stream()
                            .filter(child -> Objects.equals(parent.getId(), child.getPid()))
                            .forEach(child -> {
                                MenuDTO childDTO = new MenuDTO();
                                BeanUtils.copyProperties(child, childDTO);
                                Permission permission1 = permissionMapper.selectById(child.getPermissionId());
                                if (permission1 != null) {
                                    childDTO.setPermission(permission1.getName());
                                }
                                parentDTO.getChildren().add(childDTO);
                            });
                    return parentDTO;
                })
                .toList();
    }

    @Override
    @Transactional
    public void insert(MenuCmd cmd) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(cmd, menu);
        menu.setTitle(menu.getName());
        save(menu);
    }

    @Override
    @Transactional
    public void update(MenuCmd cmd) {
        Menu menu = getById(cmd.getId());
        if (menu == null) {
            throw new BizRuntimeException("没有查询到该菜单");
        }
        BeanUtils.copyProperties(cmd, menu);
        menuMapper.updateOneById(menu);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        Menu menu = getById(id);
        if (menu == null) {
            throw new BizRuntimeException("没有查询到该菜单");
        }
        removeById(id);
    }

}