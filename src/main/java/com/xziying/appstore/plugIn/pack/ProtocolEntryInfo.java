package com.xziying.appstore.plugIn.pack;

import com.xziying.appstore.plugIn.ProtocolEntry;

/**
 * ProtocolEntryAffixation
 *
 * @author : xziying
 * @create : 2021-04-09 22:05
 */
public class ProtocolEntryInfo {
    ProtocolEntry protocolEntry;
    String clazz;
    int weight;
    int state;

    public ProtocolEntryInfo(ProtocolEntry protocolEntry, String clazz, int weight, int state) {
        this.protocolEntry = protocolEntry;
        this.clazz = clazz;
        this.weight = weight;
        this.state = state;
    }

    public ProtocolEntry getProtocolEntry() {
        return protocolEntry;
    }

    public String getClazz() {
        return clazz;
    }

    public int getWeight() {
        return weight;
    }

    public int getState() {
        return state;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setState(int state) {
        this.state = state;
    }
}
