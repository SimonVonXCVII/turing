package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.entity.AbstractAuditable;
import com.simonvonxcvii.turing.entity.Menu;
import com.simonvonxcvii.turing.entity.MenuMeta;
import com.simonvonxcvii.turing.enums.MenuTypeEnum;
import com.simonvonxcvii.turing.model.dto.MenuDTO;
import com.simonvonxcvii.turing.repository.jpa.MenuJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.MenuMetaJpaRepository;
import com.simonvonxcvii.turing.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    private final MenuJpaRepository menuJpaRepository;
    private final MenuMetaJpaRepository menuMetaJpaRepository;
//    private final PermissionJpaRepository permissionJpaRepository;

    /**
     * Menu 转换为 MenuDTO
     *
     * @param menu Menu
     * @return MenuDTO
     * @author Simon Von
     * @since 12/15/25 11:24 PM
     */
    public @NonNull MenuDTO convertToDTO(Menu menu) {
        MenuDTO menuDTO = new MenuDTO();
        BeanUtils.copyProperties(menu, menuDTO);
        menuDTO.setType(menu.getType().getValue());
        // MenuMeta
        PredicateSpecification<MenuMeta> spec = (from, criteriaBuilder) ->
                criteriaBuilder.equal(from.get(MenuMeta.MENU_ID), menu.getId());
        menuMetaJpaRepository.findOne(spec)
                .ifPresent(menuMeta -> {
                    menuDTO.getMeta().setTitle(menuMeta.getTitle());
                    menuDTO.getMeta().setIcon(menuMeta.getIcon());
                });
        return menuDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(MenuDTO dto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu, "id", "title");
        menu.setType(MenuTypeEnum.getEnumByValue(dto.getType()));
        menuJpaRepository.save(menu);
        // MenuMeta
        MenuMeta menuMeta = new MenuMeta();
        BeanUtils.copyProperties(dto.getMeta(), menuMeta);
        menuMetaJpaRepository.save(menuMeta);
    }

    @Override
    public Boolean nameExists(String name) {
        PredicateSpecification<Menu> spec = (from, criteriaBuilder) ->
                criteriaBuilder.equal(from.get(Menu.NAME), name);
        return menuJpaRepository.exists(spec);
    }

    @Override
    public Boolean pathExists(String path) {
        PredicateSpecification<Menu> spec = (from, criteriaBuilder) ->
                criteriaBuilder.equal(from.get(Menu.PATH), path);
        return menuJpaRepository.exists(spec);
    }

    @Override
    public List<MenuDTO> selectBy() {
        // 将两次查询改为提前查询所有数据，减少查询次数，减轻数据库压力
        List<Menu> menuList = menuJpaRepository.findAll(Sort.by(AbstractAuditable.ID));
        // menu 级别分类
        List<Menu> level1MenuList = menuList.stream()
                .filter(menu ->
                        StringUtils.hasText(menu.getPath())
                                && menu.getPath().chars().filter(c -> c == '/').count() == 1
                )
                .toList();
        List<Menu> level2MenuList = menuList.stream()
                .filter(menu ->
                        StringUtils.hasText(menu.getPath())
                                && menu.getPath().chars().filter(c -> c == '/').count() == 2
                )
                .toList();
        List<Menu> level3MenuList = menuList.stream()
                .filter(menu -> !StringUtils.hasText(menu.getPath()))
                .toList();
        return level1MenuList.stream()
                .map(level1Menu -> {
                    MenuDTO level1MenuDTO = convertToDTO(level1Menu);
                    level2MenuList.stream()
                            .filter(level2Menu -> Objects.equals(level1Menu.getId(), level2Menu.getPid()))
                            .forEach(level2Menu -> {
                                MenuDTO level2MenuDTO = convertToDTO(level2Menu);
                                level1MenuDTO.getChildren().add(level2MenuDTO);
                                level3MenuList.stream()
                                        .filter(level3Menu -> Objects.equals(level2Menu.getId(), level3Menu.getPid()))
                                        .forEach(level3Menu -> {
                                            MenuDTO level3MenuDTO = convertToDTO(level3Menu);
                                            level2MenuDTO.getChildren().add(level3MenuDTO);
                                        });
                            });
                    return level1MenuDTO;
                })
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        menuJpaRepository.deleteById(id);
    }

}
