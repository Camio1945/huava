package cn.huava.sys.service.refreshtoken;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.RefreshTokenMapper;
import cn.huava.sys.pojo.po.RefreshTokenPo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

/**
 * 刷新 token 服务主入口类<br>
 *
 * @author Camio1945
 */
@Slf4j
@Service
@NullMarked
@AllArgsConstructor
public class AceRefreshTokenService extends BaseService<RefreshTokenMapper, RefreshTokenPo> {
  public void saveRefreshToken(Long sysUserId, String refreshToken) {
    RefreshTokenPo po = new RefreshTokenPo().setRefreshToken(refreshToken).setSysUserId(sysUserId);
    po.setCreatedBy(sysUserId).setUpdatedBy(sysUserId);
    BasePo.beforeCreate(po);
    save(po);
  }

  public RefreshTokenPo getByRefreshToken(String refreshToken) {
    Wrapper<RefreshTokenPo> wrapper =
        Fn.undeletedWrapper(RefreshTokenPo::getDeleteInfo)
            .eq(RefreshTokenPo::getRefreshToken, refreshToken);
    return getOne(wrapper);
  }
}
