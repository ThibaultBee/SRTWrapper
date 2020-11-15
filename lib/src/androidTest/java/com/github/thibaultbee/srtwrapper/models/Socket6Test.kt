package com.github.thibaultbee.srtwrapper.models

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.thibaultbee.srtwrapper.Srt
import com.github.thibaultbee.srtwrapper.enums.ErrorType
import com.github.thibaultbee.srtwrapper.enums.SockOpt
import com.github.thibaultbee.srtwrapper.enums.SockStatus
import com.github.thibaultbee.srtwrapper.enums.Transtype
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.StandardProtocolFamily


/*
 * Theses tests are written to check if SRT API can be called from the Kotlin part.
 */

@RunWith(AndroidJUnit4::class)
class Socket6Test {
    private val srt = Srt()
    private lateinit var socket: Socket

    @Before
    fun setUp() {
        assert(srt.startUp() >= 0)
        socket = Socket(StandardProtocolFamily.INET6)
        assertTrue(socket.isValid())
    }

    @After
    fun tearDown() {
        if (socket.isValid())
            socket.close()
        assertEquals(srt.cleanUp(), 0)
    }

    @Test
    fun bindTest() {
        assertEquals(0, socket.setSockFlag(SockOpt.TRANSTYPE, Transtype.FILE))
        assertEquals(0, socket.bind("::1", 1111))
    }

    @Test
    fun sockStatusTest() {
        assertEquals(SockStatus.INIT, socket.sockState)
        assertEquals(0, socket.bind("::1", 2222))
        assertEquals(SockStatus.OPENED, socket.sockState)
    }

    @Test
    fun closeTest() {
        socket.close()
        assertTrue(socket.isClose)
    }

    @Test
    fun listenTest() {
        assertEquals(-1, socket.listen(3))
        assertEquals(Error.getLastError(), ErrorType.EUNBOUNDSOCK)
        assertEquals(0, socket.bind("::1", 3333))
        assertEquals(0, socket.listen(3))
    }

    @Test
    fun acceptTest() {
        val pair = socket.accept()
        assertFalse(pair.first.isValid())
        assertNull(pair.second)
        assertEquals(Error.getLastError(), ErrorType.ENOLISTEN)
    }

    @Test
    fun connectTest() {
        assertEquals(-1, socket.connect("::1", 4444))
        assertEquals(Error.getLastError(), ErrorType.ENOSERVER)
    }

    @Test
    fun rendezVousTest() {
        assertEquals(-1, socket.rendezVous("::", "2001:0db8:0000:85a3:0000:0000:ac1f:8001", 5555))
        assertEquals(Error.getLastError(), ErrorType.ENOSERVER)
    }

    @Test
    fun getPeerNameTest() {
        assertNull(socket.peerName)
    }

    @Test
    fun getSockNameTest() {
        assertNull(socket.sockName)
        assertEquals(0, socket.bind("::1", 6666))
        assertEquals(socket.sockName!!.address.hostAddress, "::1")
        assertEquals(socket.sockName!!.port, 6666)
    }
}