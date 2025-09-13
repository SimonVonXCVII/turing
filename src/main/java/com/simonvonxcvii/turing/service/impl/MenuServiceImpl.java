package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.common.exception.BizRuntimeException;
import com.simonvonxcvii.turing.entity.Menu;
import com.simonvonxcvii.turing.entity.Permission;
import com.simonvonxcvii.turing.model.dto.MenuDTO;
import com.simonvonxcvii.turing.repository.MenuRepository;
import com.simonvonxcvii.turing.repository.PermissionRepository;
import com.simonvonxcvii.turing.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
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
 * @author Simon Von
 * @since 2022-12-26 18:25:51
 */
@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements IMenuService {

    private final MenuRepository menuRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(MenuDTO dto) {
        if (dto.getPid() == null && dto.getSort() % 100 != 0) {
            throw BizRuntimeException.from("父级权限的排序编号必须是一百的整数倍");
        }
        Menu menu;
        // 新增
        if (dto.getId() == null) {
            menu = new Menu();
        }
        // 修改
        else {
            menu = menuRepository.findById(dto.getId()).orElseThrow(() -> BizRuntimeException.from("无法查找到该数据"));
        }
        BeanUtils.copyProperties(dto, menu);
        menuRepository.save(menu);
    }

    @Override
    public List<MenuDTO> selectList(MenuDTO dto) {
        // 将两次查询改为提前查询所有数据，减少查询次数，减轻数据库压力
        List<Menu> menuList = menuRepository.findAll(Sort.by(Menu.SORT));
        // 按条件过滤
        List<Menu> menuList1 = menuList.stream()
                .filter(menu -> !StringUtils.hasText(dto.getName()) ||
                        (menu.getName().contains(dto.getName()) || dto.getName().contains(menu.getName())))
                .filter(menu -> !StringUtils.hasText(dto.getType()) ||
                        (menu.getType().contains(dto.getType()) || dto.getType().contains(menu.getType())))
                .filter(menu -> !StringUtils.hasText(dto.getPath()) ||
                        (menu.getPath().contains(dto.getPath()) || dto.getPath().contains(menu.getPath())))
                .filter(menu -> !StringUtils.hasText(dto.getComponent()) ||
                        (menu.getComponent().contains(dto.getComponent()) || dto.getComponent().contains(menu.getComponent())))
                .filter(menu -> dto.getSort() == null || Objects.equals(menu.getSort(), dto.getSort()))
                .filter(menu -> dto.getShowed() == null || Objects.equals(menu.isShowed(), dto.getShowed()))
                .filter(menu -> dto.getCached() == null || Objects.equals(menu.isCached(), dto.getCached()))
                .filter(menu -> dto.getExternal() == null || Objects.equals(menu.isExternal(), dto.getExternal()))
                .toList();
        if (menuList1.isEmpty()) {
            return new ArrayList<>();
        }
        // 收集 id
        List<Integer> parentIdList = menuList1.stream().filter(menu -> menu.getPid() == null).map(Menu::getId).toList();
        List<Integer> childIdList = menuList1.stream().filter(menu -> menu.getPid() != null).map(Menu::getId).toList();
        List<Integer> childPidList = menuList1.stream().map(Menu::getPid).filter(Objects::nonNull).toList();
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
                    Permission permission = permissionRepository.getReferenceById(parent.getPermissionId());
                    parentDTO.setPermission(permission.getName());
                    childList.stream()
                            .filter(child -> Objects.equals(parent.getId(), child.getPid()))
                            .forEach(child -> {
                                MenuDTO childDTO = new MenuDTO();
                                BeanUtils.copyProperties(child, childDTO);
                                Permission permission1 = permissionRepository.getReferenceById(child.getPermissionId());
                                childDTO.setPermission(permission1.getName());
                                parentDTO.getChildren().add(childDTO);
                            });
                    return parentDTO;
                })
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        menuRepository.deleteById(id);
    }

}
