package com.bluemethod.jabs.jabs.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides functionalities for queries to cast to a list
 */
public class CastList 
{
    /**
     * Allows a query to cast to a list: code from
     * https://stackoverflow.com/a/2848268
     */
    public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c)
    {
        List<T> r = new ArrayList<T>(c.size());
        for (Object o: c)
            r.add(clazz.cast(o));

        return r;
    }
}
