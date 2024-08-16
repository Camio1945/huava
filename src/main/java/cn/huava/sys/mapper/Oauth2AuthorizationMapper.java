package cn.huava.sys.mapper;

import cn.huava.sys.pojo.po.Oauth2AuthorizationPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台用户数据操作
 *
 * @author Camio1945
 */
@Mapper
public interface Oauth2AuthorizationMapper extends BaseMapper<Oauth2AuthorizationPo> {
}
