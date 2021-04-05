USE [mybatis]
GO
/****** Object:  User [mybatis]   ******/
CREATE USER [mybatis] FOR LOGIN [mybatis] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [mybatis]
GO
/****** Object:  Table [dbo].[class]   ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[class](
	[id] [bigint] NOT NULL,
	[name] [varchar](50) NULL,
	[add_time] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[class_teacher_rel]   ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[class_teacher_rel](
	[id] [bigint] NOT NULL,
	[teacher_id] [bigint] NULL,
	[class_id] [bigint] NULL,
	[add_time] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[sequence]   ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[sequence](
	[id] [int] NULL,
	[current_seq] [bigint] NULL,
	[seq_name] [varchar](50) NULL,
	[update_time] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[student]   ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[student](
	[id] [bigint] NOT NULL,
	[name] [varchar](20) NULL,
	[age] [smallint] NULL,
	[class_id] [bigint] NULL,
	[add_time] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[student_profile]   ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[student_profile](
	[id] [bigint] NOT NULL,
	[father_name] [varchar](20) NULL,
	[father_age] [smallint] NULL,
	[mother_name] [varchar](20) NULL,
	[mother_age] [smallint] NULL,
	[student_id] [bigint] NULL,
	[add_time] [datetime] NULL,
	[update_time] [datetime] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[teacher]   ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[teacher](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](20) NULL,
	[age] [smallint] NULL,
	[add_time] [datetime] NULL
) ON [PRIMARY]
GO
INSERT [dbo].[class] ([id], [name], [add_time]) VALUES (1, N'一年级', CAST(N'2017-06-04T16:14:44.000' AS DateTime))
INSERT [dbo].[class] ([id], [name], [add_time]) VALUES (2, N'二年级', CAST(N'2017-06-04T16:14:59.000' AS DateTime))
GO
INSERT [dbo].[class_teacher_rel] ([id], [teacher_id], [class_id], [add_time]) VALUES (1, 1, 1, CAST(N'2017-08-21T09:04:10.000' AS DateTime))
INSERT [dbo].[class_teacher_rel] ([id], [teacher_id], [class_id], [add_time]) VALUES (2, 1, 2, CAST(N'2017-08-21T16:46:25.000' AS DateTime))
INSERT [dbo].[class_teacher_rel] ([id], [teacher_id], [class_id], [add_time]) VALUES (3, 2, 1, CAST(N'2017-08-21T16:50:13.000' AS DateTime))
GO
INSERT [dbo].[sequence] ([id], [current_seq], [seq_name], [update_time]) VALUES (1, 500, N'默认序列', CAST(N'2021-03-14T15:26:09.000' AS DateTime))
GO
INSERT [dbo].[student] ([id], [name], [age], [class_id], [add_time]) VALUES (1, N'蓝天', 7, 1, CAST(N'2021-03-14T10:35:47.000' AS DateTime))
INSERT [dbo].[student] ([id], [name], [age], [class_id], [add_time]) VALUES (2, N'王小一', 9, 2, CAST(N'2017-06-04T16:15:11.000' AS DateTime))
INSERT [dbo].[student] ([id], [name], [age], [class_id], [add_time]) VALUES (3, N'张三', 8, 1, CAST(N'2017-06-08T11:36:48.000' AS DateTime))
INSERT [dbo].[student] ([id], [name], [age], [class_id], [add_time]) VALUES (4, N'李四', 8, 1, CAST(N'2017-06-08T14:40:16.000' AS DateTime))
INSERT [dbo].[student] ([id], [name], [age], [class_id], [add_time]) VALUES (5, N'王五', 9, 2, CAST(N'2017-06-08T14:40:43.000' AS DateTime))
GO
INSERT [dbo].[student_profile] ([id], [father_name], [father_age], [mother_name], [mother_age], [student_id], [add_time], [update_time]) VALUES (1, N'蓝标', 33, N'张兰', 28, 1, CAST(N'2021-03-14T10:38:25.000' AS DateTime), CAST(N'2021-03-14T19:14:09.000' AS DateTime))
INSERT [dbo].[student_profile] ([id], [father_name], [father_age], [mother_name], [mother_age], [student_id], [add_time], [update_time]) VALUES (2, N'王海飞', 32, N'梁晓菊', 29, 2, CAST(N'2021-03-14T10:39:34.000' AS DateTime), CAST(N'2021-03-14T19:14:11.000' AS DateTime))
GO
SET IDENTITY_INSERT [dbo].[teacher] ON 

INSERT [dbo].[teacher] ([id], [name], [age], [add_time]) VALUES (1, N'黄老师', 30, CAST(N'2017-08-21T09:03:28.000' AS DateTime))
INSERT [dbo].[teacher] ([id], [name], [age], [add_time]) VALUES (2, N'何老师', 28, CAST(N'2017-08-21T16:49:56.000' AS DateTime))
SET IDENTITY_INSERT [dbo].[teacher] OFF
GO
/****** Object:  Index [PK_CLASS]   ******/
ALTER TABLE [dbo].[class] ADD  CONSTRAINT [PK_CLASS] PRIMARY KEY NONCLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [PK_CLASS_TEACHER_REL]   ******/
ALTER TABLE [dbo].[class_teacher_rel] ADD  CONSTRAINT [PK_CLASS_TEACHER_REL] PRIMARY KEY NONCLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [PK_STUDENT]   ******/
ALTER TABLE [dbo].[student] ADD  CONSTRAINT [PK_STUDENT] PRIMARY KEY NONCLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [PK_STUDENT_PROFILE]   ******/
ALTER TABLE [dbo].[student_profile] ADD  CONSTRAINT [PK_STUDENT_PROFILE] PRIMARY KEY NONCLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [PK_TEACHER]   ******/
ALTER TABLE [dbo].[teacher] ADD  CONSTRAINT [PK_TEACHER] PRIMARY KEY NONCLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[sequence] ADD  DEFAULT (getdate()) FOR [update_time]
GO
ALTER TABLE [dbo].[student_profile] ADD  DEFAULT (getdate()) FOR [add_time]
GO
ALTER TABLE [dbo].[student_profile] ADD  DEFAULT (getdate()) FOR [update_time]
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'id' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'class', @level2type=N'COLUMN',@level2name=N'id'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'级名' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'class', @level2type=N'COLUMN',@level2name=N'name'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'年级' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'class'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'id' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'student', @level2type=N'COLUMN',@level2name=N'id'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'姓名' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'student', @level2type=N'COLUMN',@level2name=N'name'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'年龄' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'student', @level2type=N'COLUMN',@level2name=N'age'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'班级ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'student', @level2type=N'COLUMN',@level2name=N'class_id'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'姓名' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'teacher', @level2type=N'COLUMN',@level2name=N'name'
GO
