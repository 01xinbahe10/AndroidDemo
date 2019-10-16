package com.cdct.cmdim.utils;
//import com.cdct.library.utils.UserInfoCache;


/**
 * Created by hxb on 2018/3/30.
 * 布局上的消息处理以左右区分
 * 角色：《左：对方》 《右：己方》
 * 文件类型：《左：1》 《右：2》
 */

public class RoleUtils {
    //做传输数据时的类型规定
    public static final int TEXT_NET = 11;
    public static final int IMG_NET = 21;
    public static final int VOICE_NET =22;
    public static final int PLAN_NET = 31;
    //适配器左右布局
    public static final int TEXT_LEFT = 0x001;
    public static final int TEXT_RIGHT = 0x002;
    public static final int IMG_LEFT = 0x003;
    public static final int IMG_RIGHT = 0x004;
    public static final int PLAN_LEFT = 0x005;
    public static final int PLAN_RIGHT = 0x006;
    public static final int VOICE_LEFT = 0x007;
    public static final int VOICE_RIGHT = 0x008;
    //ip地址
    //端口号
    public static final String ip = "192.168.200.203";
    public static final int port = 8084;
//    public static final String ip = "192.168.200.203";
//    public static final int port = 8010;
    //发送者
//    public static final String sendUserId = UserInfoCache.getUserInfo(UserInfoCache.CYSBM)+"";
    public static final String sendUserId = "007";
    //接收者
//    public static final String reserverId = UserInfoCache.getUserInfo(UserInfoCache.CHZBM)+"";
    public static String reserverId = "001";
}
