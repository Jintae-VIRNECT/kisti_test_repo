#!/bin/bash

# KMS for Ubuntu 18.04 (Bionic)
export DISTRO="bionic"
export DEBIAN_FRONTEND=noninteractive

sudo apt-get update
sudo apt-get install --no-install-recommends --yes gnupg
echo "deb [arch=amd64] http://ubuntu.openvidu.io/6.14.0 ${DISTRO} kms6" | sudo tee /etc/apt/sources.list.d/kurento.list
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 5AFA7A83

PACKAGES=(
  cmake
  g++
  libevent-dev
  libboost-program-options-dev
  libboost-log-dev
  libwebsocketpp-dev

  kmsjsoncpp-dev
  kms-cmake-utils
  kms-core-dev
  kms-elements-dev
  kms-datachannelexample-dev
)

sudo apt-get update
sudo apt-get install --no-install-recommends --yes "${PACKAGES[@]}"
