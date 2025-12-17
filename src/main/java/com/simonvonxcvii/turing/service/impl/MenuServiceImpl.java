package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.entity.Menu;
import com.simonvonxcvii.turing.entity.MenuMeta;
import com.simonvonxcvii.turing.enums.MenuBadgeTypeEnum;
import com.simonvonxcvii.turing.enums.MenuBadgeVariantsEnum;
import com.simonvonxcvii.turing.enums.MenuTypeEnum;
import com.simonvonxcvii.turing.model.dto.MenuDTO;
import com.simonvonxcvii.turing.repository.jpa.MenuJpaRepository;
import com.simonvonxcvii.turing.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final MenuJpaRepository menuJpaRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(MenuDTO dto) {
        // MenuMeta
        MenuMeta menuMeta = new MenuMeta();
        BeanUtils.copyProperties(dto.getMeta(), menuMeta);
        if (dto.getMeta().getBadgeType() != null) {
            menuMeta.setBadgeType(MenuBadgeTypeEnum.getEnumByValue(dto.getMeta().getBadgeType()));
        }
        if (dto.getMeta().getBadgeVariants() != null) {
            menuMeta.setBadgeVariants(MenuBadgeVariantsEnum.getEnumByValue(dto.getMeta().getBadgeVariants()));
        }
        // Menu
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu, "id");
        menu.setType(MenuTypeEnum.getEnumByValue(dto.getType()));
        menu.setMeta(menuMeta);
        menuJpaRepository.save(menu);
    }

    @Override
    public Boolean nameExists(String name, Integer id) {
        // 如果 id 为 null，说明是新增操作
        if (id == null) {
            return menuJpaRepository.existsByName(name);
        }
        // 否则是修改操作，需要排除当前数据
        return menuJpaRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public Boolean pathExists(String path, Integer id) {
        // 如果 id 为 null，说明是新增操作
        if (id == null) {
            return menuJpaRepository.existsByPath(path);
        }
        // 否则是修改操作，需要排除当前数据
        return menuJpaRepository.existsByPathAndIdNot(path, id);
    }

    @Override
    public List<MenuDTO> selectBy() {
        // 1. 提前查询所有数据，减少查询次数，减轻数据库压力
        List<Menu> menuList = menuJpaRepository.findAll(Sort.by(Menu.ID));
        // 2. Menu 按 pid 分组：pid -> childrenMenuList
        Map<Integer, List<Menu>> childrenMenuListMap = menuList.stream()
                .filter(menu -> menu.getPid() != null)
                .collect(Collectors.groupingBy(Menu::getPid));
        return menuList.stream()
                .filter(menu -> menu.getPid() == null)
                .map(menu -> buildTree(menu, childrenMenuListMap)
                )
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(Integer id, MenuDTO dto) {
        // 从数据库查询出来后，如果对该对象作了修改，那么 JPA / Hibernate 会自动更新数据库
        menuJpaRepository.findById(id)
                .ifPresent(menu -> {
                    // Menu
                    BeanUtils.copyProperties(dto, menu, "id");
                    menu.setType(MenuTypeEnum.getEnumByValue(dto.getType()));
                    // MenuMeta
                    MenuMeta menuMeta = menu.getMeta();
                    BeanUtils.copyProperties(dto.getMeta(), menuMeta);
                    if (dto.getMeta().getBadgeType() != null) {
                        menuMeta.setBadgeType(MenuBadgeTypeEnum.getEnumByValue(dto.getMeta().getBadgeType()));
                    }
                    if (dto.getMeta().getBadgeVariants() != null) {
                        menuMeta.setBadgeVariants(MenuBadgeVariantsEnum.getEnumByValue(dto.getMeta().getBadgeVariants()));
                    }
                });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        // 当前级
        menuJpaRepository.deleteById(id);
        // 二级（如果有）
        Set<Integer> menuIdSet2 = menuJpaRepository.findByPid(id)
                .stream()
                .map(Menu::getId)
                .collect(Collectors.toSet());
        if (menuIdSet2.isEmpty()) {
            return;
        }
        menuJpaRepository.deleteAllByIdInBatch(menuIdSet2);
        // 三级（如果有）
        Set<Integer> menuIdSet3 = menuJpaRepository.findByPidIn(menuIdSet2)
                .stream()
                .map(Menu::getId)
                .collect(Collectors.toSet());
        if (menuIdSet3.isEmpty()) {
            return;
        }
        menuJpaRepository.deleteAllByIdInBatch(menuIdSet3);
    }

    /**
     * Menu 层级构建树
     *
     * @param menu                Menu
     * @param childrenMenuListMap Menu 按 pid 分组：pid -> childrenMenuList 后的 ListMap 集合
     * @return MenuDTO
     * @author Simon Von
     * @since 12/16/25 11:14 AM
     */
    public MenuDTO buildTree(Menu menu, Map<Integer, List<Menu>> childrenMenuListMap) {
        MenuDTO dto = convertToDTO(menu);
        childrenMenuListMap.getOrDefault(menu.getId(), Collections.emptyList())
                .forEach(child -> dto.getChildren().add(
                        buildTree(child, childrenMenuListMap)
                ));
        return dto;
    }

    /**
     * Menu 转换为 MenuDTO
     *
     * @param menu Menu
     * @return MenuDTO
     * @author Simon Von
     * @since 12/15/25 11:24 PM
     */
    public MenuDTO convertToDTO(Menu menu) {
        // Menu
        MenuDTO menuDTO = new MenuDTO();
        BeanUtils.copyProperties(menu, menuDTO);
        menuDTO.setType(menu.getType().getValue());
        // MenuMeta
        MenuMeta menuMeta = menu.getMeta();
        BeanUtils.copyProperties(menuMeta, menuDTO.getMeta());
        if (menuMeta.getBadgeType() != null) {
            menuDTO.getMeta().setBadgeType(menuMeta.getBadgeType().getValue());
        }
        if (menuMeta.getBadgeVariants() != null) {
            menuDTO.getMeta().setBadgeVariants(menuMeta.getBadgeVariants().getValue());
        }
        return menuDTO;
    }

}
