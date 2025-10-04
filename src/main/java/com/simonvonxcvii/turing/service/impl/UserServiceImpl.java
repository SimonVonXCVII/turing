package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.common.exception.BizRuntimeException;
import com.simonvonxcvii.turing.entity.*;
import com.simonvonxcvii.turing.model.dto.RoleDTO;
import com.simonvonxcvii.turing.model.dto.UserDTO;
import com.simonvonxcvii.turing.repository.jpa.OrganizationJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.RoleJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.UserJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.UserRoleJpaRepository;
import com.simonvonxcvii.turing.service.IUserService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-19 15:58:28
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserJpaRepository userJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(UserDTO dto) {
        User user;
        // 新增
        if (dto.getId() == null) {
            user = new User();
        }
        // 修改
        else {
            user = userJpaRepository.findById(dto.getId()).orElseThrow(() -> BizRuntimeException.from("无法查找到用户数据"));
        }
        dto.setAccountNonExpired(Boolean.TRUE);
        dto.setAccountNonLocked(Boolean.TRUE);
        dto.setCredentialsNonExpired(Boolean.TRUE);
        dto.setEnabled(Boolean.TRUE);
        dto.setManager(Boolean.FALSE);
        dto.setNeedResetPassword(Boolean.TRUE);
        BeanUtils.copyProperties(dto, user, AbstractAuditable.CREATED_DATE);
        // 密码
        user.setPassword(passwordEncoder.encode(dto.getMobile().toString().substring(3)));
        // 单位名称
        Organization organization = organizationJpaRepository.findById(dto.getOrgId())
                .orElseThrow(() -> BizRuntimeException.from("无法查找到单位数据"));
        user.setOrgName(organization.getName());
        userJpaRepository.save(user);
        // 更新用户角色表
        // TODO 可以优化成只添加需要添加的，只删除需要删除的
        Specification<UserRole> spec = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(UserRole.USER_ID), dto.getId());
            return query.where(predicate).getRestriction();
        };
        userRoleJpaRepository.delete(spec);
        List<UserRole> userRoleList = new LinkedList<>();
        dto.getRoleList()
                .forEach(id -> {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(user.getId());
                    userRole.setRoleId(id);
                    userRoleList.add(userRole);
                });
        userRoleJpaRepository.saveAll(userRoleList);
    }

    @Override
    public org.springframework.data.domain.Page<UserDTO> selectPage(UserDTO dto) {
        org.springframework.data.domain.Page<User> userPage;
        try {
            Specification<User> spec = (root, query, builder) -> {
                List<Predicate> predicateList = new LinkedList<>();
                if (StringUtils.hasText(dto.getName())) {
                    predicateList.add(builder.like(root.get(User.NAME),
                            "%" + dto.getName() + "%", '/'));
                }
                if (dto.getMobile() != null) {
                    predicateList.add(builder.like(root.get(User.MOBILE),
                            "%" + dto.getMobile() + "%", '/'));
                }
                if (StringUtils.hasText(dto.getGender())) {
                    predicateList.add(builder.equal(root.get(User.GENDER), dto.getGender()));
                }
                if (StringUtils.hasText(dto.getOrgName())) {
                    predicateList.add(builder.like(root.get(User.ORG_NAME),
                            "%" + dto.getOrgName() + "%", '/'));
                }
                if (StringUtils.hasText(dto.getDepartment())) {
                    predicateList.add(builder.like(root.get(User.DEPARTMENT),
                            "%" + dto.getDepartment() + "%", '/'));
                }
                if (StringUtils.hasText(dto.getUsername())) {
                    predicateList.add(builder.like(root.get(User.USERNAME),
                            "%" + dto.getUsername() + "%", '/'));
                }
                if (!CollectionUtils.isEmpty(dto.getRoleList())) {
                    Specification<UserRole> userRoleSpec = (root1, query1, builder1) -> {
                        Predicate predicate = builder1.in(root1.get(UserRole.ROLE_ID)).in(dto.getRoleList());
                        return query1.where(predicate).getRestriction();
                    };
                    List<UserRole> userRoleList = userRoleJpaRepository.findAll(userRoleSpec);
                    if (userRoleList.isEmpty()) {
                        // todo
                        throw new RuntimeException();
                    }
                    List<Integer> userIdlist = userRoleList.stream().map(UserRole::getUserId).toList();
                    Predicate predicate = root.get(User.ID).in(userIdlist);
                    predicateList.add(predicate);
                }
                if (dto.getAccountNonExpired() != null) {
                    predicateList.add(builder.equal(root.get(User.ACCOUNT_NON_EXPIRED), dto.getAccountNonExpired()));
                }
                if (dto.getAccountNonLocked() != null) {
                    predicateList.add(builder.equal(root.get(User.ACCOUNT_NON_LOCKED), dto.getAccountNonLocked()));
                }
                if (dto.getCredentialsNonExpired() != null) {
                    predicateList.add(builder.equal(root.get(User.CREDENTIALS_NON_EXPIRED), dto.getCredentialsNonExpired()));
                }
                if (dto.getEnabled() != null) {
                    predicateList.add(builder.equal(root.get(User.ENABLED), dto.getEnabled()));
                }
                if (dto.getManager() != null) {
                    predicateList.add(builder.equal(root.get(User.MANAGER), dto.getManager()));
                }
                if (dto.getNeedResetPassword() != null) {
                    predicateList.add(builder.equal(root.get(User.NEED_RESET_PASSWORD), dto.getNeedResetPassword()));
                }
                Predicate predicate = builder.and(predicateList.toArray(Predicate[]::new));
                return query.where(predicate).getRestriction();
            };
            // TODO: 2023/8/29 设置前端 number 默认从 0 开始，或许就不需要减一了
            PageRequest pageRequest = PageRequest.of(dto.getNumber() - 1, dto.getSize());
            userPage = userJpaRepository.findAll(spec, pageRequest);
        } catch (Exception e) {
            return org.springframework.data.domain.Page.empty();
        }
        return userPage.map(user -> {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            Specification<UserRole> spec = (root, query, builder) -> {
                Predicate predicate = builder.equal(root.get(UserRole.USER_ID), user.getId());
                return query.where(predicate).getRestriction();
            };
            List<UserRole> userRoleList = userRoleJpaRepository.findAll(spec);
            if (userRoleList.isEmpty()) {
                throw BizRuntimeException.from("数据异常，该用户没有角色：" + user.getUsername());
            }
            List<Role> roleList = roleJpaRepository.findAllById(userRoleList.stream().map(AbstractAuditable::getId).toList());
            if (roleList.isEmpty()) {
                throw BizRuntimeException.from("数据异常，该用户没有角色：" + user.getUsername());
            }
            List<RoleDTO> roleDTOList = roleList.stream()
                    .map(role -> {
                        RoleDTO roleDTO = new RoleDTO();
                        BeanUtils.copyProperties(role, roleDTO);
                        return roleDTO;
                    })
                    .toList();
            userDTO.setAuthorities(roleDTOList);
            return userDTO;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        // 逻辑删除用户-角色关联数据
        Specification<UserRole> spec = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(UserRole.USER_ID), id);
            return query.where(predicate).getRestriction();
        };
        userRoleJpaRepository.delete(spec);
        // 逻辑删除用户数据
        userJpaRepository.deleteById(id);
    }

}
