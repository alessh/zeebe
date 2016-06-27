package org.camunda.bpm.broker.it.process;

import org.camunda.tngp.client.cmd.BrokerRequestException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class BrokerRequestExceptionMatcher extends BaseMatcher<BrokerRequestException>
{

    protected int expectedComponentCode;
    protected int expectedDetailCode;

    public static BrokerRequestExceptionMatcher brokerException(int expectedComponentCode, int expectedDetailCode)
    {
        final BrokerRequestExceptionMatcher matcher = new BrokerRequestExceptionMatcher();
        matcher.expectedComponentCode = expectedComponentCode;
        matcher.expectedDetailCode = expectedDetailCode;
        return matcher;
    }

    @Override
    public boolean matches(Object item)
    {
        if (item == null || !(item instanceof BrokerRequestException))
        {
            return false;
        }

        final BrokerRequestException exception = (BrokerRequestException) item;

        return expectedComponentCode == exception.getComponentCode() && expectedDetailCode == exception.getDetailCode();
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText(BrokerRequestException.class.getSimpleName());
        description.appendText(" with component code " + expectedComponentCode + " and detail code " + expectedDetailCode);

    }

}