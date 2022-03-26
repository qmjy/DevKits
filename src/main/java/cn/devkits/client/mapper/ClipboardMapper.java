/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import cn.devkits.client.dto.ClipboardModel;

@Mapper
@Repository
public interface ClipboardMapper {
    @Insert({"insert into devkits_clipboard_history(content, type, createTime) values( #{content}, #{type}, #{createTime})"})
    int insert(ClipboardModel model);

    /**
     * 就近分页查询
     *
     * @param page     页数
     * @param pageSize 每一页的数目
     * @return 查询到的结果
     */
    @Select({"SELECT * FROM devkits_clipboard_history ORDER BY id DESC LIMIT #{pageSize} OFFSET (#{page}-1) * #{pageSize}"})
    List<ClipboardModel> paging(@Param("page") int page, @Param("pageSize") int pageSize);
}
