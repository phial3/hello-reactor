package com.mine.study.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mine.study.bean.SteelyardResult;
import com.mine.study.utils.SteelyardUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class SteelyardServerHandler extends SimpleChannelInboundHandler<String> {
    public static final Logger LOGGER = LoggerFactory.getLogger(SteelyardServerHandler.class);
    private static ConcurrentHashMap<Long, Double> weightMap = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<SteelyardResult> steelyardResults = new CopyOnWriteArrayList<>();
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final TaskScheduler taskScheduler;
    private static ConcurrentHashMap<Channel, String> registMap = new ConcurrentHashMap<>();

    public SteelyardServerHandler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String data) throws Exception {
        LOGGER.info("收到物位仪数据<----传感器：" + data);
        //        if (s != null && !"".equals(data)) {
        //            try {
        //                processMsg(data, context);
        context.writeAndFlush(data);
        //            } catch (Exception e) {
        //                LOGGER.error("处理数据错误：", e);
        //                e.printStackTrace();
        //            }
        //        }

    }


    public void processMsg(String msg, ChannelHandlerContext context) {
        if (SteelyardUtil.isPackage(msg)) {
            String id = SteelyardUtil.getId(msg);
            String op = SteelyardUtil.getOperate(msg);
            LOGGER.info("获取操作命令：" + op);
            switch (op) {
                case SteelyardUtil.REGISTER:
                    String sent = SteelyardUtil.allowAck(id);
                    if (!"".equals(sent)) {
                        //LOGGER.info("发送Register数据---->吊秤：" + sent);
                        //ByteBuf echo = Unpooled.copiedBuffer(sent.getBytes());
                        context.writeAndFlush(sent);
                    }
                    break;
                case SteelyardUtil.WEIGHT:
                    Double weight = SteelyardUtil.getWeight(msg);
                    Long timestamp = new Date().getTime();
                    SteelyardResult steelyardResult = new SteelyardResult();
                    steelyardResult.setId(Long.valueOf(id));
                    steelyardResult.setReportTime(timestamp);
                    steelyardResult.setWeight(weight);
                    //steelyardResults.add(steelyardResult);
                    break;
                case SteelyardUtil.MODE:
                    String mode = SteelyardUtil.getModeResult(msg);
                    break;
                case SteelyardUtil.PRINT:
                    Double print = SteelyardUtil.getPrintWeight(msg);
                    break;
                case SteelyardUtil.BAT:
                    String bat = SteelyardUtil.batAck(id);
                    if (!"".equals(bat)) {
                        //LOGGER.info("发送Bat数据---->吊秤：" + bat);
                        ByteBuf echo = Unpooled.copiedBuffer(bat.getBytes());
                        context.writeAndFlush(bat);
                    }
                    break;
                case SteelyardUtil.DISPLAY:
                    Double display = SteelyardUtil.getWeight(msg);
                    break;
                case SteelyardUtil.COMMSTATUS:
                    String status = SteelyardUtil.getCommStatus(msg);
                    break;
                case SteelyardUtil.SETZERO:
                    String zero = SteelyardUtil.getZeroResult(msg);
                    break;
                default:

            }
            //第一次上线注册
            if (!registMap.containsKey(context.channel())) {
                registMap.put(context.channel(), id);
                String mode = SteelyardUtil.setMode(id, 3, "00010", "015", "010");
                //LOGGER.info("发送Mode数据---->吊秤：" + mode);
                ByteBuf echo = Unpooled.copiedBuffer(mode.getBytes());
                context.writeAndFlush(mode);
            }
        }
    }

    //每10秒检查一次数据是否稳定
    private void processData() {
        if (atomicInteger.get() >= 1) {
            return;
        }
        atomicInteger.incrementAndGet();
        taskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                //LOGGER.info("check result list");
                try {
                    if (steelyardResults != null) {
                        int size = steelyardResults.size();
                        if (size > 0) {
                            Long timestamp = new Date().getTime();
                            SteelyardResult steelyardResult = steelyardResults.get(size - 1);
                            Long duration = timestamp - steelyardResult.getReportTime();
                            //LOGGER.info("检查稳定时间：" + duration);
                            //5s内没有新增数据则认为稳定
                            if (duration > 5 * 1000) {
                                LOGGER.info("发送数据到业务平台：" + MAPPER.writeValueAsString(steelyardResult));
                                //steelyardBusiness.pushResult(steelyardResult);
                                steelyardResults.clear();
                            }
                        }
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }, new CronTrigger("0/3 * * * * *"));
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("channelUnregistered channel取消绑定");
        registMap.remove(ctx.channel());
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("channelInactive: channel被关闭");
        super.channelInactive(ctx);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            LOGGER.info(ctx.channel().remoteAddress() + "连接空闲被检测");
            if (IdleState.READER_IDLE.equals((event.state()))) {
                ctx.close();
                //ctx.writeAndFlush("heartbeat").addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("exceptionCaught被捕获");
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

}
