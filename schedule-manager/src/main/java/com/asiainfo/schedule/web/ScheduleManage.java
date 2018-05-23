package com.asiainfo.schedule.web;

import com.asiainfo.schedule.ConsoleManager;
import com.asiainfo.schedule.core.TaskDefine;
import com.asiainfo.schedule.taskTemplate.JdbcToESTask;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liulh
 * @decription 提供任务调度外部调用接口
 * @create 2018-05-22 9:43
 **/
@RestController
@RequestMapping("/api")
public class ScheduleManage {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleManage.class);

    /**
     * @Author: liulh
     * @Description: 解析获取到的参数
     * @Date: 2018/5/22 14:31
     * @Modified By:
     */
    public TaskDefine getParameter (HttpServletRequest request) {
        String taskDefineName = request.getParameter("taskName");

        if (StringUtils.isEmpty(taskDefineName)) {
            return null;
        }

        TaskDefine taskDefine = new TaskDefine();
        //默认bean名称
        taskDefine.setTargetBean(JdbcToESTask.BEAN_NAME);
        //默认方法名称
        taskDefine.setTargetMethod(JdbcToESTask.METHOD_NAME);
        //定时任务名称
        taskDefine.setTaskDefineName(taskDefineName);
        //默认定时任务类型
        taskDefine.setType(TaskDefine.TYPE_UNCODE_SINGLE_TASK);

        return taskDefine;
    }

    /**
     * @Author: liulh
     * @Description: 批量判断是否为空
     * @Date: 2018/5/22 15:07
     * @Modified By:
     */
    public boolean isNotEmptyBatch(String... strs){
        for (String str : strs) {
            if(StringUtils.isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @Author: liulh
     * @Description: 新增sink-es任务
     * @Date: 2018/5/22 10:47
     * @Modified By:
     */
    @RequestMapping(value = "/saveTask", method = { RequestMethod.POST, RequestMethod.GET })
    public @ResponseBody String saveTask (HttpServletRequest request) {

        String cronExpression = request.getParameter("taskCron");
        String dbUrl = request.getParameter("dbUrl");
        String dbName = request.getParameter("dbName");
        String dbPasswd = request.getParameter("dbPasswd");
        String dbType = request.getParameter("dbType");
        String sql = request.getParameter("sql");
        String cols = request.getParameter("cols");

        TaskDefine taskDefine = getParameter(request);

        if (taskDefine == null || !isNotEmptyBatch(cronExpression, dbUrl, dbName, dbPasswd, dbType, sql, cols)) {
            return showResultInfo("fail", "配置信息不全，请检查！");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("url", dbUrl);
        param.put("user", dbName);
        param.put("password", dbPasswd);
        param.put("sql", sql);
        param.put("cols", cols);
        taskDefine.setParams(new Gson().toJson(param));

        //新增任务默认不启动定时
        taskDefine.setStatus(TaskDefine.STATUS_STOP);

        if(StringUtils.isNotEmpty(cronExpression) || null != taskDefine.getStartTime()){
            boolean isUpdate = false;
            try {
                if (ConsoleManager.queryScheduleTask(taskDefine) != null
                        || ConsoleManager.queryScheduleTask(taskDefine).getTaskDefineName() != null) {
                    isUpdate = true;
                }
            } catch (Exception e) {
                LOG.error("获取任务异常！", e);
                return showResultInfo("fail", "修改或新增任务失败！");
            }

            try {
                if (isUpdate) {
                    ConsoleManager.updateScheduleTask(taskDefine);
                    return showResultInfo("success", "修改任务成功！");
                } else {
                    ConsoleManager.addScheduleTask(taskDefine);
                    return showResultInfo("success", "创建任务成功！");
                }
            } catch (Exception e) {
                LOG.error("创建任务异常！", e);
                return showResultInfo("fail", "创建或修改任务失败！");
            }
        } else {
            return showResultInfo("fail", "定时信息不能为空！");
        }

    }

    /**
     * @Author: liulh
     * @Description: 启动定时任务
     * @Date: 2018/5/22 11:02
     * @Modified By:
     */
    @RequestMapping(value = "/startTask", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String startTask (HttpServletRequest request) {
        TaskDefine taskDefine = getParameter(request);
        if (taskDefine == null) {
            return showResultInfo("fail", "未接收到任务名称！");
        }

        //修改状态，启动定时任务
        taskDefine.setStatus(TaskDefine.STATUS_RUNNING);

        try {
            ConsoleManager.updateScheduleTask(taskDefine);
        } catch (Exception e) {
            LOG.error("启动定时任务异常！", e);
            return showResultInfo("fail", "启动定时任务失败！");
        }

        return showResultInfo("success", "启动定时任务成功！");
    }

    /**
     * @Author: liulh
     * @Description: 停止定时任务
     * @Date: 2018/5/22 11:13
     * @Modified By:
     */
    @RequestMapping(value = "/stopTask", method = { RequestMethod.POST, RequestMethod.GET })
    public @ResponseBody String stopTask (HttpServletRequest request) {
        TaskDefine taskDefine = getParameter(request);
        if (taskDefine == null) {
            return showResultInfo("fail", "未接收到任务名称！");
        }
        //修改状态，启动定时任务
        taskDefine.setStatus(TaskDefine.STATUS_STOP);

        try {
            ConsoleManager.updateScheduleTask(taskDefine);
        } catch (Exception e) {
            LOG.error("停止定时任务异常！", e);
            return showResultInfo("fail", "停止定时任务失败！");
        }

        return showResultInfo("success", "停止定时任务成功！");
    }

    /**
     * @Author: liulh
     * @Description: 删除定时任务
     * @Date: 2018/5/22 11:14
     * @Modified By:
     */
    @RequestMapping(value = "/delTask", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String delTask (HttpServletRequest request) {
        TaskDefine taskDefine = getParameter(request);
        if (taskDefine == null) {
            return showResultInfo("fail", "未接收到任务名称！");
        }

        try {
            ConsoleManager.delScheduleTask(taskDefine);
        } catch (Exception e) {
            LOG.error("删除定时任务异常！", e);
            return showResultInfo("fail", "删除定时任务失败！");
        }

        return showResultInfo("success", "删除定时任务成功！");
    }

    @RequestMapping(value = "/selectTask", method = { RequestMethod.POST, RequestMethod.GET })
    public @ResponseBody String selectTask (HttpServletRequest request) {
        TaskDefine taskDefine = getParameter(request);
        if (taskDefine == null) {
            try {
                List<TaskDefine> taskDefines = ConsoleManager.queryScheduleTask();
                return new Gson().toJson(taskDefines);
            } catch (Exception e) {
                LOG.error("查询定时任务异常！", e);
                return showResultInfo("fail",  "查询定时任务失败！");
            }
        } else {
            try {
                taskDefine = ConsoleManager.queryScheduleTask(taskDefine);
            } catch (Exception e) {
                LOG.error("查询定时任务异常！", e);
                if (e.getMessage().toUpperCase().contains("NONODE")) {
                    return showResultInfo("fail", "无对应定时任务！");
                } else {
                    return showResultInfo("fail", "查询定时任务失败！");
                }
            }
            return new Gson().toJson(taskDefine);
        }
    }

    /**
     * @Author: liulh
     * @Description: 输出返回信息
     * @Date: 2018/5/22 10:59
     * @Modified By:
     */
    public String showResultInfo (String status, String message) {
        Map<String, String> result = new HashMap<>();
        result.put("result", status);
        result.put("message", message);
        return new Gson().toJson(result);
    }
}
