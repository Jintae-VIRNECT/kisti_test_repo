package com.virnect.mediaserver.config.property;

import org.springframework.stereotype.Component;

@Component
public class BandwidthProperty {

    // Service Bandwidth properties
    private Integer streamsVideoMaxRecvBandwidth;
    private Integer streamsVideoMinRecvBandwidth;
    private Integer streamsVideoMaxSendBandwidth;
    private Integer streamsVideoMinSendBandwidth;

    public void setStreamsVideoMaxRecvBandwidth(Integer streamsVideoMaxRecvBandwidth) {
        this.streamsVideoMaxRecvBandwidth = streamsVideoMaxRecvBandwidth;
    }

    public Integer getStreamsVideoMaxRecvBandwidth() {
        return streamsVideoMaxRecvBandwidth;
    }

    public void setStreamsVideoMinRecvBandwidth(Integer streamsVideoMinRecvBandwidth) {
        this.streamsVideoMinRecvBandwidth = streamsVideoMinRecvBandwidth;
    }

    public Integer getStreamsVideoMinRecvBandwidth() {
        return streamsVideoMinRecvBandwidth;
    }

    public void setStreamsVideoMaxSendBandwidth(Integer streamsVideoMaxSendBandwidth) {
        this.streamsVideoMaxSendBandwidth = streamsVideoMaxSendBandwidth;
    }

    public Integer getStreamsVideoMaxSendBandwidth() {
        return streamsVideoMaxSendBandwidth;
    }

    public void setStreamsVideoMinSendBandwidth(Integer streamsVideoMinSendBandwidth) {
        this.streamsVideoMinSendBandwidth = streamsVideoMinSendBandwidth;
    }

    public Integer getStreamsVideoMinSendBandwidth() {
        return streamsVideoMinSendBandwidth;
    }














}
