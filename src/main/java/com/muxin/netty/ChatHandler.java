package com.muxin.netty;

import com.muxin.entry.enums.MsgActionEnum;
import com.muxin.entry.netty.ChatMsg;
import com.muxin.entry.netty.DataContent;
import com.muxin.entry.netty.UserChannelRel;
import com.muxin.service.UserService;
import com.muxin.utils.JsonUtils;
import com.muxin.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

//TextWebSocketFrame: 在netty中, 是用于为websocket 专门处理文本的对象, frame是消息到载体
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	//用于记录和管理所有channel
	private static final ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) throws Exception {
		//获取发送来的消息
		final String content = msg.text();
		//获取channel
		Channel channel = channelHandlerContext.channel();
		
		//判断消息类型, 根据不同的类型来处理不同的业务
		DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
		if (dataContent == null) {
			return;
		}
		final Integer action = dataContent.getAction();
		if (action.equals(MsgActionEnum.CONNECT.type)) {
			//当websocket第一次open的时候, 初始化channel, 把channel和userid关联
			final String sendId = dataContent.getChatMsg().getSendId();
			UserChannelRel.put(sendId, channel);
			
			//测试
			test();
			
		} else if (action.equals(MsgActionEnum.CHAT.type)) {
			//聊天类型的消息, 把聊天记录保存到数据库, 同时标记消息的签收状态[未签收]
			final ChatMsg chatMsg = dataContent.getChatMsg();
			final String msgText = chatMsg.getMsg();
			final String receiverId = chatMsg.getReceiverId();
			final String sendId = chatMsg.getSendId();
			
			//保存到消息数据库, 并且标记为未签收
			final UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
			final String msgId = userService.saveMsg(chatMsg);
			chatMsg.setMsgId(msgId);
			
			//发送消息
			final Channel receiveChannel = UserChannelRel.get(receiverId);
			if (receiveChannel == null) {
				//TODO channel为空代表用户离线, 推送消息
				System.out.println("用户离线");
			} else {
				//从channelGroup查找对应的channel
				final Channel findChannel = users.find(channel.id());
				if (findChannel != null) {
					//用户在线
					receiveChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
				} else {
					//TODO channel为空代表用户离线, 推送消息
					System.out.println("用户离线");
				}
			}
		} else if (action.equals(MsgActionEnum.SIGNED.type)) {
			//签收消息类型, 针对具体雄安锡进行签收, 修改数据库中对应消息的签收状态
			final UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
			//扩展字段在signed烈性消息中,代表需要去签收的id
			final String msgIdsStr = dataContent.getExtend();
			final String[] msgIds = msgIdsStr.split(",");
			
			List<String> msgIdList = new ArrayList<>();
			for (String msgId : msgIds) {
				if (StringUtils.isNoneBlank(msgId)) {
					msgIdList.add(msgId);
				}
			}
			
			System.out.println(msgIdList.toString());
			
			if (!msgIdList.isEmpty()) {
				//批量签收
				userService.updateMsgSigned(msgIdList);
			}
			
		} else if (action.equals(MsgActionEnum.KEEPALIVE.type)) {
			//心跳类型消息
			
		}
	}
	
	//当客户端连接服务器端之后(打开链接)
	//获取客户端的channel, 并且
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		users.add(ctx.channel());
	}
	
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// 当触发handlerRemoved, ChannelGroup会自动溢出对应客户端的channel
		users.remove(ctx.channel());
//		System.out.println(ctx.channel().id().asShortText());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		//发生已成之后关闭连接(关闭channel), 随后从ChannelGroup移除
		ctx.channel().close();
		users.remove(ctx.channel());
	}
	
	void test() {
		for (Channel user : users) {
			System.out.println("UserChannelId: " + user.id().asLongText());
		}
		UserChannelRel.output();
	}
}
