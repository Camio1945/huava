package cn.huava.sys.mapper;

import cn.huava.sys.pojo.po.RefreshTokenPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 刷新 token Mapper 持久化层
 *
 * @author Camio1945
 */
@Mapper
public interface RefreshTokenMapper extends BaseMapper<RefreshTokenPo> {}
