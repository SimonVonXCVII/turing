package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.common.exception.BizRuntimeException;
import com.simonvonxcvii.turing.entity.*;
import com.simonvonxcvii.turing.model.dto.RoleDTO;
import com.simonvonxcvii.turing.model.dto.UserDTO;
import com.simonvonxcvii.turing.repository.OrganizationRepository;
import com.simonvonxcvii.turing.repository.RoleRepository;
import com.simonvonxcvii.turing.repository.UserRepository;
import com.simonvonxcvii.turing.repository.UserRoleRepository;
import com.simonvonxcvii.turing.service.IUserService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
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
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(UserDTO dto) {
        User user;
        // 新增
        if (!StringUtils.hasText(dto.getId())) {
            user = new User();
        }
        // 修改
        else {
            user = userRepository.findById(dto.getId()).orElseThrow(() -> BizRuntimeException.from("无法查找到用户数据"));
        }
        dto.setAccountNonExpired(Boolean.TRUE);
        dto.setAccountNonLocked(Boolean.TRUE);
        dto.setCredentialsNonExpired(Boolean.TRUE);
        dto.setEnabled(Boolean.TRUE);
        dto.setManager(Boolean.FALSE);
        dto.setNeedSetPassword(Boolean.TRUE);
        BeanUtils.copyProperties(dto, user, AbstractAuditable.CREATED_DATE);
        // 密码
        user.setPassword(passwordEncoder.encode(dto.getMobile().substring(3)));
        // 单位名称
        Organization organization = organizationRepository.findById(dto.getOrgId()).orElseThrow(() -> BizRuntimeException.from("无法查找到单位数据"));
        user.setOrgName(organization.getName());
        userRepository.save(user);
        // 更新用户角色表
        // TODO 可以优化成只添加需要添加的，只删除需要删除的
        userRoleRepository.delete((root, _, _) -> root.get(UserRole.USER_ID).in(dto.getId()));
        List<UserRole> userRoleList = new LinkedList<>();
        dto.getRoleList()
                .forEach(id -> {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(user.getId());
                    userRole.setRoleId(id);
                    userRoleList.add(userRole);
                });
        userRoleRepository.saveAll(userRoleList);
    }

    @Override
    public org.springframework.data.domain.Page<UserDTO> selectPage(UserDTO dto) {
        org.springframework.data.domain.Page<User> userPage;
        try {
            userPage = userRepository.findAll((root, query, criteriaBuilder) -> {
                        List<Predicate> predicateList = new LinkedList<>();
                        if (StringUtils.hasText(dto.getName())) {
                            predicateList.add(criteriaBuilder.like(root.get(User.NAME),
                                    "%" + dto.getName() + "%", '/'));
                        }
                        if (StringUtils.hasText(dto.getMobile())) {
                            predicateList.add(criteriaBuilder.like(root.get(User.MOBILE),
                                    "%" + dto.getMobile() + "%", '/'));
                        }
                        if (StringUtils.hasText(dto.getGender())) {
                            predicateList.add(criteriaBuilder.equal(root.get(User.GENDER), dto.getGender()));
                        }
                        if (StringUtils.hasText(dto.getOrgName())) {
                            predicateList.add(criteriaBuilder.like(root.get(User.ORG_NAME),
                                    "%" + dto.getOrgName() + "%", '/'));
                        }
                        if (StringUtils.hasText(dto.getDepartment())) {
                            predicateList.add(criteriaBuilder.like(root.get(User.DEPARTMENT),
                                    "%" + dto.getDepartment() + "%", '/'));
                        }
                        if (StringUtils.hasText(dto.getUsername())) {
                            predicateList.add(criteriaBuilder.like(root.get(User.USERNAME),
                                    "%" + dto.getUsername() + "%", '/'));
                        }
                        if (!CollectionUtils.isEmpty(dto.getRoleList())) {
                            List<UserRole> userRoleList = userRoleRepository.findAll((root1, _, _) ->
                                    root1.get(UserRole.ROLE_ID).in(dto.getRoleList()));
                            if (userRoleList.isEmpty()) {
                                throw new RuntimeException();
                            }
                            predicateList.add(root.get(User.ID).in(userRoleList.stream().map(UserRole::getUserId).toList()));
                        }
                        if (dto.getAccountNonExpired() != null) {
                            predicateList.add(criteriaBuilder.equal(root.get(User.ACCOUNT_NON_EXPIRED), dto.getAccountNonExpired()));
                        }
                        if (dto.getAccountNonLocked() != null) {
                            predicateList.add(criteriaBuilder.equal(root.get(User.ACCOUNT_NON_LOCKED), dto.getAccountNonLocked()));
                        }
                        if (dto.getCredentialsNonExpired() != null) {
                            predicateList.add(criteriaBuilder.equal(root.get(User.CREDENTIALS_NON_EXPIRED), dto.getCredentialsNonExpired()));
                        }
                        if (dto.getEnabled() != null) {
                            predicateList.add(criteriaBuilder.equal(root.get(User.ENABLED), dto.getEnabled()));
                        }
                        if (dto.getManager() != null) {
                            predicateList.add(criteriaBuilder.equal(root.get(User.MANAGER), dto.getManager()));
                        }
                        if (dto.getNeedSetPassword() != null) {
                            predicateList.add(criteriaBuilder.equal(root.get(User.NEED_SET_PASSWORD), dto.getNeedSetPassword()));
                        }
                        assert query != null;
                        return query.where(predicateList.toArray(Predicate[]::new)).getRestriction();
                    },
                    // TODO: 2023/8/29 设置前端 number 默认从 0 开始，或许就不需要减一了
                    PageRequest.of(dto.getNumber() - 1, dto.getSize()));
        } catch (Exception e) {
            return org.springframework.data.domain.Page.empty();
        }
        return userPage.map(user -> {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            List<UserRole> userRoleList = userRoleRepository.findAll((root, _, _) ->
                    root.get(UserRole.USER_ID).in(user.getId()));
            if (userRoleList.isEmpty()) {
                throw BizRuntimeException.from("数据异常，该用户没有角色：" + user.getUsername());
            }
            List<Role> roleList = roleRepository.findAllById(userRoleList.stream().map(AbstractAuditable::getId).toList());
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
    public void deleteById(String id) {
        // 逻辑删除用户-角色关联数据
        userRoleRepository.delete((root, _, _) -> root.get(UserRole.USER_ID).in(id));
        // 逻辑删除用户数据
        userRepository.deleteById(id);
    }

}
