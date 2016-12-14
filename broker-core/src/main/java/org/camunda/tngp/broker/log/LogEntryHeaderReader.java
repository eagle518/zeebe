package org.camunda.tngp.broker.log;

import org.camunda.tngp.protocol.log.MessageHeaderDecoder;
import org.camunda.tngp.util.buffer.BufferReader;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public class LogEntryHeaderReader implements BufferReader
{

    protected MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    protected UnsafeBuffer tempBuffer = new UnsafeBuffer(0, 0);

    public EventSource source()
    {
        return EventSource.get(headerDecoder.source());
    }

    public <T extends BufferReader> Template<T> template()
    {
        return Templates.getTemplate(headerDecoder.templateId());
    }

    public int templateId()
    {
        return headerDecoder.templateId();
    }

    public long sourceEventPosition()
    {
        return headerDecoder.sourceEventPosition();
    }

    public int sourceEventLogId()
    {
        return headerDecoder.sourceEventLogId();
    }

    public void readInto(BufferReader reader)
    {
        reader.wrap(tempBuffer, 0, tempBuffer.capacity());
    }

    @Override
    public void wrap(DirectBuffer buffer, int offset, int length)
    {
        headerDecoder.wrap(buffer, offset);
        tempBuffer.wrap(buffer, offset, length);
    }

    public enum EventSource
    {
        NULL_VAL((short) 0),
        API((short) 1),
        LOG((short) 2);

        short value;

        EventSource(short value)
        {
            this.value = value;
        }

        public short value()
        {
            return value;
        }

        public static EventSource get(final short value)
        {
            switch (value)
            {
                case 0: return NULL_VAL;
                case 1: return API;
                case 2: return LOG;
            }

            throw new IllegalArgumentException("Unknown value: " + value);
        }
    }
}