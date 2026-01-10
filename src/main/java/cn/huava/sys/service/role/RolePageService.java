package cn.huava.sys.service.role;

import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.RoleMapper;
import cn.huava.sys.pojo.po.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class RolePageService extends BaseService<RoleMapper, RolePo> {

  protected PageDto<RolePo> rolePage(@NonNull PageQo<RolePo> pageQo, @NonNull final RolePo params) {
    String desc = params.getDescription();
    Wrapper<RolePo> wrapper =
        Fn.undeletedWrapper(RolePo::getDeleteInfo)
            .like(Fn.isNotBlank(params.getName()), RolePo::getName, params.getName())
            .like(Fn.isNotBlank(desc), RolePo::getDescription, desc)
            .orderByAsc(RolePo::getSort);
    pageQo = page(pageQo, wrapper);
    return new PageDto<>(pageQo.getRecords(), pageQo.getTotal());
  }
}
