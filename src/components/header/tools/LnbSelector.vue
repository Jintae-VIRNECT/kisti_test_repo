<template>
  <popover
    trigger="click"
    placement="bottom-start"
    :width="0"
    :topOffset="10"
    popperClass="header-lnb-selector"
  >
    <vue2-scrollbar>
      <ul class="header-lnb-selector__layer">
        <li
          class="header-lnb-selector__button"
          v-for="work of workspaceList"
          :key="work.uuid"
        >
          <button
            @click="changeSelect(work)"
            :class="{ active: work.uuid === workspace.uuid }"
          >
            <span
              class="header-lnb-selector__check"
              :class="{ active: work.uuid === workspace.uuid }"
            ></span>
            <p class="header-lnb-selector__title">{{ work.title }}</p>
            <!-- <p class="header-lnb-selector__description">
              워크스테이션 멤버: {{ option.member }}명
            </p> -->
          </button>
        </li>
      </ul>
    </vue2-scrollbar>

    <button slot="reference" class="header-workspace-selector">
      {{ workspace.title }}
    </button>
  </popover>
</template>

<script>
import Popover from 'Popover'
import { mapGetters, mapActions } from 'vuex'
export default {
  name: 'LnbSelector',
  components: {
    Popover,
  },
  data() {
    return {
      popoverWidth: '14rem',
    }
  },
  computed: {
    ...mapGetters(['workspaceList']),
  },
  methods: {
    ...mapActions(['changeWorkspace']),
    changeSelect(workspace) {
      this.changeWorkspace(workspace)
      // this.$push.changeSubscribe(workspace)
      this.$nextTick(() => {
        this.$eventBus.$emit('popover:close')
        this.popoverWidth = this.$el.querySelector(
          '.header-workspace-selector',
        ).offsetWidth
      })
    },
  },

  /* Lifecycles */
  mounted() {
    this.$nextTick(() => {
      if (!this.workspace.uuid) {
        this.changeSelect(this.workspaceList[0])
      }
    })
  },
}
</script>
<style lang="scss">
@import '~assets/style/vars';

.header-lnb-selector.popover {
  // background-color: $color_bg_sub;
  background-color: #252525;
  border: solid 1px #454545;
  border-radius: 0.286rem;
  box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.2);
  > .popover--body {
    padding: 0;
    > .vue-scrollbar__wrapper {
      max-height: 17.714rem;
      margin-bottom: 0;
      padding-bottom: 0;
      > .vue-scrollbar__area {
        width: 100%;
        height: 100%;
        .vue-scrollbar__scrollbar-vertical {
          // padding-bottom: 10px;
          transform: translateX(-1.071rem);
        }
      }
    }
  }
}
.header-lnb-selector__layer {
  display: block;
  padding: 10px 0;
}
.header-lnb-selector__button {
  height: 4rem;
  > button {
    position: relative;
    width: 100%;
    height: 100%;
    color: #868686;
    &:hover {
      // color: rgba($color_text, 0.7);
      // background-color: rgba($color_bg_item, 0.7);
      color: #fff;
      background-color: #424242;
      // > .header-lnb-selector__check {
      //   border-color: rgba($color_text, 0.4);
      // }
    }
    &.active {
      color: #fff;
      background-color: #424242;
      > .header-lnb-selector__check {
        border-color: $color_primary;
      }
    }
  }
}
.header-lnb-selector__check {
  position: absolute;
  top: 50%;
  left: 1.643rem;
  width: 1.714rem;
  height: 1.714rem;
  border: solid 2px #b3b3b3;
  border-radius: 50%;
  transform: translateY(-50%);
  &.active {
    border-color: $color_primary;
    &::after {
      position: absolute;
      top: 0.214rem;
      left: 0.5rem;
      display: block;

      /*Make it a small rectangle so the border will create an L-shape*/
      width: 4px;
      height: 0.571rem;

      /*Add a white border on the bottom and left, creating that 'L' */
      border: solid $color_primary;
      border-width: 0 2px 2px 0;

      /*Rotate the L 45 degrees to turn it into a checkmark*/
      transform: rotate(45deg);
      content: '';
    }
  }
}
.header-lnb-selector__title {
  display: -webkit-box;
  // height: 2.6em;
  padding: 0 1rem 0 4.429rem;
  overflow: hidden;
  font-weight: 500;
  // line-height: 1.3em;
  text-align: left;
  text-overflow: ellipsis;
  word-break: break-all;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.header-lnb-selector__description {
  padding-left: 4.429rem;
  color: rgba($color_text_sub, 0.76);
  font-size: 0.857rem;
  text-align: left;
}
</style>
