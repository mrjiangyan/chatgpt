FROM apache/beam_java17_sdk
#FROM registry.cn-hangzhou.aliyuncs.com/aiprime-backend-v2/java:apache-beam_java17

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime


# 移动文件到目标文件夹
ARG APP_LOCATION=target/chatgpt-service.jar

ADD ${APP_LOCATION} /

