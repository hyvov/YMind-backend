package com.yu.ymind.manager;

import com.yu.ymind.common.ErrorCode;
import com.yu.ymind.exception.BusinessException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class AIManager {
    // 私有的 ClientV4 实例，用于调用模型 API
    @Resource
    private ClientV4 clientV4;
    private static final float STABLE_TEMPERATURE = 0.05f;//稳定随机数


    private static final float UNSTABLE_TEMPERATURE = 0.99f;  //不稳定随机数

    /**
     * 通用请求。
     *
     * @param messages    要发送的聊天消息列表
     * @param stream      是否启用流式传输
     * @param temperature 调整生成文本的随机性（温度值）
     * @return 生成的文本内容字符串
     */
    public String doRequest(List<ChatMessage> messages, Boolean stream, Float temperature) {
        // 构造聊天完成请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)            // 使用的模型
                .stream(stream)                            // 是否启用流式传输
                .invokeMethod(Constants.invokeMethod)      // 调用的方法
                .temperature(temperature)                  // 设置生成文本的随机性
                .messages(messages)                        // 要发送的消息列表
                .build();

        try {
            // 调用模型 API 并获取响应
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            // 从响应中获取生成的消息
            ChatMessage result = invokeModelApiResp.getData().getChoices().get(0).getMessage();

            // 返回生成的文本内容
            return result.getContent().toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "调用模型API失败");
        }
    }

    /**
     * 通用请求（简化消息传递）
     *
     * @param systemMessage
     * @param userMessage
     * @param stream
     * @param temperature
     * @return
     */
    public String doRequest(String systemMessage, String userMessage, Boolean stream, Float temperature) {
        // 构造请求
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(systemChatMessage);
        messages.add(userChatMessage);
        return doRequest(messages, stream, temperature);
    }


    /**
     * 同步请求
     *
     * @param systemMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public String doSyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, temperature);
    }


    /**
     * 同步调用（答案较稳定）
     *
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncStableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage,Boolean.FALSE, STABLE_TEMPERATURE);
    }

    /**
     * 同步调用（答案较随机）
     *
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncUnstableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage,Boolean.FALSE, UNSTABLE_TEMPERATURE);
    }
}
