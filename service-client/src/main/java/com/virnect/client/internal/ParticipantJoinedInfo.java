/*
 * (C) Copyright 2017-2020 OpenVidu (https://openvidu.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.virnect.client.internal;

/**
 * @see Notification
 *
 * @author <a href="mailto:rvlad@naevatec.com">Radu Tom Vlad</a>
 */
public class ParticipantJoinedInfo extends Notification {

  private String id;

  public ParticipantJoinedInfo(String id) {
    super(ProtocolElements.PARTICIPANTJOINED_METHOD);
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    if (getMethod() != null) {
      builder.append("method=").append(getMethod()).append(", ");
    }
    if (id != null) {
      builder.append("id=").append(id);
    }
    builder.append("]");
    return builder.toString();
  }
}
