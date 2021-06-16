<template>
  <tooltip :content="$t('common.spot_control')">
    <toggle-button
      slot="body"
      customClass="toggle-header"
      :description="$t('common.spot_control')"
      size="2.429rem"
      active
      :activeSrc="require('assets/image/mdpi_gnb_ic_Spot.svg')"
      @action="openSpotControl"
    ></toggle-button>
    <!-- <button
      slot="body"
      class="header-tools__control"
      @click="openSpotControlTab"
    ></button> -->
  </tooltip>
</template>

<script>
import Tooltip from 'Tooltip'
import ToggleButton from 'ToggleButton'

export default {
  components: {
    Tooltip,
    ToggleButton,
  },
  methods: {
    openSpotControl() {
      window.open('/spot-control')
    },
  },
}
</script>

<style></style>
