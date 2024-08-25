package cn.huava.sys.service.user;

import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.UserDto;
import cn.huava.sys.pojo.po.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class UserPageService extends BaseService<UserMapper, UserExtPo> {

  protected PageDto<UserDto> userPage(@NonNull PageQo<UserExtPo> pageQo, @NonNull final UserExtPo params) {
    Wrapper<UserExtPo> wrapper =
        Fn.buildUndeletedWrapper(UserExtPo::getDeleteInfo)
            .eq(Fn.isNotBlank(params.getUsername()), UserExtPo::getUsername, params.getUsername())
            .like(Fn.isNotBlank(params.getRealName()), UserExtPo::getRealName, params.getRealName())
            .eq(
                Fn.isNotBlank(params.getPhoneNumber()),
                UserExtPo::getPhoneNumber,
                params.getPhoneNumber())
            .orderByDesc(UserExtPo::getId);
    pageQo = page(pageQo, wrapper);
    List<UserDto> list = pageQo.getRecords().stream().map(UserDto::new).toList();
    return new PageDto<>(list, pageQo.getTotal());
  }
}
