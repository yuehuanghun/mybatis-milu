--
-- PostgreSQL database dump
--

-- Dumped from database version 13.2
-- Dumped by pg_dump version 13.2


SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3030 (class 1262 OID 16394)
-- Name: mybatis; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE mybatis WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'Chinese (Simplified)_China.936';


ALTER DATABASE mybatis OWNER TO postgres;

\connect mybatis

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 200 (class 1259 OID 16432)
-- Name: class; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.class (
    id bigint NOT NULL,
    name character varying(50),
    add_time timestamp without time zone
);


ALTER TABLE public.class OWNER TO postgres;

--
-- TOC entry 3031 (class 0 OID 0)
-- Dependencies: 200
-- Name: TABLE class; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.class IS '年级';


--
-- TOC entry 3032 (class 0 OID 0)
-- Dependencies: 200
-- Name: COLUMN class.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.class.id IS 'id';


--
-- TOC entry 3033 (class 0 OID 0)
-- Dependencies: 200
-- Name: COLUMN class.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.class.name IS '级名';


--
-- TOC entry 201 (class 1259 OID 16437)
-- Name: class_teacher_rel; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.class_teacher_rel (
    id bigint NOT NULL,
    teacher_id bigint,
    class_id bigint,
    add_time timestamp without time zone
);


ALTER TABLE public.class_teacher_rel OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 16442)
-- Name: sequence; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sequence (
    id integer,
    current_seq bigint,
    seq_name character varying(50),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.sequence OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 16448)
-- Name: student; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.student (
    id integer NOT NULL,
    name character varying(20),
    age smallint,
    class_id bigint,
    add_time timestamp without time zone
);


ALTER TABLE public.student OWNER TO postgres;

--
-- TOC entry 3034 (class 0 OID 0)
-- Dependencies: 204
-- Name: COLUMN student.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.student.id IS 'id';


--
-- TOC entry 3035 (class 0 OID 0)
-- Dependencies: 204
-- Name: COLUMN student.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.student.name IS '姓名';


--
-- TOC entry 3036 (class 0 OID 0)
-- Dependencies: 204
-- Name: COLUMN student.age; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.student.age IS '年龄';


--
-- TOC entry 3037 (class 0 OID 0)
-- Dependencies: 204
-- Name: COLUMN student.class_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.student.class_id IS '班级ID';


--
-- TOC entry 203 (class 1259 OID 16446)
-- Name: student_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.student_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.student_id_seq OWNER TO postgres;

--
-- TOC entry 3038 (class 0 OID 0)
-- Dependencies: 203
-- Name: student_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.student_id_seq OWNED BY public.student.id;


--
-- TOC entry 205 (class 1259 OID 16454)
-- Name: student_profile; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.student_profile (
    id bigint NOT NULL,
    father_name character varying(20),
    father_age smallint,
    mother_name character varying(20),
    mother_age smallint,
    student_id bigint,
    add_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.student_profile OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 16463)
-- Name: teacher; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.teacher (
    id integer NOT NULL,
    name character varying(20),
    age smallint,
    add_time timestamp without time zone
);


ALTER TABLE public.teacher OWNER TO postgres;

--
-- TOC entry 3039 (class 0 OID 0)
-- Dependencies: 207
-- Name: COLUMN teacher.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.teacher.name IS '姓名';


--
-- TOC entry 206 (class 1259 OID 16461)
-- Name: teacher_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.teacher_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.teacher_id_seq OWNER TO postgres;

--
-- TOC entry 3040 (class 0 OID 0)
-- Dependencies: 206
-- Name: teacher_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.teacher_id_seq OWNED BY public.teacher.id;


--
-- TOC entry 2873 (class 2604 OID 16451)
-- Name: student id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.student ALTER COLUMN id SET DEFAULT nextval('public.student_id_seq'::regclass);


--
-- TOC entry 2876 (class 2604 OID 16466)
-- Name: teacher id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.teacher ALTER COLUMN id SET DEFAULT nextval('public.teacher_id_seq'::regclass);


--
-- TOC entry 3017 (class 0 OID 16432)
-- Dependencies: 200
-- Data for Name: class; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.class VALUES (1, '一年级', '2017-06-04 16:14:44');
INSERT INTO public.class VALUES (2, '二年级', '2017-06-04 16:14:59');


--
-- TOC entry 3018 (class 0 OID 16437)
-- Dependencies: 201
-- Data for Name: class_teacher_rel; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.class_teacher_rel VALUES (1, 1, 1, '2017-08-21 09:04:10');
INSERT INTO public.class_teacher_rel VALUES (2, 1, 2, '2017-08-21 16:46:25');
INSERT INTO public.class_teacher_rel VALUES (3, 2, 1, '2017-08-21 16:50:13');


--
-- TOC entry 3019 (class 0 OID 16442)
-- Dependencies: 202
-- Data for Name: sequence; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.sequence VALUES (1, 500, '默认序列', '2021-03-14 15:26:09');


--
-- TOC entry 3021 (class 0 OID 16448)
-- Dependencies: 204
-- Data for Name: student; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.student VALUES (1, '蓝天', 7, 1, '2021-03-14 10:35:47');
INSERT INTO public.student VALUES (2, '王小一', 9, 2, '2017-06-04 16:15:11');
INSERT INTO public.student VALUES (3, '张三', 8, 1, '2017-06-08 11:36:48');
INSERT INTO public.student VALUES (4, '李四', 8, 1, '2017-06-08 14:40:16');
INSERT INTO public.student VALUES (5, '王五', 9, 2, '2017-06-08 14:40:43');


--
-- TOC entry 3022 (class 0 OID 16454)
-- Dependencies: 205
-- Data for Name: student_profile; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.student_profile VALUES (1, '蓝标', 33, '张兰', 28, 1, '2021-03-14 10:38:25', '2021-03-14 19:14:09');
INSERT INTO public.student_profile VALUES (2, '王海飞', 32, '梁晓菊', 29, 2, '2021-03-14 10:39:34', '2021-03-14 19:14:11');


--
-- TOC entry 3024 (class 0 OID 16463)
-- Dependencies: 207
-- Data for Name: teacher; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.teacher VALUES (1, '黄老师', 30, '2017-08-21 09:03:28');
INSERT INTO public.teacher VALUES (2, '何老师', 28, '2017-08-21 16:49:56');


--
-- TOC entry 3041 (class 0 OID 0)
-- Dependencies: 203
-- Name: student_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.student_id_seq', 1, false);


--
-- TOC entry 3042 (class 0 OID 0)
-- Dependencies: 206
-- Name: teacher_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.teacher_id_seq', 1, false);


--
-- TOC entry 2878 (class 2606 OID 16436)
-- Name: class pk_class; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.class
    ADD CONSTRAINT pk_class PRIMARY KEY (id);


--
-- TOC entry 2880 (class 2606 OID 16441)
-- Name: class_teacher_rel pk_class_teacher_rel; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.class_teacher_rel
    ADD CONSTRAINT pk_class_teacher_rel PRIMARY KEY (id);


--
-- TOC entry 2882 (class 2606 OID 16453)
-- Name: student pk_student; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.student
    ADD CONSTRAINT pk_student PRIMARY KEY (id);


--
-- TOC entry 2884 (class 2606 OID 16460)
-- Name: student_profile pk_student_profile; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.student_profile
    ADD CONSTRAINT pk_student_profile PRIMARY KEY (id);


--
-- TOC entry 2886 (class 2606 OID 16468)
-- Name: teacher pk_teacher; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.teacher
    ADD CONSTRAINT pk_teacher PRIMARY KEY (id);


-- Completed on 2021-04-05 11:02:36

--
-- PostgreSQL database dump complete
--

