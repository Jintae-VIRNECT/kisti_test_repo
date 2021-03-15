<template>
  <popover
    placement="bottom-end"
    width="5.1429rem"
    trigger="click"
    popperClass="popover-language"
    @visible="toggle"
    :scrollHide="true"
    :topOffset="11"
  >
    <button
      class="language-button"
      slot="reference"
      :class="{ active: menuVisible }"
    >
      <img v-if="menuVisible" src="~assets/image/ic_language_on.svg" />
      <img v-else src="~assets/image/ic_language.svg" />
    </button>
    <div>
      <div
        v-for="(lang, index) in langs"
        :key="index"
        class="popover-language__button"
        :class="{ selected: isSelected(lang.key) }"
      >
        <button @click="changeLang(lang.key)">{{ lang.text }}</button>
      </div>
      <!-- <div
        class="popover-language__button"
        :class="{ selected: isSelected('ko') }"
      >
        <button @click="changeLang('ko')">KOR</button>
      </div>
      <div
        class="popover-language__button"
        :class="{ selected: isSelected('en') }"
      >
        <button @click="changeLang('en')">ENG</button>
      </div> -->
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
  props: {
    imgSrc: {
      type: String,
    },
    activeSrc: {
      type: String,
    },
  },
  data() {
    return {
      defaultLanguage: null,
      menuVisible: false,
      langs: [
        { text: 'KOR', key: 'ko' },
        { text: 'ENG', key: 'en' },
        { text: 'JPN', key: 'ja' },
      ],
    }
  },
  methods: {
    changeLang(lang) {
      this.mx_changeLang(lang)
      this.defaultLanguage = lang

      this.$nextTick(() => {
        this.$eventBus.$emit('popover:close')
        this.$eventBus.$emit('refresh:chart')
      })
    },
    isSelected(lang) {
      return this.defaultLanguage === lang
    },
    toggle(visible) {
      this.menuVisible = visible
    },
  },
  mounted() {
    this.defaultLanguage = this.mx_getLangCode()
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';
.popover--wrapper {
  > .language-button {
    display: flex;
    align-items: center;
    justify-content: center;

    > img {
      width: 34px;
      height: 34px;
    }
  }
}

.popover-language {
  min-width: 5.1429rem;
  height: 9.2857rem;
  background: $color_bg_sub;
  border: 1px solid $color_bg_sub_border;
  box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.2);
  > .popover--body {
    padding: 0.5rem 0;
  }
}

.popover-language__button {
  width: 100%;
  height: 2.7143rem;
  padding: 0.5714rem 0;
  text-align: center;

  &:hover {
    background: #424242;
  }

  &.selected {
    > button {
      text-decoration: underline;
    }
  }

  > button {
    width: 100%;
    color: $color_white;
    font-weight: 500;
    font-size: 1.0714rem;
    background: transparent;
  }
}
</style>
