package com.xziying.appstore.cloud.gateway;

import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.plugIn.ProtocolEntry;
import com.xziying.appstore.plugIn.domain.EventInfo;


/**
 * VerificationCodeCloud
 *
 * @author : xziying
 * @create : 2021-04-11 12:28
 */
public interface VerificationCodeCloud {

    void initialize(DatabaseService databaseService, String token);

    int processTheMessage(String clazz, ProtocolEntry protocolEntry, EventInfo eventInfo);

    String getVerification();
}
