
-- ----------------------------
-- Table structure for class
-- ----------------------------
DROP TABLE [dbo].[class]
GO
CREATE TABLE [dbo].[class] (
[id] bigint NOT NULL ,
[name] varchar(50) NULL ,
[add_time] datetime NULL 
)


GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'class', 
NULL, NULL)) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'年级'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'class'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'年级'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'class'
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'class', 
'COLUMN', N'id')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'id'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'class'
, @level2type = 'COLUMN', @level2name = N'id'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'id'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'class'
, @level2type = 'COLUMN', @level2name = N'id'
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'class', 
'COLUMN', N'name')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'级名'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'class'
, @level2type = 'COLUMN', @level2name = N'name'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'级名'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'class'
, @level2type = 'COLUMN', @level2name = N'name'
GO

-- ----------------------------
-- Records of class
-- ----------------------------
INSERT INTO [dbo].[class] ([id], [name], [add_time]) VALUES (N'1', N'一年级', N'2017-06-04 16:14:44.000')
GO
GO
INSERT INTO [dbo].[class] ([id], [name], [add_time]) VALUES (N'2', N'二年级', N'2017-06-04 16:14:59.000')
GO
GO

-- ----------------------------
-- Table structure for class_teacher_rel
-- ----------------------------
DROP TABLE [dbo].[class_teacher_rel]
GO
CREATE TABLE [dbo].[class_teacher_rel] (
[id] bigint NOT NULL ,
[teacher_id] bigint NULL ,
[class_id] bigint NULL ,
[add_time] datetime NULL 
)


GO

-- ----------------------------
-- Records of class_teacher_rel
-- ----------------------------
INSERT INTO [dbo].[class_teacher_rel] ([id], [teacher_id], [class_id], [add_time]) VALUES (N'1', N'1', N'1', N'2017-08-21 09:04:10.000')
GO
GO
INSERT INTO [dbo].[class_teacher_rel] ([id], [teacher_id], [class_id], [add_time]) VALUES (N'2', N'1', N'2', N'2017-08-21 16:46:25.000')
GO
GO
INSERT INTO [dbo].[class_teacher_rel] ([id], [teacher_id], [class_id], [add_time]) VALUES (N'3', N'2', N'1', N'2017-08-21 16:50:13.000')
GO
GO

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE [dbo].[menu]
GO
CREATE TABLE [dbo].[menu] (
[id] bigint NOT NULL IDENTITY(1,1) ,
[pid] bigint NULL ,
[name] varchar(20) NULL ,
[add_time] datetime NULL ,
[update_time] datetime NULL 
)


GO
DBCC CHECKIDENT(N'[dbo].[menu]', RESEED, 6)
GO

-- ----------------------------
-- Records of menu
-- ----------------------------
SET IDENTITY_INSERT [dbo].[menu] ON
GO
INSERT INTO [dbo].[menu] ([id], [pid], [name], [add_time], [update_time]) VALUES (N'1', N'0', N'一级1', N'2021-04-02 22:21:11.000', N'2021-04-02 22:21:13.000')
GO
GO
INSERT INTO [dbo].[menu] ([id], [pid], [name], [add_time], [update_time]) VALUES (N'2', N'0', N'一级2', N'2021-04-02 22:21:21.000', N'2021-04-02 22:21:22.000')
GO
GO
INSERT INTO [dbo].[menu] ([id], [pid], [name], [add_time], [update_time]) VALUES (N'3', N'1', N'二级1', N'2021-04-02 22:21:30.000', N'2021-04-02 22:21:31.000')
GO
GO
INSERT INTO [dbo].[menu] ([id], [pid], [name], [add_time], [update_time]) VALUES (N'4', N'1', N'二级2', N'2021-04-02 22:21:41.000', N'2021-04-02 22:21:49.000')
GO
GO
INSERT INTO [dbo].[menu] ([id], [pid], [name], [add_time], [update_time]) VALUES (N'5', N'2', N'二级3', N'2021-04-02 22:22:02.000', N'2021-04-02 22:22:02.000')
GO
GO
INSERT INTO [dbo].[menu] ([id], [pid], [name], [add_time], [update_time]) VALUES (N'6', N'5', N'三级1', N'2021-04-02 22:22:16.000', N'2021-04-02 22:22:17.000')
GO
GO
SET IDENTITY_INSERT [dbo].[menu] OFF
GO

-- ----------------------------
-- Table structure for sequence
-- ----------------------------
DROP TABLE [dbo].[sequence]
GO
CREATE TABLE [dbo].[sequence] (
[id] int NULL ,
[current_seq] bigint NULL ,
[seq_name] varchar(50) NULL ,
[update_time] datetime NULL DEFAULT (getdate()) 
)


GO

-- ----------------------------
-- Records of sequence
-- ----------------------------
INSERT INTO [dbo].[sequence] ([id], [current_seq], [seq_name], [update_time]) VALUES (N'1', N'500', N'默认序列', N'2021-03-14 15:26:09.000')
GO
GO

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE [dbo].[student]
GO
CREATE TABLE [dbo].[student] (
[id] bigint NOT NULL ,
[name] varchar(20) NULL ,
[age] smallint NULL ,
[class_id] bigint NULL ,
[add_time] datetime NULL ,
[update_time] datetime NULL 
)


GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'student', 
'COLUMN', N'id')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'id'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'student'
, @level2type = 'COLUMN', @level2name = N'id'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'id'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'student'
, @level2type = 'COLUMN', @level2name = N'id'
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'student', 
'COLUMN', N'name')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'姓名'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'student'
, @level2type = 'COLUMN', @level2name = N'name'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'姓名'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'student'
, @level2type = 'COLUMN', @level2name = N'name'
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'student', 
'COLUMN', N'age')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'年龄'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'student'
, @level2type = 'COLUMN', @level2name = N'age'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'年龄'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'student'
, @level2type = 'COLUMN', @level2name = N'age'
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'student', 
'COLUMN', N'class_id')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'班级ID'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'student'
, @level2type = 'COLUMN', @level2name = N'class_id'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'班级ID'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'student'
, @level2type = 'COLUMN', @level2name = N'class_id'
GO

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO [dbo].[student] ([id], [name], [age], [class_id], [add_time], [update_time]) VALUES (N'1', N'蓝天', N'7', N'1', N'2021-03-14 10:35:47.000', N'2021-07-18 16:58:55.463')
GO
GO
INSERT INTO [dbo].[student] ([id], [name], [age], [class_id], [add_time], [update_time]) VALUES (N'2', N'王小一', N'9', N'2', N'2017-06-04 16:15:11.000', N'2021-07-18 16:58:55.463')
GO
GO
INSERT INTO [dbo].[student] ([id], [name], [age], [class_id], [add_time], [update_time]) VALUES (N'3', N'张三', N'8', N'1', N'2017-06-08 11:36:48.000', N'2021-07-18 16:58:55.463')
GO
GO
INSERT INTO [dbo].[student] ([id], [name], [age], [class_id], [add_time], [update_time]) VALUES (N'4', N'李四', N'8', N'1', N'2017-06-08 14:40:16.000', N'2021-07-18 16:58:55.463')
GO
GO
INSERT INTO [dbo].[student] ([id], [name], [age], [class_id], [add_time], [update_time]) VALUES (N'5', N'王五', N'9', N'2', N'2017-06-08 14:40:43.000', N'2021-07-18 16:58:55.463')
GO
GO

-- ----------------------------
-- Table structure for student_profile
-- ----------------------------
DROP TABLE [dbo].[student_profile]
GO
CREATE TABLE [dbo].[student_profile] (
[id] bigint NOT NULL ,
[father_name] varchar(20) NULL ,
[father_age] smallint NULL ,
[mother_name] varchar(20) NULL ,
[mother_age] smallint NULL ,
[student_id] bigint NULL ,
[add_time] datetime NULL DEFAULT (getdate()) ,
[update_time] datetime NULL DEFAULT (getdate()) 
)


GO

-- ----------------------------
-- Records of student_profile
-- ----------------------------
INSERT INTO [dbo].[student_profile] ([id], [father_name], [father_age], [mother_name], [mother_age], [student_id], [add_time], [update_time]) VALUES (N'1', N'蓝标', N'33', N'张兰', N'28', N'1', N'2021-03-14 10:38:25.000', N'2021-03-14 19:14:09.000')
GO
GO
INSERT INTO [dbo].[student_profile] ([id], [father_name], [father_age], [mother_name], [mother_age], [student_id], [add_time], [update_time]) VALUES (N'2', N'王海飞', N'32', N'梁晓菊', N'29', N'2', N'2021-03-14 10:39:34.000', N'2021-03-14 19:14:11.000')
GO
GO

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE [dbo].[teacher]
GO
CREATE TABLE [dbo].[teacher] (
[id] bigint NOT NULL IDENTITY(1,1) ,
[name] varchar(20) NULL ,
[age] smallint NULL ,
[add_time] datetime NULL ,
[revision] int NULL 
)


GO
DBCC CHECKIDENT(N'[dbo].[teacher]', RESEED, 2)
GO
IF ((SELECT COUNT(*) from fn_listextendedproperty('MS_Description', 
'SCHEMA', N'dbo', 
'TABLE', N'teacher', 
'COLUMN', N'name')) > 0) 
EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'姓名'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'teacher'
, @level2type = 'COLUMN', @level2name = N'name'
ELSE
EXEC sp_addextendedproperty @name = N'MS_Description', @value = N'姓名'
, @level0type = 'SCHEMA', @level0name = N'dbo'
, @level1type = 'TABLE', @level1name = N'teacher'
, @level2type = 'COLUMN', @level2name = N'name'
GO

-- ----------------------------
-- Records of teacher
-- ----------------------------
SET IDENTITY_INSERT [dbo].[teacher] ON
GO
INSERT INTO [dbo].[teacher] ([id], [name], [age], [add_time], [revision]) VALUES (N'1', N'黄老师', N'30', N'2017-08-21 09:03:28.000', N'0')
GO
GO
INSERT INTO [dbo].[teacher] ([id], [name], [age], [add_time], [revision]) VALUES (N'2', N'何老师', N'28', N'2017-08-21 16:49:56.000', N'0')
GO
GO
SET IDENTITY_INSERT [dbo].[teacher] OFF
GO

-- ----------------------------
-- Indexes structure for table class
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table class
-- ----------------------------
ALTER TABLE [dbo].[class] ADD PRIMARY KEY NONCLUSTERED ([id])
GO

-- ----------------------------
-- Indexes structure for table class_teacher_rel
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table class_teacher_rel
-- ----------------------------
ALTER TABLE [dbo].[class_teacher_rel] ADD PRIMARY KEY NONCLUSTERED ([id])
GO

-- ----------------------------
-- Indexes structure for table menu
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table menu
-- ----------------------------
ALTER TABLE [dbo].[menu] ADD PRIMARY KEY ([id])
GO

-- ----------------------------
-- Indexes structure for table student
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table student
-- ----------------------------
ALTER TABLE [dbo].[student] ADD PRIMARY KEY NONCLUSTERED ([id])
GO

-- ----------------------------
-- Indexes structure for table student_profile
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table student_profile
-- ----------------------------
ALTER TABLE [dbo].[student_profile] ADD PRIMARY KEY NONCLUSTERED ([id])
GO

-- ----------------------------
-- Indexes structure for table teacher
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table teacher
-- ----------------------------
ALTER TABLE [dbo].[teacher] ADD PRIMARY KEY NONCLUSTERED ([id])
GO