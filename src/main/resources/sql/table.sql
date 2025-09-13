create table if not exists public.turing_app_file
(
    id                 bigserial
        constraint con_public_turing_app_file_constraint_1
            primary key,
    owner_id           bigint        not null,
    filename           varchar(64)   not null,
    origin_filename    varchar(64)   not null,
    suffix             varchar(8)    not null,
    content_type       varchar(128)  not null,
    content_length     bigint        not null,
    md5                varchar(64)   not null
        unique,
    path               varchar(1024) not null,
    biz_type           smallint      not null,
    remark             varchar(1024),
    created_by         bigint,
    created_time       timestamp,
    last_modified_by   bigint,
    last_modified_date timestamp,
    version            bigint,
    deleted            boolean
);

comment on table public.turing_app_file is '文件表';

comment on column public.turing_app_file.id is '文件表主键 id';

comment on column public.turing_app_file.owner_id is '所有者 id';

comment on column public.turing_app_file.filename is '文件名';

comment on column public.turing_app_file.origin_filename is '原始文件名';

comment on column public.turing_app_file.suffix is '后缀';

comment on column public.turing_app_file.content_type is '内容类型';

comment on column public.turing_app_file.content_length is '内容长度';

comment on column public.turing_app_file.md5 is 'md5';

comment on column public.turing_app_file.path is '存放路径';

comment on column public.turing_app_file.biz_type is '业务类型';

comment on column public.turing_app_file.remark is '备注';

comment on column public.turing_app_file.created_by is '创建主体';

comment on column public.turing_app_file.created_time is '创建时间';

comment on column public.turing_app_file.last_modified_by is '更新主体';

comment on column public.turing_app_file.last_modified_date is '更新时间';

comment on column public.turing_app_file.version is '乐观锁字段';

comment on column public.turing_app_file.deleted is '逻辑删除';

alter table public.turing_app_file
    owner to postgres;

create table if not exists public.turing_dict
(
    id                 bigserial
        constraint con_public_turing_dict_constraint_1
            primary key,
    type               varchar(32),
    pid                bigint,
    name               varchar(32)                         not null,
    value              integer                             not null,
    description        varchar(128),
    sort               integer,
    created_by         bigint,
    created_date       timestamp                           not null,
    last_modified_by   bigint,
    last_modified_date timestamp default CURRENT_TIMESTAMP not null,
    version            integer   default 0                 not null,
    deleted            boolean   default false             not null
);

comment on table public.turing_dict is '字典表';

comment on column public.turing_dict.id is '字典表主键 id';

comment on column public.turing_dict.type is '字典类型';

comment on column public.turing_dict.pid is '上级 id';

comment on column public.turing_dict.name is '字典名称';

comment on column public.turing_dict.value is '字典值';

comment on column public.turing_dict.description is '说明';

comment on column public.turing_dict.sort is '排序';

comment on column public.turing_dict.created_by is '创建主体';

comment on column public.turing_dict.created_date is '创建时间';

comment on column public.turing_dict.last_modified_by is '修改主体';

comment on column public.turing_dict.last_modified_date is '修改时间';

comment on column public.turing_dict.version is '乐观锁字段';

comment on column public.turing_dict.deleted is '逻辑删除';

alter table public.turing_dict
    owner to postgres;

create table if not exists public.turing_menu
(
    id                 bigserial
        constraint con_public_turing_menu_constraint_1
            primary key,
    pid                bigint,
    permission_id      bigint                              not null
        unique,
    name               varchar(64)                         not null
        unique,
    title              varchar(128)                        not null
        unique,
    type               varchar(32),
    path               varchar(128)                        not null
        unique,
    component          varchar(256)                        not null,
    icon               varchar(256),
    sort               integer                             not null
        unique,
    showed             boolean                             not null,
    cached             boolean                             not null,
    external           boolean                             not null,
    created_by         bigint,
    created_date       timestamp                           not null,
    last_modified_by   bigint,
    last_modified_date timestamp default CURRENT_TIMESTAMP not null,
    version            integer   default 0                 not null,
    deleted            boolean   default false             not null
);

comment on table public.turing_menu is '菜单表';

comment on column public.turing_menu.id is '菜单 id';

comment on column public.turing_menu.pid is '上级菜单 id';

comment on column public.turing_menu.permission_id is '系统权限 id';

comment on column public.turing_menu.name is '菜单名称';

comment on column public.turing_menu.title is '菜单标题';

comment on column public.turing_menu.type is '菜单类型';

comment on column public.turing_menu.path is '菜单路径';

comment on column public.turing_menu.component is '组件路径';

comment on column public.turing_menu.icon is '图标';

comment on column public.turing_menu.sort is '排序编号';

comment on column public.turing_menu.showed is '是否显示';

comment on column public.turing_menu.cached is '是否缓存';

comment on column public.turing_menu.external is '是否为外部链接';

comment on column public.turing_menu.created_by is '创建主体';

comment on column public.turing_menu.created_date is '创建时间';

comment on column public.turing_menu.last_modified_by is '更新主体';

comment on column public.turing_menu.last_modified_date is '更新时间';

comment on column public.turing_menu.version is '乐观锁字段';

comment on column public.turing_menu.deleted is '逻辑删除';

alter table public.turing_menu
    owner to postgres;

create table if not exists public.turing_organization
(
    id                 bigserial
        constraint con_public_turing_organization_constraint_1
            primary key,
    pid                bigint,
    name               varchar(64)                         not null
        unique,
    code               varchar(18)                         not null
        unique,
    type               varchar(6)                          not null,
    province_code      integer                             not null,
    city_code          integer                             not null,
    district_code      integer                             not null,
    province_name      varchar(16)                         not null,
    city_name          varchar(16)                         not null,
    district_name      varchar(16)                         not null,
    address            varchar(128)                        not null,
    legal_person       varchar(32)                         not null,
    phone              varchar(32)                         not null
        unique,
    created_by         bigint,
    created_date       timestamp                           not null,
    last_modified_by   bigint,
    last_modified_date timestamp default CURRENT_TIMESTAMP not null,
    version            integer   default 0                 not null,
    deleted            boolean   default false             not null
);

comment on table public.turing_organization is '单位表';

comment on column public.turing_organization.id is '单位表主键 id';

comment on column public.turing_organization.pid is '上级单位 id';

comment on column public.turing_organization.name is '单位名称';

comment on column public.turing_organization.code is '信用代码';

comment on column public.turing_organization.type is '单位类型';

comment on column public.turing_organization.province_code is '单位所在省编码';

comment on column public.turing_organization.city_code is '单位所在市编码';

comment on column public.turing_organization.district_code is '单位所在区县编码';

comment on column public.turing_organization.province_name is '单位所在省名称';

comment on column public.turing_organization.city_name is '单位所在市名称';

comment on column public.turing_organization.district_name is '单位所在区县名称';

comment on column public.turing_organization.address is '单位地址详情';

comment on column public.turing_organization.legal_person is '单位法人';

comment on column public.turing_organization.phone is '单位联系电话';

comment on column public.turing_organization.created_by is '创建主体';

comment on column public.turing_organization.created_date is '记录创建时间';

comment on column public.turing_organization.last_modified_by is '更新主体';

comment on column public.turing_organization.last_modified_date is '记录更新时间';

comment on column public.turing_organization.version is '乐观锁字段';

comment on column public.turing_organization.deleted is '逻辑删除';

alter table public.turing_organization
    owner to postgres;

create table if not exists public.turing_organization_business
(
    id                 bigserial
        constraint con_public_mine_organization_business_constraint_1
            primary key,
    org_id             bigint,
    org_name           varchar(128),
    link               varchar(128),
    type               varchar(128),
    province_code      integer,
    city_code          integer,
    district_code      integer,
    province_name      varchar(16),
    city_name          varchar(16),
    district_name      varchar(16),
    state              varchar(3),
    business_level     varchar(16),
    created_by         bigint,
    created_date       timestamp,
    last_modified_by   bigint,
    last_modified_date timestamp,
    version            integer,
    deleted            boolean
);

comment on table public.turing_organization_business is '单位业务表';

comment on column public.turing_organization_business.id is '单位业务表主键 id';

comment on column public.turing_organization_business.org_id is '单位 id';

comment on column public.turing_organization_business.org_name is '单位名称';

comment on column public.turing_organization_business.link is '业务环节';

comment on column public.turing_organization_business.type is '质控类型';

comment on column public.turing_organization_business.province_code is '业务申请所在省区号';

comment on column public.turing_organization_business.city_code is '业务申请所在市区号';

comment on column public.turing_organization_business.district_code is '业务申请所在区县区号';

comment on column public.turing_organization_business.province_name is '业务申请所在省名称';

comment on column public.turing_organization_business.city_name is '业务申请所在市名称';

comment on column public.turing_organization_business.district_name is '业务申请所在区县名称';

comment on column public.turing_organization_business.state is '业务申请状态';

comment on column public.turing_organization_business.business_level is '申请业务级别';

comment on column public.turing_organization_business.created_by is '创建主体';

comment on column public.turing_organization_business.created_date is '记录创建时间';

comment on column public.turing_organization_business.last_modified_by is '更新主体';

comment on column public.turing_organization_business.last_modified_date is '记录更新时间';

comment on column public.turing_organization_business.version is '乐观锁字段';

comment on column public.turing_organization_business.deleted is '逻辑删除';

alter table public.turing_organization_business
    owner to postgres;

create table if not exists public.turing_permission
(
    id                 bigserial
        constraint con_public_turing_permission_constraint_1
            primary key,
    pid                bigint,
    name               varchar(32)                         not null
        unique,
    code               varchar(32)
        unique,
    sort               integer                             not null
        unique,
    created_by         bigint,
    created_date       timestamp                           not null,
    last_modified_by   bigint,
    last_modified_date timestamp default CURRENT_TIMESTAMP not null,
    version            integer   default 0                 not null,
    deleted            boolean   default false             not null
);

comment on table public.turing_permission is '权限表';

comment on column public.turing_permission.id is '权限 id';

comment on column public.turing_permission.pid is '上级权限 id';

comment on column public.turing_permission.name is '权限名称';

comment on column public.turing_permission.code is '权限编码';

comment on column public.turing_permission.sort is '权限排序';

comment on column public.turing_permission.created_by is '创建主体';

comment on column public.turing_permission.created_date is '创建时间';

comment on column public.turing_permission.last_modified_by is '更新主体';

comment on column public.turing_permission.last_modified_date is '更新时间';

comment on column public.turing_permission.version is '乐观锁字段';

comment on column public.turing_permission.deleted is '逻辑删除';

alter table public.turing_permission
    owner to postgres;

create table if not exists public.turing_role
(
    id                 bigserial
        constraint con_public_turing_role_constraint_1
            primary key,
    name               varchar(64)                         not null
        unique,
    authority          varchar(64)                         not null
        unique,
    description        varchar(128)                        not null,
    created_by         bigint,
    created_date       timestamp                           not null,
    last_modified_by   bigint,
    last_modified_date timestamp default CURRENT_TIMESTAMP not null,
    version            integer   default 0                 not null,
    deleted            boolean   default false             not null
);

comment on table public.turing_role is '角色表';

comment on column public.turing_role.id is '角色 id';

comment on column public.turing_role.name is '角色名称';

comment on column public.turing_role.authority is '角色编码';

comment on column public.turing_role.description is '角色说明';

comment on column public.turing_role.created_by is '创建主体';

comment on column public.turing_role.created_date is '创建时间';

comment on column public.turing_role.last_modified_by is '更新主体';

comment on column public.turing_role.last_modified_date is '更新时间';

comment on column public.turing_role.version is '乐观锁字段';

comment on column public.turing_role.deleted is '逻辑删除';

alter table public.turing_role
    owner to postgres;

create table if not exists public.turing_role_permission
(
    id                 bigserial
        constraint con_public_turing_role_permission_constraint_1
            primary key,
    role_id            bigint                              not null,
    permission_id      bigint                              not null,
    created_by         bigint,
    created_date       timestamp                           not null,
    last_modified_by   bigint,
    last_modified_date timestamp default CURRENT_TIMESTAMP not null,
    version            integer   default 0                 not null,
    deleted            boolean   default false             not null
);

comment on table public.turing_role_permission is '角色与权限关联记录表';

comment on column public.turing_role_permission.id is '角色与权限关联记录 id';

comment on column public.turing_role_permission.role_id is '角色 id';

comment on column public.turing_role_permission.permission_id is '权限 id';

comment on column public.turing_role_permission.created_by is '创建主体';

comment on column public.turing_role_permission.created_date is '创建时间';

comment on column public.turing_role_permission.last_modified_by is '更新主体';

comment on column public.turing_role_permission.last_modified_date is '更新时间';

comment on column public.turing_role_permission.version is '乐观锁字段';

comment on column public.turing_role_permission.deleted is '逻辑删除';

alter table public.turing_role_permission
    owner to postgres;

create table if not exists public.turing_user
(
    id                      bigserial
        constraint con_public_turing_user_constraint_1
            primary key,
    name                    varchar(64)                         not null,
    mobile                  bigint                              not null
        unique,
    gender                  varchar(1)                          not null,
    org_id                  bigint                              not null,
    org_name                varchar(128)                        not null,
    department              varchar(128),
    username                varchar(64)                         not null
        unique,
    password                varchar(128)                        not null,
    account_non_expired     boolean   default true              not null,
    account_non_locked      boolean   default true              not null,
    credentials_non_expired boolean   default true              not null,
    enabled                 boolean   default true              not null,
    manager                 boolean   default false             not null,
    need_set_password       boolean   default true              not null,
    created_by              bigint,
    created_date            timestamp                           not null,
    last_modified_by        bigint,
    last_modified_date      timestamp default CURRENT_TIMESTAMP not null,
    version                 integer   default 0                 not null,
    deleted                 boolean   default false             not null
);

comment on table public.turing_user is '用户表';

comment on column public.turing_user.id is '用户主键 id';

comment on column public.turing_user.name is '用户姓名';

comment on column public.turing_user.mobile is '用户手机号';

comment on column public.turing_user.gender is '用户性别';

comment on column public.turing_user.org_id is '组织机构 id';

comment on column public.turing_user.org_name is '组织机构名称';

comment on column public.turing_user.department is '部门';

comment on column public.turing_user.username is '用户名';

comment on column public.turing_user.password is '用户密码';

comment on column public.turing_user.account_non_expired is '是否已过期';

comment on column public.turing_user.account_non_locked is '是否已锁定';

comment on column public.turing_user.credentials_non_expired is '是否凭证已过期';

comment on column public.turing_user.enabled is '是否启用';

comment on column public.turing_user.manager is '是否单位管理员';

comment on column public.turing_user.need_set_password is '是否需要重新设置密码';

comment on column public.turing_user.created_by is '创建主体';

comment on column public.turing_user.created_date is '创建时间';

comment on column public.turing_user.last_modified_by is '更新主体';

comment on column public.turing_user.last_modified_date is '更新时间';

comment on column public.turing_user.version is '乐观锁字段';

comment on column public.turing_user.deleted is '逻辑删除';

alter table public.turing_user
    owner to postgres;

create table if not exists public.turing_user_role
(
    id                 bigserial
        constraint con_public_turing_user_role_constraint_1
            primary key,
    user_id            bigint                              not null,
    role_id            bigint                              not null,
    created_by         bigint,
    created_date       timestamp                           not null,
    last_modified_by   bigint,
    last_modified_date timestamp default CURRENT_TIMESTAMP not null,
    version            integer   default 0                 not null,
    deleted            boolean   default false             not null
);

comment on table public.turing_user_role is '角色与用户关联记录表';

comment on column public.turing_user_role.id is '角色与用户关联记录 id';

comment on column public.turing_user_role.user_id is '用户 id';

comment on column public.turing_user_role.role_id is '角色 id';

comment on column public.turing_user_role.created_by is '创建主体';

comment on column public.turing_user_role.created_date is '创建时间';

comment on column public.turing_user_role.last_modified_by is '更新主体';

comment on column public.turing_user_role.last_modified_date is '更新时间';

comment on column public.turing_user_role.version is '乐观锁字段';

comment on column public.turing_user_role.deleted is '逻辑删除';

alter table public.turing_user_role
    owner to postgres;
