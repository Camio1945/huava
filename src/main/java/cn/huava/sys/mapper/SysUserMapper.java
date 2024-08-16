package cn.huava.sys.mapper;

import cn.huava.sys.pojo.po.SysUserPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台用户数据操作
 *
 * @author Camio1945
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserPo> {
}
