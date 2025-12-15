package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.entity.Menu;
import com.simonvonxcvii.turing.model.dto.MenuDTO;
import com.simonvonxcvii.turing.model.dto.MenuMetaDTO;
import com.simonvonxcvii.turing.repository.jpa.MenuJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.PermissionJpaRepository;
import com.simonvonxcvii.turing.service.IMenuService;
import lombok.RequiredArgsConstructor;
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
    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(MenuDTO dto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu, "id", "title");
        menu.setTitle(dto.getMeta().getTitle());
        menuJpaRepository.save(menu);
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
        List<Menu> menuList = menuJpaRepository.findAll(Sort.by(Menu.SORT));
        // menu 级别分类
        List<Menu> level1MenuList = menuList.stream()
                .filter(menu ->
                        StringUtils.hasText(menu.getPath()) && menu.getPath().chars().filter(c -> c == '/').count() == 1
                )
                .toList();
        List<Menu> level2MenuList = menuList.stream()
                .filter(menu ->
                        StringUtils.hasText(menu.getPath()) && menu.getPath().chars().filter(c -> c == '/').count() == 2
                )
                .toList();
        List<Menu> level3MenuList = menuList.stream()
                .filter(menu -> !StringUtils.hasText(menu.getPath()))
                .toList();
        return level1MenuList.stream()
                .map(level1Menu -> {
                    MenuDTO level1MenuDTO = new MenuDTO();
                    BeanUtils.copyProperties(level1Menu, level1MenuDTO);
                    level1MenuDTO.setType(level1Menu.getType().getDesc());
                    MenuMetaDTO level1MenuMetaDTO = new MenuMetaDTO();
                    level1MenuMetaDTO.setTitle(level1Menu.getTitle());
                    level1MenuMetaDTO.setIcon(level1Menu.getIcon());
//                    level1MenuMetaDTO.setHideMenu(!level1Menu.isShowed());
                    level1MenuDTO.setMeta(level1MenuMetaDTO);
                    level2MenuList.stream()
                            .filter(level2Menu -> Objects.equals(level1Menu.getId(), level2Menu.getPid()))
                            .forEach(level2Menu -> {
                                MenuDTO level2MenuDTO = new MenuDTO();
                                BeanUtils.copyProperties(level2Menu, level2MenuDTO);
                                level2MenuDTO.setType(level2Menu.getType().getDesc());
                                MenuMetaDTO level2MenuMetaDTO = new MenuMetaDTO();
                                level2MenuMetaDTO.setTitle(level2Menu.getTitle());
                                level2MenuMetaDTO.setIcon(level2Menu.getIcon());
//                                level2MenuMetaDTO.setHideMenu(!level2Menu.isShowed());
                                level2MenuDTO.setMeta(level2MenuMetaDTO);
                                level1MenuDTO.getChildren().add(level2MenuDTO);
                                level3MenuList.stream()
                                        .filter(level3Menu -> Objects.equals(level2Menu.getId(), level3Menu.getPid()))
                                        .forEach(level3Menu -> {
                                            MenuDTO level3MenuDTO = new MenuDTO();
                                            BeanUtils.copyProperties(level3Menu, level3MenuDTO);
                                            level3MenuDTO.setType(level3Menu.getType().getDesc());
                                            MenuMetaDTO level3MenuMetaDTO = new MenuMetaDTO();
                                            level3MenuMetaDTO.setTitle(level3Menu.getTitle());
                                            level3MenuMetaDTO.setIcon(level3Menu.getIcon());
//                                            level3MenuMetaDTO.setHideMenu(!level3Menu.isShowed());
                                            level3MenuDTO.setMeta(level3MenuMetaDTO);
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
