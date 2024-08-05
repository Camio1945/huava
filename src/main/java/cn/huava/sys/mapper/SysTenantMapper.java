package cn.huava.sys.mapper;

import cn.huava.sys.pojo.po.SysTenant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 租户数据库操作
 *
 * @author Camio1945
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenant> {

  /**
   * 根据 ID 查询租户
   *
   * @param id 主键ID
   * @return 租户
   */
  @Select("""
      select `id`, `code`, `name`, `domain`, `contact_name`,
        `contact_number`, `expire_time`, `max_user`, `remark`, `delete_flag`
      from sys_tenant
      where id = #{id}
      """)
  SysTenant selectByIdTest(Long id);

  /**
   * 根据 ID 查询租户
   *
   * @param id 主键ID
   * @return 租户
   */
  @Select("select count(1) from user")
  SysTenant test(Long id);
}
