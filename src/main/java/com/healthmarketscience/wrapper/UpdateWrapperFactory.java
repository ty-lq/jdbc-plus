package com.healthmarketscience.wrapper;

import java.util.HashMap;
import java.util.Map;

public class UpdateWrapperFactory {
    private final static Map<UpdateEnums, UpdateWrapperHandler> map = new HashMap<>();

    static {
        map.put(UpdateEnums.UPDATE, new UpdateHandler());
        map.put(UpdateEnums.DELETE, new DeleteHandler());
        map.put(UpdateEnums.INSERT, new InsertHandler());
    }

    public static UpdateWrapperHandler create(UpdateEnums updateEnums) {
        return map.get(updateEnums);
    }
}
