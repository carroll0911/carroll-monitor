package com.carroll.monitor.analyzer.controller.v2;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.carroll.monitor.analyzer.dto.CurrentMonitorDto;
import com.carroll.monitor.analyzer.dto.UserCacheDto;
import com.carroll.monitor.analyzer.enums.Role;
import com.carroll.monitor.analyzer.model.Project;
import com.carroll.monitor.analyzer.request.CurrentMonitorRequest;
import com.carroll.monitor.analyzer.response.CurrentMonitorResponse;
import com.carroll.monitor.analyzer.service.IProjectService;
import com.carroll.monitor.analyzer.service.IWarningDataService;
import com.carroll.monitor.analyzer.utils.BizContext;
import com.carroll.spring.rest.starter.BaseController;
import com.carroll.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.*;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
@Api(value = "GrafanaControllerV2", description = "grafana监控数据接口")
@RestController
@RequestMapping("/v2/grafana")
public class GrafanaControllerV2 extends BaseController {

    @Autowired
    private IWarningDataService warningDataService;

    private JSONArray colums;
    @Autowired
    private IProjectService projectService;

    @ApiOperation(value = "search")
    @RequestMapping(value = "/{tag}/search", method = RequestMethod.POST)
    public List<String> refresh(@PathVariable(name = "tag") String tag) {
        return Arrays.asList("REAL_TIME", "REAL_SUM");
    }

    @ApiOperation(value = "root")
    @RequestMapping(value = "/{tag}", method = RequestMethod.GET)
    public List<String> root(@PathVariable(name = "tag") String tag) {
        return Arrays.asList("REAL_TIME", "REAL_SUM");
    }

    @ApiOperation(value = "query")
    @RequestMapping(value = "/{tag}/query", method = RequestMethod.POST)
    public List query(@PathVariable(name = "tag") String tag, @Valid @ModelAttribute CurrentMonitorRequest request, BindingResult result) {
        UserCacheDto cacheDto = new UserCacheDto();
        cacheDto.setRole(Role.SUPPER);
        BizContext.setData(BizContext.MONITOR_USER_CACHE, cacheDto);
        Project project = projectService.getByTag(tag);
        CurrentMonitorRequest rq = new CurrentMonitorRequest();
        BeanUtils.copyPropertiesIgnorException(request, rq);
        if (project != null) {
            rq.setProjectId(project.getId());
        }
        rq.setProjectTag(tag);
        rq.setCurPage(0);
        rq.setPageSize(30);
        CurrentMonitorResponse response = warningDataService.currentMonitor(rq);
        Map resultMap = new HashMap();
        resultMap.put("columns", colums);
        List<List<Object>> data = new ArrayList<>();
        for (CurrentMonitorDto dto : response.getList()) {
            data.add(Arrays.asList(dto.getName(), dto.getLevel(), String.format("%s%s", StringUtils.isEmpty(dto.getApplicationName()) ? "" : dto.getApplicationName(),
                    StringUtils.isEmpty(dto.getHost()) ? "" : String.format("(%s)", dto.getHost())), dto.getTarget(),
                    dto.getLatestTime(), dto.getTimes()));
        }
        resultMap.put("rows", data);
        resultMap.put("type", "table");
        return Arrays.asList(resultMap);
    }

    @PostConstruct
    @SuppressWarnings("unused")
    private void init() {
        String columnsStr = "[{\"text\": \"监控项\",\"type\": \"string\" },{\"text\": \"级别\",\"type\": \"string\" }," +
                "{\"text\": \"告警源\",\"type\": \"string\" },{\"text\": \"告警对象\",\"type\": \"string\" },{\"text\": \"更新时间\",\"type\": \"time\" }," +
                "{\"text\": \"次数\",\"type\": \"number\" }]";
        this.colums = JSONObject.parseArray(columnsStr);
    }
}
