create table yupi.z_user
(
    id         int auto_increment
        primary key,
    uaccount   varchar(256)                       not null comment '账号',
    uname      varchar(256)                       null comment '昵称',
    avatarUrl  varchar(1024)                      null comment '头像',
    gender     tinyint                            null comment '性别
',
    mpassword  varchar(512)                       not null comment '密码
',
    phone      varchar(128)                       null comment '电话',
    email      varchar(512)                       null comment '邮箱',
    ustatus    int      default 0                 not null comment '用户状态
0  正常
1  封号',
    createTime datetime default CURRENT_TIMESTAMP null,
    updateTime datetime default CURRENT_TIMESTAMP null,
    version    int      default 1                 null,
    isDeleted  int      default 0                 null
)
    comment '用户';

