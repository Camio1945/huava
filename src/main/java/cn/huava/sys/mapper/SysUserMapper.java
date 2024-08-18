package cn.huava.sys.mapper;

import cn.huava.sys.pojo.po.SysUserPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Camio1945
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserPo> {
}
