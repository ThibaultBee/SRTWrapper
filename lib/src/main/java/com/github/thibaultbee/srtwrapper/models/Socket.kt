package com.github.thibaultbee.srtwrapper.models

import com.github.thibaultbee.srtwrapper.enums.SockOpt
import java.io.File
import java.net.InetSocketAddress
import java.net.StandardProtocolFamily
import android.util.Pair

class Socket {
    private external fun nativeSocket(af: StandardProtocolFamily, type: Int, protocol:  Int) : Int
    private external fun nativeCreateSocket() : Int
    private external fun nativeBind(address: InetSocketAddress) : Int
    private external fun nativeClose() : Int

    private external fun nativeListen(backlog: Int) : Int
    private external fun nativeAccept() : Pair<Socket, InetSocketAddress?>
    private external fun nativeConnect(address: InetSocketAddress) : Int

    private external fun nativeSetSockOpt(level: Int /*ignored*/, opt: SockOpt, value: Any) : Int

    private external fun nativeSend(msg: ByteArray) : Int
    private external fun nativeSendMsg(msg: ByteArray, ttl: Int, inOrder: Boolean) : Int
    private external fun nativeSendMsg2(msg: ByteArray, msgCtrl: MsgCtrl?) : Int
    private external fun nativeSendFile(path: String, offset: Long, size: Long, block: Int) : Long

    private external fun nativeRecv(size: Int) : ByteArray
    private external fun nativeRecvMsg2(size: Int, msgCtrl: MsgCtrl?) : ByteArray
    private external fun nativeRecvFile(path: String, offset: Long, size: Long, block: Int) : Long

    private var srtsocket: Int

    companion object {
        const val INVALID_SOCK = -1
    }

    constructor() {
        srtsocket = nativeCreateSocket()
    }

    constructor(af: StandardProtocolFamily) {
        srtsocket = nativeSocket(af, 0, 0)
    }

    private constructor(socket: Int) {
        srtsocket = socket
    }

    fun isValid() = srtsocket > INVALID_SOCK

    fun bind(address: InetSocketAddress) : Int {
        return nativeBind(address)
    }

    fun bind(address: String, port: Int) : Int {
        return nativeBind(InetSocketAddress(address, port))
    }

    fun close(): Int {
        val res = nativeClose()
        srtsocket = INVALID_SOCK
        return res
    }

    // Connecting
    fun listen(backlog: Int) : Int {
        return nativeListen(backlog)
    }

    fun accept() : Pair<Socket, InetSocketAddress?> {
        return nativeAccept()
    }

    fun connect(address: InetSocketAddress) : Int {
        return nativeConnect(address)
    }

    fun connect(address: String, port: Int) : Int {
        return nativeConnect(InetSocketAddress(address, port))
    }

    // Options and properties
    fun setSockOpt(opt: SockOpt, value: Any) : Int {
        return nativeSetSockOpt(0, opt, value)
    }

    // Transmission
    fun sendMsg(msg: ByteArray): Int {
        return nativeSend(msg)
    }

    fun sendMsg(msg: String): Int {
        return nativeSend(msg.toByteArray())
    }

    fun sendMsg(msg: ByteArray, ttl: Int = -1, inOrder: Boolean = false): Int {
        return nativeSendMsg(msg, ttl, inOrder)
    }

    fun sendMsg(msg: String, ttl: Int = -1, inOrder: Boolean = false): Int {
        return nativeSendMsg(msg.toByteArray(), ttl, inOrder)
    }

    fun sendMsg(msg: ByteArray, msgCtrl: MsgCtrl?): Int {
        return nativeSendMsg2(msg, msgCtrl)
    }

    fun sendMsg(msg: String, msgCtrl: MsgCtrl?): Int {
        return nativeSendMsg2(msg.toByteArray(), msgCtrl)
    }

    fun recv(size: Int): ByteArray {
        return nativeRecv(size)
    }

    fun recvMsg2(size: Int, msgCtrl: MsgCtrl?): ByteArray {
        return nativeRecvMsg2(size, msgCtrl)
    }

    fun sendFile(file: File, offset: Long, size: Long, block: Int = 364000): Long {
        return nativeSendFile(file.path, offset, size, block)
    }

    fun sendFile(file: File, block: Int = 364000): Long {
        return nativeSendFile(file.path, 0, file.totalSpace, block)
    }

    fun recvFile(file: File, offset: Long, size: Long, block: Int = 7280000): Long {
        return nativeRecvFile(file.path, offset, size, block)
    }
}