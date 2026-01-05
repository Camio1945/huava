package cn.huava.sys.mapper;

import cn.huava.sys.pojo.po.UserRolePo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.NonNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户所拥有的角色 Mapper 持久化层
 *
 * @author Camio1945
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRolePo> {

  /**
   * 根据角色 id 查询用户数量
   *
   * @param roleId 角色 id
   * @return
   */
  @Select(
      """
      select count(1) from sys_user_role as ur, sys_user as u
      where ur.role_id = u.id
      and ur.role_id = #{roleId}
      and u.delete_info = 0
      """)
  long countUserByRoleId(@NonNull Long roleId);
}
