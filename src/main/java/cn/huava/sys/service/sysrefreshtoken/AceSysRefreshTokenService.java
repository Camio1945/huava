package cn.huava.sys.service.sysrefreshtoken;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.SysRefreshTokenMapper;
import cn.huava.sys.pojo.po.SysRefreshTokenPo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@AllArgsConstructor
public class AceSysRefreshTokenService
    extends BaseService<SysRefreshTokenMapper, SysRefreshTokenPo> {
  public void saveRefreshToken(@NonNull Long sysUserId, @NonNull String refreshToken) {
    SysRefreshTokenPo po =
        new SysRefreshTokenPo().setRefreshToken(refreshToken).setSysUserId(sysUserId);
    BasePo.beforeCreate(po);
    save(po);
  }

  public SysRefreshTokenPo getByRefreshToken(@NonNull String refreshToken) {
    Wrapper<SysRefreshTokenPo> wrapper =
        new LambdaQueryWrapper<SysRefreshTokenPo>()
            .eq(SysRefreshTokenPo::getRefreshToken, refreshToken);
    return getOne(wrapper);
  }
}
