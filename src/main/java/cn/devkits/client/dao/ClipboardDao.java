package cn.devkits.client.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.devkits.client.model.ClipboardModel;

@Mapper
@Repository
public interface ClipboardDao {
    @Insert({"insert into devkits_clipboard_history(content, type, createTime) values( #{content}, #{type}, #{createTime})"})
    int insert(ClipboardModel model);
}
