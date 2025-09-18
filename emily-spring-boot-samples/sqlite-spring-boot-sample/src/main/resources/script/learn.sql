--删除数据库表
DROP TABLE IF EXISTS STUDENT;
--创建数据库表
CREATE TABLE STUDENT(
                        ID INTEGER PRIMARY KEY NOT NULL, --主键
                        NAME TEXT NOT NULL, --姓名
                        HEIGHT NUMERIC NOT NULL DEFAULT 0, --身高
                        CREDIT REAL DEFAULT 0, --分数
                        ADDRESS BLOB, --地址
                        OPENING INTEGER CHECK(OPENING IN (0,1)), --是否开学
                        INSERT_DATE TEXT NOT NULL, --入库时间
                        UPDATE_DATE TEXT NOT NULL --更新时间
);
--查询数据
SELECT * FROM STUDENT s where s.ID =3;
--更新数据
UPDATE STUDENT SET NAME ='老九-3' WHERE ID =3;
-- 插入数据库表
INSERT INTO STUDENT (NAME,HEIGHT,CREDIT,ADDRESS,OPENING,INSERT_DATE,UPDATE_DATE) VALUES('叶子农',185,98.6,'圣保罗大街6号',TRUE,datetime('now','localtime'),datetime('now','localtime'));
INSERT INTO STUDENT (NAME,HEIGHT,CREDIT,ADDRESS,OPENING,INSERT_DATE,UPDATE_DATE) VALUES('林雪红',175,86.6,'圣保罗大街36号',FALSE,datetime('now','localtime'),datetime('now','localtime'));
INSERT INTO STUDENT (NAME,HEIGHT,CREDIT,ADDRESS,OPENING,INSERT_DATE,UPDATE_DATE) VALUES('老九',195,89.8,'美国纽约饭王',TRUE,datetime('now','localtime'),datetime('now','localtime'));
INSERT INTO STUDENT (NAME,HEIGHT,CREDIT,ADDRESS,OPENING,INSERT_DATE,UPDATE_DATE) VALUES('罗家明',192,79.8,'苏联莫斯科',FALSE,datetime('now','localtime'),datetime('now','localtime'));
INSERT INTO STUDENT (NAME,HEIGHT,CREDIT,ADDRESS,OPENING,INSERT_DATE,UPDATE_DATE) VALUES('戴梦妍',192,99.8,'香港中环',FALSE,datetime('now','localtime'),datetime('now','localtime'));

--查询数据库范围内的所有索引
SELECT * FROM sqlite_master WHERE type = 'index' and tbl_name='STUDENT';
--创建复合索引
CREATE INDEX STUDENT_PK_NH ON STUDENT (NAME,HEIGHT);
--创建唯一索引
CREATE UNIQUE INDEX STUDENT_PK_N ON STUDENT (NAME);
--删除索引
DROP INDEX STUDENT_PK_NH;
DROP INDEX STUDENT_PK_N;

SELECT strftime('%s','now');