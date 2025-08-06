package com.simonvonxcvii.turing.service;

import com.simonvonxcvii.turing.model.dto.OrganizationDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * <p>
 * 单位表 服务类
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-29 11:33:31
 */
public interface IOrganizationService {

    /**
     * 单个新增或修改
     */
    void insertOrUpdate(OrganizationDTO dto);

    /**
     * 分页查询
     */
    Page<OrganizationDTO> selectPage(OrganizationDTO dto);

    /**
     * 查询单位 id 和单位名称列表
     */
    List<OrganizationDTO> selectIdAndNameList();

    /**
     * 【测试项目分类管理】菜单【添加测试项目分类】弹窗中的检测实验室和检测质控实验室接口
     *
     * @param name 实验室名称
     * @return 符合条件的实验室数据
     * @author Simon Von
     * @since 2023/4/4 14:52
     */
    List<OrganizationDTO> selectList(String name);

    /**
     * 根据 id 逻辑删除单位及其关联的用户和用户的角色
     *
     * @param id 主键 id
     */
    void deleteById(String id);

}
