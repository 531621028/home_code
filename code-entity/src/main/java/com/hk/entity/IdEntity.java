package com.hk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * Created by kang on 2019/1/10.
 */
public class IdEntity implements Serializable{
    private static final long serialVersionUID = 3112305838240927983L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static final String ID = "id";
}
