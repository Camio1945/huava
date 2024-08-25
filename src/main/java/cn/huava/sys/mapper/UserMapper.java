package cn.huava.sys.mapper;

import cn.huava.sys.pojo.po.UserExtPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Camio1945
 */
@Mapper
public interface UserMapper extends BaseMapper<UserExtPo> {}
