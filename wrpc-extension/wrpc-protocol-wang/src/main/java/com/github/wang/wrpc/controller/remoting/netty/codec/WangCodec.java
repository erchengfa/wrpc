package com.github.wang.wrpc.controller.remoting.netty.codec;

import com.github.wang.wrpc.common.utils.BitUtil;
import com.github.wang.wrpc.common.utils.BytesUtils;
import com.github.wang.wrpc.context.codec.Codec;
import com.github.wang.wrpc.context.common.Request;
import com.github.wang.wrpc.context.common.Response;
import com.github.wang.wrpc.context.serializer.Serializer;
import com.github.wang.wrpc.context.serializer.SerializerUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.List;


/**
 * @author : wang
 * @date : 2019/12/25
 */
public class WangCodec implements Codec {

    // header length.
    protected static final int HEADER_LENGTH = 15;

    protected static final short MAGIC = (short) 0x7fff;

    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_HEARTBEAT = (byte) 0x40;
    protected static final byte FLAG_BACK = (byte) 0x20;

    @Override
    public void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf out) throws IOException {
        // header.
        byte[] header = new byte[HEADER_LENGTH];
        // set magic number.
        BytesUtils.short2bytes(MAGIC, header);
        if (o instanceof Request) {
            Request req = (Request) o;
            header[2] = (byte) (FLAG_REQUEST | req.getSerializerId());
            if (req.isHeartbeat()) {
                header[2] |= FLAG_HEARTBEAT;
            }
            if (req.isBack()){
                header[2] |= FLAG_BACK;
            }
            // set request id.
            BytesUtils.long2bytes(req.getId(), header, 3);
            Serializer serializer = SerializerUtils.getSerializer(req.getSerializerId());
            byte[] bodyByte = serializer.serialize(req.getBody());
            int len = bodyByte.length;
            BytesUtils.int2bytes(len, header, 11);
            out.writeBytes(header);//写入请求头
            out.writeBytes(bodyByte);//写入请求体
        } else if (o instanceof Response) {
            Response res = (Response) o;
            Serializer serializer = SerializerUtils.getSerializer(res.getSerializerId());
            header[2] = res.getSerializerId();
            if (res.isHeartbeat()) {
                header[2] |= FLAG_HEARTBEAT;
            }
            // set request id.
            BytesUtils.long2bytes(res.getId(), header, 3);
            byte[] bodyByte = serializer.serialize(res.getBody());
            int len = bodyByte.length;
            BytesUtils.int2bytes(len, header, 11);
            out.writeBytes(header);//写入请求头
            out.writeBytes(bodyByte);//写入请求体
        }
    }


    @Override
    public Object decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws IOException {

        // 可读长度必须大于基本长度
        if (in.readableBytes() >= HEADER_LENGTH) {

            // 记录包头开始的index
            int beginReader;
            while (true) {
                // 获取包头开始的index
                beginReader = in.readerIndex();
                // 标记包头开始的index
                in.markReaderIndex();
                // 读到协议的开始标志，结束while循环
                if (in.readShort() == MAGIC) {
                    in.resetReaderIndex();//这个地方重置 下方全部读到header字节数组中
                    break;
                }
                in.resetReaderIndex();
                // 未读到包头，跳过一个字节
                // 每次跳过一个字节后，再去读取包头信息的开始标记
                in.readByte();

                // 当跳过一个字节后，数据包的长度又变的不满足，此时应该结束，等待后边数据流的到达
                if (in.readableBytes() < HEADER_LENGTH) {
                    return null;
                }
            }
            // 代码到这里，说明已经读到了报文标志
            byte[] header = new byte[HEADER_LENGTH];
            in.readBytes(header);
            byte flag = header[2];
            int res = BitUtil.getBit(flag, 7);
            int heartbeat = BitUtil.getBit(flag, 6);
            byte serializeId = (byte)BitUtil.getBits(flag, 0,4);
            // 消息i
            long id = BytesUtils.bytes2long(header, 3);
            // 消息长度
            int length = BytesUtils.bytes2int(header, 11);
            // 判断请求数据包是否到齐
            if (in.readableBytes() < length) { // 数据不齐，回退读指针
                // 还原读指针
                in.readerIndex(beginReader);
                return null;
            }
            // 至此，读到一条完整报文
            byte[] body = new byte[length];
            in.readBytes(body);
            Serializer decodeSerializer = SerializerUtils.getSerializer(serializeId);
            if (res == 1){
                Request request = new Request(id);
                request.setSerializerId(serializeId);
                if (heartbeat == 1){
                    request.setHeartbeat(true);
                }

                if (!request.isHeartbeat()){
                    int back = BitUtil.getBit(flag, 5);
                    if (back == 1){
                        request.setBack(true);
                    }
                    Object requestBody = decodeSerializer.deserialize(body);
                    request.setBody(requestBody);
                }
                list.add(request);
            }else{
                Response response = new Response(id,serializeId);
                if (heartbeat == 1){
                    response.setHeartbeat(true);
                }
                if (!response.isHeartbeat()){
                    Object object = decodeSerializer.deserialize(body);
                    response.setBody(object);
                }
                list.add(response);
            }

            return null;
        }

        return null;
    }

}