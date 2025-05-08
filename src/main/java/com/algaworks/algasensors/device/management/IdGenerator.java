package com.algaworks.algasensors.device.management;

import io.hypersistence.tsid.TSID;

import java.util.Optional;

public class IdGenerator {

    public static final TSID.Factory factory;

    private static final String TSID_NODE = "tsid.node";
    private static final String TSID_NODE_COUNT = "tsid.node.count";

    static {
        Optional.ofNullable(System.getenv(TSID_NODE))
                .ifPresent(tsidNode -> System.setProperty(TSID_NODE, tsidNode));

        Optional.ofNullable(System.getenv(TSID_NODE_COUNT))
                .ifPresent(tsidNode -> System.setProperty(TSID_NODE_COUNT, tsidNode));

        factory = TSID.Factory.builder().build();
    }

    private IdGenerator(){
    }

    public static TSID generateTSID() {
        return factory.generate();
    }
}
