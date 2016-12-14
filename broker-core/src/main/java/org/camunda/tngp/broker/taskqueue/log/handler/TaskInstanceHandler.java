package org.camunda.tngp.broker.taskqueue.log.handler;

import org.camunda.tngp.broker.log.LogEntryTypeHandler;
import org.camunda.tngp.broker.log.LogWriters;
import org.camunda.tngp.broker.log.ResponseControl;
import org.camunda.tngp.broker.taskqueue.LockedTaskBatchWriter;
import org.camunda.tngp.broker.taskqueue.SingleTaskAckResponseWriter;
import org.camunda.tngp.broker.taskqueue.subscription.LockTasksOperator;
import org.camunda.tngp.protocol.log.TaskInstanceState;
import org.camunda.tngp.protocol.taskqueue.TaskInstanceReader;

public class TaskInstanceHandler implements LogEntryTypeHandler<TaskInstanceReader>
{
    protected SingleTaskAckResponseWriter singleTaskResponseWriter = new SingleTaskAckResponseWriter();
    protected LockedTaskBatchWriter taskBatchResponseWriter = new LockedTaskBatchWriter();

    protected LockTasksOperator lockTasksOperator;

    public TaskInstanceHandler(LockTasksOperator lockTasksOperator)
    {
        this.lockTasksOperator = lockTasksOperator;
    }

    @Override
    public void handle(TaskInstanceReader reader, ResponseControl responseControl, LogWriters logWriters)
    {
        final TaskInstanceState state = reader.state();

        if (state == TaskInstanceState.NEW)
        {
            singleTaskResponseWriter.taskId(reader.id());

            responseControl.accept(singleTaskResponseWriter);
        }
        else if (state == TaskInstanceState.LOCKED)
        {
            lockTasksOperator.onTaskLocked(reader);
        }
    }

}