<template>
  <div class="component">
    <h1 class="component-title">Component Test</h1>

    <section>
      <h2 class="component-subtitle">Popover</h2>
      <div class="action-sandbox">
        <popover>
          <toggle-button
            slot="reference"
            description="마이크 on/off"
            :size="34"
            :active="toggle"
            :activeSrc="require('assets/image/call/gnb_ic_voice_on.png')"
            :inactiveSrc="require('assets/image/call/gnb_ic_voice_off.png')"
            @action="toogleOnOff"
          ></toggle-button>
        </popover>
      </div>
    </section>
    <section>
      <h2 class="component-subtitle">Tooltip</h2>
      <div class="action-sandbox">
        <tooltip
          content="마이크 on/off">
          <toggle-button
            slot="body"
            description="마이크 on/off"
            :size="34"
            :active="toggle"
            :activeSrc="require('assets/image/call/gnb_ic_voice_on.png')"
            :inactiveSrc="require('assets/image/call/gnb_ic_voice_off.png')"
            @action="toogleOnOff"
          ></toggle-button>
        </tooltip>
      </div>
    </section>
  </div>
</template>
<script>
import Popover from 'Popover'
import Tooltip from 'Tooltip'
import ToggleButton from 'ToggleButton'
export default {
  components: {
    Popover,
    Tooltip,
    ToggleButton
  },
  data() {
    return {
      toggle: false
    }
  },
  methods: {
    toogleOnOff() {
      
    }
  }
};
</script>

<style lang="scss" scoped>
.component {
  height: 100%;
  background-color: #5a5a5a;
}
</style>
