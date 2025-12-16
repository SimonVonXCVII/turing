CREATE TABLE IF NOT EXISTS public.turing_app_file
(
    id                 SERIAL
        CONSTRAINT con_public_turing_app_file_constraint_1
            PRIMARY KEY,
    owner_id           INTEGER                             NOT NULL,
    filename           VARCHAR(128)                        NOT NULL,
    origin_filename    VARCHAR(128)                        NOT NULL,
    suffix             VARCHAR(8)                          NOT NULL,
    content_type       VARCHAR(128)                        NOT NULL,
    content_length     BIGINT                              NOT NULL,
    md5                VARCHAR(64)                         NOT NULL
        UNIQUE,
    path               VARCHAR(1024)                       NOT NULL,
    biz_type           VARCHAR(64)                         NOT NULL,
    remark             VARCHAR(1024),
    created_by         INTEGER,
    created_date       TIMESTAMP                           NOT NULL,
    last_modified_by   INTEGER,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version            INTEGER   DEFAULT 0                 NOT NULL,
    deleted            BOOLEAN   DEFAULT FALSE             NOT NULL
);

COMMENT ON TABLE public.turing_app_file IS '文件表';

COMMENT ON COLUMN public.turing_app_file.id IS '文件表主键 id';

COMMENT ON COLUMN public.turing_app_file.owner_id IS '所有者 id';

COMMENT ON COLUMN public.turing_app_file.filename IS '文件名';

COMMENT ON COLUMN public.turing_app_file.origin_filename IS '原始文件名';

COMMENT ON COLUMN public.turing_app_file.suffix IS '后缀';

COMMENT ON COLUMN public.turing_app_file.content_type IS '内容类型';

COMMENT ON COLUMN public.turing_app_file.content_length IS '内容长度';

COMMENT ON COLUMN public.turing_app_file.md5 IS 'md5';

COMMENT ON COLUMN public.turing_app_file.path IS '存放路径';

COMMENT ON COLUMN public.turing_app_file.biz_type IS '业务类型';

COMMENT ON COLUMN public.turing_app_file.remark IS '备注';

COMMENT ON COLUMN public.turing_app_file.created_by IS '创建者';

COMMENT ON COLUMN public.turing_app_file.created_date IS '创建时间';

COMMENT ON COLUMN public.turing_app_file.last_modified_by IS '最后修改者';

COMMENT ON COLUMN public.turing_app_file.last_modified_date IS '最后修改时间';

COMMENT ON COLUMN public.turing_app_file.version IS '乐观锁版本';

COMMENT ON COLUMN public.turing_app_file.deleted IS '逻辑删除';

ALTER TABLE public.turing_app_file
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.turing_dict
(
    id                 SERIAL
        CONSTRAINT con_public_turing_dict_constraint_1
            PRIMARY KEY,
    type               VARCHAR(32),
    pid                INTEGER,
    name               VARCHAR(32)                         NOT NULL,
    value              VARCHAR(32)                         NOT NULL,
    description        VARCHAR(128),
    sort               SMALLINT,
    created_by         INTEGER,
    created_date       TIMESTAMP                           NOT NULL,
    last_modified_by   INTEGER,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version            INTEGER   DEFAULT 0                 NOT NULL,
    deleted            BOOLEAN   DEFAULT FALSE             NOT NULL
);

COMMENT ON TABLE public.turing_dict IS '字典表';

COMMENT ON COLUMN public.turing_dict.id IS '字典表主键 id';

COMMENT ON COLUMN public.turing_dict.type IS '字典类型';

COMMENT ON COLUMN public.turing_dict.pid IS '上级 id';

COMMENT ON COLUMN public.turing_dict.name IS '字典名称';

COMMENT ON COLUMN public.turing_dict.value IS '字典值';

COMMENT ON COLUMN public.turing_dict.description IS '说明';

COMMENT ON COLUMN public.turing_dict.sort IS '排序';

COMMENT ON COLUMN public.turing_dict.created_by IS '创建者';

COMMENT ON COLUMN public.turing_dict.created_date IS '创建时间';

COMMENT ON COLUMN public.turing_dict.last_modified_by IS '最后修改者';

COMMENT ON COLUMN public.turing_dict.last_modified_date IS '最后修改时间';

COMMENT ON COLUMN public.turing_dict.version IS '乐观锁版本';

COMMENT ON COLUMN public.turing_dict.deleted IS '逻辑删除';

ALTER TABLE public.turing_dict
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.turing_menu
(
    id                 SERIAL
        CONSTRAINT con_public_turing_menu_constraint_1
            PRIMARY KEY,
    type               VARCHAR(8)                          NOT NULL,
    name               VARCHAR(64)                         NOT NULL
        UNIQUE,
    pid                INTEGER,
    path               VARCHAR(128),
    active_path        VARCHAR(128),
    component          VARCHAR(128),
    auth_code          VARCHAR(128)
        UNIQUE,
    status             SMALLINT                            NOT NULL,
    created_by         INTEGER,
    created_date       TIMESTAMP                           NOT NULL,
    last_modified_by   INTEGER,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version            INTEGER   DEFAULT 0                 NOT NULL,
    deleted            BOOLEAN   DEFAULT FALSE             NOT NULL
);

COMMENT ON TABLE public.turing_menu IS '菜单表';

COMMENT ON COLUMN public.turing_menu.id IS '菜单 id';

COMMENT ON COLUMN public.turing_menu.type IS '菜单类型';

COMMENT ON COLUMN public.turing_menu.name IS '菜单名称';

COMMENT ON COLUMN public.turing_menu.pid IS '上级菜单 id';

COMMENT ON COLUMN public.turing_menu.path IS '路由地址';

COMMENT ON COLUMN public.turing_menu.active_path IS '激活路径';

COMMENT ON COLUMN public.turing_menu.component IS '页面组件';

COMMENT ON COLUMN public.turing_menu.auth_code IS '权限标识';

COMMENT ON COLUMN public.turing_menu.status IS '状态';

COMMENT ON COLUMN public.turing_menu.created_by IS '创建者';

COMMENT ON COLUMN public.turing_menu.created_date IS '创建时间';

COMMENT ON COLUMN public.turing_menu.last_modified_by IS '最后修改者';

COMMENT ON COLUMN public.turing_menu.last_modified_date IS '最后修改时间';

COMMENT ON COLUMN public.turing_menu.version IS '乐观锁版本';

COMMENT ON COLUMN public.turing_menu.deleted IS '逻辑删除';

ALTER TABLE public.turing_menu
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.turing_menu_meta
(
    id                    SERIAL
        CONSTRAINT con_public_turing_menu_meta_constraint_1
            PRIMARY KEY,
    menu_id               INTEGER                             NOT NULL,
    title                 VARCHAR(64)                         NOT NULL,
    icon                  VARCHAR(128),
    active_icon           VARCHAR(128),
    badge_type            VARCHAR(8),
    badge                 VARCHAR(128),
    badge_variants        VARCHAR(16),
    keep_alive            BOOLEAN   DEFAULT FALSE,
    affix_tab             BOOLEAN   DEFAULT FALSE,
    hide_in_menu          BOOLEAN   DEFAULT FALSE,
    hide_children_in_menu BOOLEAN   DEFAULT FALSE,
    hide_in_breadcrumb    BOOLEAN   DEFAULT FALSE,
    hide_in_tab           BOOLEAN   DEFAULT FALSE,
    iframe_src            VARCHAR(128),
    link                  VARCHAR(128),
    cached                BOOLEAN,
    external              BOOLEAN,
    created_by            INTEGER,
    created_date          TIMESTAMP                           NOT NULL,
    last_modified_by      INTEGER,
    last_modified_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version               INTEGER   DEFAULT 0                 NOT NULL,
    deleted               BOOLEAN   DEFAULT FALSE             NOT NULL
);

COMMENT ON TABLE public.turing_menu_meta IS '菜单表';

COMMENT ON COLUMN public.turing_menu_meta.id IS '菜单 id';

COMMENT ON COLUMN public.turing_menu_meta.menu_id IS '菜单 id';

COMMENT ON COLUMN public.turing_menu_meta.title IS '标题';

COMMENT ON COLUMN public.turing_menu_meta.icon IS '图标';

COMMENT ON COLUMN public.turing_menu_meta.active_icon IS '激活图标';

COMMENT ON COLUMN public.turing_menu_meta.badge_type IS '徽标类型';

COMMENT ON COLUMN public.turing_menu_meta.badge IS '徽章内容';

COMMENT ON COLUMN public.turing_menu_meta.badge_variants IS '徽标样式';

COMMENT ON COLUMN public.turing_menu_meta.keep_alive IS '缓存标签页';

COMMENT ON COLUMN public.turing_menu_meta.affix_tab IS '固定在标签';

COMMENT ON COLUMN public.turing_menu_meta.hide_in_menu IS '隐藏菜单';

COMMENT ON COLUMN public.turing_menu_meta.hide_children_in_menu IS '隐藏子菜单';

COMMENT ON COLUMN public.turing_menu_meta.hide_in_breadcrumb IS '在面包屑中隐藏';

COMMENT ON COLUMN public.turing_menu_meta.hide_in_tab IS '在标签栏中隐藏';

COMMENT ON COLUMN public.turing_menu_meta.iframe_src IS '内嵌-链接地址';

COMMENT ON COLUMN public.turing_menu_meta.link IS '外链-链接地址';

COMMENT ON COLUMN public.turing_menu_meta.created_by IS '创建者';

COMMENT ON COLUMN public.turing_menu_meta.created_date IS '创建时间';

COMMENT ON COLUMN public.turing_menu_meta.last_modified_by IS '最后修改者';

COMMENT ON COLUMN public.turing_menu_meta.last_modified_date IS '最后修改时间';

COMMENT ON COLUMN public.turing_menu_meta.version IS '乐观锁版本';

COMMENT ON COLUMN public.turing_menu_meta.deleted IS '逻辑删除';

ALTER TABLE public.turing_menu_meta
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.turing_organization
(
    id                 SERIAL
        CONSTRAINT con_public_turing_organization_constraint_1
            PRIMARY KEY,
    pid                INTEGER,
    name               VARCHAR(64)                         NOT NULL
        UNIQUE,
    code               VARCHAR(18)                         NOT NULL
        UNIQUE,
    legal_person       VARCHAR(32)                         NOT NULL,
    phone              VARCHAR(32)                         NOT NULL
        UNIQUE,
    type               VARCHAR(64)                         NOT NULL,
    province_code      INTEGER                             NOT NULL,
    city_code          INTEGER                             NOT NULL,
    district_code      INTEGER                             NOT NULL,
    province_name      VARCHAR(16)                         NOT NULL,
    city_name          VARCHAR(16)                         NOT NULL,
    district_name      VARCHAR(16)                         NOT NULL,
    address            VARCHAR(128)                        NOT NULL,
    created_by         INTEGER,
    created_date       TIMESTAMP                           NOT NULL,
    last_modified_by   INTEGER,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version            INTEGER   DEFAULT 0                 NOT NULL,
    deleted            BOOLEAN   DEFAULT FALSE             NOT NULL
);

COMMENT ON TABLE public.turing_organization IS '单位表';

COMMENT ON COLUMN public.turing_organization.id IS '单位表主键 id';

COMMENT ON COLUMN public.turing_organization.pid IS '上级单位 id';

COMMENT ON COLUMN public.turing_organization.name IS '单位名称';

COMMENT ON COLUMN public.turing_organization.code IS '信用代码';

COMMENT ON COLUMN public.turing_organization.legal_person IS '单位法人';

COMMENT ON COLUMN public.turing_organization.phone IS '单位联系电话';

COMMENT ON COLUMN public.turing_organization.type IS '单位类型';

COMMENT ON COLUMN public.turing_organization.province_code IS '单位所在省（市、区）编码';

COMMENT ON COLUMN public.turing_organization.city_code IS '单位所在市（州、盟）编码';

COMMENT ON COLUMN public.turing_organization.district_code IS '单位所在区县（市、旗）编码';

COMMENT ON COLUMN public.turing_organization.province_name IS '单位所在省（市、区）名称';

COMMENT ON COLUMN public.turing_organization.city_name IS '单位所在市（州、盟）名称';

COMMENT ON COLUMN public.turing_organization.district_name IS '单位所在区县（市、旗）名称';

COMMENT ON COLUMN public.turing_organization.address IS '单位地址详情';

COMMENT ON COLUMN public.turing_organization.created_by IS '创建者';

COMMENT ON COLUMN public.turing_organization.created_date IS '创建时间';

COMMENT ON COLUMN public.turing_organization.last_modified_by IS '最后修改者';

COMMENT ON COLUMN public.turing_organization.last_modified_date IS '最后修改时间';

COMMENT ON COLUMN public.turing_organization.version IS '乐观锁版本';

COMMENT ON COLUMN public.turing_organization.deleted IS '逻辑删除';

ALTER TABLE public.turing_organization
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.turing_organization_business
(
    id                 SERIAL
        CONSTRAINT con_public_mine_organization_business_constraint_1
            PRIMARY KEY,
    org_id             INTEGER                             NOT NULL,
    org_name           VARCHAR(128)                        NOT NULL,
    link               VARCHAR[],
    type               VARCHAR[],
    province_code      INTEGER                             NOT NULL,
    city_code          INTEGER,
    district_code      INTEGER,
    province_name      VARCHAR(16)                         NOT NULL,
    city_name          VARCHAR(16),
    district_name      VARCHAR(16),
    state              VARCHAR(64)                         NOT NULL,
    business_level     VARCHAR(64)                         NOT NULL,
    created_by         INTEGER,
    created_date       TIMESTAMP                           NOT NULL,
    last_modified_by   INTEGER,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version            INTEGER   DEFAULT 0                 NOT NULL,
    deleted            BOOLEAN   DEFAULT FALSE             NOT NULL
);

COMMENT ON TABLE public.turing_organization_business IS '单位业务表';

COMMENT ON COLUMN public.turing_organization_business.id IS '单位业务表主键 id';

COMMENT ON COLUMN public.turing_organization_business.org_id IS '单位 id';

COMMENT ON COLUMN public.turing_organization_business.org_name IS '单位名称';

COMMENT ON COLUMN public.turing_organization_business.link IS '业务环节';

COMMENT ON COLUMN public.turing_organization_business.type IS '质控类型';

COMMENT ON COLUMN public.turing_organization_business.province_code IS '业务申请所在省（市、区）编码';

COMMENT ON COLUMN public.turing_organization_business.city_code IS '业务申请所在市（州、盟）编码';

COMMENT ON COLUMN public.turing_organization_business.district_code IS '业务申请所在区县（市、旗）编码';

COMMENT ON COLUMN public.turing_organization_business.province_name IS '业务申请所在省（市、区）名称';

COMMENT ON COLUMN public.turing_organization_business.city_name IS '业务申请所在市（州、盟）名称';

COMMENT ON COLUMN public.turing_organization_business.district_name IS '业务申请所在区县（市、旗）名称';

COMMENT ON COLUMN public.turing_organization_business.state IS '业务申请状态';

COMMENT ON COLUMN public.turing_organization_business.business_level IS '申请业务级别';

COMMENT ON COLUMN public.turing_organization_business.created_by IS '创建者';

COMMENT ON COLUMN public.turing_organization_business.created_date IS '创建时间';

COMMENT ON COLUMN public.turing_organization_business.last_modified_by IS '最后修改者';

COMMENT ON COLUMN public.turing_organization_business.last_modified_date IS '最后修改时间';

COMMENT ON COLUMN public.turing_organization_business.version IS '乐观锁版本';

COMMENT ON COLUMN public.turing_organization_business.deleted IS '逻辑删除';

ALTER TABLE public.turing_organization_business
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.turing_permission
(
    id                 SERIAL
        CONSTRAINT con_public_turing_permission_constraint_1
            PRIMARY KEY,
    pid                INTEGER,
    name               VARCHAR(32)                         NOT NULL
        UNIQUE,
    code               VARCHAR(32)
        UNIQUE,
    sort               INTEGER                             NOT NULL
        UNIQUE,
    created_by         INTEGER,
    created_date       TIMESTAMP                           NOT NULL,
    last_modified_by   INTEGER,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version            INTEGER   DEFAULT 0                 NOT NULL,
    deleted            BOOLEAN   DEFAULT FALSE             NOT NULL
);

COMMENT ON TABLE public.turing_permission IS '权限表';

COMMENT ON COLUMN public.turing_permission.id IS '权限 id';

COMMENT ON COLUMN public.turing_permission.pid IS '上级权限 id';

COMMENT ON COLUMN public.turing_permission.name IS '权限名称';

COMMENT ON COLUMN public.turing_permission.code IS '权限编码';

COMMENT ON COLUMN public.turing_permission.sort IS '排序编号';

COMMENT ON COLUMN public.turing_permission.created_by IS '创建者';

COMMENT ON COLUMN public.turing_permission.created_date IS '创建时间';

COMMENT ON COLUMN public.turing_permission.last_modified_by IS '最后修改者';

COMMENT ON COLUMN public.turing_permission.last_modified_date IS '最后修改时间';

COMMENT ON COLUMN public.turing_permission.version IS '乐观锁版本';

COMMENT ON COLUMN public.turing_permission.deleted IS '逻辑删除';

ALTER TABLE public.turing_permission
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.turing_role
(
    id                 SERIAL
        CONSTRAINT con_public_turing_role_constraint_1
            PRIMARY KEY,
    name               VARCHAR(64)                         NOT NULL
        UNIQUE,
    authority          VARCHAR(64)                         NOT NULL
        UNIQUE,
    status             SMALLINT                            NOT NULL,
    remark             VARCHAR(128),
    created_by         INTEGER,
    created_date       TIMESTAMP                           NOT NULL,
    last_modified_by   INTEGER,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version            INTEGER   DEFAULT 0                 NOT NULL,
    deleted            BOOLEAN   DEFAULT FALSE             NOT NULL
);

COMMENT ON TABLE public.turing_role IS '角色表';

COMMENT ON COLUMN public.turing_role.id IS '角色 id';

COMMENT ON COLUMN public.turing_role.name IS '角色名称';

COMMENT ON COLUMN public.turing_role.authority IS '角色编码';

COMMENT ON COLUMN public.turing_role.status IS '状态';

COMMENT ON COLUMN public.turing_role.remark IS '备注';

COMMENT ON COLUMN public.turing_role.created_by IS '创建者';

COMMENT ON COLUMN public.turing_role.created_date IS '创建时间';

COMMENT ON COLUMN public.turing_role.last_modified_by IS '最后修改者';

COMMENT ON COLUMN public.turing_role.last_modified_date IS '最后修改时间';

COMMENT ON COLUMN public.turing_role.version IS '乐观锁版本';

COMMENT ON COLUMN public.turing_role.deleted IS '逻辑删除';

ALTER TABLE public.turing_role
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.turing_role_permission
(
    id                 SERIAL
        CONSTRAINT con_public_turing_role_permission_constraint_1
            PRIMARY KEY,
    role_id            INTEGER                             NOT NULL,
    permission_id      INTEGER                             NOT NULL,
    created_by         INTEGER,
    created_date       TIMESTAMP                           NOT NULL,
    last_modified_by   INTEGER,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version            INTEGER   DEFAULT 0                 NOT NULL,
    deleted            BOOLEAN   DEFAULT FALSE             NOT NULL
);

COMMENT ON TABLE public.turing_role_permission IS '角色与权限关联记录表';

COMMENT ON COLUMN public.turing_role_permission.id IS '角色与权限关联记录 id';

COMMENT ON COLUMN public.turing_role_permission.role_id IS '角色 id';

COMMENT ON COLUMN public.turing_role_permission.permission_id IS '权限 id';

COMMENT ON COLUMN public.turing_role_permission.created_by IS '创建者';

COMMENT ON COLUMN public.turing_role_permission.created_date IS '创建时间';

COMMENT ON COLUMN public.turing_role_permission.last_modified_by IS '最后修改者';

COMMENT ON COLUMN public.turing_role_permission.last_modified_date IS '最后修改时间';

COMMENT ON COLUMN public.turing_role_permission.version IS '乐观锁版本';

COMMENT ON COLUMN public.turing_role_permission.deleted IS '逻辑删除';

ALTER TABLE public.turing_role_permission
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.turing_user
(
    id                      SERIAL
        CONSTRAINT con_public_turing_user_constraint_1
            PRIMARY KEY,
    name                    VARCHAR(64)                         NOT NULL,
    mobile                  BIGINT                              NOT NULL,
    gender                  VARCHAR(1)                          NOT NULL,
    org_id                  INTEGER                             NOT NULL,
    org_name                VARCHAR(128)                        NOT NULL,
    department              VARCHAR(128),
    username                VARCHAR(64)                         NOT NULL
        UNIQUE,
    password                VARCHAR(128)                        NOT NULL,
    account_non_expired     BOOLEAN   DEFAULT true              NOT NULL,
    account_non_locked      BOOLEAN   DEFAULT true              NOT NULL,
    credentials_non_expired BOOLEAN   DEFAULT true              NOT NULL,
    enabled                 BOOLEAN   DEFAULT true              NOT NULL,
    manager                 BOOLEAN   DEFAULT FALSE             NOT NULL,
    need_reset_password     BOOLEAN   DEFAULT true              NOT NULL,
    created_by              INTEGER,
    created_date            TIMESTAMP                           NOT NULL,
    last_modified_by        INTEGER,
    last_modified_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version                 INTEGER   DEFAULT 0                 NOT NULL,
    deleted                 BOOLEAN   DEFAULT FALSE             NOT NULL
);

COMMENT ON TABLE public.turing_user IS '用户表';

COMMENT ON COLUMN public.turing_user.id IS '用户主键 id';

COMMENT ON COLUMN public.turing_user.name IS '用户姓名';

COMMENT ON COLUMN public.turing_user.mobile IS '用户电话';

COMMENT ON COLUMN public.turing_user.gender IS '用户性别';

COMMENT ON COLUMN public.turing_user.org_id IS '组织机构 id';

COMMENT ON COLUMN public.turing_user.org_name IS '组织机构名称';

COMMENT ON COLUMN public.turing_user.department IS '部门';

COMMENT ON COLUMN public.turing_user.username IS '用户名';

COMMENT ON COLUMN public.turing_user.password IS '用户密码';

COMMENT ON COLUMN public.turing_user.account_non_expired IS '是否已过期';

COMMENT ON COLUMN public.turing_user.account_non_locked IS '是否已锁定';

COMMENT ON COLUMN public.turing_user.credentials_non_expired IS '是否凭证已过期';

COMMENT ON COLUMN public.turing_user.enabled IS '是否启用';

COMMENT ON COLUMN public.turing_user.manager IS '是否单位管理员';

COMMENT ON COLUMN public.turing_user.need_reset_password IS '是否需要重新设置密码';

COMMENT ON COLUMN public.turing_user.created_by IS '创建者';

COMMENT ON COLUMN public.turing_user.created_date IS '创建时间';

COMMENT ON COLUMN public.turing_user.last_modified_by IS '最后修改者';

COMMENT ON COLUMN public.turing_user.last_modified_date IS '最后修改时间';

COMMENT ON COLUMN public.turing_user.version IS '乐观锁版本';

COMMENT ON COLUMN public.turing_user.deleted IS '逻辑删除';

ALTER TABLE public.turing_user
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.turing_user_role
(
    id                 SERIAL
        CONSTRAINT con_public_turing_user_role_constraint_1
            PRIMARY KEY,
    user_id            INTEGER                             NOT NULL,
    role_id            INTEGER                             NOT NULL,
    created_by         INTEGER,
    created_date       TIMESTAMP                           NOT NULL,
    last_modified_by   INTEGER,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version            INTEGER   DEFAULT 0                 NOT NULL,
    deleted            BOOLEAN   DEFAULT FALSE             NOT NULL
);

COMMENT ON TABLE public.turing_user_role IS '角色与用户关联记录表';

COMMENT ON COLUMN public.turing_user_role.id IS '角色与用户关联记录 id';

COMMENT ON COLUMN public.turing_user_role.user_id IS '用户 id';

COMMENT ON COLUMN public.turing_user_role.role_id IS '角色 id';

COMMENT ON COLUMN public.turing_user_role.created_by IS '创建者';

COMMENT ON COLUMN public.turing_user_role.created_date IS '创建时间';

COMMENT ON COLUMN public.turing_user_role.last_modified_by IS '最后修改者';

COMMENT ON COLUMN public.turing_user_role.last_modified_date IS '最后修改时间';

COMMENT ON COLUMN public.turing_user_role.version IS '乐观锁版本';

COMMENT ON COLUMN public.turing_user_role.deleted IS '逻辑删除';

ALTER TABLE public.turing_user_role
    OWNER TO postgres;
