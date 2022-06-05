package com.meeting.login_and_register.mapper;

import com.meeting.common.entity.Code;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserCodeMapper {

    /**
     * 根据用户id和类型返回code对象
     * @param id 用户id
     * @param type code类型
     * @return code对象
     */
    Code findCodeByUserIdAndType(@Param("user_id") Long id, @Param("type") int type);

    /**
     * 添加用户code
     * @param code code对象
     * @return 被影响的行数
     */
    Integer insertCode(@Param("code") Code code);

    /**
     * 更新code
     * @param code code对象
     * @return 被影响的行数
     */
    Integer updateCode(@Param("code") Code code);

}
