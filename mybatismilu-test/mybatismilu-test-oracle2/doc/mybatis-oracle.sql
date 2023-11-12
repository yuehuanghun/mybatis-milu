
-- ----------------------------
-- Table structure for class
-- ----------------------------
DROP TABLE "MYBATIS"."class";
CREATE TABLE "MYBATIS"."class" (
"id" NUMBER NOT NULL ,
"name" VARCHAR2(50 BYTE) NULL ,
"add_time" DATE NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON TABLE "MYBATIS"."class" IS '年级';
COMMENT ON COLUMN "MYBATIS"."class"."id" IS 'id';
COMMENT ON COLUMN "MYBATIS"."class"."name" IS '级名';

-- ----------------------------
-- Records of class
-- ----------------------------
INSERT INTO "MYBATIS"."class" VALUES ('1', '一年级', TO_DATE('2017-06-04 16:14:44', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO "MYBATIS"."class" VALUES ('2', '二年级', TO_DATE('2017-06-04 16:14:59', 'YYYY-MM-DD HH24:MI:SS'));

-- ----------------------------
-- Table structure for class_teacher_rel
-- ----------------------------
DROP TABLE "MYBATIS"."class_teacher_rel";
CREATE TABLE "MYBATIS"."class_teacher_rel" (
"id" NUMBER NOT NULL ,
"teacher_id" NUMBER NULL ,
"class_id" NUMBER NULL ,
"add_time" DATE NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;

-- ----------------------------
-- Records of class_teacher_rel
-- ----------------------------
INSERT INTO "MYBATIS"."class_teacher_rel" VALUES ('1', '1', '1', TO_DATE('2017-08-21 09:04:10', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO "MYBATIS"."class_teacher_rel" VALUES ('2', '1', '2', TO_DATE('2017-08-21 16:46:25', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO "MYBATIS"."class_teacher_rel" VALUES ('3', '2', '1', TO_DATE('2017-08-21 16:50:13', 'YYYY-MM-DD HH24:MI:SS'));

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE "MYBATIS"."student";
CREATE TABLE "MYBATIS"."student" (
"id" NUMBER NOT NULL ,
"name" VARCHAR2(20 BYTE) NULL ,
"age" NUMBER NULL ,
"class_id" NUMBER NULL ,
"add_time" DATE NULL ,
"update_time" DATE NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON COLUMN "MYBATIS"."student"."id" IS 'id';
COMMENT ON COLUMN "MYBATIS"."student"."name" IS '姓名';
COMMENT ON COLUMN "MYBATIS"."student"."age" IS '年龄';
COMMENT ON COLUMN "MYBATIS"."student"."class_id" IS '班级ID';

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO "MYBATIS"."student" VALUES ('1', '蓝天', '7', '1', TO_DATE('2021-03-14 10:35:47', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2021-07-17 22:28:12', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO "MYBATIS"."student" VALUES ('2', '王小一', '9', '2', TO_DATE('2017-06-04 16:15:11', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2021-07-17 22:28:16', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO "MYBATIS"."student" VALUES ('3', '张三', '8', '1', TO_DATE('2017-06-08 11:36:48', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2021-07-17 22:28:19', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO "MYBATIS"."student" VALUES ('4', '李四', '8', '1', TO_DATE('2017-06-08 14:40:16', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2021-07-17 22:28:22', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO "MYBATIS"."student" VALUES ('5', '王五', '9', '2', TO_DATE('2017-06-08 14:40:43', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2021-07-17 22:28:23', 'YYYY-MM-DD HH24:MI:SS'));

-- ----------------------------
-- Table structure for student_profile
-- ----------------------------
DROP TABLE "MYBATIS"."student_profile";
CREATE TABLE "MYBATIS"."student_profile" (
"id" NUMBER NOT NULL ,
"father_name" VARCHAR2(20 BYTE) NULL ,
"father_age" NUMBER NULL ,
"mother_name" VARCHAR2(20 BYTE) NULL ,
"mother_age" NUMBER NULL ,
"student_id" NUMBER NULL ,
"add_time" DATE NULL ,
"update_time" DATE NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;

-- ----------------------------
-- Records of student_profile
-- ----------------------------
INSERT INTO "MYBATIS"."student_profile" VALUES ('1', '蓝标', '33', '张兰', '28', '1', TO_DATE('2021-03-14 10:38:25', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2021-03-14 19:14:09', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO "MYBATIS"."student_profile" VALUES ('2', '王海飞', '32', '梁晓菊', '29', '2', TO_DATE('2021-03-14 10:39:34', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2021-03-14 19:14:11', 'YYYY-MM-DD HH24:MI:SS'));

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE "MYBATIS"."teacher";
CREATE TABLE "MYBATIS"."teacher" (
"id" NUMBER NOT NULL ,
"name" VARCHAR2(20 BYTE) NULL ,
"age" NUMBER NULL ,
"add_time" DATE NULL ,
"revision" NUMBER NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON COLUMN "MYBATIS"."teacher"."name" IS '姓名';

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO "MYBATIS"."teacher" VALUES ('1', '黄老师', '30', TO_DATE('2017-08-21 09:03:28', 'YYYY-MM-DD HH24:MI:SS'), '0');
INSERT INTO "MYBATIS"."teacher" VALUES ('2', '何老师', '28', TO_DATE('2017-08-21 16:49:56', 'YYYY-MM-DD HH24:MI:SS'), '0');

-- ----------------------------
-- Sequence structure for SEQ_STUDENT_ID
-- ----------------------------
DROP SEQUENCE "MYBATIS"."SEQ_STUDENT_ID";
CREATE SEQUENCE "MYBATIS"."SEQ_STUDENT_ID"
 INCREMENT BY 1
 MINVALUE 1
 MAXVALUE 9999999999
 START WITH 687
 CACHE 5;

-- ----------------------------
-- Indexes structure for table class
-- ----------------------------

-- ----------------------------
-- Checks structure for table class
-- ----------------------------
ALTER TABLE "MYBATIS"."class" ADD CHECK ("id" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table class
-- ----------------------------
ALTER TABLE "MYBATIS"."class" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table class_teacher_rel
-- ----------------------------

-- ----------------------------
-- Checks structure for table class_teacher_rel
-- ----------------------------
ALTER TABLE "MYBATIS"."class_teacher_rel" ADD CHECK ("id" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table class_teacher_rel
-- ----------------------------
ALTER TABLE "MYBATIS"."class_teacher_rel" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table student
-- ----------------------------

-- ----------------------------
-- Checks structure for table student
-- ----------------------------
ALTER TABLE "MYBATIS"."student" ADD CHECK ("id" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table student
-- ----------------------------
ALTER TABLE "MYBATIS"."student" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table student_profile
-- ----------------------------

-- ----------------------------
-- Checks structure for table student_profile
-- ----------------------------
ALTER TABLE "MYBATIS"."student_profile" ADD CHECK ("id" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table student_profile
-- ----------------------------
ALTER TABLE "MYBATIS"."student_profile" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table teacher
-- ----------------------------

-- ----------------------------
-- Checks structure for table teacher
-- ----------------------------
ALTER TABLE "MYBATIS"."teacher" ADD CHECK ("id" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table teacher
-- ----------------------------
ALTER TABLE "MYBATIS"."teacher" ADD PRIMARY KEY ("id");
