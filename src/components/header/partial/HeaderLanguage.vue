<template>
  <popover
    placement="bottom-end"
    width="5.1429rem"
    trigger="click"
    popperClass="popover-language"
    :scrollHide="true"
  >
    <button class="language-button" slot="reference"></button>
    <div>
      <div
        class="popover-language__button"
        :class="{ selected: isSelected('ko') }"
      >
        <button @click="changeLang('ko')">
          KOR
        </button>
      </div>
      <div
        class="popover-language__button"
        :class="{ selected: isSelected('en') }"
      >
        <button @click="changeLang('en')">
          ENG
        </button>
      </div>
    </div>
  </popover>
</template>

<script>
import Popover from 'Popover'
import langMixin from 'mixins/language'
import confirmMixin from 'mixins/confirm'
export default {
  name: 'HeaderLanguage',
  mixins: [langMixin, confirmMixin],
  components: {
    Popover,
  },
  data() {
    return {
      defaultLanguage: null,
    }
  },
  methods: {
    // changeLang(lang) {
    //   this.mx_changeLang(lang)
    //   this.defaultLanguage = lang

    //   this.$nextTick(() => {
    //     this.$eventBus.$emit('popover:close')
    //   })
    // },
    changeLang() {
      this.confirmDefault('현재 준비중인 기능입니다.')
    },
    isSelected(lang) {
      return this.defaultLanguage === lang
    },
  },
  mounted() {
    this.defaultLanguage = this.mx_getLangCode()
  },
}
</script>

<style lang="scss">
.language-button {
  width: 2.4286rem;
  height: 2.4286rem;
  background: url('~assets/image/ic_language.svg') 50% no-repeat;
}

.popover-language {
  min-width: 5.1429rem;
  height: 6.5714rem;
  background: rgb(255, 255, 255);
  > .popover--body {
    padding: 7px 0;
  }
}

.popover-language__button {
  width: 100%;
  height: 2.7143rem;
  padding: 0.5714rem 0;
  text-align: center;

  &:hover {
    background: #e3e7ed;
  }

  &.selected {
    background-color: #e3e7ed;
    > button {
      text-decoration: underline;
    }
  }

  > button {
    width: 100%;
    color: #0b1f48;
    font-weight: 500;
    font-size: 1.0714rem;
    background: transparent;
    &:active {
      font-weight: 500;
    }
  }
}
</style>
