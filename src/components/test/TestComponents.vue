<template>
  <div class="test">
    <h1 class="test-title">Components</h1>

    <section class="test-section">
      <h2 class="subtitle">Popover</h2>
      <div class="action-box">
        <div class="component">
          <popover
            :placement="popover.position"
            :trigger="popover.trigger"
          >
            <toggle-button
              slot="reference"
              description="마이크 on/off"
              :size="34"
              :active="toggle"
              :activeSrc="require('assets/image/call/gnb_ic_voice_on.svg')"
              :inactiveSrc="require('assets/image/call/gnb_ic_voice_off.svg')"
              @action="toogleOnOff"
            ></toggle-button>
          </popover>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">placement</p>
            
            <select class="props-options" v-model="popover.position">
              <option v-for="(option, idx) in popover.positionOptions"
                      :key="idx"
                      :value="option">{{ option }}</option>
            </select>
          </div>
          <div class="props-option">
            <p class="props-title">trigger</p>
            
            <select class="props-options" v-model="popover.trigger">
              <option v-for="(option, idx) in popover.triggerOptions"
                      :key="idx"
                      :value="option">{{ option }}</option>
            </select>
          </div>
        </div>
      </div>
    </section>
    <section class="test-section">
      <h2 class="subtitle">Tooltip</h2>
      <div class="action-box">
        <div class="component">
          <tooltip
            :content="tooltip.content"
            :placement="tooltip.placement"
          >
            <toggle-button
              slot="body"
              description="마이크 on/off"
              :size="34"
              :active="toggle"
              :activeSrc="require('assets/image/call/gnb_ic_voice_on.svg')"
              :inactiveSrc="require('assets/image/call/gnb_ic_voice_off.svg')"
              @action="toogleOnOff"
            ></toggle-button>
          </tooltip>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">content</p>
            <input class="props-options" v-model="tooltip.content"/>
          </div>
          <div class="props-option">
            <p class="props-title">placement</p>
            
            <select class="props-options" v-model="tooltip.placement">
              <option v-for="(option, idx) in tooltip.placementOptions"
                      :key="idx"
                      :value="option">{{ option }}</option>
            </select>
          </div>
        </div>
      </div>
    </section>
    <section class="test-section">
      <h2 class="subtitle">Modal</h2>
      <div class="action-box">
        <div class="component">
          <modal
            :visible="modal.visible"
          ></modal>
          <tooltip
            :content="tooltip.content"
            :placement="tooltip.placement"
          >
            <toggle-button
              slot="body"
              description="마이크 on/off"
              :size="34"
              :active="toggle"
              :activeSrc="require('assets/image/call/gnb_ic_voice_on.svg')"
              :inactiveSrc="require('assets/image/call/gnb_ic_voice_off.svg')"
              @action="toogleOnOff"
            ></toggle-button>
          </tooltip>
        </div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">content</p>
            <input class="props-options" v-model="tooltip.content"/>
          </div>
          <div class="props-option">
            <p class="props-title">placement</p>
            
            <select class="props-options" v-model="tooltip.placement">
              <option v-for="(option, idx) in tooltip.placementOptions"
                      :key="idx"
                      :value="option">{{ option }}</option>
            </select>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
<script>
import Popover from 'Popover'
import Tooltip from 'Tooltip'
import Modal from 'Modal'
import ToggleButton from 'ToggleButton'
export default {
  components: {
    Popover,
    Tooltip,
    Modal,
    ToggleButton
  },
  data() {
    return {
      toggle: false,
      popover: {
        position: 'bottom',
        positionOptions: [
          "top",
          "top-start",
          "top-end",
          "right",
          "right-start",
          "right-end",
          "bottom",
          "bottom-start",
          "bottom-end",
          "left",
          "left-start",
          "left-end"
        ],
        trigger: "click",
        triggerOptions: [
          "click",
          "hover",
          "focus",
          "manual"
        ]
      },
      tooltip: {
        content: '마이크 on / off',
        placement: 'bottom',
        placementOptions: [
          "top",
          "top-start",
          "top-end",
          "right",
          "right-start",
          "right-end",
          "bottom",
          "bottom-start",
          "bottom-end",
          "left",
          "left-start",
          "left-end"
        ],
      },
      modal: {
        visible: false
      }
    }
  },
  methods: {
    toogleOnOff() {
      
    }
  }
};
</script>

<style lang="scss" scoped>
.test {
  height: 100%;
  padding: 30px;

  &-title {
    font-weight: 500;
    margin-bottom: 30px;
  }

  &-section {
    border-radius: 10px;
    margin-bottom: 30px;
    background-color: #bebebe;
    .subtitle {
      color: #000;
      font-weight: 500;
    }
    .action-box {
      display: flex;
      .component {
        width: 100%;
      }

      .props {
        width: 250px;
        flex-shrink: 0;
        &-option {
          display: flex;
          margin-bottom: 10px;
        }
        &-title {
          width: 100px;
          padding-right: 10px;
          text-align: right;
          flex-shrink: 0;
        }
        &-options {
          width: 100%;
        }
      }
    }
  }
}
</style>
