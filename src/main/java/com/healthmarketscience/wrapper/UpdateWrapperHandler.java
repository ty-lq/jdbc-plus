package com.healthmarketscience.wrapper;

import java.util.List;

public interface UpdateWrapperHandler {

    int handle(Table table, List<Condition> conditions);

    String builder();
}
