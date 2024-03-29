package com.info.schedule.web;

import com.info.schedule.ConsoleManager;
import com.info.schedule.core.TaskDefine;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@WebServlet(name="schedule",urlPatterns="/uncode/schedule")
public class ManagerServlet extends HttpServlet{

	/**
	 *
	 */
	private static final long serialVersionUID = 8160082230341182715L;

	private static final String UNCODE_SESSION_KEY = "uncode_key_session";

	private static final String HEAD =
		    "<!DOCTYPE html>\n"+
		    "<html>\n"+
		    "<head>\n"+
		    "<meta charset=\"utf-8\"/>\n"+
		    "\t  <title>Uncode-Schedule管理</title>\n"+
		    "\t  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"+
		    "\t  <meta name=\"viewport\" content=\"width=device-width\"/>\n"+
		    "\t  <meta name=\"keywords\" content=\"uncode,冶卫军\"/>\n"+
		    "\t  <meta name=\"description\" content=\"Uncode-Schedule管理\"/>\n"+
		    "\t  <link rel=\"stylesheet\"  href=\"http://cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css\">\n"+
		    "\t  <script type=\"text/javascript\" src=\"http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js\"></script>\n"+
		    "\t  <script type=\"text/javascript\" src=\"http://cdn.bootcss.com/bootstrap/3.3.4/js/bootstrap.min.js\"></script>\n"+
			"</head>\n";

	private static final String SCRIPT =
			"\t	<script type=\"text/javascript\">\n"+
			"\t		$(document).ready(function(){\n"+
			"\t			$(\"#myModal\").on('show.bs.modal', function(event){\n"+
			"\t		    var button = $(event.relatedTarget); \n"+
			"\t			var titleData = button.data('title'); \n"+
			"\t		    var modal = $(this)\n"+
			"\t	       	modal.find('.modal-title').text(titleData + '定时任务');\n"+
			"\t	  		});\n"+
			"\t		});\n"+
			"\t		function formSubmit(){\n"+
			"\t			document.getElementById(\"addform\").submit();\n"+
			"\t		}\n"+
			"\t	</script>";
	private static final String SCRIPT_LOGIN =
			"\t	<script type=\"text/javascript\">\n"+
			"\t		function loginSubmit(){\n"+
			"\t		    var accout = $(\"#account\").val(); \n"+
			"\t		    var password = $(\"#password\").val(); \n"+
			"\t		    if(accout == null || accout.length == 0){ \n"+
			"\t		    	alert(\"用户名不能为空\"); \n"+
			"\t		    	return; \n"+
			"\t		    	return; \n"+
			"\t		    } \n"+
			"\t		    if(password == null || password.length == 0){ \n"+
			"\t		    	alert(\"密码不能为空\"); \n"+
			"\t		    	return; \n"+
			"\t		    } \n"+
			"\t			document.getElementById(\"loginform\").submit();\n"+
			"\t		}\n"+
			"\t	</script>";

	private static final String PAGE_LOGIN_STYLE =
			"\t <style>	\n"+
			"\t ul{\n"+
			"\t 	list-style-type: none;\n"+
			"\t }\n"+
			"\t a, button {\n"+
			"\t 	cursor: pointer; \n"+
			"\t }\n"+
			"\t .loginContDiv{\n"+
			"\t 	width: 400px;\n"+
			"\t 	height: 500px;\n"+
			"\t 	background-color:  -#f0f0f0;\n"+
			"\t 	margin: 10%  auto;\n"+
			"\t 	text-align: left;\n"+
			"\t }\n"+
			"\t .loginContUl{\n"+
			"\t 	width :80%;\n"+
			"\t 	margin: 0px  auto ;\n"+
			"\t }\n"+
			"\t .loginContLi{\n"+
			"\t 	width :100%;\n"+
			"\t 	margin: 0px auto 20px;\n"+
			"\t }\n"+
			"\t .loginContLi div{\n"+
			"\t 	width :100%;\n"+
			"\t }\n"+
			"\t .loginContLi div input{\n"+
			"\t 	width:296px;\n"+
			"\t 	height:30px;\n"+
			"\t }\n"+
			"\t .txt{\n"+
			"\t 	font-size:16px;\n"+
			"\t 	padding:  5px 10px ;\n"+
			"\t }\n"+
			"\t .loginBtn{\n"+
			"\t 	width :100%;\n"+
			"\t 	height: 42px;\n"+
			"\t 	border: 0;\n"+
			"\t 	border-bottom-style: hidden;\n"+
			"\t 	background-color:#84AF00;	\n"+
			"\t }\n"+
			"\t .loginBtn span{\n"+
			"\t 	color:white;\n"+
			"\t 	font-size: 20px;\n"+
			"\t 	line-height: 40px;\n"+
			"\t 	letter-spacing: 14px;\n"+
			"\t }\n"+
			"\t </style>\n";

	private static final String PAGE_LOGIN_HTML_1 =
			"\t <body>\n"+
			"\t <script src='http://git.oschina.net/uncode/uncode-schedule/star_widget_preview'></script>"+
			"\t <div class=\"loginContDiv\">\n"+
			"\t 	<ul class=\"loginContUl\">\n"+
			"\t 		<li class=\"loginContLi\"> \n"+
			"\t 			<div style=\"text-align:center;height:90px;margin:0 auto;font-size: 250%;\">uncode-schedule</div>\n"+
			"\t 		</li>\n"+
			"\t 		<li class=\"loginContLi\"> \n"+
			"\t 			<div><form id=\"loginform\" method=\"post\" action=\"";


	private static final String PAGE_LOGIN_HTML_2 =
			"\">\n"+
			"\t 				<input  type=\"text\" name =\"account\" id=\"account\" placeholder=\"帐号(与zk一致)\" /><br/><br/>\n"+
			"\t 				<input  class=\"txt\" type=\"password\" name=\"password\" id=\"password\" placeholder=\"密码(与zk一致)\" /><br/><br/>\n"+
			"\t 				<button type=\"button\" style=\"width:100px;\" onclick=\"loginSubmit()\">登录</button>\t"+
			"\t 				&nbsp;&nbsp;&nbsp;<a target=\"_blank\" href=\"http://git.oschina.net/uncode\">使用帮助</a>"+
			"\t 			</div></form>\n"+
			"\t 		</li>\n"+
			"\t 	</ul>\n"+
			"\t </div>\n"+



			"\t </body>";


	private static final String PAGE =
			"\t <body>\n"+
			"\t <div class=\"container-fluid\">\n"+
			"\t 	<h1>Uncode-Schedule管理页面</h1>\n"+
			"\t     <a  target=\"_blank\" href=\"http://git.oschina.net/uncode/uncode-schedule\">【uncode-schedule】</a>\t\t"+
			"\t     <div class=\"navbar-right\">\n"+
			"\t     	<button type=\"button\" class=\"btn btn-primary\"  data-toggle=\"modal\" data-target=\"#myModal\" data-title=\"新增\">新增</button>\n"+
			"\t     </div>\n"+
			"\t     <div id=\"myModal\" class=\"modal fade\">\n"+
			"\t         <div class=\"modal-dialog\">\n"+
			"\t             <div class=\"modal-content\">\n"+
			"\t                 <div class=\"modal-header\">\n"+
			"\t                     <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n"+
			"\t                     <h4 class=\"modal-title\">Modal Window</h4>\n"+
			"\t                 </div>\n"+
			"\t                 <div class=\"modal-body\">\n"+
			"\t 					<div class=\"container\">\n"+
			"\t 						<form id=\"addform\" method=\"post\" action=\"%s\" class=\"form-horizontal\">\n"+
			"\t 						<div class=\"row\">\n"+
			"\t 							<div class=\"col-md-6\">\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"bean\">任务名称<span style=\"color:red\">(必填)</span></label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"taskName\" name=\"taskName\" type=\"text\" class=\"form-control\" placeholder=\"\" required>任务的名称\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"bean\">bean名称<span style=\"color:red\">(必填)</span></label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"bean\" name=\"bean\" type=\"text\" class=\"form-control\" placeholder=\"测试用simpleTask\" required>Spring bean的名称\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"before\">before方法名称</label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"before\" name=\"before\" type=\"text\" class=\"form-control\" placeholder=\"测试分布式任务用before\" required>分布式任务数据获取方法，返回值必须为List类型，单任务无该项。\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"method\">执行方法名称<span style=\"color:red\">(必填)</span></label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"method\" name=\"method\" type=\"text\" class=\"form-control\"  placeholder=\"测试单任务print1,分布式任务before\" required>任务执行方法，分布式任务时方法必须带String参数，单任务自定义。\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"after\">after方法名称</label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"after\" name=\"after\" type=\"text\" class=\"form-control\" placeholder=\"测试分布式任务用after\" required>分布式任务在所有节点目标方法执行完成后执行该方法\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"type\">类型<span style=\"color:red\">(必填)</span></label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t											    <select id=\"type\" name=\"type\" class=\"form-control\" required>\n"+
			"\t										        	<option value=\"uncode-single-task\">单个任务</option>\n"+
			"\t										        	<option value=\"uncode-multi-main-task\">分布式任务</option>\n"+
			"\t										        </select>\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"cronExpression\">cron表达式</label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"cronExpression\" name=\"cronExpression\" type=\"text\" class=\"form-control\" required>\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"threadNum\">线程数量</label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"threadNum\" name=\"threadNum\" type=\"text\" value=\"1\" class=\"form-control\" required>单任务必须为单线程\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"period\">时间间隔period(毫秒)</label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"period\" name=\"period\" type=\"text\" class=\"form-control\" required>两次任务启动时间之间的间隔时间,period与delay不能同时填\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t                                     <div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"delay\">时间间隔delay(毫秒)</label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"delay\" name=\"delay\" type=\"text\" class=\"form-control\" required>上次任务结束时间与下次任务开始时间之间的间隔时间,period与delay不能同时填\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"startTime\">开始时间</label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"startTime\" name=\"startTime\" type=\"text\" class=\"form-control\" placeholder=\"yyyy-MM-dd HH:mm:ss SSS\">未填时默认使用系统当前时间\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"param\">参数(字符串)</label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"param\" name=\"param\" type=\"text\" class=\"form-control\" required>该项只支持单任务，String类型参数一个，多参时建议用json串\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t 									<div class=\"form-group\">\n"+
			"\t 										<label class=\"col-sm-4 control-label\" for=\"param\">后缀</label>\n"+
			"\t 										<div class=\"col-sm-6\">\n"+
			"\t 											<input id=\"extKeySuffix\" name=\"extKeySuffix\" type=\"text\" class=\"form-control\" required>任务重复多次调用时选填\n"+
			"\t 										</div>\n"+
			"\t 									</div>\n"+
			"\t              		   				<div class=\"modal-footer\">\n"+
			"\t               		      				<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">取消</button>\n"+
			"\t               		      				<button type=\"button\" onclick=\"formSubmit()\" class=\"btn btn-primary\">保存</button>\n"+
			"\t             	    				</div>\n"+
			"\t                 				</div>\n"+
			"\t                 			</div>\n"+

			"\t 							</form>\n"+
			"\t         				</div>\n"+
			"\t         			</div>\n"+
			"\t         		</div>\n"+
			"\t     	</div>\n"+
		    "\t 	</div>\n"+
		    "\t </div>\n"+
			"\t <div class=\"container-fluid\">\n"+
			"\t 	<div class=\"row-fluid\">\n"+
			"\t 		<div class=\"span12\">\n"+
			"\t 			<h3>集群节点</h3>\n"+
			"\t 			<table class=\"table\">\n"+
			"\t 				<thead>\n"+
			"\t 					<tr>\n"+
			"\t 						<th width=\"100px\">序号</th>\n"+
			"\t 						<th>名称</th>\n"+
			"\t 						<th>调度节点</th>\n"+
			"\t 					</tr>\n"+
			"\t 				</thead>\n"+
			"\t 				<tbody>\n"+
			"\t 					%s \n"+
			"\t 				</tbody>\n"+
			"\t 			</table>\n"+
			"\t 		</div>\n"+
			"\t 		<div class=\"span12\">\n"+
			"\t 			<h3>定时任务列表</h3>\n"+
			"\t 			<table class=\"table\">\n"+
			"\t 				<thead>\n"+
			"\t 					<tr>\n"+
			"\t 						<th width=\"50\">序号</th>\n"+
			"\t 						<th width=\"50\">任务名称</th>\n"+
			"\t 						<th width=\"50\">参数</th>\n"+
			"\t 						<th width=\"50\">类型</th>\n"+
			"\t 						<th width=\"50\">线程数量</th>\n"+
			"\t 						<th width=\"50\">cron表达式</th>\n"+
			"\t 						<th width=\"50\">开始时间</th>\n"+
			"\t 						<th width=\"50\">period（秒）</th>\n"+
			"\t 						<th width=\"50\">delay（秒）</th>\n"+
			"\t 						<th width=\"50\">执行节点</th>\n"+
			"\t 						<th width=\"50\">运行状态</th>\n"+
			"\t 						<th width=\"50\">执行次数</th>\n"+
			"\t 						<th width=\"50\">执行信息</th>\n"+
			"\t 						<th width=\"50\">最近执行时间</th>\n"+
			"\t 						<th width=\"50\">操作</th>\n"+
			"\t 					</tr>\n"+
			"\t 				</thead>\n"+
			"\t 				<tbody>\n"+
			"\t 					%s\n "+
			"\t 				</tbody>\n"+
			"\t 			</table>\n"+
			"\t 		</div>\n"+
			"\t 	</div>\n"+
			"\t </div>\n"+
			"\t </body>";


	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String login = (String) request.getSession().getAttribute(UNCODE_SESSION_KEY);
		if(StringUtils.isBlank(login)){
			String account = request.getParameter("account");
			String password = request.getParameter("password");
			boolean avilb = false;
			try {
				avilb = ConsoleManager.getScheduleManager().checkAdminUser(account, password);
				if(avilb){
					request.getSession().setAttribute(UNCODE_SESSION_KEY, "uncode_login_success");
					response.sendRedirect(request.getSession().getServletContext().getContextPath()+"/uncode/schedule");
				}
			} catch (Exception e) {
			}
			response.setContentType("text/html;charset=UTF-8");
	        PrintWriter out = response.getWriter();
			out.write(HEAD);
			out.write(SCRIPT_LOGIN);
			out.write(PAGE_LOGIN_STYLE);
			out.write(PAGE_LOGIN_HTML_1 + request.getSession().getServletContext().getContextPath()+"/uncode/schedule" + PAGE_LOGIN_HTML_2);
/*			if(!avilb){
				out.write("\t <div>\n");
				out.write("\t 用户名或密码错误!\n");
				out.write("\t </div>\n");
			}
*/
		}else{
			String del = request.getParameter("del");
			String start = request.getParameter("start");
			String stop = request.getParameter("stop");
			String taskDefineName = request.getParameter("taskName");
			String bean = request.getParameter("bean");
			String method = request.getParameter("method");
			if(StringUtils.isNotEmpty(del)){
				TaskDefine taskDefine = new TaskDefine();
				String[] dels = del.split("_");
				taskDefine.setTargetBean(dels[0]);
				taskDefine.setTargetMethod(dels[1]);
				taskDefine.setTaskDefineName(dels[2]);
				if(dels.length > 3){
					taskDefine.setExtKeySuffix(dels[3]);
				}
				try {
					ConsoleManager.delScheduleTask(taskDefine);
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.sendRedirect(request.getSession().getServletContext().getContextPath()+"/uncode/schedule");
			}else if(StringUtils.isNotEmpty(start)){
				TaskDefine taskDefine = new TaskDefine();
				String[] dels = start.split("_");
				taskDefine.setTargetBean(dels[0]);
				taskDefine.setTargetMethod(dels[1]);
				taskDefine.setTaskDefineName(dels[2]);
				if(dels.length > 3){
					taskDefine.setExtKeySuffix(dels[3]);
				}
				taskDefine.setStatus(TaskDefine.STATUS_RUNNING);
				try {
					ConsoleManager.updateScheduleTask(taskDefine);
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.sendRedirect(request.getSession().getServletContext().getContextPath()+"/uncode/schedule");
			}else if(StringUtils.isNotEmpty(stop)){
				TaskDefine taskDefine = new TaskDefine();
				String[] dels = stop.split("_");
				taskDefine.setTargetBean(dels[0]);
				taskDefine.setTargetMethod(dels[1]);
				taskDefine.setTaskDefineName(dels[2]);
				if(dels.length > 3){
					taskDefine.setExtKeySuffix(dels[3]);
				}
				taskDefine.setStatus(TaskDefine.STATUS_STOP);
				try {
					ConsoleManager.updateScheduleTask(taskDefine);
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.sendRedirect(request.getSession().getServletContext().getContextPath()+"/uncode/schedule");
			}else if(StringUtils.isNotEmpty(bean) && StringUtils.isNotEmpty(method) && StringUtils.isNotEmpty(taskDefineName)){
				TaskDefine taskDefine = new TaskDefine();
				taskDefine.setTargetBean(bean);
				taskDefine.setTargetMethod(method);
				taskDefine.setTaskDefineName(taskDefineName);
				String before = request.getParameter("before");
				if(StringUtils.isNotBlank(before)){
					taskDefine.setBeforeMethod(before);
				}
				String after = request.getParameter("after");
				if(StringUtils.isNotBlank(after)){
					taskDefine.setAfterMethod(after);
				}
				String threadNum = request.getParameter("threadNum");
				int num = 0;
				if(StringUtils.isNotBlank(threadNum)){
					num = Integer.parseInt(threadNum);
					taskDefine.setThreadNum(num);
				}
				String type = request.getParameter("type");
				if(StringUtils.isNotBlank(type)){
					if(TaskDefine.TYPE_UNCODE_MULTI_MAIN_TASK.equals(type)){
						taskDefine.setType(TaskDefine.TYPE_UNCODE_MULTI_MAIN_TASK);
					}else if(TaskDefine.TYPE_UNCODE_SINGLE_TASK.equals(type)){
						taskDefine.setType(TaskDefine.TYPE_UNCODE_SINGLE_TASK);
					}
				}else{
					if(num > 1){
						taskDefine.setType(TaskDefine.TYPE_UNCODE_MULTI_MAIN_TASK);
					}else{
						taskDefine.setType(TaskDefine.TYPE_UNCODE_SINGLE_TASK);
					}
				}

				String cronExpression = request.getParameter("cronExpression");
				if(StringUtils.isNotEmpty(cronExpression)){
					taskDefine.setCronExpression(cronExpression);
				}
				String period = request.getParameter("period");
				if(StringUtils.isNotEmpty(period)){
					taskDefine.setPeriod(Long.valueOf(period));
				}
				String delay = request.getParameter("delay");
				if(StringUtils.isNotEmpty(delay)){
					taskDefine.setDelay(Long.valueOf(delay));
				}
				String startTime = request.getParameter("startTime");
				if(StringUtils.isNotEmpty(startTime)){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
				    Date date = null;
					try {
						date = sdf.parse(startTime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					taskDefine.setStartTime(date);
				}else{
					taskDefine.setStartTime(new Date());
				}
				String param = request.getParameter("param");
				if(StringUtils.isNotEmpty(param)){
					taskDefine.setParams(param);
				}
				String extKeySuffix = request.getParameter("extKeySuffix");
				if(StringUtils.isNotEmpty(extKeySuffix)){
					taskDefine.setExtKeySuffix(extKeySuffix);
				}
				if(StringUtils.isNotEmpty(cronExpression) || StringUtils.isNotEmpty(period) || null != taskDefine.getStartTime()){
					try {
						ConsoleManager.addScheduleTask(taskDefine);
					} catch (Exception e) {

					}
				}
				response.sendRedirect(request.getSession().getServletContext().getContextPath()+"/uncode/schedule");
			}
			try {
				List<String> servers = ConsoleManager.getScheduleManager().getScheduleDataManager().loadScheduleServerNames();
				if(servers != null){
					response.setContentType("text/html;charset=UTF-8");
			        PrintWriter out = response.getWriter();
			        StringBuffer sb = new StringBuffer();
		    		for(int i=0; i< servers.size();i++){
		    			String ser = servers.get(i);
		    			sb.append("<tr class=\"info\">")
		    			  .append("<td>").append(i+1).append("</td>")
		    			  .append("<td>").append(ser).append("</td>");
						if( ConsoleManager.getScheduleManager().getScheduleDataManager().isLeader(ser, servers)){
							sb.append("<td>").append("是").append("</td>");
						}else{
							sb.append("<td>").append("否").append("</td>");
						}
		    			sb.append("</tr>");
		    		}

		    		List<TaskDefine> tasks = ConsoleManager.queryScheduleTask();
		    		StringBuffer sbTask = new StringBuffer();
		    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    		for(int i=0; i< tasks.size();i++){
		    			TaskDefine taskDefine = tasks.get(i);
		    			sbTask.append("<tr class=\"info\">")
		    			  .append("<td>").append(i+1).append("</td>")
		    			  .append("<td>").append(taskDefine.getTaskDefineName()).append("</td>")
		    			  .append("<td>").append(taskDefine.getParams()).append("</td>")
		    			  .append("<td>").append(taskDefine.getType()).append("</td>")
		    			  .append("<td>").append(taskDefine.getThreadNum()).append("</td>")
		    			  .append("<td>").append(taskDefine.getCronExpression()).append("</td>")
		    			  .append("<td>").append(taskDefine.getStartTime()).append("</td>")
		    			  .append("<td>").append(taskDefine.getPeriod()).append("</td>")
		    			  .append("<td>").append(taskDefine.getDelay()).append("</td>")
		    			  .append("<td>").append(taskDefine.getCurrentServer()).append("</td>")
		    			  .append("<td>").append(taskDefine.getStatus()).append("</td>")
		    			  .append("<td>").append(taskDefine.getRunTimes()).append("</td>")
		    			  .append("<td>");
		    			if(StringUtils.isBlank(taskDefine.getPercentage())){
		    				sbTask.append("-");
		    			}else{
		    				sbTask.append(taskDefine.getPercentage());
		    			}
		    			sbTask.append("</td>");
		    			if(taskDefine.getLastRunningTime() > 0){
		    				Date date = new Date(taskDefine.getLastRunningTime());
			    			sbTask.append("<td>").append(sdf.format(date)).append("</td>");
		    			}else{
		    				sbTask.append("<td>").append("-").append("</td>");
		    			}
		    			sbTask.append("<td>");
		    			if(taskDefine.isStop()){
		    				sbTask.append("<a href=\"").append(request.getSession().getServletContext().getContextPath())
			  				 .append("/uncode/schedule?start=")
			                 .append(taskDefine.getTargetBean())
			                 .append("_")
			                 .append(taskDefine.getTargetMethod())
							 .append("_")
							 .append(taskDefine.getTaskDefineName());
		    				if(StringUtils.isNotBlank(taskDefine.getExtKeySuffix())){
		    					sbTask.append("_").append(taskDefine.getExtKeySuffix());
		    				}
		    				sbTask.append("\" style=\"color:green\">运行</a>");
		    			}else{
		    				sbTask.append("<a href=\"").append(request.getSession().getServletContext().getContextPath())
			  				 .append("/uncode/schedule?stop=")
			                 .append(taskDefine.getTargetBean())
			                 .append("_")
			                 .append(taskDefine.getTargetMethod())
							 .append("_")
							 .append(taskDefine.getTaskDefineName());
		    				if(StringUtils.isNotBlank(taskDefine.getExtKeySuffix())){
		    					sbTask.append("_").append(taskDefine.getExtKeySuffix());
		    				}
		    				sbTask.append("\" style=\"color:red\">停止</a>");
		    			}
		    			sbTask.append(" <a href=\"").append(request.getSession().getServletContext().getContextPath())
		    			  				 .append("/uncode/schedule?del=")
		    			                 .append(taskDefine.getTargetBean())
		    			                 .append("_")
		    			                 .append(taskDefine.getTargetMethod())
								         .append("_")
								         .append(taskDefine.getTaskDefineName());
		    			if(StringUtils.isNotBlank(taskDefine.getExtKeySuffix())){
	    					sbTask.append("_").append(taskDefine.getExtKeySuffix());
	    				}
		    			sbTask.append("\" >删除</a>")
		    			                 .append("</td>");
						sbTask.append("</tr>");
		    		}
		    		 out.write(HEAD);
		    		 out.write(SCRIPT);
		    		 out.write(String.format(PAGE, request.getSession().getServletContext().getContextPath()+"/uncode/schedule",
		    				sb.toString(), sbTask.toString()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
