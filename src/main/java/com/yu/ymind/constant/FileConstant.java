package com.yu.ymind.constant;

import com.yu.ymind.config.CosClientConfig;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

/**
 * 文件相关常量接口
 * <p>
 * 该接口定义了与文件操作相关的常量信息，包括 COS 访问地址等。
 */
public interface FileConstant {



    String bucket ="ymind-1307828773";

    String region ="ap-shanghai";


    /**
     * COS 存储服务访问地址
     * <p>
     * todo 需要替换为实际的 COS 访问地址
     */
    String COS_HOST = "https://" + bucket + ".cos." + region + ".myqcloud.com/";

}