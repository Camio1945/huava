package cn.huava.sys.service.role;

import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.RoleMapper;
import cn.huava.sys.pojo.po.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

  protected PageDto<RolePo> rolePage(@NonNull final PageQo pageQo, @NonNull final RolePo params) {
    Page<RolePo> page = Fn.buildPage(pageQo);
    Wrapper<RolePo> wrapper =
        Fn.buildUndeletedWrapper(RolePo::getDeleteInfo)
            .like(Fn.isNotBlank(params.getName()), RolePo::getName, params.getName())
            .like(
                Fn.isNotBlank(params.getDescription()),
                RolePo::getDescription,
                params.getDescription())
            .orderByDesc(RolePo::getId);
    page = page(page, wrapper);
    return new PageDto<>(page.getRecords(), page.getTotal());
  }
}
