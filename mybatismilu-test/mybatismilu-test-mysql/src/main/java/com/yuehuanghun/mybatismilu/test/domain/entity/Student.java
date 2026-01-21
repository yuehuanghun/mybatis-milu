package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.yuehuanghun.mybatis.milu.annotation.AttributeOptions;
import com.yuehuanghun.mybatis.milu.annotation.EntityOptions.FetchRef;
import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery;
import com.yuehuanghun.mybatis.milu.annotation.ExampleQuery.MatchType;
import com.yuehuanghun.mybatis.milu.annotation.Filler;
import com.yuehuanghun.mybatis.milu.annotation.Filler.FillMode;
import com.yuehuanghun.mybatis.milu.annotation.FuncColumn;
import com.yuehuanghun.mybatis.milu.annotation.LogicDelete;
import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;
import com.yuehuanghun.mybatis.milu.pagehelper.PageRequest;
import com.yuehuanghun.mybatismilu.test.config.JsonTypeHandler;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@FetchRef(group = "class", refAttrs = "classs")
public class Student extends PageRequest {

	@SnowflakeId
//	@Id
//	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tableSequence")
//	@TableGenerator(name = "tableSequence", table = "sequence", valueColumnName = "current_seq", pkColumnName = "id", pkColumnValue = "1", allocationSize = 50)
	private Long id; //使用数据表模拟数字序列
	
	@AttributeOptions(filler = @Filler(fillOnInsert = true), exampleQuery = @ExampleQuery(startKeyName = "params.addTimeBegin", endKeyName = "params.addTimeEnd")) //当在插入数据时，如果该属性为null则自动填充值
	private LocalDateTime addTime;
	
	@AttributeOptions(filler = @Filler(fillOnInsert = true, fillOnUpdate = true, fillMode = FillMode.ANY)) //当在插入或更新数据时，如果该属性为null则自动填充值
	private Date updateTime;
	
	@AttributeOptions(exampleQuery = @ExampleQuery(matchType = MatchType.CONTAIN, inKeyName = "params.nameIn")) //使用findByExample方法时name不为空时，即执行name LIKE %nameValue%
	private String name;
	
	private Integer age;
	
	@FuncColumn(expression = "AES_DECRYPT(FROM_BASE64(${idNo}),'123456')", upsertExp = "TO_BASE64(AES_ENCRYPT(${value},'123456'))")
	private String idNo;
	
	private Long classId;
	
	@AttributeOptions(typeHandler = JsonTypeHandler.class)
	private Map<String, Object> parents;
	
	@AttributeOptions(logicDelete = @LogicDelete)
	private Boolean isDeleted;
	
	@ManyToOne
	@JoinColumn(name = "class_id", referencedColumnName = "id")
	private Classs classs; //对多一引用
	
	@OneToOne(mappedBy = "student")
	private StudentProfile studentProfile; //一对一引用
	
	@FuncColumn(expression = "${parents} ->> '$.fatherName'")
	private String fatherName;
	
	@Transient
	private Map<String, Object> params;
	
	public Map<String, Object> getParams() {
		return params == null ? (params = new HashMap<>()) : params;
	}
}
