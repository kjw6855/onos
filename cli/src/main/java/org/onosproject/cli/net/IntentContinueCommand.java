/*
 * Copyright 2014-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.cli.net;

import com.google.common.collect.ImmutableList;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.cli.app.ApplicationIdWithIntentNameCompleter;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.intent.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.onosproject.net.intent.IntentState.FAILED;
import static org.onosproject.net.intent.IntentState.WITHDRAWN;

/**
 * Removes an intent.
 */
@Service
@Command(scope = "onos", name = "continue-intent",
        description = "Continues the specified intent")
public class IntentContinueCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "app",
            description = "Application ID",
            required = false, multiValued = false)
    @Completion(ApplicationIdWithIntentNameCompleter.class)
    String applicationIdString = null;

    @Argument(index = 1, name = "key",
            description = "Intent Key",
            required = false, multiValued = false)
    @Completion(IntentKeyCompleter.class)
    String keyString = null;

    private static final EnumSet<IntentState> CAN_PURGE = EnumSet.of(WITHDRAWN, FAILED);

    @Override
    protected void doExecute() {
        IntentService intentService = get(IntentService.class);
        continueIntent(intentService.getIntents(),
                applicationIdString, keyString);
    }

    private void continueIntent(Iterable<Intent> intents,
                                String applicationIdString, String keyString)
    {

        IntentService intentService = get(IntentService.class);
        CoreService coreService = get(CoreService.class);
        this.applicationIdString = applicationIdString;
        this.keyString = keyString;

        ApplicationId appId = appId();
        if (!isNullOrEmpty(applicationIdString)) {
            appId = coreService.getAppId(applicationIdString);
            if (appId == null) {
                print("Cannot find application Id %s", applicationIdString);
                return;
            }
        }

        if (isNullOrEmpty(keyString)) {
            continueIntentsByAppId(intentService, intents, appId);

        } else {
            final Key key;
            if (keyString.startsWith("0x")) {
                // The intent uses a LongKey
                keyString = keyString.replaceFirst("0x", "");
                key = Key.of(new BigInteger(keyString, 16).longValue(), appId);
            } else {
                // The intent uses a StringKey
                key = Key.of(keyString, appId);
            }

            Intent intent = intentService.getIntent(key);
            if (intent != null) {
                intentService.continueIntent(intent);
            }
        }
    }

    /**
     * Removes the intents passed as argument.
     *
     * If an application ID is provided, it will be used to further
     * filter the intents to be removed.
     *
     * @param intentService IntentService object
     * @param intents intents to remove
     * @param appId application ID to filter intents
     */
    private void continueIntentsByAppId(IntentService intentService,
                                     Iterable<Intent> intents,
                                     ApplicationId appId) {
        for (Intent intent : intents) {
            if (appId == null || intent.appId().equals(appId)) {
                intentService.continueIntent(intent);
            }
        }
    }
}
