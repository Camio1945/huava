package cn.huava.sys.service.sysrefreshtoken;

import cn.huava.sys.mapper.SysRefreshTokenMapper;
import cn.huava.sys.pojo.po.SysRefreshTokenPo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Date;
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
public class SysRefreshTokenAceService
    extends ServiceImpl<SysRefreshTokenMapper, SysRefreshTokenPo> {
  public void saveRefreshToken(@NonNull Long sysUserId, @NonNull String refreshToken) {
    Date date = new Date();
    SysRefreshTokenPo po =
        new SysRefreshTokenPo()
            .setRefreshToken(refreshToken)
            .setSysUserId(sysUserId)
            .setCreateTime(date)
            .setUpdateTime(date)
            .setDeleteInfo(0L);
    save(po);
  }

  public SysRefreshTokenPo getByRefreshToken(@NonNull String refreshToken) {
    Wrapper<SysRefreshTokenPo> wrapper =
        new LambdaQueryWrapper<SysRefreshTokenPo>()
            .eq(SysRefreshTokenPo::getRefreshToken, refreshToken);
    return getOne(wrapper);
  }
}
