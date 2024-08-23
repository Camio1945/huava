package cn.huava.sys.service.refreshtoken;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.RefreshTokenMapper;
import cn.huava.sys.pojo.po.RefreshTokenPo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
public class AceRefreshTokenService extends BaseService<RefreshTokenMapper, RefreshTokenPo> {
  public void saveRefreshToken(@NonNull Long sysUserId, @NonNull String refreshToken) {
    RefreshTokenPo po = new RefreshTokenPo().setRefreshToken(refreshToken).setSysUserId(sysUserId);
    BasePo.beforeCreate(po);
    save(po);
  }

  public RefreshTokenPo getByRefreshToken(@NonNull String refreshToken) {
    Wrapper<RefreshTokenPo> wrapper =
        Fn.buildUndeletedWrapper(RefreshTokenPo::getDeleteInfo)
            .eq(RefreshTokenPo::getRefreshToken, refreshToken);
    return getOne(wrapper);
  }
}
