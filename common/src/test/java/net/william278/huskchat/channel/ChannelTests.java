/*
 * This file is part of HuskChat, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskchat.channel;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChannelTests {
  Channel plainTextChannel = Channel
      .builder().id("plaintext").restrictedServers(List.of("plain", "text", "restrictions")).build();
  Channel regexTextChannel = Channel
      .builder().id("regex").restrictedServers(List.of(".*regex.*", "matcher.*", ".*channel")).build();

  @Test
  public void testPlaintextUnrestrictedServer() {
    Assertions.assertFalse(plainTextChannel.isServerRestricted("nota"));
    Assertions.assertFalse(plainTextChannel.isServerRestricted("plaintext"));
    Assertions.assertFalse(plainTextChannel.isServerRestricted("restricted server"));
  }

  @Test
  public void testPlaintextRestrictedServer() {
    Assertions.assertTrue(plainTextChannel.isServerRestricted("plain"));
    Assertions.assertTrue(plainTextChannel.isServerRestricted("text"));
    Assertions.assertTrue(plainTextChannel.isServerRestricted("restrictions"));
  }

  @Test
  public void testPlaintextRestrictedServerIgnoreCase() {
    Assertions.assertTrue(plainTextChannel.isServerRestricted("PLAIN"));
    Assertions.assertTrue(plainTextChannel.isServerRestricted("tExT"));
    Assertions.assertTrue(plainTextChannel.isServerRestricted("resTriCTioNs"));
  }

  @Test
  public void testRegexUnrestrictedServer() {
    Assertions.assertFalse(plainTextChannel.isServerRestricted("does"));
    Assertions.assertFalse(plainTextChannel.isServerRestricted("not"));
    Assertions.assertFalse(plainTextChannel.isServerRestricted("matcher"));
  }

  @Test
  public void testRegexRestrictedServer() {
    Assertions.assertTrue(regexTextChannel.isServerRestricted("xxx-regex-1234"));
    Assertions.assertTrue(regexTextChannel.isServerRestricted("matcher-funtime"));
    Assertions.assertTrue(regexTextChannel.isServerRestricted("super-channel"));
  }

  @Test
  public void testRegexRestrictedServerIgnoreCase() {
    Assertions.assertTrue(regexTextChannel.isServerRestricted("xXx-REGEX-1234"));
    Assertions.assertTrue(regexTextChannel.isServerRestricted("maTCher-funtime"));
    Assertions.assertTrue(regexTextChannel.isServerRestricted("sUPEr-chANnel"));
  }
}
