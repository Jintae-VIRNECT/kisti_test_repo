FROM ubuntu:16.04

WORKDIR /kurento
COPY . /kurento

ENV DISTRO "xenial"
ENV APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=DontWarn
ENV DEBIAN_FRONTEND noninteractive
ENV LANG "C.UTF-8"
ENV GST_DEBUG_NO_COLOR 1
ENV GST_DEBUG ""
ENV KMS_MTU ""
ENV KMS_EXTERNAL_ADDRESS ""
ENV KMS_NETWORK_INTERFACES ""
ENV KMS_STUN_IP ""
ENV KMS_STUN_PORT ""
ENV KMS_TURN_URL ""
ENV KMS_DTLS_PEM_CERT_RSA "/etc/kurento/cert/cert+key.pem"
ENV KMS_DTLS_PEM_CERT_ECDSA ""
ENV INFLUXDB_URL "http://192.168.0.9:8086/MEDIA"
ENV NODE_ENV production

COPY ./docker/entrypoint.sh ./docker/healthchecker.sh ./docker/start.sh /
COPY ./cert/cert+key.pem /etc/kurento/cert/cert+key.pem
COPY ./stats-collector/package.json ./stats-collector/main.js ./stats-collector/run.sh /stats-collector/

RUN apt-get update && \
    apt-get install --no-install-recommends --yes gnupg && \
    echo "deb [arch=amd64] http://ubuntu.openvidu.io/6.14.0 ${DISTRO} kms6" | tee /etc/apt/sources.list.d/kurento.list && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 5AFA7A83 && \
    apt-get update && \
    apt-get install --no-install-recommends --yes cmake g++ libevent-dev libboost-program-options-dev libboost-log-dev libwebsocketpp-dev gstreamer1.5 && \
    apt-get install --no-install-recommends --yes kmsjsoncpp-dev kms-cmake-utils kms-core-dev kms-elements-dev kms-datachannelexample-dev && \
    cmake -DCMAKE_INSTALL_SYSCONFDIR=/etc -DCMAKE_INSTALL_PREFIX=/usr . && \
    make install && \
    apt-get remove --yes cmake g++ libboost-*-dev libwebsocketpp-dev kms-cmake-utils kms-core-dev kms-elements-dev kms-datachannelexample-dev && \
    apt-get remove --yes libnice-dev openwebrtc-gst-plugins-dev kmsjsoncpp-dev && \
    apt-get install --no-install-recommends --yes libboost-filesystem1.58.0 libboost-thread1.58.0 libboost-program-options1.58.0 libboost-log1.58.0 && \
    apt-get install --no-install-recommends --yes kms-core kms-elements kms-datachannelexample && \
    apt-get install --no-install-recommends --yes curl git && \
    curl -sL https://deb.nodesource.com/setup_12.x | bash - && \
    apt-get install --no-install-recommends --yes nodejs && \
    cd /stats-collector && npm install && npm install -g pm2 && \
    apt-get remove --yes curl git && \
    apt-get autoremove --yes && \
    rm -rf /kurento && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 8888

HEALTHCHECK --start-period=15s --interval=30s --timeout=3s --retries=1 CMD /healthchecker.sh

ENTRYPOINT ["/start.sh"]