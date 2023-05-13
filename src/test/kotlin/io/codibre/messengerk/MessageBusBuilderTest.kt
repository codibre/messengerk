package io.codibre.messengerk

import io.codibre.messengerk.annotations.MessageHandler
import io.codibre.messengerk.contracts.MessageBus
import io.codibre.messengerk.handler.HandlerDescriptor
import io.codibre.messengerk.stamp.BusNameStamp
import io.codibre.messengerk.stamp.HandledStamp
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MessageBusBuilderTest {

    class FooMessage

    class FooListener {
        @MessageHandler
        fun handleFoo(envelope: Envelope<in FooMessage>): Envelope<in FooMessage> {
            println("Handling FooMessage")
            return envelope
        }
    }

    @Test
    fun `it can build a minimal message bus`() {
        val bus: MessageBus = MessageBusBuilder("Foo bus").build {
            allowNoHandlers(true)
        }

        val result = bus.dispatch(FooMessage()).getOrThrow()
        val stamp = result.firstOf<BusNameStamp>() as BusNameStamp
        assertTrue(result.contains<BusNameStamp>())
        assertTrue(stamp.name == "Foo bus")
    }

    @Test
    fun `it can build a bus with handlers`() {
        val bus: MessageBus = MessageBusBuilder("Foo bus").build {
            withHandler(HandlerDescriptor.fromKFunction(FooListener::handleFoo))
        }

        val result = bus.dispatch(FooMessage()).getOrThrow()
        assertTrue(result.contains<HandledStamp>())
    }

//    @Test
//    fun `it can build a bus with senders`() {
//        val transport = InMemoryTransport(name = "fooInMemory")
//        val channel = Channel("fooChannel")
//        val bus: MessageBus = MessageBusBuilder("Foo bus").build {
//            withRoute(FooMessage::class.qualifiedName.toString(), Route(channel, transport))
//            withHandler(FooListener())
//        }
//
//        val resultEnvelope = bus.dispatch(FooMessage()).getOrThrow()
//        assertTrue(resultEnvelope.contains<SentStamp>())
//        assertTrue(resultEnvelope.notContains<HandledStamp>())
//    }
//
//    @Test
//    fun `it throws exception when there is no handler for the message`() {
//        val bus: MessageBus = MessageBusBuilder("Foo bus").build {}
//
//        assertThrows<NoHandlerForMessageException> {
//            bus.dispatch(FooMessage()).getOrThrow()
//        }
//    }
}