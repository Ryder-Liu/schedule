package com.info.schedule.util;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Pattern;


/**
 * 调度处理工具类
 *
 * @author juny.ye
 *
 */
public class ScheduleUtil {

    public static String OWN_SIGN_BASE ="BASE";

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    public static final String DOUBLE_CHECK_DISTRIBUTE_NAME_SUFFIX = "_check";

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    public static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "";
        }
    }

    public static int getFreeSocketPort() {
        try {
            ServerSocket ss = new ServerSocket(0);
            int freePort = ss.getLocalPort();
            ss.close();
            return freePort;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

	public static String getLocalIP(){
		String localip = null;// 本地IP，如果没有配置外网IP则返回它
		String netip = null;// 外网IP

		Enumeration<NetworkInterface> netInterfaces=null;
		try {
			netInterfaces = NetworkInterface
					.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		}
		InetAddress ip = null;
		boolean finded = false;// 是否找到外网IP
		while (netInterfaces.hasMoreElements() && !finded) {
			NetworkInterface ni = netInterfaces.nextElement();
			Enumeration<InetAddress> address = ni.getInetAddresses();
			while (address.hasMoreElements()) {
				ip = address.nextElement();
				if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
						&& ip.getHostAddress().indexOf(":") == -1) {// 外网IP
					netip = ip.getHostAddress();
					finded = true;
					break;
				} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
						&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
					localip = ip.getHostAddress();
				}
			}
		}

		if (netip != null && !"".equals(netip)) {
			return netip;
		} else {
			return localip;
		}
	}


    public static String transferDataToString(Date d){
        SimpleDateFormat DATA_FORMAT_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return DATA_FORMAT_yyyyMMddHHmmss.format(d);
    }
    public static String transferDataToString(Date d, String formate){
        SimpleDateFormat DATA_FORMAT_yyyyMMddHHmmss = new SimpleDateFormat(formate);
        return DATA_FORMAT_yyyyMMddHHmmss.format(d);
    }
    public static Date transferStringToDate(String d) throws ParseException{
        SimpleDateFormat DATA_FORMAT_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return DATA_FORMAT_yyyyMMddHHmmss.parse(d);
    }
    public static Date transferStringToDate(String d,String formate) throws ParseException{
        SimpleDateFormat FORMAT = new SimpleDateFormat(formate);
        return FORMAT.parse(d);
    }
    public static String getTaskTypeByBaseAndOwnSign(String baseType,String ownSign){
        if(ownSign.equals(OWN_SIGN_BASE)){
            return baseType;
        }
        return baseType+"$" + ownSign;
    }
    public static String splitBaseTaskTypeFromTaskType(String taskType){
         if(taskType.contains("$")){
             return taskType.substring(0,taskType.indexOf("$"));
         }else{
             return taskType;
         }

    }
    public static String splitOwnsignFromTaskType(String taskType){
         if(taskType.contains("$")){
             return taskType.substring(taskType.indexOf("$")+1);
         }else{
             return OWN_SIGN_BASE;
         }
    }

    public static String buildScheduleKey(String beanName, String methodName, String extKeySuffix){
    	String result = beanName + "#" + methodName;
    	if(StringUtils.isNotBlank(extKeySuffix)){
    		result += "-" + extKeySuffix;
    	}
    	return result;
    }


    public static String buildScheduleKey(String beanName, String methodName){
    	return buildScheduleKey(beanName, methodName, null);
    }

    public static String buildDoubleCheckDistributedName(String beanName){
    	return beanName + DOUBLE_CHECK_DISTRIBUTE_NAME_SUFFIX;
    }

    /**
     * 分配任务数量
     * @param serverNum 总的服务器数量
     * @param taskItemNum 任务项数量
     * @param maxNumOfOneServer 每个server最大任务项数目
     * @return null
     */
    public static int[] assignTaskNumber(int serverNum,int taskItemNum,int maxNumOfOneServer){
        int[] taskNums = new int[serverNum];
        int numOfSingle = taskItemNum / serverNum;
        int otherNum = taskItemNum % serverNum;
        if (maxNumOfOneServer >0 && numOfSingle >= maxNumOfOneServer) {
            numOfSingle = maxNumOfOneServer;
            otherNum = 0;
        }
        for (int i = 0; i < taskNums.length; i++) {
            if (i < otherNum) {
                taskNums[i] = numOfSingle + 1;
            } else {
                taskNums[i] = numOfSingle;
            }
        }
        return taskNums;
    }

    private static String printArray(int[] items){
        String s="";
        for(int i=0;i<items.length;i++){
            if(i >0){s = s +",";}
            s = s + items[i];
        }
        return s;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null
                && ! ANYHOST.equals(name)
                && ! LOCALHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }

    public static void main(String[] args) {

    	System.out.println(getLocalIP());

        /*System.out.println(printArray(assignTaskNumber(1,10,0)));
        System.out.println(printArray(assignTaskNumber(2,10,0)));
        System.out.println(printArray(assignTaskNumber(3,10,0)));
        System.out.println(printArray(assignTaskNumber(4,10,0)));
        System.out.println(printArray(assignTaskNumber(5,10,0)));
        System.out.println(printArray(assignTaskNumber(6,10,0)));
        System.out.println(printArray(assignTaskNumber(7,10,0)));
        System.out.println(printArray(assignTaskNumber(8,10,0)));
        System.out.println(printArray(assignTaskNumber(9,10,0)));
        System.out.println(printArray(assignTaskNumber(10,10,0)));

        System.out.println("-----------------");

        System.out.println(printArray(assignTaskNumber(1,10,3)));
        System.out.println(printArray(assignTaskNumber(2,10,3)));
        System.out.println(printArray(assignTaskNumber(3,10,3)));
        System.out.println(printArray(assignTaskNumber(4,10,3)));
        System.out.println(printArray(assignTaskNumber(5,10,3)));
        System.out.println(printArray(assignTaskNumber(6,10,3)));
        System.out.println(printArray(assignTaskNumber(7,10,3)));
        System.out.println(printArray(assignTaskNumber(8,10,3)));
        System.out.println(printArray(assignTaskNumber(9,10,3)));
        System.out.println(printArray(assignTaskNumber(10,10,3)));*/

    }
}
